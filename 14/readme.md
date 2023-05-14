# Android 14 변경 사항

## 개요

- Android 14의 변경 사항에 대해서 살펴보는 문서입니다.
- Beta 2 관련 내용을 추가하였습니다.

## 요약

- 다양한 기기와 폼 팩터에서 작동
    - 태블릿과 폴더블 폼 팩터에 대한 지원 강화
- 백그라운드 작업 간소화
    - JobScheduler와 포그라운드 서비스 업데이트 및 기능 추가
        - 우선순위가 가장 높은 사용자 대상 작업에만 포그라운드 서비스 사용
        - 사용자가 시작한 데이터 전송을 위한 새로운 기능 추가(기존 API 변경)
        - 포그라운드 서비스 유형 선언이 요구되어 포그라운드 서비스에 맞는 사용 사레를 명확히 정의 -> Google Play가 API의 적절한 사용을 보장하는 새로운 정책
          발표 예정
    - 브로드캐스트 최적화
        - 앱이 캐시된 상태에서는 컨텍스트 등록 브로드캐스트가 큐에 저장되며 전달되기 전에 하나로 병합(BATTERY_CHANGED 같은 반복 브로드캐스트)될 수 있음
    - 정확한 알람
        - Android 13이상을 대상으로 새로 설치된 앱은 SCHEDULE_EXACT_ALARM을 사용자에게 허가받아야 함
        - 시계 및 캘린더 앱은 설치시 권한이 부여되는 USE_EXACT_ALARM 권한을 선언할 수 있지만 핵심 앱 기능이 아닌 경우 Google Play에 게시할 수
          없음
- 맞춤 설정
    - 비선형 크기 조정으로 200%까지 확대
        - 텍스트가 너무 커지는 문제를 완화하기 위해 이미 충분히 큰 텍스트는 적은 비율로 확대
    - 앱별 언어 설정
        - localConfig를 동적으로 업데이트하여 Android 설정의 앱별 언어 목록에 표시될 언어 집합을 맞춤 설정 가능
        - IME는 현재 앱의 UI 언어를 파악하여 표시될 키보드 언어를 업데이트 가능
    - 성별이 있는 언어 지원
        - 문자열 기준으로 적용되는 ICU의 SelectFormat 보다 쉽게 성별이 있는 언어를 지원
- 개인정보 보호 및 보안
    - 런타임 브로드캐스트 리시버
        - 동적으로 등록되는 브로드캐시트 리시버는 exported 또는 unexported로 지정해야 함
    - 더 안전한 암시적 인텐트
        - 패키지가 지정되지 않은 인텐트를 내부적으로 전송할 수 없음(내보내지 않은 활동을 시작하려면 명식적 인텐트를 사용해야 함)
    - 더 안전한 동적 코드 로딩
        - 동적으로 로드된 파일은 읽기 전용으로 설정해야 함
    - 앱 설치 차단
        - targetSdkVersion 23 미만 설치 불가
    - Credential Manager와 패스 키 지원
        - 사용자 인증 과정을 단순화하고 패스키 지원으로 보안 강화할 수 있는 Credential Manager API(생체 인증으로 안전한 로그인)
- 앱 호환성
    - OpenJDK 17 지원
        - Java 17 언어 기능 제공
    - 쉬워진 변경 사항 테스트와 디버깅
        - adb 또는 개발자 옵션에서 변경 사항을 강제 적용 및 해제 가능(앱의 targetSdkVersion을 변경하지 않고 특정 변경사항만 적용해 볼 수 있음)

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
  must target at least SDK version 23, but found 7 메시지가 표시됩니다.
- Android 14로 업그레이드하는 장치에서 기존에 설치된 앱은 그대로 유지되며, ADB 명령어 adb install --bypass-low-target-sdk-block
  FILENAME.apk를 통해 이전 API 레벨의 앱을 설치하여 테스트할 수 있습니다.

##### 미디어 owner package name이 수정될 수 있습니다.

- 미디어 저장소는 특정 미디어 파일을 저장한 앱을 나타내는 OWNER_PACKAGE_NAME 칼럼에 대한 쿼리를 지원합니다.
- Android 14 부터는 다음 조건 중 하나 이상이 참인 경우 이 값이 수정됩니다.
    - 항상 다른 앱이 볼 수 있는 패키지 이름으로 된 미디어 파일을 저장한 앱
    - QUERY_ALL_PACKAGES 권한을 요청하며 미디어 저장소를 쿼리하는 앱
        - Google Play는 위험하거나 민감한 권한을 사용하는 것을 제한하고 있습니다.
          따라서 QUERY_ALL_PACKAGES 권한이 앱의 주요 목적에 사용되는 경우에만 사용가능하며
          Google Play의 정책 요구사항을 만족하는 경우에만 게시될 수 있습니다.

#### 사용자 경험

##### 사진과 영상에 대한 부분적인 액세스 권한 부여

- Android 13에서 도입된 READ_MEDIA_IMAGES, READ_MEDIA_VIDEO 권한을 요청할 경우 사용자는 부분적으로 미디어 액세스 권한을 허가할 수 있습니다.
- 새로운 권한 선택 다이얼로그에서는 사진과 영상 선택, 항상 모두 허용, 거부 옵션이 표시됩니다.
- 앱에서 photo picker를 사용하는 경우에는 변경사항에 대응할 필요가 없습니다.
- 더 세밀하게 권한을 다루고 싶다면 READ_MEDIA_VISUAL_USER_SELECTED 권한을 사용하는 것을 고려하세요.

##### 안전한 전체 화면 인텐트 알림

- Android 11(API level 30)에서는 어떤 앱이든 폰이 잠겨 있는 상태에서 Notification.Builder.setFullScreenIndent를 이용하여
  전체화면 인덴트를 전송할 수 있었습니다.
    - AndroidManifest에 USE_FULL_SCREEN_INDENT 권한을 선언하면 앱 설치 시 이를 자동 부여할 수 있습니다.
- 전체 화면 인덴트 알림은 전화와 알람같이 사용자의 즉각적인 주의가 요구되는 매우 높은 우선 순위 알림을 위해 설계되었습니다.
    - Android 14부터는 권한 사용 목적을 전화와 알람으로 제한합니다.
    - Google Play Store는 위 사항을 준수하지 않고 USE_FULL_SCREEN_INTENT 권한을 적용한 앱을 거부합니다.
    - Android 14로 업데이트 하기 이전에 설치된 앱은 권한이 허용된 상태로 유지되며 사용자가 권한을 거부할 수 있게 됩니다.
    - NotificationManager.canUseFullScreenIntent API를 이용하여 앱에 권한이 있는지 확인할 수 있습니다.
    - 권한이 거부된 경우, ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT로 사용자가 권한을 허가할 수 있는 설정 페이지를 열 수 있습니다.

##### 고정(제거할 수 없는) 알림에 대한 사용자 경험 변경

- Notification.FLAG_ONGOING_EVENT를 Notification.Builder.setOngoing(true)로 지정한 경우 사용자는 해당 알림을 제거할 수
  있습니다.
- 아래 사항에서는 여전히 알림을 제거할 수 없습니다.
    - 휴대전화가 잠금 상태인 경우
    - Clear all 버튼으로 알림을 제거하는 경우
    - MediaStyle을 사용하여 만들어진 미디어 재생 알림
    - 보안과 개인 정보와 연관되어 정책을 제한하는 경우
    - DPC(Device Policy Controller)와 기업용 지원 패키지

##### 찾기 쉬워진 데이터 보안 정보

- 사용자 개인 정보 보호 강화를 위해 Android 14에서는 개발자가 Play Console에 입력한 정보가 더 다양한 곳에 표시됩니다.
- 현재 Google Play의 각 앱 설명의 데이터 보안 항목에서 확인할 수 있습니다.
- 앱의 위치 데이터 공유 정책을 검토하여 Google Play 데이터 보안 항목을 업데이트하시기 바랍니다.

#### 접근성

##### 비선형 폰트 200% 스케일링

- 최대 200% 스케일링과 저시력 사용자를 위해 WCAG(Web Content Accessibility Guidelines)에 맞게 정렬하는 추가적인 접근성 옵션을 지원합니다.
- 텍스트에 sp 단위를 사용하고 있다면 앱에 큰 영향은 없을 것으로 예상됩니다.
  그러나 앱이 사용성에 영향을 주지 않는지 200% 스케일링을 적용한 상태에서 UI 테스트가 필요합니다.

