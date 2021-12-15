/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.codelabs.state.todo

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.codelabs.state.ui.StateCodelabTheme

class TodoActivity : AppCompatActivity() {

    private val todoViewModel by viewModels<TodoViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StateCodelabTheme {
                Surface {
                    TodoActivityScreen(todoViewModel = todoViewModel)
                }
            }
        }
    }
}

/**
 * ViewModel에 저장된 state와 TodoScreen 컴포저블 사이의 매개체 역할을 할 컴포저블
 */
@Composable
private fun TodoActivityScreen(todoViewModel: TodoViewModel) {
    // ViewModel이 State를 사용함에 따라 코드 업데이트
    TodoScreen(
        items = todoViewModel.todoItems,
        currentlyEditing = todoViewModel.currentEditItem,
        onAddItem = todoViewModel::addItem,
        onRemoveItem = todoViewModel::removeItem,
        onStartEdit = todoViewModel::onEditItemSelected,
        onEditItemChange = todoViewModel::onEditItemChange,
        onEditDone = todoViewModel::onEditDone
    )



//    // LiveData를 observe하고 현재 데이터를 List<TodoItem>의 형태로 바로 사용할 수 있도록 함
//    val items: List<TodoItem> by todoViewModel.todoItems.
//
//    // TodoScreen 컴포저블이 바로 ViewModel을 받게 할 수 있지만,
//    // 더 단순한 파라미터를 사용함으로써(List 형태 등) state가 hoist된 특정 장소에 종속되지 않도록함
//    TodoScreen(
//        items = items,
//        // TodoScreen이 해당 이벤트를 호출하면 ViewModel의 이벤트로 호출을 패스할 수 있게
//        onAddItem = { todoViewModel.addItem(it) },
//        onRemoveItem = { todoViewModel.removeItem(it) }
//    )
}
