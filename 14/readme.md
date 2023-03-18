# Android 14 변경 사항

## 개요

- Android 14의 변경 사항에 대해서 살펴보는 문서입니다.
- Developer Preview 2를 기준으로 작성되었습니다.

## 동작 변경 사항

### 모든 앱에 적용되는 사항

#### 핵심 기능

##### schedule exact alarm이 기본적으로 거부됩니다.

- exact alarm은 사용자가 지정한 알림이거나 정확한 시간에 발생할 필요가 있는 동작을 의미합니다.
- SCHEDULE_EXACT_ALARM 권한은 Android 13 이상을 타겟팅하는 새로 설치된 앱에서 더이상 미리 허가되지 않으며 기본적으로 거부됩니다.

##### context에 등록된 브로드캐스트는 앱이 캐시된 상태에서 대기합니다.

- 매니페스트에 선언된 브로드캐스트는 대기열(큐)에 추가되지 않으며 브로드캐스트 전달을 위해 앱이 캐시된 상태에서 제거됩니다.
- 앱이 캐시된 상태에서 벗어나 포그라운드로 돌아오면, 시스템은 대기열(큐)의 모든 브로드캐스트를 가져오고 특정 브로드캐스트의 여러 인스턴스는 하나의 브로드캐스트로 합쳐질 수
  있습니다.

##### 앱은 자신의 백그라운드 프로세스만 종료할 수 있습니다.

- killBackgroundProcesses()를 호출하여도 자신의 앱의 백그라운드 프로세스만 종료됩니다.
- 다른 앱의 패키지 이름을 통해 종료를 시도하여도 해당 앱의 백그라운드 프로세스에는 어떠한 영향도 없으며 Logcat에 Invalid packageName:
  com.example.anotherapp 메시지가 표시됩니다.
- 이전 버전의 OS에서도 killBackgroundProcesses() API를 사용해서는 안되며(shouldn't) 다른 앱의 생명주기에 영향을 끼치려고 해서는 안 됩니다.
- 안드로이드는 백그라운드에 캐시된 앱을 유지하며 시스템이 메모리가 필요한 경우에 자동으로 종료되도록 설계되었습니다.
- 내 앱이 다른 앱을 불필요하게 종료한다면, 나중에 다른 앱을 완전히 다시 시작해야하므로 캐시된 앱을 재시작하는 것보다 훨씬 더 많은 리소스가 필요합니다. 이는 시스템 성능을
  저하시키고 배터리 소모를 증가시킬 수 있습니다.

#### 보안

##### 설치 가능한 최소 타겟 API 레벨

- targetSdkVersion이 23 미만인 앱은 설치할 수 없습니다.
- 맬웨어는 오래된 API 레벨을 타겟하여 새로운 안드로이드 버젼에서 적용된 보안과 개인 정보 보호를 우회합니다.
- 대표적으로 Android 6.0(API level 23)에서 도입된 런타임 퍼미션 모델을 회피하기 위해 targetSdkVersion을 22로 하는 멜웨어 앱이 있습니다.
- 낮은 API 레벨을 타겟팅하는 앱을 설치하려고 시도하면 설치가 실패하며 Logcat에 INSTALL_FAILED_DEPRECATED_SDK_VERSION: App package
  must target at least SDK version 23, but found 7가 표시됩니다.
- Android 14로 업그레이드하는 장치에 기존에 설치된 앱은 그대로 유지되며, ADB 명령어 adb install --bypass-low-target-sdk-block
  FILENAME.apk를 통해 이전 API 레벨의 앱을 설치하여 테스트할 수 있습니다.

##### 미디어 owner package name이 수정될 수 있습니다.

- 미디어 저장소는 특정 미디어 파일을 저장한 앱을 나타내는 OWNER_PACKAGE_NAME 칼럼에 대한 쿼리를 지원합니다.
- Android 14 부터는 다음 조건 중 하나 이상이 참인 경우 이 값이 수정됩니다.
  - 항상 다른 앱이 볼 수 있는 패키지 이름으로 된 미디어 파일을 저장한 앱
  - QUERY_ALL_PACKAGES 권한을 요청하며 미디어 저장소를 쿼리하는 앱
    - Google Play는 위험하거나 민감한 권한을 사용하는 것을 제한하고 있습니다. 따라서 QUERY_ALL_PACKAGES 권한이 앱의 주요 목적에 사용되는
      경우에만 사용가능하며 Play의 정책 요구사항을 만족하는 경우에만 Google Play에 게시될 수 있습니다.

#### 사용자 경험

##### 고정(제거할 수 없는) 알림에 대한 사용자 경험 변경

- Notification.FLAG_ONGOING_EVENT를 Notification.Builder.setOngoing(true)로 지정한 경우 사용자는 해당 알림을 제거할 수
  있습니다.
- 아래 사항에서는 여전히 알림을 제거할 수 없습니다.
  - 휴대전화가 잠금 상태인 경우
  - Clear all 버튼으로 알림을 제거하는 경우
  - MediaStyle을 사용하여 만들어진 미디어 재생 알림
  - 보안과 개인 정보와 연관되어 정책을 제한하는 경우
  - DPC(Device Policy Controller)와 기업용 지원 패키지

##### 사진과 영상에 대한 부분적인 액세스 권한 부여

- Android 13에서 도입된 READ_MEDIA_IMAGES, READ_MEDIA_VIDEO 권한을 요청할 경우 사용자는 부분적인 액세스 권한을 허가할 수 있습니다.
- 새로운 권한 선택 다이얼로그에서는 사진과 영상 선택, 항상 모두 허용, 거부 옵션이 표시됩니다.
- 앱에서 photo picker를 사용하는 경우에는 변경사항에 대응할 필요가 없습니다.
- 더 세밀하게 권한을 다루고 싶다면 READ_MEDIA_VISUAL_USER_SELECTED 권한을 사용하는 것을 고려하십시오.
