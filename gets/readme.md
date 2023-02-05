# GETs(겟츠) 프로젝트 코드 리뷰

## AndroidManifest 파일

- 권한, 구성요소 등이 모두 표시되므로 프로젝트 전체를 판단 가능
- decompile할 경우 gradle 파일은 표시되지 않음
- 추가적으로 패키지 경로를 통해 사용한 라이브러리 파악 가능
- usesClearTextTraffic 옵션(http 통신 허용)은 사용하지 않는 것을 권장
- 가독성을 위해 시스템 흐름대로 activity 순서 정렬 권장
- 권한, 구성요소 등이 모두 표시되므로 프로젝트 전체를 판단 가능
- decompile할 경우 gradle 파일은 표시되지 않음
- 추가적으로 패키지 경로를 통해 사용한 라이브러리 파악 가능
- usesClearTextTraffic 옵션(http 통신 허용)은 사용하지 않는 것을 권장
- 가독성을 위해 시스템 흐름대로 activity 순서 정렬 권장

## 리소스 폴더

### mipmap과 drawable의 차이

#### mipmap

- 다양한 런처 아이콘 밀도에 관한 드로어블 파일
- 일부 앱 런처는 기기의 밀도 버킷에서 요구하는 것보다 최대 25% 더 크게 앱 아이콘을 표시합니다. 앱 아이콘이 이렇게 확장될 수 있기 때문에 모든 앱 아이콘을 drawable 디렉터리가 아닌 mipmap
  디렉터리에 넣어야 합니다. 밀도별 APK를 빌드하더라도 drawable 디렉터리와 달리, 모든 mipmap 디렉터리는 APK에 유지됩니다. 이렇게 하면 런처 앱이 최적 해상도의 아이콘을 선택하여 홈 화면에 표시할
  수 있습니다.
- 적응형 아이콘: Android 8.0(API 수준 26)에는 다양한 기기 모델에서 여러 가지 형태로 표시되는 적응형 런처 아이콘이 도입되었습니다. 예를 들어 적응형 런처 아이콘은 한 OEM 기기에서는 원형으로
  표시되고 다른 기기에서는 모서리가 둥근 사각형으로 표시될 수 있습니다. 각 기기 OEM에서는 마스크가 제공되는데, 시스템에서는 이를 사용하여 모든 적응형 아이콘을 같은 형태로 렌더링합니다. 적응형 런처 아이콘은
  바로가기, 설정 앱, 공유 대화상자 및 개요 화면에서도 사용됩니다.
  백그라운드와 포그라운드로 구성된 2개의 레이어를 정의하여 적응형 런처 아이콘의 형태를 제어할 수 있습니다. 아이콘 윤곽선에 마스크나 백그라운드 그림자를 사용하지 않고 아이콘 레이어를 드로어블로 제공해야 합니다.

### drawable

- 다음 드로어블 리소스 하위 유형으로 컴파일되는 비트맵 파일(.png, .9.png, .jpg, .gif) 또는 XML 파일

### color 폴더와 values/color.xml

- color 폴더는 selector를 이용하여 색상 상태 목록 리소스를 관리(눌렀을 때, 포커스가 있을 때 등 다른 색상 적용)
- color.xml은 색상 값 관리

### drawable을 shape로 만들기

- [Shape Example](../assignment/Shape)
- 색상과 그라데이션을 포함하여 기하학적 도형을 정의하는 XML 파일. GradientDrawable을 만듬

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape
        xmlns:android="http://schemas.android.com/apk/res/android"
        android:shape=["rectangle" | "oval" | "line" | "ring"] >
<corners
android:radius="integer"
android:topLeftRadius="integer"
android:topRightRadius="integer"
android:bottomLeftRadius="integer"
android:bottomRightRadius="integer"/>
<gradient
android:angle="integer"
android:centerX="float"
android:centerY="float"
android:centerColor="integer"
android:endColor="color"
android:gradientRadius="integer"
android:startColor="color"
android:type=["linear" | "radial" | "sweep"]
        android:useLevel=["true" | "false"] />
