# Screenshot #

<img src="https://github.com/vathanak-mao/solarnoon/blob/main/.github/demo.jpg" width="30%"/>

# Testing #

## Instrumented Tests ##

The Android Virtual Device (AVD) "Small Phone API 30" will automatically be downloaded, installed, and started before running the tests.

**Run all tests:**
```
$ ./gradlew smallphoneapi30DebugAndroidTest
```

**Run specific test class:**
```
$ ./gradlew smallphoneapi30DebugAndroidTest -P android.testInstrumentationRunnerArguments.class=com.vathanakmao.solarnoon.ui.MainActivityUITest
```
**Run specific test method:**
```
$ ./gradlew smallphoneapi30DebugAndroidTest -P android.testInstrumentationRunnerArguments.class=com.vathanakmao.solarnoon.ui.MainActivityUITest#changeLanguage
```



