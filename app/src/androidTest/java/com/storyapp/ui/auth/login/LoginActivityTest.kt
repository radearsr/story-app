package com.storyapp.ui.auth.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.storyapp.R
import com.storyapp.data.pref.IUserPreference
import com.storyapp.di.testModule
import com.storyapp.ui.main.MainActivity
import com.storyapp.utils.EspressoIdlingResource
import com.storyapp.utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.test.KoinTest
import org.koin.test.get

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class LoginActivityTest : KoinTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        loadKoinModules(testModule)
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        unloadKoinModules(testModule)
    }

    private val fakeEmailAddressValid = "megumichan@test.kyun"
    private val fakePasswordValid = "iloveyou"

    private val fakeEmailAddressNotValid = "user@test.com"
    private val fakePasswordNotValid = "userkyun"

    @Test
    fun loginPerform_Success() = runTest {
        Intents.init()
        onView(withId(R.id.ed_login_email)).check(matches(isDisplayed()))
        onView(withId(R.id.ed_login_email))
            .perform(typeText(fakeEmailAddressValid), closeSoftKeyboard())
        onView(withId(R.id.ed_login_password)).check(matches(isDisplayed()))
        onView(withId(R.id.ed_login_password))
            .perform(typeText(fakePasswordValid), closeSoftKeyboard())
        onView(withId(R.id.btn_login)).perform(click())
        intended(hasComponent(MainActivity::class.java.name))
        val fakeUserPreference: IUserPreference = get()
        val isLoggedIn = fakeUserPreference.getSession().first().isLogin
        assertTrue("User should be logged in", isLoggedIn)
        val userToken = fakeUserPreference.getSession().first().token
        assertEquals("Token should match", "fake-token", userToken)
        Intents.release()
    }

    @Test
    fun loginPerform_Error() {
        onView(withId(R.id.ed_login_email)).check(matches(isDisplayed()))
        onView(withId(R.id.ed_login_email))
            .perform(typeText(fakeEmailAddressNotValid), closeSoftKeyboard())
        onView(withId(R.id.ed_login_password)).check(matches(isDisplayed()))
        onView(withId(R.id.ed_login_password))
            .perform(typeText(fakePasswordNotValid), closeSoftKeyboard())
        onView(withId(R.id.btn_login)).perform(click())
        onView(withText("Invalid email or password"))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
    }
}