<padding
android:left="integer"
android:top="integer"
android:right="integer"
android:bottom="integer"/>
<size
android:width="integer"
android:height="integer"/>
<solid
android:color="color"/>
<stroke
android:width="integer"
android:color="color"
android:dashWidth="integer"
android:dashGap="integer"/>
        </shape>
```

### 해상도 수치

- ldpi: 저밀도(ldpi)의 화면(~120dpi)에 대한 리소스입니다.
- mdpi: 중밀도(mdpi)의 화면(~160dpi)에 대한 리소스입니다. (이것이 기준 밀도입니다.)
- hdpi: 고밀도(hdpi)의 화면(~240dpi)에 대한 리소스입니다.
- xhdpi: 초고밀도(xhdpi)의 화면(~320dpi)에 대한 리소스입니다.
- xxhdpi: 초초고밀도(xxhdpi)의 화면(~480dpi)에 대한 리소스입니다.
- xxxhdpi: 초초초고밀도(xxxhdpi) 사용(~640dpi)에 대한 리소스입니다.
- nodpi: 모든 밀도에 대한 리소스입니다. 이들은 밀도 독립적 리소스입니다. 이 한정자 태그가 지정된 리소스의 경우 현재 화면의 밀도에 관계없이 시스템에서 리소스 크기를 조정하지 않습니다.

### TextView에서 때때로 dp 사용하는 이유

- 픽셀로 크기를 정의하면 화면 크기에 따라 픽셀 밀도가 달라져서 같은 개수의 픽셀이라도 기기가 다르면 실제 크기가 달라질 수 있으므로 문제가 됨
- 밀도가 서로 다른 화면에서 UI 표시 크기를 유지하려면 밀도 독립형 픽셀(dp)을 측정 단위로 사용해서 UI를 디자인해야
- 1dp는 중밀도 화면(160dpi, '기준' 밀도)의 1픽셀과 거의 동일한 가상 픽셀 단위
- Android는 이 값을 밀도마다 적합한 실제 픽셀 수로 변환
- 텍스트 크기를 정의할 때는 확장 가능 픽셀(sp)을 단위로 사용해야
- sp 단위는 기본적으로 dp와 같은 크기이지만, 사용자가 선호하는 텍스트 크기에 따라 크기가 조절
- UI 구조상 사용자가 선택한 텍스트 크기에 유연하게 반응할 수 있다면 sp, 반드시 고정형 크기로 표시되어야 한다면 dp를 사용해야 함

### ImageView vs ImageButton

- ImageButton은 아래의 추가 스타일이 적용됨

```xml

<style name="Widget.ImageButton">
    <item name="android:focusable">true</item>
    <item name="android:clickable">true</item>
    <item name="android:scaleType">center</item>
    <item name="android:background">@android:drawable/btn_default</item>
</style>

