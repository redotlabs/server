# 새 홈서버 통합 실행 계획

사용자 승인 범위: develop 문의번호 수정과 prepare/kubernetes-runtime 통합, 격리 테스트, 이슈·커밋·PR. 이미지 게시·실제 배포·PR 병합은 후속 작업.

1. 두 브랜치의 테스트를 별도 클래스로 보존하고 Gradle 검증 작업을 통합한다.
2. runtime-images.json의 PostgreSQL 17.11 amd64로 문의번호 회귀 테스트를 실행한다. Docker 내부 테스트는 Docker 소켓 없이 실행하고 Testcontainers 테스트는 호스트에서 별도로 실행한다.
3. 오래된 develop→Docker Hub→gitops-repo 테스트 배포를 제거한다. GHCR 게시를 develop 수동 실행으로 제한하고 게시 전 양쪽 검증을 실행한다.
4. Redis 7.4.11 AOF 재시작, Flyway/Hibernate, OAuth·CORS·health 노출, 패키징 허용목록을 검증한다.
5. 결과와 제한을 기록하고 전체 변경 리뷰 후 이슈·커밋·PR을 생성한다.

기존 PG16 성능 측정 자료는 당시 조건의 이력으로 보존한다. 이번 PG17 검증 결과를 해당 수치의 재측정으로 표현하지 않는다.
