# 새 홈서버 런타임 통합 검증 · 2026-10-14

## 통합 이유와 변경

기존 develop의 문의번호 수정 PR #140은 새 홈서버가 실행하는 prepare/kubernetes-runtime 코드와 분리돼 있었다. develop의 오래된 자동 배포도 별도 gitops-repo를 갱신하고 있었다.

- 통합 부모: develop `f80f035cf459aadded71bc4821539e32a5193867`, runtime `ce08b1eec394ef4c5bdad8350ad30b28647f39b2`.
- 문의번호 날짜 초기화·비관적 락·트랜잭션 코드는 develop 그대로 보존했다. 변경한 운영 코드는 기존 runtime 브랜치의 설정·health·CORS다.
- build.gradle 충돌은 test/inquiryExperiment와 integrationTest/kubernetesBootJar를 함께 유지해 해결했다. 동일 이름의 컨텍스트 테스트 충돌은 두 클래스를 보존해 해결했다.
- 기본 test는 문의 12 + 기존 컨텍스트 1 + 홈랩 단위 12를 포함한다. Docker 내부에는 Docker 소켓을 전달하지 않고 homelabUnitTest 12 + integrationTest 5를 실행한다. 중복 단위 12를 제외하면 서로 다른 검증은 30개다.
- PostgreSQL 17.11, Redis 7.4.11 및 Java 21 이미지는 runtime-images.json의 linux/amd64 digest를 사용한다. 로컬 호스트는 macOS arm64이므로 Docker amd64 에뮬레이션이다. 운영 N100 성능 측정이 아니다.
- 기존 test 서버 자동 배포 workflow 제거. 새 GHCR 발행은 develop workflow_dispatch만 허용한다. 기본 브랜치 develop을 GitHub API로 확인했다. main EC2 workflow는 유지한다.
- 기존 성능 실험의 임시 사본에도 runtime-images.json을 포함하고 새 manifest에 이미지 정보를 기록한다. 기존 PG16 측정 데이터는 보존한다.

## 실행 기록

- `./gradlew clean build`: 25개 통과, 실패·오류·스킵 0. 문의 동시성 12개는 PostgreSQL 17.11 digest에서 실행했다.
- `python3 scripts/test-kubernetes.py`: 종료 코드 0. homelabUnitTest 12개와 integrationTest 5개 통과, 실패·오류·스킵 0. Redis AOF 재시작 후 값 유지 PASS. Flyway V1~V7, Hibernate validate, Redis TTL, OAuth callback·보호된 API·health 검증 포함.
- `python3 scripts/check-kubernetes-jar.py`: 공개 리소스 허용목록 및 원본 일치 PASS.
- `docker build --platform linux/amd64 -f Dockerfile.kubernetes`: 로컬 빌드 PASS. 이미지 linux/amd64, USER 10001:10001, Java 21.0.12.1 실행 확인. 레지스트리 게시 안 함. 이 단계는 컨테이너의 전체 서비스 기동 시험을 대신하지 않는다.
- 임시 redot-it 테스트 컨테이너가 남지 않은 것을 확인했다.
- 워크플로 YAML 구문 검사와 git diff --check 통과.
- 요약: [실측 검증 JSON](homelab-integration-validation.json). native 25와 container 17은 12개 단위 테스트가 중복이므로 총 42개의 서로 다른 테스트로 표현하지 않는다.
- 원시 Spring 통합 보고서는 임시 fixture 연결 정보를 포함할 수 있어 Git에 넣지 않는다. 로컬 build/test-results, build/kubernetes-validation.log에 남긴다.
- amd64 Docker 실행에서 Gradle 파일 감시 경고가 있었지만 전체 테스트·패키징 종료 코드는 0이었다. 테스트의 OAuth2ClientAutoConfiguration deprecated 경고도 남는다. 기능 실패로 판정되지는 않았다.

## 발견·수정 및 리뷰

- 첫 native 실행: Testcontainers가 tag@digest 표기를 postgres 이미지로 인식하지 못하여 컨테이너 초기화 실패 2개. 같은 digest의 repository@digest로 변환한 뒤 전체 빌드를 재실행했다. digest나 버전을 낮추지 않았다.
- 기존 실험 스크립트의 docs 일괄 제외가 새 이미지 설정 파일을 누락시켜 보완했다. 실제 main을 실행하고 Gradle subprocess만 mock한 사본 준비 검증으로 3개 대조군 모두 해당 파일과 runtime provenance 존재를 확인했다. 이 smoke 검증은 부하 실험이 아니다.
- 독립 읽기 전용 리뷰에서 중요 추가 지적 없음. 리뷰 담당자는 테스트를 재실행하지 않았으며 실행 결과 확인은 작업 담당자가 수행했다.

## 한계와 다음 배포 단계

이번 작업은 이슈·PR까지만 진행한다. 새 이미지 게시, homelab manifest digest 갱신, Argo CD Sync, 운영 문의 생성/조회는 수행하지 않는다. 실제 Google 로그인·SMTP·S3 외부 호출도 검증하지 않는다.

과거 PG16 72,000건 실험 수치를 PG17이나 운영 성능으로 옮겨 쓰지 않는다. 이번에는 정합성·런타임 호환성 회귀 검증이며 성능 개선율을 새로 주장하지 않는다.

PR 병합 후 develop 수동 GHCR 발행 → registry digest 확인 → homelab manifest 변경 PR → 승인 후 수동 Sync → readiness와 문의 생성/조회 확인이 남는다.

추적 이슈: [#141](https://github.com/redotlabs/server/issues/141).
