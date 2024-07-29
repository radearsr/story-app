package com.storyapp.ui.main

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
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
import com.storyapp.ui.welcome.WelcomeActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.test.KoinTest
import org.koin.test.get
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class MainActivityTest : KoinTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setUp() {
        loadKoinModules(testModule)
    }

    @Test
    fun logoutPerform_Success() = runBlocking {
        Intents.init()
        try {
            onView(withId(R.id.action_logout)).perform(click())

            onView(withText(R.string.txt_confirm))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))

            onView(withText(R.string.txt_logout)).perform(click())

            intended(hasComponent(WelcomeActivity::class.java.name))

            val fakeUserPreference: IUserPreference = get()
            suspend fun checkLogoutState(): Boolean {
                return withContext(Dispatchers.IO) {
                    !fakeUserPreference.getSession().first().isLogin
                }
            }

            withTimeout(10000) {
                var isLoggedOut = false
                repeat(10) {
                    if (checkLogoutState()) {
                        isLoggedOut = true
                        return@withTimeout
                    }
                    delay(1000)
                }
                assertTrue("User should be logged out", isLoggedOut)
            }

            val user = withContext(Dispatchers.IO) {
                fakeUserPreference.getSession().first()
            }
            assertTrue("Token should be empty", user.token.isEmpty())
        } finally {
            Intents.release()
        }
    }


    @Test
    fun logoutPerform_Cancel() {
        onView(withId(R.id.action_logout)).check(matches(isDisplayed()))
        onView(withId(R.id.action_logout)).perform(click())
        onView(withText(R.string.txt_confirm))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))

        onView(withText(R.string.txt_close)).perform(click())
        onView(withId(R.id.fab_create_story)).check(matches(isDisplayed()))
    }
}