### Android 14를 타겟팅하는 앱에 적용되는 사항

#### 핵심 기능

##### 포그라운드 서비스의 타입이 요구됨

- 앱의 각 포그라운드 서비스에는 하나 이상의 포그라운드 서비스 타입이 명시되어야 합니다.
- 시스템은 특정 유즈케이스를 충족하기 위해 특정 포그라운드 서비스 타입을 기대합니다.
    - Android 14에서는 health, remote messaging, short services, special use cases, system exemptions
      같은 포그라운드 서비스 타입을 도입하였습니다.
- 앱의 유즈 케이스가 이러한 타입과 연관되지 않은 경우 로직을
  WorkManager 또는 user-initiated data transfer jobs로 이관하는 것을 강력히 추천합니다.

##### OpenJDK 17 업데이트

Android 14는 최신 OpenJDK LTS 릴리즈의 기능에 맞게 Android 코어 라이브러리를 수정하는 작업을 계속합니다.
이 개선에는 앱과 플랫폼 개발자를 위한 라이브러리 업데이트와 Java 17 언어 지원을 포함합니다.

몇 가지 변경사항은 앱 호환성에 영향을 미칠 수 있습니다.

- 정규식의 변화
    - OpenJDK의 의미(체계)를 더 밀접하게 따르도록 잘못된 그룹 참조(Invalid group reference)가 더 이상 허용되지 않습니다.
    - java.util.regex.Matcher 클래스에서 Illegal Argument Exception이 발생할 수 있으므로
      정규식을 사용하는 코드를 점검하시기 바랍니다.
    - 테스트 중 호환성 프레임워크 도구의 DISALLOW_INVALID_GROUP_REFERENCE 플래그를 사용하여 켜거나 끌 수 있습니다.
- UUID 처리
    - java.util.UUID.fromString() 메소드가 입력 인자 유효성을 더 엄격하게 검사합니다.
    - 역직렬화 시, IllegalArgumentException이 발생할 수 있습니다.
    - 호환성 프레임워크 도구의 ENABLE_STRICT_VALIDATION 플래그로 켜거나 끌 수 있습니다.
- 프로가드 이슈
    - java.lang.ClassValue 클래스를 추가하는 경우 프로가드를 통한 축소, 난독화, 최적화 과정에서 문제가 발생할 수 있습니다.
    - 이는 일부 코틀린 라이브러리에서 Class.forName("java.lang.ClassValue")가
      클래스를 반환하는지 여부에 따라 런타임 동작을 변경하는 문제 때문에 발생합니다.
    - java.lang.ClassValue 클래스를 사용할 수 없는 이전 버전의 런타임을 대상으로 앱을 개발한 경우
      최적화 과정에서 java.lang.ClassValue에서 파생된 클래스에서 computeValue 메서드가 제거될 수 있습니다.

#### 보안

##### 암시적 인텐트와 펜딩 인텐트의 제한 사항

- 암시적 인텐트는 exported 컴포넌트에만 전달됩니다. exported 되지 않은 컴포넌트에 인텐트를 전달하려면 명시적 인텐트를 사용해야 합니다.
- 변경할 수 있는(MUTABLE) 펜딩 인텐트를 컨포넌트 또는 패키지 명시 없이 생성하면 시스템이 예외를 발생시킵니다.
- 변경 사항으로 악성 앱이 앱의 내부 컴포넌트를 실행할 목적으로 생성한 암시적 인텐트를 가로챌 수 없도록 보호합니다.

```kotlin
val explicitIntent = Intent("com.example.action.APP_ACTION")
explicitIntent.apply {
    package = context.packageName
}
context.startActivity(explicitIntent)
```

##### 런타임에 등록된 브로드캐스트 리시버는 export 동작을 명시해야 합니다.

- 컨텍스트에 등록되는 리시버는 디바이스의 모든 앱에 내보낼지 여부를 플래그로 지정해야 합니다.
- Android 13 에서 추가된 기능(모든 앱이 동적으로 등록된 리시버로 보호되지 않은 리시버를 보낼 수 있는 문제 해결)을 강제하여
  보안 취약성으로부터 앱을 보호할 수 있습니다.

##### 시스템 브로드캐스트만 수신하는 리시버에 예외 발생

- 시스템 브로드캐스트만을 수신하는 리시버를 컨텍스트에 등록할 때, flag를 지정하면 얘외가 발생합니다.

##### 안전한 동적 코드 로딩

- DCL(Dynamic Code Loading)을 사용할 때, 동적으로 로드된 파일은 읽기 전용으로 표시되어야 합니다. 그렇지 않으면 시스템은 예외를 발생시킵니다.
- 코드 삽입, 변조로 인해 앱이 손상될 수 있는 위험이 크게 증가되므로 가능하다면 앱에서 동적으로 코드를 로드하지 않아야 합니다.
- 그럼에도 코드 동적 로드가 필요한 경우, 파일을 열자마자 읽기 전용으로 설정하고 내용을 작성해야 합니다.
- 동적으로 로드된 기존 파일에서 예외가 발생하지 않도록 하려면 파일을 삭제하고 다시 로드하는 것을 권장합니다.

##### 압축 파일 경로 순회 취약점

Android 14를 타겟팅하는 앱에 대해 Android가 압축 파일 경로 순회 취약점이 발생하지 않도록 차단합니다.
ZipFile(String)과 ZipInputStream.getNextEntry()는 압축 파일 이름에 ".."이 포함되거나 "/"로 시작되면 ZipException을 일으킵니다.

앱은 dalvik.system.ZipPathValidator.clearCallback()을 호출하여 유효성 검사를 해제할 수 있습니다.

##### 백그라운드에서 액티비티를 시작할 때, 추가 제한 사항 적용

- 펜딩 인텐트를 send 또는 유사 함수로 보낼 때, 펜딩 인텐트를 시작하여 백그라운드에서 액티비티를 시작하는 권한을 허용하려면 ActivityOptions 번들에
  setPendingIntentBackgroundActivityStartMode(MODE_BACKGROUND_ACTIVITY_START_ALLOWED)를 지정해야 합니다.
- 보이는 앱이 백그라운드에 있는 다른 앱의 서비스를 bindService()로 바인드하고 바인드된 서비스에서 백그라운드 활동 시작 권한을 부여하려는 경우,
  bindService()를 호출할 때 BIND_ALLOW_ACTIVITY_STARTS 플래그를 포함해야 합니다.
- 추가 제한 사항 적용으로 악성 앱이 API를 남용하여 파괴적인 액티비티를 백그라운드에서 시작하는 것을 막을 수 있습니다.

#### 비 SDK 제한 사항 업데이트

- Android 14에는 일부 제한된 비 SDK 인터페이스가 변경되었습니다.
- 일부 비 SDK 인터페이스를 사용할 수 있지만 이러한 메소드와 필드를 사용하면 항상 앱이 중단될 위험이 높아지므로 대체할 수 있는 SDK 인터페이스로 마이그레이션해야 합니다.
- 앱의 기능에 비 SDK 인터페이스 사용 외에 다른 방법을 찾을 수 없는 경우 새 공개 API를 요청해야 합니다.

### Schedule exact alarm 권한이 기본적으로 거부

#### SCHEDULE_EXACT_ALARM 개요

Exact alarm은 특정한 시간에 발생할 필요가 있는 사용자가 의도한 알림 또는 액션을 의미합니다.

SCHEDULE_EXACT_ALARM 권한은 Android 12에서 도입되었으며 사용자의 동의가 필요없는 미리 허가된 권한이었습니다.
그러나 이제부터 Android 13 이상을 타겟팅하는 대부분의 새롭게 설치되는 앱에서 기본적으로 권한이 거부됩니다.
백업 및 복구를 통해 Android 14를 실행하는 디바이스에 앱 데이터를 전송하는 경우에도 권한이 거부됩니다.
이 권한을 이미 취득한 앱의 경우에는 디바이스가 Android 14로 업데이트되어도 권한이 유지됩니다.

SCHEDULE_EXACT_ALARM 권한은 setExact(), setExactAndAllowWhileIdle(), setAlarmClock() 같은 API를 사용하여
정확한 알람을 설정하려는 경우 필요하며, 권한이 없는 경우에는 SecurityException이 발생합니다.
단 알람 매니저의 OnAlarmListener를 사용하여 정확한 알람을 설정하는 경우에는 권한이 필요하지 않습니다.

