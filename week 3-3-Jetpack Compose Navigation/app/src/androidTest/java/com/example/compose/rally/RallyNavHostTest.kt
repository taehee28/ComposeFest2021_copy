package com.example.compose.rally

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RallyNavHostTest {

    @get:Rule
    val composeTestRule = createComposeRule()
    lateinit var navController: NavHostController   // 프로퍼티를 lateinit으로 선언해서 테스트 코드에서 쓰도록

    // 모든 테스트에서 똑같이 설정되어야 하기 때문에 따로 추출
    @Before
    fun setupRallyNavHost() {
        composeTestRule.setContent {
            navController = rememberNavController()
            RallyNavHost(navController = navController)
        }
    }

    @Test
    fun rallyNavHost() {
        composeTestRule
            .onNodeWithContentDescription("Overview Screen")
            .assertIsDisplayed()
    }

    @Test
    fun rallyNavHost_navigateToAllAccounts_viaUI() {
        // "Accounts Screen"으로 이동하는 "All Accounts" 버튼에 클릭을 발생시킴
        // 올바른 화면으로 이동하는지 테스트

        composeTestRule
            .onNodeWithContentDescription("All Accounts")
            .performClick()
        composeTestRule
            .onNodeWithContentDescription("Accounts Screen")
            .assertIsDisplayed()
    }

    @Test
    fun rallyNavHost_navigateToBills_viaUI() {
        // UI와 navController를 사용해서 테스트하기

        // "All Bills"를 눌렀을 때
        composeTestRule.onNodeWithContentDescription("All Bills").apply {
            performScrollTo()
            performClick()
        }

        // 루트의 이름이 "Bills"인지 체크
        val route = navController.currentBackStackEntry?.destination?.route
        assertEquals(route, "Bills")
    }

    @Test
    fun rallyNavHost_navigateToAllAccounts_callingNavigate() {
        // 직접 navController.navigate 호출해서 테스트하기
        // navigate 함수는 UI 스레드에서 실행되어야 하기 때문에 코루틴으로 스레드 지정해서 실행
        // 함수의 호출이 assertion 전에 실행되어야 하기 때문에 runBlocking으로 동시실행을 막음

        runBlocking {
            withContext(Dispatchers.Main) {
                navController.navigate(RallyScreen.Accounts.name)
            }
        }

        composeTestRule
            .onNodeWithContentDescription("Accounts Screen")
            .assertIsDisplayed()
    }
}