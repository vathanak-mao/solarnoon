# Screenshot #

This is how the UI would look like:

<img src="https://github.com/vathanak-mao/solarnoon/blob/main/.github/demo.jpg" width="30%"/>

# Instrumented Tests #

### Run all tests ###

<code>
$ ./gradlew smallphoneapi30DebugAndroidTest
</code>

### Run specific test class ###

<code>
$ ./gradlew smallphoneapi30DebugAndroidTest -P android.testInstrumentationRunnerArguments.class=com.vathanakmao.solarnoon.ui.MainActivityUITest
</code>

### Run specific test method ###

<code>
$ ./gradlew smallphoneapi30DebugAndroidTest -P android.testInstrumentationRunnerArguments.class=com.vathanakmao.solarnoon.ui.MainActivityUITest#changeLanguage
</code>