- canScheduleExactAlarms() 함수로 정확한 알람을 설정하기 전 권한을 확인하세요
- 알람 매니저의 ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED 포그라운드 브로드캐스트를 수신하여
  사용자가 권한 허가를 변경하는 경우에 대응할 수 있습니다.

#### 영향을 받는 앱

Android 14 이상을 실행하는 장치에서 다음 특성에 해당하는 새롭게 설치되는 앱이 영향을 받습니다.

- Android 13 이상을 타겟(API level 33)
- manifest에 SCHEDULE_EXACT_ALARM 권한 선언
- 여타 면제 또는 사전 허가 시나리오에 해당되지 않는 경우
- 캘린더 또는 알람 시계 앱이 아닌 경우

#### 캘린더와 알람 시계 앱은 USE_EXACT_ALARM 권한을 선언해야 합니다.

캘린더 또는 알람 시계 앱은 일정 알림, 기상 알람, 알림을 앱이 실행 중이지 않을 때 표시할 필요가 있습니다.
이러한 경우 USE_EXACT_ALARM 일반 권한을 사용할 수 있습니다.
설치 시 USE_EXACT_ALARM 권한이 허가되며 권한을 계속해서 유지합니다.
USE_EXACT_ALARM 권한을 사용하는 앱은 SCHEDULE_EXACT_ALARM 권한을 선언한 앱과 같이 정확한 알람을 설정할 수 있습니다.

2023년 7월 31일을 기하여 앱의 핵심 기능에 정확한 알람이 필요한 경우에만 권한을 선언해야 하며
기준을 충족하지 않는 경우 Google Play에 게시가 차단됩니다.

#### 정확한 알람이 필요하지 않을 수 있는 사례

이제부터는 사용자가 정확한 알람을 허가하는 단계가 추가되기 때문에
개발자는 앱의 사용 사례를 직접 평가하여 정확한 알람을 꼭 사용해야하는지 검토해야 합니다.

- 앱이 동작하는 동안 반복적인 작업을 수행해야 하는 경우
    - 알람 매니저의 set(), Handler의 postAtTime(), postDelayed() 메서드를 사용할 수 있습니다.
- 백그라운드 작업을 예약하는 경우
    - 앱 업데이트, 로그 업로드 같은 백그라운드 작업
    - WorkManager가 타이밍이 중요한 반복적인 작업을 예약하는 방법 제공합니다.
      반복 간격과 실행 유연 간격(flex interval, 최소 15분)을 설정하여 세분화된 런타임 정의가 가능합니다.
- 시스템이 idle 상태일 때, 대략적인 시간에 알람 필요
    - 정확하지 않은 알람 사용. AlarmManager의 setAndAllowWhileIdle() 사용 가능
- 특정 시간 이후에 실행되어야 하는 사용자 지정 작업
    - 정확하지 않은 알람 사용. AlarmManager의 set() 사용 가능
- 지정된 time window내에 발생(시작 시간으로 부터 일정 시간 이내에 시작되는 형태)할 수 있는 사용자 지정 작업
    - 정확하지 않은 알람 사용. AlarmManager의 setWindow() 사용 가능. 허용되는 최소 window 길이는 10분입니다.

#### 정확한 알람을 설정하는 방법

setExact(), setExactAndAllowWhileIdel(), setAlarmClock() 메서드로 정확한 알람을 설정할 수 있으며
후자일수록 더 시간이 중요하게 고려되는 작업을 수행하며 시스템 리소스를 더 많이 소모합니다.

앱에서 이러한 메서드로 정확한 알람을 설정할 경우 디바이스 리소스 특히 배터리 타임에 중대한 영향을 끼칠 수 있습니다.

- setExact()
    - 배터리 절약 옵션이 적용되지 않는 경우, 미래의 거의 정확한 시간에 알람이 호출됩니다.
    - 수행하려는 작업이 사용자 시간에 그다지 중요하지 않은 경우 사용할 수 있습니다.
- setExactAndAllowWhileIdle()
    - 배터리 절약 옵션이 적용되고 있더라도 미래의 거의 정확한 시간에 알람이 호출됩니다.
- setAlarmClock()
    - 미래의 정확한 시간에 알람을 호출합니다.
    - 사용자에게 잘 전달되어야 하기 때문에 시스템이 제공할 시간을 조정하지 않습니다.
    - 시스템은 이러한 알람을 가장 중요한 것으로 인식하며, 알람을 제공할 필요가 있을 때 저전력 모드에서 벗어날 수 있습니다.

#### 시스템 리소스 소비

시스템이 앱이 설정한 정확한 알람을 실행할 경우, 디바이스는 특히 절전 모드인 경우 배터리 수명 같은 많은 리소스를 소모하게 됩니다.
게다가 시스템이 리소스를 더 효율적으로 사용하도록 쉽게 알람 요청을 처리할 수 없습니다.

가능하다면 정확하지 않은 알람을 생성하는 것을 강력히 권장합니다.
오래 걸리는 작업을 수행할 때는 알람의 브로드캐스트 리시버에서 WorkManager나 JobScheduler를 사용하여 작업을 예약하세요.
장치가 잠자기(Doze) 모드인 동안 작업을 수행하려면 setAndAllowWhileIdle() 메서드로 정확하지 않은 알림을 생성하고 알람에서 작업을 시작하면 됩니다.

안드로이드에서는 정확한 알람을 중요하고 시간이 중요한 중단(interruptions)으로 취급합니다.
따라서 정확한 알람은 포그라운드 서비스 시작 제한에 영향을 받지 않습니다.

#### 적합한 알람 권한 선언

앱이 Android 12 이상을 타겟하고 있다면 알람 및 리마인더를 위한 특별한 앱 엑세스 권한을 얻어야 합니다. SCHEDULE_EXACT_ALARM 권한을 매니페스트 파일에
선언하면 됩니다.

```manifest
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM"/>
```

앱이 Android 13 이상을 타겟한다면 USE_EXACT_ALARM 권한을 얻을 수도 있습니다.

```manifest
<uses-permission android:name="android.permission.USE_EXACT_ALARM"/>
```

두 권한 모두 같은 기능을 하지만 권한 허가 과정과 사용 예에 있어 차이점이 존재합니다.

##### USE_EXACT_ALARM

- 자동으로 허가됨
- 사용자에 의해 거부될 수 없음
- 구글 플레이 정책에 따라야 함
- 주요 기능이 알람 및 리마인더 기능인 경우에만 사용 가능

##### SCHEDULE_EXACT_ALARM

- 사용자가 허가
- 다양한 사용 방식
- 관련 기능 사용 전 권한이 허가되었는지 확인해야 함

#### 계속해서 정확한 알람을 사용하기 위한 조치

최소한 앱이 정확한 알람을 설정하기 전에 권한이 있는지 확인하는 과정이 필요합니다.
앱에 권한이 없다면 인텐트를 통해 사용자에게 권한을 요청하도록 구현해야 합니다.
기존에 동의가 필요한 권한을 요청하는 과정과 별반 다르지 않습니다.

- 앱은 알람 매니저의 canScheduleExactAlarms()로 권한이 있는지 확인해야 합니다.
- 앱에 권한이 없는 경우 앱 패키지 이름과 함께 ACTION_REQUEST_SCHEDULE_EXACT_ALARM을 포함한 인텐트를 날려 사용자에게 권한을 허가받아야 합니다.
- 사용자가 권한을 허가하면 발생되는 알람 매니저의 ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED 브로드캐스트를 수신합니다.
- 사용자가 권한을 허가해야 앱이 정확한 알람을 설정할 수 있게 됩니다.
  권한을 거부한 경우 정확한 알람을 포함하는 기능을 사용하지 않아도 앱이 작동하도록 구현하여 적절하게 저하된 사용자 경험을 제공해야 합니다.

```kotlin
val alarmManager: AlarmManager = context.getSystemService<AlarmManager>()!!
when {
    // 권한이 허가된 상태라면 정확한 알람 설정
    alarmManager.canScheduleExactAlarms() -> {
        alarmManager.setExact(...)
    }
    else -> {
        // 환경 설정에서 정확한 알람 페이지로 이동시켜 사용자에게 권한 요청
        startActivity(Intent(ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
    }
}
```

onResume에서는 권한을 확인하고 사용자의 결정에 따라 처리할 수 있습니다.

