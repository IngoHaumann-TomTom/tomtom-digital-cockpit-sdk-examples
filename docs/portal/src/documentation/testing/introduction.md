# Introduction

## Why do we write tests?

Running tests regularly checks whether your code is still doing what it is supposed to do. Tests
verify each individual component, component interaction, and the product as a whole.

Because tests keep verifying the code, it gives the developers the confidence to refactor whenever
needed, without worrying about breaking something unforeseen; the tests will catch that. This
results in well maintained code that is being worked on with confidence, while the tests themselves
serve as examples of how to use the code.

The build machines, that verify the pull requests for new code changes, run all tests to verify a
commit will not introduce an unexpected change in system behavior. In short, these automated tests
make sure the system as a whole functions as each part was originally designed to function.

## Types of tests

There are many types of tests. We'll only discuss the three main categories:

- Unit tests
- Integration tests
- End-to-End (E2E) tests

These types of tests all operate on a different level of the test pyramid.

![Test pyramid](images/test-pyramid.svg)

Unit tests are at the bottom. There are many of them and they run fast. They typically verify that a
small unit, or component, works as it is expected by the developer. Typical units are classes. They
have no dependencies and their runtime should be in milliseconds.

The next layer of the pyramid consists of integration tests. These verify that a set of components
work together to achieve a common goal. They are usually written for a set of components, be it
classes or packages, that need to reach a common goal; they are typically restricted to a single
module. Because of these dependencies they are slightly more complex than unit tests. They also have
longer run times than unit tests but should still be in the milliseconds range.

At the top we find the E2E tests which verify that all parts of the system work together to
accomplish the tasks the end-user wants to perform; so they emulate end-user behavior. Usually they
require some set up of the system, may have many dependencies, and are therefore the most complex of
the three test types.

To summarize:

- Unit tests are easy to write, run fast, and have no dependencies.
- Integration tests are more complex than unit tests because they _do_ have dependencies.
- E2E tests simulate end-user interaction with the product and may be complex.

## Why a pyramid?

So why are these tests shown in a pyramid shape?

Preferably, there are a great many unit tests. They work on a single small piece of code, require
almost no setup, and are therefore fast and reliable. Since you write so many of them, they are at
the bottom of the pyramid, supporting all the tests above them. Everything you test in a unit test,
you can assume is working in the tests above it; you don't test that behavior again.

Integration tests take the unit test verified behavior of the components to test interactions
between those units. Since they mostly test interaction, there are fewer of them than there are unit
tests.

Now the individual components are tested, and they are proven to work together flawlessly, we can
verify whether the system as a whole works as an end-user would expect. There are typically far less
E2E scenarios than there are integration tests, which is why E2E testing sits at the top of the
pyramid. They rely on everything working as expected and only verify whether all modules can work
together to achieve a common goal.

> **Note:** Since E2E tests are so complex to set up and run, they may suffer from all kinds of
influences that may make the test results less reliable. Things like timing, threads, mocked network
access, etc. can all cause issues that make the test fail, not because the code is wrong but because
creating a reliable test bed is difficult. Tests that do not always give the same result with each
run are called flaky tests, and they cause headaches and take a lot of time to fix. So make every
effort to write tests that are not flaky but guarantee correct results each time they are run.

## Running TomTom IndiGO tests

All types of tests, except unit tests, need an Android device to run;
launch [an emulator](/tomtom-indigo/documentation/getting-started/the-tomtom-indigo-emulator) if no
physical device is available. The tests will only run on a rooted device, which can be achieved by
running `adb root` before executing any of the Gradle commands below. All these commands are
performed in the project's root directory:

- To run all the unit tests: `./gradlew runUnitSuite`
- To run all the integration tests: `./gradlew runIntegrationSuite`
- To run all the E2E tests: `./gradlew runE2eSuite`

If you only want to run tests of a single module, or just a specific test case:

- To run all unit tests of a single module: `./gradlew <module>:test`
- To run a single test class: `./gradlew <module>:testDebugUnitTest --tests MyTestClass`

After the tests have run, a full report of the results is available at `IviTest/<date_time>/report.html`.

## Mocking

To eliminate any influence of dependencies while performing your test, you can mock the dependency's
behavior. Basically, you hard code the results of interacting with it, so you can verify the
responses of the code you are testing.

Dependencies you should certainly mock are: disk and network access, threading, and complex logic.
Do _not_ mock simple data classes, data structures like lists, or `LiveData`.

You can use [MockK](https://mockk.io) for Kotlin tests. A simple test, with a single mocked
dependency, may look like this:

```kotlin
import com.tomtom.tools.android.testing.mock.niceMockk

private class Subject(
    private val dependency: Dependency
) {
    fun add() = dependency.count + 1
}

@Test
fun `adds exactly one`() {
    // GIVEN I have a Subject with a Dependency
    val dependency = niceMockk<Dependency>()
    val sut = Subject(dependency)
    // AND that dependency has a count of 5
    every { dependency.count } return 5
    // WHEN I call add() on the Subject
    val result = sut.add()
    // THEN the result should be 6
    assertEquals(6, result)
}
```

Here, `Subject` depends on a `Dependency` instance to get the `count` from. Since you want to test
the behavior of `Subject`, and _not_ `Dependency`, you need to rule out any interference from
`Dependency`. So you mock the dependency's behavior.

You create a `dependency` instance that is a mocked object having the same public interface as a
`Dependency`. You instruct it to return the value `5` whenever its `count` property is read. Since
the behavior of the dependency is now 100% predicable, we can verify whether our `Subject` responds
properly to it, without running any of the `Dependency`'s code.

---

**See also:**

- Martin Fowler:
  ["The Practical Test Pyramid"](https://martinfowler.com/articles/practical-test-pyramid.html)
- Mocking library for Kotlin: [MockK](https://mockk.io)
