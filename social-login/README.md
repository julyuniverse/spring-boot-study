# Social login

작성일: 2024-07-25

### 필수 조건

- jdk 17
- mysql community edition 8(mysql 8)
- redis6

### Guides

- 모바일 클라이언트에 대한 설계 및 개발이 되어 있어요. 웹 클라이언트 지원은 하지 않아요.

### Client reference

ios: https://github.com/julyuniverse/ios-study/tree/main/IosStudy/SocialLogin

### Developments

- social login을 각 social server를 통해 인증 후 회원가입 또는 로그인을 처리되고 그 이후 api 통신은 서버에서 자체 발행한 토큰으로 통신해요.
- sign in with apple
    - ios 클라이언트는 uuid login api 요청 후 social login api 요청해요.
- sign in with google