```kotlin
override fun onResume() {
    if (alarmManager.canScheduleExactAlarms()) {
        // 정확한 알람 설정
        alarmManager.setExact(...)
    } else {
        // 아직 권한이 허가되지 않았음. 사용자에게 알림 보여주고 대체 로직 수행
        alarmManager.setWindow(...)
    }
}
```

##### 권한 거부에 따른 사용자 경험 저하

사용자가 권한을 거부할 수도 있습니다.
이런 경우에도 사용자 경험을 적정하게 저하시켜 사용자에게 가능한한 최고의 사용자 경험을 제공하는 것이 좋습니다.

#### 면제

다음 앱은 항상 setExact(), setExactAndAllowWhileIdle() 메소드를 사용할 수 있습니다.

- 플랫폼 인증서로 서명된 앱
- 이전에 권한을 허가받은 앱
- 전원 허용 목록(배터리 최적화 무시)에 있는 앱

#### 사전 허가

SYSTEM_WELLBEING 역할을 소유한 앱은 SCHEDULE_EXACT_ALARM 권한을 사전에 허가받습니다.

#### 테스트

이번 변경 사항을 테스트하려면 "설정 > 앱 > 특별 앱 액세스 > 알람과 리마인더"에서 권한을 조정하여 앱의 변화를 확인할 수 있습니다.

### 사진과 영상에 대한 부분적 접근 권한 부여

#### 사진과 영상 부분 접근 권한 개요

Android 14 디바이스부터 사용자는 앱이 Android 13에서 도입된 비주얼 미디어 권한(READ_MEDIA_IMAGES, READ_MEDIA_VIDEO)을 요청하면
사진과 영상(비주얼 미디어 라이브러리)에 대한 부분적인 접근 권한을 부여할 수 있습니다.

새로운 다이얼로그에는 다음 옵션이 표시됩니다.

- 사진과 영상 선택: Android 14에 새롭게 추가. 사용자가 앱에서 사용할 수 있는 특정 사진과 영상을 선택합니다.
- 모두 허용: 디바이스의 모든 사진과 영상에 라이브러리 접근 권한을 허가합니다.
- 거부: 모든 접근을 거부합니다.

사용자가 "사진과 영상 선택"을 고른 상태에서 앱에서 READ_MEDIA_IMAGES, READ_MEDIA_VIDEO 권한을 다시 요청하는 경우
시스템이 사용자에게 추가 사진 및 비디오에 대한 접근 권한을 부여할 수 있는 새로운 다이얼로그를 표시합니다.

앱에서 새로운 변경 사항을 지원하기 위해 READ_MEDIA_VISUAL_USER_SELECTED 권한이 새롭게 도입되었습니다.

앱에서 Android 11에서 도입된 사진 선택 도구를 사용하고 있는 경우에는 이 변경사항을 지원하기 위해 별다른 추가작업이 필요하지 않습니다.
사진 선택 도구를 사용하고 있지 않다면 이 변경사항에 대응하기 보다는 사진 선택 도구를 도입하는 것을 권장합니다.

#### 새로운 권한 선언에 따른 영향

READ_MEDIA_VISUAL_USER_SELECTED 권한을 선언하고 사용자가 "사진과 영상 선택"을 권한 다이얼로그에서 고른 경우 다음과 같은 일이 벌어집니다.

- READ_MEDIA_IMAGES, READ_MEDIA_VIDEO 권한은 거부됩니다.
- READ_MEDIA_VISUAL_USER_SELECTED 권한이 허가됩니다.
  사용자 사진과 영상에 대해 부분적, 임시적 권한이 허가됩니다.
- 앱에서 다른 사진과 영상에 접근이 필요하게 되면 READ_MEDIA_IMAGES 또는 READ_MEDIA_VIDEO 권한을 다시 요청해야 합니다.

READ_MEDIA_IMAGES 또는 READ_MEDIA_VIDEO 권한을 다시 요청할 때는 별도의 UI 요소를 추가하여
사용자가 요소를 눌렀을 때 다이얼로그가 표시되도록 하여 갑자기 시스템 다이얼로그 나타나 당황하는 상황이 발생하지 않도록 해야합니다.

READ_MEDIA_IMAGES 또는 READ_MEDIA_VIDEO 권한이 사용자의 사진과 영상 라이브러리에 접근하는데 필요한 유일한 권한입니다.
READ_MEDIA_VISUAL_USER_SELECTED 권한을 선언하면 권한 컨트롤러가 앱이 더 많은 사진과 영상을 선택하기 위해 수동으로 권한을 재요청하는 기능을 지원합니다.

사용자에게 여러 개의 시스템 런타임 다이얼로그 상자가 표시되지 않도록 하려면,
READ_MEDIA_VISUAL_USER_SELECTED, ACCESS_MEDIA_LOCATION, READ_MEDIA_IMAGES. READ_MEDIA_VIDEO 권한을
한 번에 요청하세요.

#### 새로운 권한을 선언하지 않았을 때의 영향

READ_MEDIA_VISUAL_USER_SELECTED 권한을 선언하지 않은 경우 다음과 같은 일이 발생합니다.

- READ_MEDIA_IMAGES, READ_MEDIA_VIDEO 권한이 앱 세션에 임시 권한으로 부여됩니다.
  사용자가 선택한 사진과 영상에 임시적으로 접근할 수 있습니다.
  앱이 백그라운드로 전환되거나 종료되면 시스템이 권한을 거부합니다.
- 나중에 추가적으로 사진과 영상에 접근하려면 READ_MEDIA_IMAGES, READ_MEDIA_VIDEO 권한을 다시 요청해야 합니다.
  권한 요청은 권한이 처음 요청되었을 때와 같은 흐름으로 진행되며 사용자에게 사진과 영상을 선택하는 화면이 표시됩니다.

앱은 URI 접근이 계속 가능하다고 가정해서는 절대 안됩니다.
권한이 변경되면 더이상 URI에 접근하지 못할 수 있으므로 기존에 보여지는 사진을 새로고침해야 합니다.

#### 업그레이드 시 사진과 영상 접근 권한은 유지됩니다.

Android 14 미만에서 업그레이드 되는 장치에서 시스템은 사용자의 사진과 영상 전체 접근 권한을 계속 유지합니다. 
정확한 동작은 업그레이드 전 허가된 권한에 따라 달라질 수 있습니다.

##### Android 13 권한

READ_MEDIA_IMAGES, READ_MEDIA_VIDEO 권한이 허가된 경우,
업그레이드된 앱은 사용자 사진 및 영상에 대한 전체 접근 권한을 유지합니다.
시스템이 자동으로 READ_MEDIA_IMAGES, READ_MEDIA_VIDEO 권한 허가를 유지합니다.

##### Android 12 이하 권한

READ_EXTERNAL_STORAGE 또는 WRITE_EXTERNAL_STORAGE 권한이 허가된 경우,
업그레이드된 앱은 사용자 사진 및 영상에 대한 전체 접근 권한을 유지합니다.
시스템이 자동으로 READ_MEDIA_IMAGES, READ_MEDIA_VIDEO 권한을 허가합니다.

#### 권장 사항(모범 사례)

##### 백그라운드 미디어 처리에는 새로운 권한이 필요합니다.

앱이 압축, 업로드 같이 백그라운드에서 미디어 처리를 수행하는 경우
READ_MEDIA_IMAGES, READ_MEDIA_VIDEO 권한이 다시 거부될 수 있다는 점을 염두에 두어야 합니다.
READ_MEDIA_VISUAL_USER_SELECTED 권한 지원을 추가하는 것을 강력하게 권장합니다.
그렇지 못한 경우에는 InputStream을 열거나 ContentResolver를 사용해 쿼리하여
해당 사진 또는 영상에 대해 접근 권한이 있는지 확인해야 합니다.

##### 권한 상태를 영구적으로 저장하지 마세요.

SharedPreferences 또는 DataStore 같은 곳에 권한 상태를 저장해 두지 마세요.
권한 상태는 권한 초기화, 앱 절전 모드, 설정에서 사용자가 변경, 앱의 백그라운드 전환 등으로 인해 변경될 수 있습니다.
따라서 저장된 상태는 실제 상태와 다를 수 있습니다.
ContextCompat의 checkSelfPermission()으로 권한을 확인하세요.

##### 사진과 영상에 계속 접근할 수 있다고 가정하지 마세요.

