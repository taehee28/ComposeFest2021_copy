package com.thk.mybasicscodelab

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thk.mybasicscodelab.ui.theme.MyBasicsCodelabTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyBasicsCodelabTheme {
                MyApp()
            }
        }
    }
}

// 컴포저블 함수들은 대문자로 시작하나봄....
@Composable
private fun MyApp(names: List<String> = listOf("World", "Compose")) {
    // state 를 공유하기 위해서 해당 변수를 전역변수로 만드는게 아니라 호출 체인의 상위로 끌어올림(hoist)
//    var shouldShowOnBoarding by remember { mutableStateOf(true) }     // 변경 전

    // change configuration, process death 에서도 상태를 잃지 않도록 rememberSaveable 사용
    var shouldShowOnBoarding by rememberSaveable { mutableStateOf(true) }

    // 온보딩 화면을 숨기는게(invisible or gone) 아니라 recomposition 하면서 아예 UI를 추가하지 않음
    if (shouldShowOnBoarding) {
        // state 를 조작하는 코드를 state 가 있는 곳에서 작성하여 람다로 넘김 -> state 가 캡쳐될 수 있도록
        OnboardingScreen { shouldShowOnBoarding = false }
    } else {
        Greetings()
    }
}


@Composable
fun OnboardingScreen(onContinueClicked: () -> Unit) {
//    var shouldShowOnBoarding by remember { mutableStateOf(true) }     // 반드시 hoisted 되어야 하는 state

    Surface() {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "베이직 코드랩에 오신걸 환영합니다!")
            Button(
                modifier = Modifier.padding(vertical = 24.dp),
                onClick = onContinueClicked     // 버튼 터치하면 state 조작 발생 -> 해당 state 가지고있는 MyApp()이 recomposed
                /* onClick = { shouldShowOnBoarding = false } */    // hoist 하기 전
            ) {
                Text(text = "계속하기")
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun OnboardingPreview() {
    MyBasicsCodelabTheme {
        OnboardingScreen(onContinueClicked = {})
    }
}

@Composable
private fun Greetings(names: List<String> = List(1000) { "$it" } ) {
    // 해당 함수에서 한번 감싸서 Greeting 함수 호출하는 곳을 하나로 줄이고 나머지 부분엔 이 함수가 쓰이도록
    //  -> 인자로 넘길 스트링을 여기서 한번만 수정하면 됨

    // RecyclerView 와 비슷한 형태이지만 recycle 하지 않고 스크롤에 맞춰서 그냥 새로운 컴포저블을 표시 -> 굳이 recycle 하지않을만큼 비용이 낮음
    LazyColumn(modifier = Modifier.padding(vertical = 4.dp)) {
        // LazyColumn 에서 제공하는 items 요소. 쓰여진 코드대로 각각의 항목을 렌더링
        items(items = names) { name ->
            CardFrame(name = name)
//            Greeting(name = name)
        }
    }
}

@Composable
private fun CardFrame(name: String) {
    Card(
        backgroundColor = MaterialTheme.colors.primary,
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Greeting(name = name)
    }
}

@Composable
fun Greeting(name: String) {
    // 그냥 Boolean 아니라 MutableStateOf<Boolean> 인거라서 .value 로 값 접근해야 함
    // 리컴포지션이 발생하면 변수들의 상태도 초기화되기때문에 상태를 유지하기 위해 remember 가 필요함
    // LazyColumn으로 인해 다시 그려지면서 상태가 초기화되는 것을 막으려면 rememberSaveable 사용할
    var expended by remember { mutableStateOf(false) }
    
    // remember 할 필요 없음 <- expended 상태에 맞춰서 바뀌니까
    // 넘겨준 targetValue 까지 dp 값을 변화시켜주는(애니매이션) 함수
    // animate*AsState 류의 함수들은 다른 요소에 의해서 중간에 중단 가능함
    val extraPadding by animateDpAsState(
        if (expended) 48.dp else 0.dp,
        animationSpec = spring(     // 해당 인자에 애니메이션을 넘겨서 적용시킴
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ))


    Row(
        modifier = Modifier
            .padding(12.dp)
            .animateContentSize(animationSpec = spring(     // 해당 인자에 애니메이션을 넘겨서 적용시킴
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow)
            )
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(12.dp)     // margin 으로 적용됨. Row의 스프링 애니메이션으로 인해 Row의 패딩(마진)이 Column 영역을 침범하지 못하도록 함
        ) {
            Text(text = "Hello,")
            Text(text = name, style = MaterialTheme.typography.h4.copy(     // 제공되는 MaterialTheme 으로 스타일 지정 가능
                fontWeight = FontWeight.ExtraBold       // 이미 정의된 스타일을 copy()로 수정 가능함
            ))
            if (expended) {
                Text(
                    text = ("Composem ipsum color sit lazy, padding theme elit, sed do bouncy. ").repeat(4)
                )
            }
        }

        IconButton(
            onClick = { expended = !expended }
        ) {
            Icon(
                imageVector = if (expended) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = (if (expended) R.string.show_less else R.string.show_more).toString()
            )

//                Text(text = if (expended.value) "Show less" else "Show more")
        }
    }

}

@Preview(   // 다크모드 프리뷰 추가
    showBackground = true,
    widthDp = 320,
    uiMode = UI_MODE_NIGHT_YES,
    name = "DefaultPreviewDark"
)
@Preview(showBackground = true, widthDp = 320)
@Composable
fun DefaultPreview() {
    MyBasicsCodelabTheme {
        Greetings()
    }
}