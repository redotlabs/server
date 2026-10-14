# 배포 경로

새 홈서버는 `eraser502/homelab`을 GitOps 기준으로 사용한다. 소스 통합 후 검증·수동 이미지 발행·GitOps PR·수동 Sync 순서는 [홈랩 발행 문서](docs/HOMELAB-IMAGE-PUBLISHING.md)를 따른다.

기존 develop 테스트 서버 자동 배포는 폐기했다. main의 기존 EC2 workflow는 별도이며 유지한다.

## 기존 기록

- 251130 deploy test

Kubernetes 배포 준비는 [별도 문서](docs/KUBERNETES-PREPARATION.md)를 따른다. 기존 EC2 workflow의 image는 runtime 설정을 포함하므로 새 홈랩 image로 재사용하지 않는다.