Android 14에서 도입된 이번 변경사항으로 미디어 라이브러리에 대해 부분적인 권한만 있을 수도 있습니다.
앱이 ContentResolver를 사용하여 쿼리할 때 캐시하고 있는 MediaStore 데이터를 이용하는 경우 캐시가 최신이 아닐 수 있습니다.

항상 ContentResolver를 사용하여 MediaStore를 쿼리하세요. 저장된 캐시를 사용하지 마세요.
앱이 포그라운드에 있는 동안 결과를 메모리에 유지하세요.

##### URI 접근은 임시적인 것으로 취급하세요.

사용자가 시스템 권한 다이얼로그에서 "사진과 영상 선택"을 고를 경우 선택된 사진과 영상에 대한 접근은 언제든지 차단될 수 있습니다.
앱은 URI에 접근할 수 없는 상황을 항상 염두에 두고 있어야 합니다.

### 찾기 쉬워진 데이터 보안 정보

#### 찾기 쉬워진 데이터 보안 정보 개요

사용자 개인정보 보호 강화를 위해 Android 14에서는 개발자가 Play Console에 입력한 정보를 더 다양한 곳에 표시합니다.
현재, Google Play의 각 앱 설명의 데이터 보안 항목에서 확인할 수 있습니다.
앱의 위치 데이터 공유 정책을 검토하여 Google Play 데이터 보안 항목을 업데이트하시기 바랍니다.

#### 권한을 허가해야 하는 이유 표시

몇몇 권한의 경우, 시스템 런타임 권한 다이얼로그에 데이터 공유 행위를 강조할 수 있는 클릭 가능한 부분이 추가됩니다.
해당 부분에서는 왜 앱에서 서드파티와 데이터를 공유하는지 또 앱의 데이터 접근을 제어할 수 있는 링크 같은 정보를 표시할 수 있습니다.

Android 14에서는 위치 권한에 한해 제 3자와 위치 데이터를 공유하는 경우에 데이터 공유 정보를 표시합니다.
다만, 다음 릴리즈에서는 확장 가능한 부분 대신 별도의 대화상자에 정보가 표시되도록 변경될 수 있습니다.

#### 시스템 알림

사용자가 앱을 통해 위치를 공유하고 위치 공유가 아래 사항에 해당된다면, 30일 간의 내역이 시스템 알림으로 표시됩니다.

- 제 3자와 위치 데이터를 공유
- 광고 관련 목적으로 위치 데이터를 공유

표시되는 알림을 선택하면 새롭게 추가된 데이터 공유 업데이트 페이지로 이동합니다.
해당 페이지에서는 최근 위치를 공유한 앱 목록이 표시되어 각 앱의 권한 설정을 쉽게 변경할 수 있도록 합니다.

새로운 위치 데이터 공유 업데이트 페이지는 알림을 통하지 않아도
Settings > Privacy or Settings > Security & Privacy 페이지로 항상 접근 가능합니다.
페이지에서는 최근 위치 데이터 공유 증가 여부를 확인할 수도 있습니다.

### 포그라운드 서비스의 타입 명시 필요

#### 포그라운드 서비스 타입 개요

개발자들이 보다 계획적으로 사용자와 마주하는 포그라운드 서비스를 정의할 수 있도록
Android 10에서 <service> 요소 내에 android:foregroundServiceType 속성이 추가된 바 있습니다.

Android 14를 타겟으로 하는 앱은 적합한 포그라운드 서비스 타입을 명시해야만 합니다.
이전 버전의 안드로이드에서처럼 여러 타입이 결합 가능합니다. 포그라운드 서비스 타입은 다음과 같습니다.

- camera
- connectedDevice
- dataSync
- health
- location
- mediaPlayback
- microphone
- phoneCall
- remoteMessaging
- shortService
- specialUse
- systemExempted

서비스 타입 중 앱에 적합한 타입이 없는 경우,
WorkManager 또는 사용자 시작 데이터 전송 작업(user-initiated data transfer jobs)으로 로직을 옮기는 것을 권장합니다.

Android 14에서 health, remoteMessaging, shortService, specialUse, systemExempted 타입이 새롭게 추가되었습니다.

Android 14를 타겟팅하는 앱이 manifest에 서비스의 타입을 정의하지 않으면
시스템이 startForeground() 호출 시 MissingForegroundServiceTypeException을 발생시킵니다.

#### 포그라운드 서비스 타입 사용을 위해 새로운 권한 선언

Android 14에서 포그라운드 서비스를 사용하려면 포그라운드 서비스 타입에 맞는 특정 권한을 반드시 선언해야 합니다.

모든 권한은 일반 권한으로 정의되며 기본적으로 허가되어 사용자가 권한을 거부할 수 없습니다.

##### 런타임에 포그라운드 서비스 타입 포함하기

포그라운드 서비스를 시작할 때는 포그라운드 서비스 타입을 비트 형태의 정수로 전달 받는 오버로드된 버전의 startForeground()를 사용하세요.
메서드에 하나 이상의 타입을 전달할 수 있습니다.

일반적으로 앱에서 사용하는 특정 사용 사례에 맞는 타입만 선언해야 합니다.
그래야 각 포그라운드 서비스 타입에 대해 시스템이 요구하는 부분을 쉽게 충족시킬 수 있습니다.
포그라운드 서비스가 여러 타입으로 시작되는 경우, 해당 포그라운드 서비스는 선언한 모든 타입에 대해 플랫폼에서 요구하는 사항을 준수해야 합니다.

그러나 camera, location, microphone 타입을 사용하는 포그라운드 서비스를 시작할 경우,
startForeground를 호출할 때마다 해당 타입을 포함하여 실행해야 합니다.

``` kotlin
    Service.startForeground(0, notification, FOREGROUND_SERVICE_TYPE_LOCATION)
```

startForeground에 타입을 전달하지 않으면 manifest 파일에 정의된 값을 기본값으로 사용합니다.

#### 시스템의 런타임 확인

시스템은 포그라운드 서비스 타입이 적합하게 사용되는지 확인하고
앱이 적절한 런타임 권한을 요청했는지 또는 필요한 API를 사용하는지 확인합니다.
그 예로 시스템은 FOREGROUND_SERVICE_TYPE_LOCATION 타입을 사용하는 서비스가 있는 앱이
ACCESS_COARSE_LOCATION 또는 ACCESS_FINE_LOCATION 권한을 요청할 것으로 예상합니다.

이는 앱이 사용자에게 권한을 요청하고 포그라운드 서비스를 시작할 때 일련의 동작 순서를 따라야 함을 의미합니다.
권한은 반드시 startForeground 호출 전 요청되어 허가되어야 합니다.
포그라운드 서비스가 시작된 후에 필요한 권한을 요청하는 앱은 포그라운드 서비스 시작 전에 권한을 요청하도록 변경해야 합니다.

앱이 포그라운드 서비스 시작 전 모든 런타임 요구사항을 만족하지 않는 경우
startForeground를 호출 후 해당 서비스에서 SecurityException이 발생합니다.
다시말해 포그라운드 서비스가 시작되지 않도록 막습니다.
이는 실행 중인 포그라운드 서비스가 포그라운드 프로세스 상태에서 제거될 수 있으며
앱에 크래시가 발생할 수 있다는 것을 의미합니다.

#### 각 포그라운드 서비스 타입별 사용 사례 및 강제 사항

포그라운드 서비스 타입을 사용하기 위해 매니페스트 파일에 특정 권한을 선언해야 합니다.
앱은 해당 타입의 사용 사례를 충족해야 하고 모든 런타임 요구사항을 만족해야 합니다.

