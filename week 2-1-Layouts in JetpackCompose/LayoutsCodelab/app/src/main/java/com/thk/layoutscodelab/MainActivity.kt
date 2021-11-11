package com.thk.layoutscodelab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Anchor
import androidx.compose.material.icons.filled.Savings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.thk.layoutscodelab.ui.theme.LayoutsCodelabTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LayoutsCodelabTheme {
                ImageList()
            }
        }
    }
}

@Composable
fun ImageList() {
    val listSize = 100

    // 스크롤 포지션을 state로 저장하기 위해, 프로그래밍적으로 리스트를 스크롤 할수있게
//    val scrollState = rememberScrollState()   // 일반 column 에서 쓰는 스크롤 state
    val scrollState = rememberLazyListState()   // lazy column 에서 쓰는 state

    // 생성된 coroutineScope 는 호출된 곳의 lifecycle을 따름
    val coroutineScope = rememberCoroutineScope()   // 자동 스크롤이 실행되는 곳에 coroutine scope 저장

    Column {
        Row {
            Button(onClick = { coroutineScope.launch {
                scrollState.animateScrollToItem(0)  // 리스트의 첫번째 아이템으로 이동
            } }) {
                Text(text = "Scroll to the top")
            }

            Button(onClick = { coroutineScope.launch {
                scrollState.animateScrollToItem(listSize - 1) // 리스트의 마지막 아이템으로 이동
            } }) {
                Text(text = "Scroll to the end")
            }
        }

        LazyColumn(state = scrollState) {
            items(100) {
                ImageListItem(index = it)
            }
        }
    }

}

@Preview
@Composable
fun ImageListPreview() {
    LayoutsCodelabTheme {
        ImageList()
    }
}

@Composable
fun ImageListItem(index: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = rememberImagePainter(data = "https://developer.android.com/images/brand/Android_Robot.png"),  // Coil 사용해서 이미지 로딩
            contentDescription = "Android Logo",
            modifier = Modifier.size(50.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = "Item #$index", style = MaterialTheme.typography.subtitle1)
    }
}

@Composable
fun LayoutsCodelab() {
    /*
    body content에 해당하는 람다 파라미터는 작성 필수
    해당 람다가 받는 패딩값은 내부에 구현될 가장 상위(root)의 컴포저블에 적용되어야 함 <- 화면에 보일 아이템들을 알맞게 constrain 하기 위해..

    topBar 파라미터는 상단의 앱바를 위한 슬롯. 그냥 Text를 넣어도 되고, 기본제공하는 TopAppBar 사용 가능
    */


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "LayoutsCodelab")
                },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Filled.Savings, contentDescription = null)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /*TODO*/ }) {
                Icon(Icons.Filled.Anchor, contentDescription = null)
            }
        }
    ) { innerPadding ->
        BodyContent(Modifier.padding(innerPadding).padding(8.dp))
    }
}

@Composable
fun BodyContent(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = "Hi there!")
        Text(text = "Thanks for going through the Layouts codelab")
    }
}

@Preview
@Composable
fun LayoutsCodelabPreview() {
    LayoutsCodelabTheme {
        LayoutsCodelab()
    }
}

@Composable
fun PhotographerCard() {
    Row {
        // 이미지의 플레이스홀더
        Surface(
            modifier = Modifier.size(50.dp),
            shape = CircleShape,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.2f)
        ) {

        }

        // Modifier 체이닝은 작성한 순서대로 컴포저블에 적용되기 때문에 순서를 고려해서 작성해야 함
        Column(
            modifier = Modifier
                .padding(start = 8.dp)
                .align(Alignment.CenterVertically)  // 레이아웃 관련 modifier. 레이아웃 별로 받을 수 있는 관련 modifier가 정해져 있다. 컴파일타임에 체크함
        ) {
            Text(text = "Alfred Sisley", fontWeight = FontWeight.Bold)

            // 컴포지션 트리에 명시적으로 값을 넘기게 해주는 프로바이더
            // 자식으로 있는 컴포저블의 불투명도 설정하기 위해 LocalContentAlpha 사용
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(text = "3 minutes ago", style = MaterialTheme.typography.body2)
            }
        }
    }

}

@Preview
@Composable
fun PhotographerCardPreview() {
    LayoutsCodelabTheme {
        PhotographerCard()
    }
}