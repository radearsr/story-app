package com.storyapp.ui.auth.login

import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.storyapp.R
import com.storyapp.data.ResultState
import com.storyapp.data.remote.response.LoginResponse
import com.storyapp.data.remote.response.LoginResultResponse
import com.storyapp.ui.auth.AuthViewModel
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)


    private lateinit var mockViewModel: AuthViewModel

    @Before
    fun setUp() {
        mockViewModel = mock(AuthViewModel::class.java)
    }

    private val fakeEmailAddress = "user@test.com"
    private val fakePassword = "user_test_kyo"

    @Test
    fun loginPerform_Success() {
        val successLiveData = MutableLiveData<ResultState<LoginResponse>>()
        `when`(mockViewModel.loginAction(fakeEmailAddress, fakePassword)).thenReturn(successLiveData)

        activityRule.scenario.onActivity { activity ->

//            activity.authViewModel = mockViewModel

            onView(withId(R.id.ed_login_email)).check(matches(isDisplayed()))
            onView(withId(R.id.ed_login_email)).perform(typeText(fakeEmailAddress), closeSoftKeyboard())

            onView(withId(R.id.ed_login_password)).check(matches(isDisplayed()))
            onView(withId(R.id.ed_login_password)).perform(typeText(fakePassword), closeSoftKeyboard())

            onView(withId(R.id.btn_login)).check(matches(isDisplayed()))
            onView(withId(R.id.btn_login)).perform(click())

            successLiveData.postValue(ResultState.Success(LoginResponse(
                LoginResultResponse(
                    name = "UserTest",
                    token = "token-xxx",
                    userId = "user-xxx"
                ),
                message = "Success login"
            )))
        }
    }
}