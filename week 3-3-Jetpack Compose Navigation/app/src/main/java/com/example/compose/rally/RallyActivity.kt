/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.compose.rally

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.compose.rally.data.UserData
import com.example.compose.rally.ui.accounts.AccountsBody
import com.example.compose.rally.ui.accounts.SingleAccountBody
import com.example.compose.rally.ui.bills.BillsBody
import com.example.compose.rally.ui.components.RallyTabRow
import com.example.compose.rally.ui.overview.OverviewBody
import com.example.compose.rally.ui.theme.RallyTheme

/**
 * This Activity recreates part of the Rally Material Study from
 * https://material.io/design/material-studies/rally.html
 */
class RallyActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RallyApp()
        }
    }
}

@Composable
fun RallyApp() {
    RallyTheme {
        val allScreens = RallyScreen.values().toList()
        // FIXME: This duplicate source of truth
//        var currentScreen by rememberSaveable { mutableStateOf(RallyScreen.Overview) }
        val navController = rememberNavController()
        val backstackEntry = navController.currentBackStackEntryAsState()
        // Navigation 사용에 맞게 현재화면 업데이트하는 방법 변경
        val currentScreen = RallyScreen.fromRoute(backstackEntry.value?.destination?.route)

        Scaffold(
            topBar = {
                RallyTabRow(
                    allScreens = allScreens,
                    onTabSelected = { screen -> navController.navigate(screen.name) },
                    currentScreen = currentScreen
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = RallyScreen.Overview.name,   // 시작점
                modifier = Modifier.padding(innerPadding)
            ) {
                // composable 확장함수로 감싸진 하나 하나가 네비게이션 그래프에 목적지로 추가됨  
                composable(route = RallyScreen.Overview.name) {
                    OverviewBody(
                        onClickSeeAllAccounts = { navController.navigate(RallyScreen.Accounts.name) },
                        onAccountClick = { name -> navigateToSingleAccount(navController, name) },
                        onClickSeeAllBills = { navController.navigate(RallyScreen.Bills.name) }
                    )
                }
                composable(route = RallyScreen.Accounts.name) {
                    AccountsBody(accounts = UserData.accounts) { name ->    // onAccountClick
                        navigateToSingleAccount(navController, name)
                    }
                }
                val accountsName = RallyScreen.Accounts.name
                composable(
                    route = "$accountsName/{name}",
                    arguments = listOf(
                        navArgument("name") {
                            type = NavType.StringType
                        }
                    ),
                    // 외부에서 해당 앱의 특정 화면을 열 수 있도록 딥링크 추가
                    deepLinks = listOf(navDeepLink {
                        uriPattern = "rally://$accountsName/{name}"
                    })
                ) { entry ->
                    // composable의 arguments 항목에 넘긴 인자들을 여기서 파라미터인 entry로 꺼내쓸 수 있음
                    // 전달된 argument로 destination에 필요한 요소를 찾음
                    val accountName = entry.arguments?.getString("name")
                    val account = UserData.getAccount(accountName)
                    SingleAccountBody(account = account)
                }

                composable(route = RallyScreen.Bills.name) {
                    BillsBody(bills = UserData.bills)
                }
            }

//            Box(Modifier.padding(innerPadding)) {
//                currentScreen.content(
//                    onScreenChange = { screen ->
//                        currentScreen = RallyScreen.valueOf(screen)
//                    }
//                )
//            }
        }
    }
}

private fun navigateToSingleAccount(navController: NavController, accountName: String) {
    navController.navigate("${RallyScreen.Accounts.name}/$accountName")
}
