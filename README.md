
# 업무현황일지
![3생활관](https://github.com/user-attachments/assets/69723344-8ce0-41fe-83b4-a1386c48852d)

## 프로젝트 소개  
**업무현황일지**는 부대 운영 진행 상황과 계획을 실시간으로 공유 및 기록할 수 있는 **칸반보드 형식의 어플리케이션** 백엔드 시스템이다.  

## 주요 목표  
부대 내 각 부서 간 원활한 **정보 공유와 일정 관리**, 그리고 **운영 기록의 데이터화 및 분석**을 통해 **의사 결정의 신속성을 높이기 위한 어플리케이션 백엔드** 개발에 중점을 둔다.

---

## 기술 스택  

### 백엔드  
- **Spring Boot**  

### 데이터베이스  
- **Amazon RDS**  
- **Redis** (캐싱)  

### 인프라 및 배포  
- **Docker**  
- **Amazon EC2**  
- **Amazon ECR**  
- **Amazon S3**  

### CI/CD  
- **GitHub Actions**  

---

## ERD (Entity Relationship Diagram)  
![image](https://github.com/user-attachments/assets/d69d51f2-f00d-48de-9846-ed45b5c91df1)

---

## 시스템 구조
![image](https://github.com/user-attachments/assets/5973ebf5-85c5-47a4-af2a-f3df22c31cb2)

---

# API 목록

| **필수 여부** | **담당자**            | **기능**                          | **Method** | **URL**                                          | **Request Header**                         | **Response Header** | **Request**                                                                                         | **Response**                                                                                         |
|---------------|-----------------------|-----------------------------------|------------|-------------------------------------------------|-------------------------------------------|--------------------|------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------|
| 필수          | 이건                  | 회원가입                         | POST       | /api/v1/auth/signup                             | Authorization: Bearer {REST_API_KEY}      |                    | `{ "email": 이메일, "password": 비밀번호, "userRole": 유저권한 }`                                      | `{ "bearerToken": jwt 토큰 }`                                                                         |
| 필수          | 이건                  | 로그인                           | POST       | /api/v1/auth/signin                             | Authorization: Bearer {REST_API_KEY}      |                    | `{ "email": 이메일, "password": 비밀번호 }`                                                           | `{ "bearerToken": jwt 토큰 }`                                                                         |
| 필수          | 이건                  | 유저 조회                        | GET        | /api/v1/users/{userId}                         | Authorization: Bearer {REST_API_KEY}      |                    |                                                                                                      | `{ "userId": 유저ID, "email": 이메일, "workspaces": [{ workSpaceDto }] }`                             |
| 필수          | 이건                  | 회원탈퇴                         | DELETE     | /api/v1/users                                  | Authorization: Bearer {REST_API_KEY}      |                    |                                                                                                      |                                                                                                       |
| 필수          | 이건                  | 비밀번호 변경                    | PATCH      | /api/v1/users                                  | Authorization: Bearer {REST_API_KEY}      |                    | `{ "oldPassword": 현재 비밀번호, "newPassword": 새로운 비밀번호 }`                                     |                                                                                                       |
| 필수          | 정승헌                | 워크스페이스 생성 (Admin Only)   | POST       | /api/v1/admin/workspaces                       | Authorization: Bearer {REST_API_KEY}      |                    | `{ "userID": 유저아이디, "workSpaceTitle": 제목, "explain": 설명 }`                                   | `{ "workSpaceId": 워크스페이스ID, "userId": 유저아이디, "workSpaceTitle": 제목, "explain": 설명 }`      |
| 필수          | 정승헌                | 워크스페이스 역할 수정 (Admin)   | PUT        | /api/v1/admin/{workSpaceId}/members/{memberId} | Authorization: Bearer {REST_API_KEY}      |                    | `{ "memberRole": 멤버권한 }`                                                                           | `{ "memberId": 멤버ID, "userId": 유저ID, "workspaceId": 워크스페이스ID, "memberRole": 멤버권한 }`     |
| 필수          | 정승헌                | 멤버 역할 수정                   | PUT        | /api/v1/members/{memberId}                     | Authorization: Bearer {REST_API_KEY}      |                    | `{ "memberRole": 멤버권한 }`                                                                           | `{ "memberId": 멤버ID, "userId": 유저ID, "workspaceId": 워크스페이스ID, "memberRole": 멤버권한 }`     |
| 필수          | 정승헌                | 워크스페이스 조회                | GET        | /api/v1/workspaces/{userId}                    | Authorization: Bearer {REST_API_KEY}      |                    |                                                                                                      | `{ "workSpaceId": 워크스페이스ID }`                                                                    |
| 필수          | 정승헌                | 워크스페이스 멤버 초대 (Admin)   | POST       | /api/v1/admin/invites                          | Authorization: Bearer {REST_API_KEY}      |                    | `{ "workSpaceId": 워크스페이스Id, "userId": 초대될 유저Id, "memberRole": 멤버권한 }`                   | `{ "workSpaceId": 워크스페이스Id, "memberRole": 멤버권한, "invitetoUserId": 초대된유저Id, "inviteId": 초대Id }` |
| 필수          | 정승헌                | 멤버 초대                        | POST       | /api/v1/invites                                | Authorization: Bearer {REST_API_KEY}      |                    | `{ "workSpaceId": 워크스페이스Id, "userId": 초대될 유저Id, "memberRole": 멤버권한 }`                   | `{ "workSpaceId": 워크스페이스Id, "memberRole": 멤버권한, "invitetoUserId": 초대된유저Id, "inviteId": 초대Id }` |
| 필수          | 정승헌                | 멤버 초대 목록 조회              | GET        | /api/v1/invites                                | Authorization: Bearer {REST_API_KEY}      |                    |                                                                                                      | `{ "workSpaceId": 워크스페이스Id, "memberRole": 멤버권한, "invitetoUserId": 초대된유저Id, "inviteId": 초대Id }` |
| 필수          | 정승헌                | 멤버 초대 수락                   | POST       | /api/v1/invites/{inviteId}/accept              | Authorization: Bearer {REST_API_KEY}      |                    |                                                                                                      | `{ "workSpaceId": 워크스페이스Id, "memberId": 멤버Id, "memberRole": 멤버권한 }`                         |
| 필수          | 정승헌                | 멤버 초대 거절                   | DELETE     | /api/v1/invite/{inviteId}/refuse               | Authorization: Bearer {REST_API_KEY}      |                    |                                                                                                      | 200 OK                                                                                                 |
| 필수          | 정승헌                | 워크스페이스 수정                | PUT        | /api/v1/workspaces/{workSpaceId}               | Authorization: Bearer {REST_API_KEY}      |                    | `{ "workSpaceTitle": 이름, "explain": 설명 }`                                                          | `{ "workSpaceId": 워크스페이스ID, "userId": 유저아이디, "workSpaceTitle": 제목, "explain": 설명 }`      |
| 필수          | 정승헌                | 워크스페이스 삭제                | DELETE     | /api/v1/workspaces/{workSpaceId}               | Authorization: Bearer {REST_API_KEY}      |                    |                                                                                                      | 200 OK                                                                                                 |
| 필수          | 정승헌                | 보드 생성                        | POST       | /api/v1/workspaces/{workSpaceId}/boards        | Authorization: Bearer {REST_API_KEY}      |                    | `{ "boardTitle": 보드이름, "background": 배경 }`                                                       | `{ "workSpaceId": 워크스페이스ID, "boardId": 보드아이디, "boardTitle": 제목, "background": 설명 }`      |
| 필수          | 정승헌                | 보드 수정                        | PUT        | /api/v1/workspaces/{workSpaceId}/boards/{boardId} | Authorization: Bearer {REST_API_KEY} |                    | `{ "boardTitle": 보드이름, "background": 배경 }`                                                       | `{ "workSpaceId": 워크스페이스ID, "boardId": 보드아이디, "boardTitle": 제목, "background": 설명 }`      |
| 필수          | 정승헌                | 보드 조회                        | GET        | /api/v1/workspaces/boards                     | Authorization: Bearer {REST_API_KEY}      |                    |                                                                                                      | `{ "workSpaceId": 워크스페이스ID, "boardId": 보드아이디 }`                                             |
| 필수          | 정승헌                | 보드 단건 조회                   | GET        | /api/v1/workspaces/boards/{boardId}           | Authorization: Bearer {REST_API_KEY}      |                    |                                                                                                      | `{ "listId": 리스트ID, "cardId": 카드ID }`                                                             |
| 필수          | 박예서                | 카드 검색                        | GET        | /api/v1/lists/cards/search                    | Authorization: Bearer {REST_API_KEY}      |                    | Query Params: `cardTitle`, `cardExplain`, `duedate`, `deadline`, `page`, `size`                        | `CardSearchResponseDtos`                                                                              |
| 필수          | 박예서                | 리스트 생성                      | POST       | /api/v1/lists                                 | Authorization: Bearer {REST_API_KEY}      |                    | `{ "title": "백엔드 작업 목록", "position": 1, "boardId": 12345 }`                                     |                                                                                                       |
| 필수          | 박예서                | 리스트 수정                      | PATCH      | /api/v1/lists/{listId}                        | Authorization: Bearer {REST_API_KEY}      |                    | `{ "title": "백엔드 작업 수정", "position": 1, "boardId": 12345 }`                                     |                                                                                                       |
| 필수          | 박예서                | 리스트 삭제                      | DELETE     | /api/v1/lists/{listId}                        | Authorization: Bearer {REST_API_KEY}      |                    |                                                                                                      |                                                                                                       |
| 필수          | 이주호                | 카드 생성                        | POST       | /api/v1/lists/{listId}/cards                  | Authorization: Bearer {REST_API_KEY}      |                    | Form-data: `title`, `cardExplain`, `deadline`, `attachment`                                           | `{ "id": 13, "title": "title", "cardExplain": "explain", "deadline": "2024-10-01T02:24:49", "attachment": null }` |
| 필수          | 이주호                | 카드 수정                        | PUT        | /api/v1/lists/{listId}/cards/{cardId}         | Authorization: Bearer {REST_API_KEY}      |                    | `{ "title": "title23", "cardExplain": "explain23", "deadline": "2024-10-01T02:24:49", "attachment": "url23" }` | `{ "id": 2, "title": "title23", "cardExplain": "explain23", "deadline": "2024-10-01T02:24:49", "attachment": "url23" }` |
| 필수          | 이주호                | 댓글 생성                        | POST       | /api/v1/cards/{cardId}/comments               | Authorization: Bearer {REST_API_KEY}      |                    | `{ "contents": "contents", "emoji": "emoji zz" }`                                                     | `{ "commentId": 3, "contents": "contents", "emoji": "emoji zz", "createdAt": "2024-10-17T05:05:14", "modifiedAt": "2024-10-17T05:05:14" }` |
| 필수          | 이주호                | 댓글 조회                        | GET        | /api/v1/cards/{cardId}/comments                 | Authorization: Bearer {REST_API_KEY}      |                    |                                                         | `[ { "commentId": 2, "commentContents": "contents", "emoji": "emoji zz", "createdAt": "2024-10-17T04:00:49", "modifiedAt": "2024-10-17T04:00:49" }, { "commentId": 3, "commentContents": "contents", "emoji": "emoji zz", "createdAt": "2024-10-17T05:05:14", "modifiedAt": "2024-10-17T05:05:14" } ]` |
| 필수          | 이주호                | 댓글 수정                        | PUT        | /api/v1/cards/{cardId}/comments/{commentId}    | Authorization: Bearer {REST_API_KEY}      |                    | `{ "contents": "contents", "emoji": "emoji" }`          | `{ "id": 1, "contents": "put le go", "emoji": "put le go", "createdAt": "2024-10-17T03:41:22", "modifiedAt": "2024-10-17T03:41:22" }` |
| 필수          | 이주호                | 댓글 삭제                        | DELETE     | /api/v1/cards/{cardId}/comments/{commentId}    | Authorization: Bearer {REST_API_KEY}      |                    |                                                         | 200 OK                                                   |
| 필수          | 이주호                | 담당자 추가                      | POST       | /api/v1/cards/{cardId}/assignees               | Authorization: Bearer {REST_API_KEY}      |                    |                                                         |                                                          |
| 필수          | 이주호                | 담당자 조회                      | GET        | /api/v1/cards/{cardId}/assignees               | Authorization: Bearer {REST_API_KEY}      |                    |                                                         |                                                          |
| 필수          | 이주호                | 담당자 제거                      | DELETE     | /api/v1/cards/{cardId}/assignees/{assigneeId} | Authorization: Bearer {REST_API_KEY}      |                    |                                                         |                                                          |
| 필수          | 이주호, 정승헌        | 첨부 조회                        | GET        | /api/v1/lists/cards/{cardId}/attachment        | Authorization: Bearer {REST_API_KEY}      |                    |                                                         | 첨부파일 URL (비공개 처리)                              |
| 필수          | 이주호, 정승헌        | 첨부 삭제                        | DELETE     | /api/v1/lists/cards/{cardId}/attachment        | Authorization: Bearer {REST_API_KEY}      |                    |                                                         | 200 OK                                                   |
| 필수          | 이건                  | 알림                             |            |                                                |                                               |                    |                                                         |                                                          |
