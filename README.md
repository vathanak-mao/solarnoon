# Screenshot #

<img src="https://github.com/vathanak-mao/solarnoon/blob/main/.github/demo.jpg" width="30%"/>

# Instrumented Tests #

Run all tests
```
$ ./gradlew smallphoneapi30DebugAndroidTest
```

Run specific test class
```
$ ./gradlew smallphoneapi30DebugAndroidTest -P android.testInstrumentationRunnerArguments.class=com.vathanakmao.solarnoon.ui.MainActivityUITest
```
Run specific test method
```
$ ./gradlew smallphoneapi30DebugAndroidTest -P android.testInstrumentationRunnerArguments.class=com.vathanakmao.solarnoon.ui.MainActivityUITest#changeLanguage
```