```

### themes.xml

- AppCompatTheme 사용을 위해서는 AppCompatActivity 사용이 필요

### R class

- 리소스 요소를 식별할 수 있는 방법 제공
- 각 리소스 항목에 대해 int 변수가 자동으로 R 클래스에 추가

#### 다른 레이아웃 파일에서 동일 id 사용

- 동일한 id가 여러 개 있어도 하나만 추가
- 액티비티 또는 특정 뷰에서 파생되는 findViewById 함수는 해당 id를 가진 첫번째 자손 뷰를 찾음

## Gradle

- build.gradle(project), build.gradle(module), settings.gradle 존재

### settings.gradle

- 참여할 프로젝트 인스턴스의 계층 구조를 인스턴스화하고 구성하는데 필요한 구성을 정의
- 프로젝트 이름과 모듈을 확인

#### poject module로 dependency 추가

- volley 라이브러리를 이용하여 테스트
    - 해당 라이브러리의 소스코드 다운로드
    - module을 import
    - settings.gradle에 include ':volley' 추가
    - module(app)의 build.gradle의 dependencies에 implementation project(":volley") 추가
    - 단, volley의 경우 ErrorProne 라이브러리의 이슈로 추가 작업 필요

### build.gradle(project)

- repositories: dependency를 가져올 저장소 설정
    - jcenter는 지원 종료
    - maven에서 라이브러리 버전, 버전별 dependency, sample code 확인 가능
- dependencies: 그레이들 관련 플러그인 로드
    - Android Gradle Plugin: 구글이 제공하는 gradle supporting 도구
    - AGP 버전이 여기에 기술
    - gradle 버전은 gradle-wrapper.properties에서 확인
    - 이전 버전에서는 gradle과 AGP 버전이 일치하지 않을 수 있음

### build.gradle(module)

- compileSdkVersion: 컴파일할 때 사용할 SDK 버전, sdk 버전 올릴 때에는 추가, 삭제, 수정된 변경사항 확인 필수
- buildToolsVersion: SDK 빌드 도구의 버전
- minSdkVersion: 최소 지원 SDK 버전
- targetSdkVersion: 주요 목표로 하는 버전, 일반적으로 compileSdkVersion과 동일하게 정의하는 것이 권장
- versionCode: 앱의 버전 코드, play store 업데이트를 위해서는 이전 버전 이상으로 설정해야 함
- versionName: 사용자에게 표시되는 앱의 버전 이름

#### plugins의 com.android.application과 com.android.library

- Android 라이브러리는 구조적으로 앱 모듈과 동일
- 라이브러리는 APK 대신 AAR파일로 컴파일
- AAR파일은 Android 리소스와 manifest 파일이 포함됨 -> 클래스와 메서드 외 레이아웃, 드로어블 같은 리소스 번들로 구성 가능

#### maxSdkVersion

- manifest의 <uses-sdk>
    - android 2.0.1 미만에서 최대 sdk version 정의에 사용(이후 버전에서는 무시됨)
    - google play store에서 필터링시에도 사용되었음
    - 개발 권장사항을 따른 경우 새 버전에서도 호환되며 업데이트 후에 앱이 사용자 기기에서 삭제되는 문제가 있어 제거됨
- manifest의 <uses-permission>
    - 앱에 권한을 부여해야 하는 최상위 API 레벨 정의에 사용
    - 특정 API 수준 이상에서 필요하지 않는 경우 설정(Ex: WRITE_EXTERNAL_STORAGE)

#### android

- buildTypes
    - debug, release 등 빌드 타입 지정 가능
    - minifyEnabled: 난독화(디컴파일링 방지, 코드 보안 강화), 최적화(미사용 코드 제거)
    - shringResources: 사용하지 않는 리소스 제거 (동적으로 리소스 접근하거나 타모듈 접근하는 경우 문제 발생 가능)
    - proguardFiles: 난독화 제외할 파일 정의, 사용될 코드의 의도치 않은 삭제 방지
- compileOptions
    - sourceCompatibility는 컴파일에 사용할 JDK 버전을, targetCompatibility는 바이트코드가 생성될 JDK 버전 정의
- kotlinOptions
    - jvmTarget 설정
- buildFeatures
    - 프로젝트에 사용할 빌드 기능 활성화
    - viewBinding, compose, buildConfig, prefab 등

#### dependencies

- 라이브러리 업데이트, 삭제, 마이그레이션시 테스트
    - side effect를 미리 고려 -> 코드 작성할 때 에러 예방하도록 작성
    - support 라이브러리는 androidx로 이전
- 라이브러리 도입
    - 기능의 장단점과 도입시 문제점 분석 필요

#### dependencies에서 빌드 의존성 제어

- dependency 관리
    - 디펜던시는 모듈 형태로 제공
    - 레포지토리: 모듈이 저장되는 곳, 로컬 디렉토리나 원격 저장소가 될 수 있음
    - Dependency resolution: 런타임에 그레이들이 특정 작업을 수행하기 위해 필요한 경우 선언된 디펜던시를 위치시키는 과정
        - 원격 레포지토리에서 다운로드되거나 로컬 디렉토리에서 찾거나 멀티 프로젝트 설정에 따라 다른 프로젝트가 빌드되어야 할 수 있음
        - 로컬 캐시에 디펜던시 파일을 저장하고 다음 빌드에서 캐시에 저장된 파일을 재사용
- dependency 구성
    - 그레이들 프로젝트의 디펜던시는 특정 스코프에 적용
    - 모듈 컴파일, 실행과 같이 특정한 목표를 위해 함께 그룹화된 고유한 이름으로 식별되는 디펜던시 컬렉션
    - 상속 관계를 가질 수 있음
    - 라이브러리가 사용되는 시점을 기준으로 라이브러리를 클래스 경로에 추가할지 결정할 수 있음

| 디펜던시 구성             | 설명                                                                                                                                                                                                                   |
|---------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| api                 | 컴파일 클래스 경로와 빌드 출력에 디펜던시 추가<br/>모듈이 다른 모듈로 디펜던시를 이전하여 다른 모듈에서 런타임, 컴파일 시간에 사용할 수 있음<br/>컴파일시 디펜던시 항목에 권한이 있는 모든 모듈을 다시 컴파일                                                                                            |
| implementation      | 컴파일 클래스 경로에 디펜던시 추가, 빌드 출력에 디펜던시 패키징, 모듈에서 디펜던시를 구성하면 모듈이 컴파일 시간에 디펜던시를 다른 모듈에 노출하지 않음(디펜던시는 런타임에만 다른 모듈에서 사용 가능), 디펜던시가 API를 변경하면 이 디펜던시와 직접적으로 종속된 모듈만 다시 컴파일                                                      |
| compileOnly         | 컴파일 클래스 경로에만 디펜던시 추가(빌드 출력에는 추가하지 않음) 디펜던시가 런타임에 제공되지 않아도 작동할 수 있도록 동작을 변경해야 할 수 있음                                                                                                                                  |
| runtimeOnly         | 런타임에 사용하기 위해 빌드 출력에만 디펜던시 추가, 컴파일 클래스 경로에는 추가되지 않음                                                                                                                                                                   |
| annotationProcessor | 어노테이션 프로세서인 라이브러리를 디펜던시로 추가 컴파일 클래스 경로를 어노테이션 프로세서 클래스 경로에서 분리하여 빌드 성능 개선(컴파일 클래스 경로에서 어노테이션 프로세서를 찾으면 컴파일 회피(b에 종속된 a에서 b의 특정 메소드 바디만 바뀐 경우, a는 재컴파일하지 않는 것)를 비활성화하여 빌드 시간 늘림, 5.0 이상에서는 컴파일 클래스 경로의 어노테이션 프로세서 무시) |
| lintChecks          | 프로젝트 빌드시 그레이들이 실행할 lint 검사 포함                                                                                                                                                                                        |
| lintPublish         | lint.jar 파일로 컴파일하고 AAR에 패키징할 린트 검사 포함 AAR을 사용하는 프로젝트에서 이 린트 검사가 적용됨                                                                                                                                                  |

#### bulid type과 product flavors 사용 방법

- 빌드 변형을 구성하여 단일 프로젝트에서 다양한 버전의 앱을 만들고 디펜던시 및 서명 구성을 관리 가능
- 그레이들이 특정 규칙 셋을 이용하여 제품 버전과 빌드 유형에 구성된 설정, 코드, 리소스를 조합
- 빌드 유형에는 빌드 및 패키징 설정(디버그 옵션, 서명 키) 적용 가능
- 제품 버전에는 특정 기능과 기기 요구사항(소스 코드, 리소스, 최소 API 수준) 지정 가능
- 빌드 유형을 구성하려면 buildTypes에 빌드 유형을 추가하여 특정 설정을 추가하거나 변경 가능
- 제품 버전을 구성하려면 productFlavors 블록에 제품 버전을 추가하고 원하는 설정 포함(제품 버전은 defaultConfig와 동일한 속성 지원)
- 어플리케이션 ID를 다르게 설정하려면 productFlavors 블록 내부의 각 제품 버전에 applicationId 속성을 재정의하거나 applicationIdSuffix를 사용하여 제품 버전 또는 빌드 유형에
  따라 세그먼트 추가
    - 제품 버전으로 Google play store에 앱 개시시 free, pro 버전 별도 관리 가능
    - 빌드 유형을 통해 하나의 기기에 디버그 빌드와 릴리즈 빌드를 모두 설치 가능
