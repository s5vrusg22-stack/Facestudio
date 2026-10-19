# Face Studio 한국어 Android 프로젝트

한국어 Face Studio를 Android 앱으로 실행하는 프로젝트입니다. 사진 선택, 얼굴 위치와 크기 조절, 원형·둥근 사각형·사각형 테두리 설정, 기본 이모지와 투명 PNG·WebP 소품 추가 및 이동·크기·회전, PNG 저장, Android 공유 기능을 포함합니다. 사진은 앱 안에서 처리하며 네트워크 권한을 요청하지 않습니다.

## APK 만들기

1. 컴퓨터에 Android Studio를 설치합니다.
2. 이 폴더를 Android Studio에서 엽니다.
3. JDK 17, Android SDK Platform 36, Build Tools 36.0.0, Gradle 9.6.0을 사용합니다.
4. Gradle 작업 :app:assembleDebug 를 실행합니다.
5. 설치용 APK는 app/build/outputs/apk/debug/app-debug.apk 에 생성됩니다.

Gradle 9.6.0 및 Android SDK 36이 설치된 컴퓨터에서는 build-apk.sh (Windows는 build-apk.bat)를 실행해도 됩니다. 생성되는 APK는 디버그 서명용이며 Google Play 배포 서명은 포함하지 않습니다.

## 휴대폰에 설치

APK를 휴대폰으로 옮겨 파일 앱에서 열고 설치합니다. Android가 안내하면 해당 파일 앱의 알 수 없는 앱 설치를 허용하세요. 설치한 뒤 Face Studio 아이콘을 눌러 실행합니다.

## 지원 범위

원본 링크에서는 편집 가능한 프로젝트/API를 가져올 수 없어, 확인 가능한 화면을 기준으로 수동 사진 합성 기능을 새로 구성했습니다. 자동 얼굴 인식/분리는 포함하지 않았습니다. 앱을 닫으면 편집 상태가 사라집니다. PNG 저장 또는 공유를 누르면 결과가 사진 폴더에 기록됩니다.

## GitHub Actions로 APK 만들기

이 방법은 웹사이트를 게시하지 않습니다. GitHub 저장소에서 앱을 빌드한 뒤 설치용 APK를 빌드 결과물로 내려받습니다.

1. GitHub에서 비공개(Private) 저장소를 만듭니다.
2. android-project 폴더 안의 파일과 폴더를 저장소의 최상위에 올립니다. .github/workflows/build-apk.yml 파일도 포함해야 합니다.
3. GitHub 저장소의 Actions 탭에서 Build Android APK를 선택하고 Run workflow를 누릅니다. 처음 사용하는 저장소라면 Actions 사용을 먼저 허용합니다.
4. 빌드가 끝나면 해당 실행 페이지의 Artifacts에서 face-studio-debug-apk를 내려받고 압축을 풉니다.
5. 안의 app-debug.apk를 휴대폰으로 옮겨 열어 설치합니다. 개인 설치용 디버그 서명 APK이므로 Play 스토어 배포 서명은 포함하지 않습니다.

워크플로는 소스 코드나 APK를 공개 릴리스로 게시하지 않습니다. 코드는 비공개 저장소에 남고, APK는 Actions 실행 결과물로 14일간 보관됩니다. 개인 설치용 빌드에는 서명 비밀키를 저장할 필요가 없습니다.
