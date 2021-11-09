package com.thk.layoutscodelab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thk.layoutscodelab.ui.theme.LayoutsCodelabTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LayoutsCodelabTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Greeting("Android")
                }
            }
        }
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

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    LayoutsCodelabTheme {
        Greeting("Android")
    }
}