# Homelab 이미지 발행 준비

2026-09-27: 파일 작성/로컬 검증만 완료. commit/push/image publish는 미실행.

- 별도 workflow: `.github/workflows/publish-homelab-image.yml`.
- 초기 배포 브랜치: `prepare/kubernetes-runtime`. main/develop의 기존 EC2/test workflow는 그대로다.
- 승인된 Registry/image: GHCR `ghcr.io/redotlabs/server-homelab`, Private. Organization의 Private package 생성 허용 여부는 아직 확인되지 않았으며 승인 후 실제 최초 publish 결과로 검증한다.
- push trigger는 준비 브랜치만. workflow_dispatch는 준비 브랜치/main으로 제한하지만 default branch에 workflow가 있어야 UI 사용 가능하다. 최초는 준비 브랜치 push 방식.
- Test: `python3 scripts/test-kubernetes.py`. 고정 PostgreSQL/Redis의 임시 Docker fixture로 17개 테스트/Flyway/Hibernate 및 Redis AOF 검증 후 `kubernetesBootJar` 생성.
- `python3 scripts/check-kubernetes-jar.py`로 JAR resource allowlist/공개 소스 일치를 검사.
- Dockerfile.kubernetes, amd64, non-root UID10001, pinned Java21 JRE.
- GHCR publish는 job의 GITHUB_TOKEN contents:read/packages:write. 기존 repository secret YAML이나 DockerHub/EC2 credential을 참조하지 않는다.
- tag는 sha-전체commit. 이미 있는 tag는 덮어쓰기 거부. registry 조회 실패 시 발행 중단.
- registry 자체 불변성을 가정하지 않는다. Homelab에는 발행된 `:sha-<commit>@sha256:<digest>`를 사용한다. local image ID/JAR SHA를 registry digest 대신 넣지 않는다.
- Private package를 유지한다. Kubernetes pull credential은 실제 package 생성 후에만 준비한다. package Read 권한 계정의 classic PAT read:packages + SOPS Secret을 사용한다. 별도 publish PAT는 생성하지 않는다. Argo SSH key는 image pull credential이 아니다.
- DB/OAuth/JWT/SMTP/AWS/Discord 값은 JAR/image/Actions secret YAML에서 분리하여 Kubernetes runtime에만 주입한다.
- workflow나 이미지에는 실제 application credential이 필요 없다. Secret은 image build-arg로도 전달하지 않는다.
- test logs/reports는 runtime fixture 정보가 들어갈 수 있어 자동 artifact 업로드하지 않는다. Buildx build record 업로드도 비활성화했다.

로컬 검증: unit12/integration5 통과, Docker build 통과, packaging allowlist 통과, actionlint1.7.12 통과. 실제 GitHub workflow/registry push는 승인 전 미실행이다.

Homelab 상세 설계는 homelab repo의 `docs/19-redot-image-gitops-preparation.md`와 `applications/redot`에 있다. frontend는 아직 미확정이다. Google callback은 `https://redot-api.dhxxn.kr/api/v1/sign-in/redot-member/social/callback/redot-member-google`.

다음 단계는 파일 리뷰 후 Application repository commit/push 및 그에 따른 최초 publish 승인이다. 현재는 commit/push 직전에 멈춘다. 준비 브랜치 push는 publish workflow를 자동 실행하므로 push와 image publish를 별개인 것처럼 취급하지 않는다. 이 workflow는 Homelab Git을 수정하거나 Kubernetes에 접근하지 않는다.


## 승인된 실행 순서와 담당 (2026-09-27)

현재 단계에서는 모든 실제 credential 입력을 보류한다. frontend origin/redirect도 미정 상태다.

| 순서 | 담당 | 수행할 작업 | Secret 입력 |
|---|---|---|---|
| 1 | Codex | 로컬 브랜치/17개 테스트/JAR/image/workflow 최종 검사와 변경 보고 후 중단 | 없음 |
| 2 | 사용자 | Application repo commit/push와 이에 따른 최초 image publish를 명시적으로 승인 | 없음 |
| 3 | Codex | 승인된 파일만 commit. 인증이 이미 제공된 범위면 준비 브랜치에 push; 인증 부재 시 사용자 N100 push로 인계 | Codex가 값을 받지 않음 |
| 4 | 사용자 N100 터미널 | 인계받은 경우에만 git push -u origin prepare/kubernetes-runtime. 개인 terminal의 Git 인증 prompt에서 입력; credential을 command line/history/docs에 넣지 않음 | Git push 인증이 필요할 때만 |
| 5 | GitHub Actions / Codex | GITHUB_TOKEN으로 최초 발행 시도. Actions 결과와 오류를 확인해 org package 생성 권한을 실제 검증. SHA/digest/amd64 확인 | publish PAT 불필요 |
| 6 | 사용자 GitHub UI | 성공하면 Package settings에서 Private, redotlabs/server 연결/Actions 접근 확인. package 생성 정책 오류면 조직 Settings→Packages→Package Creation의 Private 허용을 관리자가 수정. 기존 package 접근 오류면 Manage Actions access에서 repository Write 확인 | 없음 |
| 7 | Codex | 실패 시 credential을 추가하는 대신 원인별 수정 계획 제시. 성공 시 실제 package read 권한/visibility 확인 및 pull 준비. 권한 오류 때 재실행도 사용자와 조율 | 없음 |
| 8 | 사용자 GitHub UI | package 생성 성공 후 pull 계정의 package Read 접근을 부여하고 classic PAT read:packages 생성, 필요한 SSO 승인 | PAT 생성; 채팅 공유 금지 |
| 9 | Codex / 사용자 N100 터미널 | 그 시점에 registry 전용 숨김 입력 절차를 준비한 뒤 사용자 입력→SOPS pull Secret 파일 준비→안전한 pull 인증 검증 | pull credential만 |
| 10 | 사용자 / Codex | 별도 단계에서 frontend 일반 설정과 application credential을 준비하고 runtime 설정 검증 | Google/SMTP/AWS 등은 이 단계까지 보류 |
| 11 | 사용자 / Codex | Homelab 변경 review 및 별도 commit/push 승인. Kubernetes 등록/Sync는 다시 별도 배포 승인 후 | 기존 SOPS 파일 사용 |

현재 기존 `prepare-redot-secrets.py`는 application credential과 pull credential을 함께 묻는다. 지금 실행하지 않는다. package 생성 후 pull만 먼저 준비할 때는 registry 전용 입력 절차를 별도로 준비한다. 장기 Git push PAT와 registry pull PAT도 용도/권한을 혼동하지 않는다.

GitHub UI에서 workflow를 수동 실행하거나 기존 EC2 workflow를 실행할 필요는 없다. 첫 준비 브랜치 push가 신규 workflow의 실행 계기다. 워크플로우는 private visibility를 강제로 설정하는 API 작업을 하지 않으므로 실제 package 설정은 최초 발행 후 확인한다. package가 이미 존재한다면 그 visibility/접근 권한 확인이 필요하며 public으로 된 기존 package를 private이라고 간주하지 않는다.

최종 검사에서 Gradle UP-TO-DATE 재사용을 발견하여 홈랩 검증 스크립트에 `--rerun-tasks`를 추가했다. 임시 DB/Redis가 매번 새로 생성되므로 테스트도 매번 실제 실행한다. 이전 XML 존재만으로 신규 DB migration 검증을 주장하지 않는다. 기존 EC2 workflow 명령에는 영향이 없다.
