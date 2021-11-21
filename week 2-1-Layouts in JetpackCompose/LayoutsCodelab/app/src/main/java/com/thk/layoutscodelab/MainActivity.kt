package com.thk.layoutscodelab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Anchor
import androidx.compose.material.icons.filled.Savings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
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

val topics = listOf(
    "Arts & Crafts", "Beauty", "Books", "Business", "Comics", "Culinary",
    "Design", "Fashion", "Film", "History", "Maths", "Music", "People", "Philosophy",
    "Religion", "Social sciences", "Technology", "TV", "Writing"
)

@Composable
fun Chip(modifier: Modifier = Modifier, text: String) {
    Card(
        modifier = modifier,
        border = BorderStroke(color = Color.Black, width = Dp.Hairline),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = modifier.padding(start = 8.dp, top = 4.dp, end = 8.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier
                .size(16.dp, 16.dp)
                .background(color = MaterialTheme.colors.secondary))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = text)
        }
    }
}

@Preview
@Composable
fun ChipPreview() {
    LayoutsCodelabTheme {
        Chip(text = "Hi there")
    }
}

@Composable
fun StaggeredGrid(modifier: Modifier = Modifier, rows: Int = 3, content: @Composable () -> Unit) {
    Layout(modifier = modifier, content = content) { measurables, constraints ->
        val rowWidths = IntArray(rows) { 0 }        // 각 행의 width
        val rowHeights = IntArray(rows) { 0 }       // 각 행의 최대 height

        val placeables = measurables.mapIndexed { index, measurable ->

            // 각 children 측정 진행함
            val placeable = measurable.measure(constraints = constraints)

            val row = index % rows
            rowWidths[row] += placeable.width
            rowHeights[row] = Math.max(rowHeights[row], placeable.height)

            placeable   // return
        }

        // 각 행 중 가장 넓은 width가 grid의 전체 width가 됨
        val gridWidth = rowWidths.maxOrNull()?.coerceIn(constraints.minWidth.rangeTo(constraints.maxWidth)) ?: constraints.minWidth
        // 각 행의 가장 큰 childern의 height의 합이 전체 grid의 height
        val gridHeight = rowHeights.sumOf { it }.coerceIn(constraints.minHeight.rangeTo(constraints.maxHeight))

        // 이전 행의 y값과 높이를 더해서 다음 행의 y position 계산
        val rowY = IntArray(rows) { 0 }
        for (i in 1 until rows) {
            rowY[i] = rowY[i-1] + rowHeights[i-1]
        }



        layout(gridWidth, gridHeight) {
            val rowX = IntArray(rows) { 0 }

            placeables.forEachIndexed { index, placeable ->
                val row = index % rows

                placeable.placeRelative(x = rowX[row], y = rowY[row])

                rowX[row] += placeable.width    // 다음 아이템이 놓여야 할 x position으로 계속 갱신되다가, 마지막에는 row의 전체 width가 됨
            }

        }
    }
}

// Custom Composable
@Composable
fun MyOwnColumn(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Layout(content = content, modifier = modifier) { measurables, constraints ->
        // 측정된 children 에 대한 리스트
        val placeables = measurables.map { measurable ->
            measurable.measure(constraints)
        }

        // column 처럼 아이템을 쌓기 위해서 y 좌표가 쌓이는 아이템만큼 늘어나야 함
        var yPosition = 0

        // 레이아웃의 사이즈 지정. 부모만큼 크게할거라서 넘어온 constraints의 max값 사용
        layout(constraints.maxWidth, constraints.maxHeight) {
            // place children

            // 리스트니까 각각의 placeable 만큼 반복
            placeables.forEach { placeable ->
                placeable.placeRelative(x = 0, y = yPosition)

                // 그 다음 아이템이 놓일 y 좌표 계산
                yPosition += placeable.height
            }
        }
    }
}


// Custom Layout Modifier
fun Modifier.firstBaselineToTop(firstBaselineToTop: Dp) = this.then(    // TODO: 왜 this.then()을 쓰는지 찾아보기
    layout { measurable, constraints ->     // measurable = 측정 및 배치할 child, constraints = child의 넓이,높이의 최대/최소값

        // 딱 한번 child를 측정
        val placeable = measurable.measure(constraints = constraints)   // constraints 는 직접 만들어도 되고, 그냥 넘어오는거 넘겨도 되고

        // 컴포저블이 베이스라인을 가지는지 확인
        check(placeable[FirstBaseline] != AlignmentLine.Unspecified)    // 특정 alignment의 포지션을 가져와서 값이 없는지(Unspecified) 확인
        val firstBaseline = placeable[FirstBaseline]
        // TODO: Alignment의 position이라는게 어떻게 표현되는 Int 값인지??

        val placeableY = firstBaselineToTop.roundToPx() - firstBaseline     // 설정하고 싶은 padding 에서 baseline 위치만큼 뺌
        val height = placeable.height + placeableY      // placeable의 높이(child의 높이)에 계산한 padding 더하기

        // measure 단계가 끝났으니까, layout을 호출해서 사이즈를 계산하고 명시해줘야 함
        // 람다로 넘기는 placementBlock 안에 child를 위치시키기 위한 코드를 적어줘야 함
        layout(placeable.width, height) {
            // 이걸 호출해야 화면에 나타남. 호출하면 알아서 현재의 layoutDirection에 맞춰서 잘 포지셔닝 해줌
            placeable.placeRelative(0, placeableY)
        }
    }
)

@Preview
@Composable
fun TextWithPaddingToBaselinePreview() {    // 베이스라인부터 시작하는 패딩 적용
    LayoutsCodelabTheme {
        Text("Hi there!", Modifier.firstBaselineToTop(32.dp))
    }
}

@Preview
@Composable
fun TextWithNormalPaddingPreview() {    // 그냥 패딩 적용
    LayoutsCodelabTheme {
        Text("Hi there!", Modifier.padding(top = 32.dp))
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
        BodyContent(
            Modifier
                .padding(innerPadding)
                .padding(8.dp))
    }
}

@Composable
fun BodyContent(modifier: Modifier = Modifier) {
    StaggeredGrid(modifier, rows = 5) {
        for (topic in topics) {
            Chip(modifier = modifier.padding(8.dp), text = topic)
        }
    }

//    MyOwnColumn(modifier.padding(8.dp)) {
//        Text(text = "MyOwnColumn")
//        Text(text = "아이템을")
//        Text(text = "수직으로 표시한다")
//        Text(text = "내가 직접 만듬~~~")
//    }

//    Column(modifier = modifier) {
//        Text(text = "Hi there!")
//        Text(text = "Thanks for going through the Layouts codelab")
//    }
}

@Preview
@Composable
fun LayoutsCodelabPreview() {
    LayoutsCodelabTheme {
        BodyContent()
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