각 타입에 대해 의도된 사용 사례와 반드시 선언해야 하는 권한에 대한 설명은
[해당 페이지](https://developer.android.com/about/versions/14/changes/fgs-types-required#use-cases)를
참고하세요.

#### 포그라운드 서비스 타입 사용에 대한 구글 플레이 정책 강제 사항

Android 14 출시에 발 맞추어,
Google Play는 포그라운드 서비스 타입이 시스템이 기대하는 바와 일치하지 않을 때에
앱이 포그라운드 서비스 타입을 선언하는 시기와 방법을 제한하는 정책에 대한 자세한 내용을 준비하고 있습니다.

### 포그라운드 서비스를 사용자 시작 데이터 전송 작업으로 변경

#### 사용자 시작 데이터 전송 작업(user-initiated data transfer jobs) 개요

Android 14에서는 앱의 포그라운드 서비스 사용에 엄격한 규칙(타입 명시 및 그에 따른 요구사항 강제)을 적용합니다.

따라서 Android 14에서는 작업이 사용자 시작 데이터 전송 작업임을 지정하는 새로운 API를 도입하였습니다.
이 API는 원격 서버에서 파일을 다운로드하는 것과 같이 장기간의 사용자 시작 데이터 전송이 필요한 사례에 적합합니다.
이러한 유형의 작업에 해당한다면 포그라운드 서비스 대신 사용자 시작 데이터 전송 작업을 사용해야 합니다.

이름에서 알 수 있듯이, 사용자 시작 데이터 전송 작업은 사용자가 시작합니다.
작업은 즉시 시작되며 알림이 표시되고 시스템 상태에서 허용하는 한 오랜 시간 동안 실행될 수 있습니다.
또 여러 개의 사용자 시작 데이터 전송 작업을 병행해서 실행할 수도 있습니다.

작업은 어플리케이션이 몇가지 허용되는 상태를 제외하면 사용자에게 보여지고 있는 중에만 시작될 수 있습니다.
모든 제약 조건을 만족하면 시스템 상태 제한(system health restrictions)에 따라 운영체제에서 사용자가 시작한 작업을 실행할 수 있게 됩니다.
시스템은 제공되는 예상 페이로드 크기를 이용하여 작업 실행 시간을 결정할 수 있습니다.

단, 현재 WorkManager를 이용하여 네트워크 데이터 전송을 수행하고 있다면
사용자 시작 데이터 전송 작업을 사용하는 대신
계속해서 WorkManager 라이브러리를 사용할 것을 권장합니다.

#### 사용자 시작 데이터 전송 작업을 위한 권한

사용자 시작 데이터 전송 작업을 실행하려면 새로운 권한인 RUN_USER_INITIATED_JOBS가 필요합니다.
시스템은 자동으로 이 권한을 허가합니다.
앱 메니페스트에 권한을 선언하지 않은 경우 시스템이 SecurityException을 발생시킵니다.

#### 사용자 시작 데이터 전송 작업 실행 과정

1. 매니페스트 파일에 RUN_USER_INITIATED_JOBS 권한을 선언합니다.

```manifest
<uses-permission android:name="android.permission.RUN_USER_INITIATED_JOBS" />
```

2. 새롭게 추가된 setUserInitiated()와 setDataTransfer() 메소드를 호출하여 JobInfo 객체를 생성합니다.
   작업을 생성할 때에는 setEstimatedNetworkBytes() 메소드를 호출하여 페이로드 크기 예상치를 제공하는 것이 좋습니다.

```kotlin
val networkRequestBuilder = NetworkRequest.Builder()
    .addCapability(NET_CAPABILITY_INTERNET)
    .addCapability(NET_CAPABILITY_VALIDATED)

val jobInfo = JobInfo.Builder()
    // ...
    .setUserInitiated(true)
    .setDataTransfer(true)
    .setRequiredNetwork(networkRequestBuilder.build())
    .setEstimatedNetworkBytes(1024 * 1024 * 1024)
    // ...
    .build()
```

3. 앱이 표시되는 중이거나 허용되는 조건에 있을 때, 작업을 예약합니다.

```kotlin
val jobScheduler: JobScheduler =
    context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
jobScheduler.schedule(jobInfo)
```

4. 작업이 실행 중일 때 JobService 객체에서 setNotification() 호출을 잊어서는 안됩니다.
   이 값은 작업 관리자와 상태 표시줄 알림 영역을 통해 사용자에게 작업이 실행 중임을 알리는데 사용됩니다.
   짧은 시간 내에 setNotification()을 호출하지 않으면 앱에 ANR이 발생합니다.

```kotlin
val notification = Notification.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
    .setContentTitle("My user-initiated data transfer job")
    .setSmallIcon(android.R.mipmap.myicon)
    .setContentText("Job is running")
    .build()

class CustomJobService : JobService() {
    override fun onStartJob(params: JobParameters?): Boolean {
        setNotification(
            params, notification.id, notification,
            JobService.JOB_END_NOTIFICATION_POLICY_DETACH
        )
        // Do the job execution.
    }
}
```

5. 주기적으로 알림을 업데이트하여 사용자가 작업 진행 상태와 진행율을 알 수 있도록 해야 합니다.
   작업을 실행하는 시점에 전송할 크기를 알 수 없는 경우에는
   새로운 API에서 제공하는 updateEstimatedNetworkBytes()를 사용하여 전송 크기를 알 수 있는 시점에 업데이트할 수 있습니다.
6. 실행이 완료되면 jobFinished()를 호출하여 작업이 끝났거나 다시 작업을 수행해야 함을 시스템에 알리면 됩니다.

#### 사용자 시작 데이터 전송 작업의 중단 가능성

사용자와 시스템은 사용자 시작 전송 작업을 취소할 수 있습니다.

##### 사용자가 작업 관리자를 통해 중단

사용자는 작업 관리자에 표시되는 사용자 시작 데이터 전송 작업을 취소할 수 있습니다.
사용자가 정지 버튼을 누르면 시스템은 다음 사항에 따라 작업을 취소합니다.

- 앱 프로세스를 즉시 중단합니다. 실행 중인 모든 작업과 포그라운드 서비스가 함께 종료됩니다.
- 실행 중인 작업에 대해 onStopJob()을 호출하지 않습니다.
- 사용자에게 표시되는 작업을 다시 예약하지 않도록 방지합니다.

이러한 이유 때문에 작업을 나타내는 알림에 작업을 종료하거나 재시작할 수 있는 방법(action)을 제공하는 것이 좋습니다.

특정한 상황(시스템 수준 앱, 보안 앱, 데모 모드인 장치)에서는 작업 관리자에 작업 종료 버튼이 표시되지 않거나 작업 자체가 표시되지 않을 수 있습니다.

##### 시스템에 의한 중단

일반적인 작업과 다르게 사용자 시작 데이터 전송 작업은 앱 대기 버킷 제한의 영향을 받지 않습니다.  
(앱 대기 버킷:  앱이 얼마나 최근에 얼마나 자주 사용되었는지에 기반해 시스템이 앱의 리소스 요청에 우선순위 정하는 것)  
그렇지만 시스템은 다음과 같은 상황에서 작업을 중단할 수도 있습니다.

- 개발자 정의 조건을 충족하지 않는 경우
- 데이터 전송 작업을 처리하기에 시스템이 필요하다고 생각되는 시간 보다 오래 실행되는 경우
- 온도 상승 등으로 시스템 관리를 위해 작업을 중단할 필요가 있을 때
- 장치의 메모리 부족으로 앱 프로세스가 종료된 경우

메모리로 인한 종료를 제외하고 작업이 시스템에 의해 종료될 때 시스템은 onStopJob()을 호출합니다.
시스템은 최적이라 판단되는 상황에 작업을 다시 재시도합니다.
onStopJob()이 호출되지 않더라도 데이터 전송 상태를 유지하여 onStartJob()이 다시 호출되었을 때 상태를 복구할 수 있는지 확인하세요.

#### 사용자 시작 데이터 전송 작업 예약 시 허용되는 조건

앱은 보이는 중이거나 특정 조건을 만족하는 경우에만 사용자 시작 데이터 전송 작업을 시작할 수 있습니다.
언제 사용자 시작 데이터 전송 작업을 예약할 수 있는지 판단하기 위해
시스템은 앱이 백그라운드에서 액티비티를 시작할 수 있도록 하는 조건과 동일한 조건 목록을 이용합니다.
특히 이 조건 목록은 백그라운드에서 시작되는 포그라운드 서비스 제한에 적용되는 면제 사항과 동일하지 않습니다.

해당되는 몇 가지 예외 사항은 다음과 같습니다.

- 앱이 백그라운드에서 앱을 시작할 수 있다면 사용자 시작 데이터 전송 작업을 백그라운드에서 시작할 수 있습니다.
- 최근 실행 스크린에 표시되는 기존 작업의 백 스택에 앱의 액티비티가 있더라도
  그것만으로는 사용자 시작 데이터 전송 작업 실행이 불가능합니다.

작업이 허용되는 조건에 해당하지 않는 경우에 예약되면 작업을 실패하고 RESULT_FAILURE 에러 코드를 반환합니다.

#### 사용자 시작 데이터 전송 작업 제약 조건

Android는 각 작업의 종류마다 제약 조건을 두어 작업이 최적의 지점에서 수행될 수 있도록 지원합니다.
제약 조건은 Android 13에서 도입되었으며 Android 14에서는 사용자 시작 데이터 전송 작업에 적용할 수 있습니다.

사용자 시작 데이터 전송 작업에 허용되는 제약 조건은 다음과 같습니다.

- setBackoffCriteria(JobInfo.BACKOFF_POLICY_EXPONENTIAL)
- setClipData()
- setEstimatedNetworkBytes()
- setMinimumNetworkChunkBytes()
- setPersisted()
- setNamespace()
- setRequiredNetwork()
- setRequiredNetworkType()
- setRequiresBatteryNotLow()
- setRequiresCharging()
- setRequiresStorageNotLow()

#### 테스트

- 작업 ID를 확인하려면 작업이 만들어질 때에 정의된 값을 가져오면 됩니다.
- 작업을 즉시 실행하거나 중단된 작업을 다시 시도하도록 하려면 터미널에서 다음 명령을 입력하세요

```shell
adb shell cmd jobscheduler run -f <package_name> <job_id>
```

- 시스템이 작업을 강제로 중단시키는 상황을 시뮬레이션하려면 터미널에서 다음 명령을 입력하세요

```shell
adb shell cmd jobscheduler timeout <package_name> <job_id>
```

## 새로운 기능

### 기능 및 API 개요

Android 14에서는 개발자를 위한 멋진 새로운 기능과 API를 도입하였습니다.
새롭게 추가, 수정, 삭제된 API는 API 차이 보고서
([API difference report](https://developer.android.com/sdk/api_diff/u-dp1/changes))
에서 상세 목록 확인이 가능합니다.

### 국제화

#### 앱별 언어 기본 설정

Android 13에서 시작된 앱별 언어 기능이 확장되었습니다.

- 앱의 localConfig를 자동으로 생성합니다.
    - Android Studio Giraffe Canary 7과 AGP 8.1.0-alpha07 이상에서는 앱별 언어 기본 설정을 자동으로 지원하도록 앱을 구성할 수
      있습니다.
    - 프로젝트 리소스를 기반으로 Android Gradle 플러그인이 LocalConfig 파일을 생성하고 최종 매니페스트 파일에 해당 파일에 대한 참조를 추가하여 수동으로
      파일을 생성하거나 업데이트할 필요가
      없어집니다.
    - AGP는 앱 모듈의 res 폴더에 있는 리소스와 라이브러리 모듈의 디펜던시를 사용하여 LocaleConfig 파일에 포함될 로케일을 결정합니다.
- 앱의 localConfig를 동적으로 변경할 수 있습니다.
    - LocaleManager의 setOverrideLocaleConfig 또는 getOverrideLocaleConfig를 이용하여 기기 시스템 설정에서 표시되는 앱의
      지원되는 언어 목록을 동적으로 변경할 수
      있습니다.
    - 지역별로 지원되는 언어 목록을 변경하거나, A/B 테스트 수행, 현지화를 위해 서버 측 푸시를 사용하여 업데이트된 로케일 목록을 제공받는 경우 유용하게 사용할 수 있을
      것입니다.
- 입력기(Input Method Editor)가 앱 언어를 파악할 수 있습니다.
    - IME는 getApplicationLocales 메소드로 현재 앱의 언어를 확인하여 입력 언어를 일치시킬 수 있습니다.

#### 굴절 문법에 대한 API

굴절 문법 API는 전달되는 사람에 따라 문법적으로 성별이 바뀌는 언어를 쉽게 지원할 수 있도록 합니다.
이를 통해 개인화되고 자연스러운 사용자 경험을 제공할 수 있습니다.

- 문법적 성별에 대한 굴절 예시
    - 일부 언어에서는 문법적으로 성별을 구분하며 영어처럼 쉽게 해결할 수 없는 경우가 있습니다.
    - 예를 들어 프랑스어에서는 같은 표현에 대해 남성, 여성, 중성적 표현을 선택할 수 있습니다.
    - 남성, 여성 표현은 사용자를 직접적으로 표현합니다. 그러나 이러한 프랑스어의 문법적 특징을 수용할 수 있는 매커니즘이 없는 경우 중성적 표현만 제공할 수 있습니다.
      중성적 표현은 메시지의 어조를 변경하고 사용자 인터페이스에 표시하려는 것과 동떨어진 것일 수 있습니다.
    - 이러한 경우에 굴절 문법 API는 사용자의 문법적 성별에 대해 알맞은 문자열을 표시할 수 있도록 합니다.
    - 앱에서 사용자에 맞춘 번역을 제공하기 위해서는 각 문법적 성별에 따라 변형된 번역을 추가하고
      GrammaticalInflectionManager API를 사용하여 사용자에 표시되는 번역을 조절해야 합니다.
    - Android Studio Giraffe Canary 7 이상부터 성별에 따른 리소스 구분을 인식하고 지원합니다.
    - 이전에 이와 유사한 지원을 추가하려면 ICU의 SelectFormat API를 사용해야 했으며 문자열 별로 적용되어야 했습니다.
- 문법적 성별이 있는 언어에 대한 번역 추가
    - 성별이 있는 언어에 대한 현지화된 텍스트 제공을 위해서는 로케일 이름 바로 뒤에 성별 구분자를 추가하여 리소스 파일을 만들면 됩니다.
    - 예를 들어 프랑스어에서 남성, 여성, 중성에 대한 번역을 제공하려면 res/values-fr-feminine/strings.xml,
      res/values-fr-masculine/strings.xml, res/values-fr-neuter/strings.xml를 생성하면 됩니다.
    - 기존 언어 설정에서와 같이 문법 성별 버전을 사용할 수 없는 경우 기본 리소스 파일에 있는 텍스트가 표시됩니다.
      따라서 의도한 경우 중성 표현은 res/values-fr/strings.xml 파일에 작성되어도 됩니다.

#### 지역 기본 설정(preference)

지역 설정은 사용자에 맞는 온도 단위, 주의 시작 요일, 숫자 시스템을 개인화할 수 있도록 합니다.
미국에 있더라도 온도를 화씨 단위가 아닌 섭씨로 확인하거나 일요일이 주의 시작이 아니라 월요일이 주의 시작이 되도록 설정하고 싶을 수도 있습니다.

이제 안드로이드 설정 메뉴에서 사용자에게 지역 기본 설정을 변경할 수 있는 메뉴가 제공됩니다.
이러한 설정은 백업과 복구 과정을 통해 유지됩니다.
getTemperatureUnit, getFirstDayOfWeek 같은 API와 인텐트를 통해 앱에서 이러한 사용자 설정을 읽어와 정보를 어떻게 표시할지 결정할 수 있습니다.
또한 ACTION_LOCALE_CHANGED 브로드캐스트 리시버를 등록하여 지역 기본 설정 변경에 대응할 수도 있습니다.

지역 기본 설정은 설정 앱의 시스템 > 언어 및 입력 > 지역 기본 설정에서 가능하며 온도, 주의 시작 요일, 숫자 시스템을 설정할 수 있습니다.

### 접근성

#### 폰트를 비선형으로 200%까지 확대

Android 14 부터는 웹 컨텐츠 접근성 가이드라인(WCAG)을 준수하는 추가 접근성 기능을 포함하는 저시력자를 사용자를 위한 최대 200% 폰트 확대를 지원합니다.

큰 텍스트 요소가 과도하게 커지는 것을 방지하기 위해 시스템은 비선형 배율로 텍스트를 확대합니다.
이러한 확대 전략으로 큰 텍스트는 작은 텍스트와 동일한 비율로 커지지 않습니다.
따라서 과도하게 커진 텍스트로 인해 텍스트가 잘리거나 더 읽기 어려워지는 현상을 예방할 수 있습니다.

##### 비선형 폰트 확대 테스트

기존에 sp 단위를 텍스트에 사용하고 있다면 앱의 텍스트에 개선 사항이 자동으로 적용됩니다.
앱의 폰트 사이즈가 알맞게 조절되고 큰 텍스트로 인해 사용성을 해치지 않는지 확인하기 위해 폰트 사이즈를 200%로 설정 후 UI 테스트를 진행하는 것을 권장합니다.

##### 텍스트 사이즈 지정 시 sp 단위 사용

텍스트 사이즈는 항상 sp 단위로 설정해야 합니다.
앱이 sp 단위를 사용하면, 안드로이드가 사용자가 선호하는 텍스트 크기와 비율을 적합하게 적용할 수 있습니다.

padding이나 view의 높이를 지정할 때는 sp 단위를 사용하지 마세요.
폰트 확대는 비선형하게 이루어질 수 있으므로 비율이 일치하지 않을 수 있습니다.
예를 들어 4sp + 20sp는 24sp와 동일하지 않을 수 있습니다.

##### sp 단위로 변경하기

TypedValue.applyDimension()을 이용하여 sp 에서 px로 변경할 수 있으며
TypedValue.deriveDimension()을 이용하여 px을 sp로 변경할 수 있습니다.
두 메서드는 비선형 확대 비율을 자동으로 적용하여 값을 계산합니다.

Configuration.fontScale이나 DisplayMetrics.scaledDensity를 이용하여 하드코딩된 식을 사용해서는 안됩니다.
폰트 확대는 이제 비선형적이므로 이렇게 계산한 값은 정확하지 않습니다.

### 사용자 경험

#### 앱 스토어 개선

몇몇 새로운 PackageInstaller API 제공으로 앱 스토어가 사용자 경험을 개선할 수 있도록 하였습니다.

##### 다운로드 전 설치 허가 요청

앱 설치와 업데이트에서 사용자 동의가 필요한 경우가 있습니다.
REQUEST_INSTALL_PACKAGES 권한을 사용하는 설치 프로그램이 새 앱을 설치하려고 할 경우,
이전 버전에서는 앱 스토어는 APK가 설치 세션에 쓰여지고 세션이 커밋된 뒤에야 사용자 동의를 받을 수 있었습니다.

Android 14부터는 requestUserPreapproval() 메서드로 설치 프로그램이 설치 세션을 커밋하기 전에 사용자 동의를 요청할 수 있습니다.
이러한 개선으로 앱 스토어는 사용자가 설치에 동의하기 전까지 APK 다운로드를 미룰 수 있습니다.
게다가 사용자가 한 번 설치에 동의하면, 앱 스토어는 사용자를 방해하지 않고 백그라운드에서 앱을 다운로드하고 설치할 수 있습니다.

##### 향후 업데이트를 위한 책임 요구

새로운 setRequestUpdateOwnerShip() 메서드는 설치 프로그램이 설치하는 앱의 향후 업데이트에 대한 책임을 질 예정임을 시스템에 나타낼 수 있습니다.
이 기능은 업데이트 소유권을 적용하여 업데이트 권한을 가진 설치 프로그램만 자동 업데이트를 설치할 수 있습니다.
업데이트 소유권 적용을 통해 사용자가 원하는 앱 스토어에서만 업데이트를 받도록 보장할 수 있습니다.
INSTALL_PACKAGES 권한을 사용하는 설치 프로그램을 포함하여 다른 모든 설치 프로그램은 업데이트 설치를 위해 사용자 승인이 필요하게 됩니다.
사용자가 다른 출처에서 업데이트를 계속 진행하는데 동의하면 업데이트 소유권이 해제됩니다.

##### 방해가 덜한 시간에 앱 업데이트

앱 스토어는 앱 업데이트로 인해 사용자가 실행 중이던 앱이 꺼지거나 사용자를 방해하지 않기 위해 사용자가 활발하게 사용하는 시간에 앱 업데이트를 피하고 싶을 수 있습니다.

InstallConstraints API는 설치 프로그램이 적절한 순간에 앱 업데이트가 이루어지도록 하는 방법을 제공합니다.
앱 스토어는 commitSessionAfterInstallConstraintsAreMet() 메서드를 호출하여
사용자가 해당 앱을 더이상 사용하고 있지 않을 때, 업데이트가 커밋되도록 보장할 수 있습니다.

##### 선택적 분할 apk를 원활하게 설치

분할 apk를 사용하여 앱의 기능을 별도의 apk 파일로 나누어 제공할 수 있습니다.
분할 apk를 통해 앱 스토어에서 다양한 앱 구성요소를 제공하는 작업을 최적화할 수 있습니다.
예를 들어 앱 스토어가 대상 디바이스의 속성을 기반으로 최적화할 수 있습니다.
PackageInstaller API는 API level 22이상에서 분할 apk를 사용할 수 있습니다.

Android 14에서는 setDontKillApp() 메서드를 통해 새로운 분할된 apk가 설치될 때,
기존 앱의 실행 중인 프로세스가 종료되지 않도록 해야 함을 설치 프로그램에 나타낼 수 있습니다.
앱 스토어는 이 기능을 이용하여 사용자가 앱을 사용 중인 경우에 앱의 새로운 기능을 원활하게 설치할 수 있습니다.

#### 사용자가 스크린샷을 찍을 때 감지

스크린샷 감지에 대한 보다 일관적인 경험을 제공하기 위해 Android 14에서는 개인 정보를 보호하는 스크린샷 감지 API를 도입하였습니다.
API로 앱은 액티비티 단위로 콜백을 등록할 수 있습니다.
액티비티가 보이는 상태에서 스크린샷을 찍으면 해당 콜백이 호출되며 사용자에게 알려집니다.
단 콜백에 촬영한 스크린샷 이미지가 제공되는 것이 아니라 사용자가 스크린샷을 찍을 때 화면에 나타날 내용을 앱이 결정할 수 있습니다.

#### 뒤로 탐색 예측 애니메이션 지원 추가

앱을 새로운 시스템 back API로 마이그레이션 하였다면
뒤로 탐색 예측에 자동적으로 in-app 애니메이션이 적용되며 커스텀 전환도 지원할 수 있습니다.

##### 내장 in-app 애니메이션 지원 추가

시스템 back API를 적용하면 앱에서 홈으로 뒤로가기, 액티비티간 전환, 작업간 전환 시 애니메이션이 표시됩니다.

머터리얼 컴포넌트 디펜던시(MDC-Android)를 1.10.0 버전 이상을 적용하면
바텀 시트, 사이드 시트, 서치 바 같은 머터리얼 컴포넌트에서 애니메이션을 지원합니다.

##### 커스텀 in-app 전환 및 애니메이션

Android 14에서는 커스텀 인앱 전환과 애니메이션을 적용할 수 있습니다.

뒤로 탐색 예측 API를 이용하여 맞춤형 인앱 전환과 애니메이션을 개발할 수 있습니다.

AndroidX의 Activity 1.8.0-alpha01 이상부터 앱의 뒤로 탐색 예측 동작에 맞춤 전환을 추가할 수 있습니다.
OnBackPressedCallback 내에 handleOnBackProgressed, handleOnBackCancelled, handleOnBackStarted
메소드를 도입하여 사용자가 뒤로 스와이프하는 동안 애니매이션을 보여줄 수 있습니다.
이 메소드로 시스템 기본 애니메이션 또는 머터리얼 컴포넌트 애니메이션을 맞춤 설정하면 됩니다.

대부분의 앱이 이전 버전과 호환되는 AndroidX API를 사용하겠지만
Android 14 Developer Preview 1 이상에서만 지원하는
플랫폼 API의 OnBackAnimationCallback 같은 비슷한 항목을 지원합니다.

##### 커스텀 액티비티 전환 추가(Android 14 이상)

Android 14 이상에서 맞춤 액티비티 전환이 뒤로 탐색 예측을 지원하도록 하려면
overridePendingTransition 대신 overrideActivityTransition을 사용할 수 있습니다.
사용자가 스와이프하면 전환 애니메이션이 재생됩니다.

작동 과정을 설명하기 위해, 액티비티 B가 표시되고 있고 액티비티 A가 백 스택에 있는 상황을 생각해보세요.

- 액티비티 B의 onCreate 메소드에서 열기 또는 닫기 전환을 호출합니다.
- 사용자가 액티비티 B로 이동할 때 OVERRIDE_TRANSITION_OPEN이 사용되며,
  액티비티 A로 다시 돌아올 때 OVERRIDE_TRANSITION_CLOSE가 사용됩니다.
- OVERRIDE_TRANSITION_CLOSE가 사용될 때 enterAnim은 액티비티 A의 시작 애니메이션이며
  exitAnim은 액티비티 B의 종료 애니메이션입니다.
- exitAnim이 설정되지 않았거나 0으로 설정된 경우, 기본 '액티비티가 전환' 뒤로 탐색 예측 애니메이션이 표시됩니다.