# Screenshot #

<img src="https://github.com/vathanak-mao/solarnoon/blob/main/.github/demo.jpg" width="30%"/>


# Build


**Output Directory**
```
<project-root>/app/build/outputs
```

**Remove build directory**
```
$ ./gradlew clean
```


## Compile

**Debug variant**
```
$ ./gradlew buildDebug
```

## Compile & Generate APK

**Debug variant**
```
$ ./gradlew assembleDebug
```

**ClosedTesting variant**
```
$ ./gradlew assembleClosedTesting
```

**Release variant**
```
$ export admob_ad_unit_id=<ADMOB_AD_UNIT_ID>
$ ./gradlew assembleRelease
```

# Run Tests #

## Instrumented Tests ##

The Android Virtual Device (AVD) "Small Phone API 31" will automatically be downloaded, installed, and started before running the tests.

**Run all tests:**
```
$ ./gradlew smallphoneapi31DebugAndroidTest
```

**Run specific test class:**
```
$ ./gradlew smallphoneapi31DebugAndroidTest -P android.testInstrumentationRunnerArguments.class=com.vathanakmao.solarnoon.ui.MainActivityUiAutomatorTest
```
**Run specific test method:**
```
$ ./gradlew smallphoneapi31DebugAndroidTest -P android.testInstrumentationRunnerArguments.class=com.vathanakmao.solarnoon.ui.MainActivityUiAutomatorTest#changeLanguage
```



