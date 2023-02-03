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

