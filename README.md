![VocaRoutine_introduce](https://github.com/gurumdevv/VocaRoutine/assets/129643788/90e983ed-9593-41ea-bdfb-fcf4e14dfb9e)

# 보카루틴 - 암기부터 복습까지 모두 챙겨주는 스마트 단어장

## Project Explanation
🌟 단어장 작성, 퀴즈 기능을 넘어서 열심히 외웠던 단어를 까먹지 않도록 최적화된 주기로 복습할 수 있는 기능을 제공합니다.<br>
🌟 기존의 뜻과 단어만 있는 단어장과 차별화될 수 있도록 단순 암기가 아닌 인공지능 API를 활용해 어원으로 보다 쉽게 암기할 수 있도록 기능을 제공합니다.
  
✏️ **어원으로 암기**<br>
단어장을 작성할 때는 단어와 뜻만 입력하면 됩니다. 인공지능 API를 활용해 단어를 분석해 어원을 제공하기 때문에 완성된 단어장에서는 어원과 함께 쉽게 단어를 암기할 수 있습니다.<br>
✏️ **사진으로 단어장 만들기**<br>
ML Kit의 Document Scan, OCR 기능을 도입했습니다. 카메라로 문서를 스캔하면 자동으로 페이지를 인식하고 페이지 내 글자를 문자로 변환해 쉽게 단어장을 만들 수 있습니다.<br>
✏️ **복습 알림**<br>
나중에 복습하길 원한다면 단어장 상세 페이지에서 알림 버튼만 터치하면 1, 3, 7일 후 복습할 수 있도록 알림과 퀴즈가 제공됩니다.<br>
✏️ **단어장 공유**<br>
작성한 단어장을 친구, 동료들과 공유할 수 있도록 단어장 공유 기능을 제공합니다.<br>
✏️ **오프라인 단어장**<br>
네트워크가 안 되는 상황에서도 단어장 상세 페이지에서 다운로드 버튼만 누르면 어디서든 단어를 학습할 수 있습니다.<br>

## Screenshot
<p align="center">
<img src="https://github.com/gurumdevv/VocaRoutine/assets/129643788/60b32cde-11e2-47c2-8d40-da8e1007df47" width="23%" height="30%">
<img src="https://github.com/gurumdevv/VocaRoutine/assets/129643788/39780f2b-e423-4d61-8d5e-1a85d0597967" width="23%" height="30%">
<img src="https://github.com/gurumdevv/VocaRoutine/assets/129643788/62a0b8ff-5765-4359-a146-033a46ca3515" width="23%" height="30%">
<img src="https://github.com/gurumdevv/VocaRoutine/assets/129643788/334de7ff-a3b4-4a1a-8f8d-a016bd50cda1" width="23%" height="30%">
</p>

## App Architecture
<img width="960" alt="appArchitecure" src="https://github.com/gurumdevv/VocaRoutine/assets/129643788/5cb4231e-3288-4101-b2e0-2fc16e8380ff">

## History
- ✔️ **2023.10 ~ 2023.12:** 개발 완료
- ✔️ **2024.05.13:** 다크모드 대응
- ✔️ **2024.05.15 ~ 2024.05.22:** Flow 기술 전환, 버그 수정, PlayStore 출시 준비

## Tech Skill
| Category | Stack |
| --- | --- |
| **Architecture** | MVVM |
| **Android Jetpack** | ViewModel, LiveData, Navigation, Room, Data Binding |
| **Dependency Injection** | Hilt |
| **Networking** | Retrofit2, OkHttp3 |
| **Asynchronous** | Coroutine, Flow, SharedFlow, StateFlow |
| **Local DB** | Room, DataStore |
| **Background** | AlarmManager |
| **Notification** | NotificationManager |
| **Firebase** | Crashlytics |

## How to Download
PlayStore 출시를 위해 현재 비공개 테스트 중입니다. 출시가 완료되면 PlayStore에서 다운로드 가능합니다.<br>
현재는 아래 링크에서 apk 파일을 다운받으실 수 있습니다.<br><br>
<a href="https://drive.google.com/drive/folders/18l2NL2vXX4qghP2n8YrMIlTD-qJ9stF2?usp=sharing">
    <img src="https://github.com/gurumdevv/VocaRoutine/assets/129643788/bb151951-8222-4d6d-b729-9cbc959707f5" alt="어원으로 암기" width="45" height="45">
</a> 
&nbsp;&nbsp;← 구글 드라이브 아이콘을 클릭해주세요.
