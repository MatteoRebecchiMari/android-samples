package com.matteo.rebecchimari.composeclock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import com.matteo.rebecchimari.composeclock.ui.theme.ComposeClockTheme
import java.util.Collections.rotate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeClockTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    ClockStateful()
                }
            }
        }
    }
}

@Composable
fun ClockStateful() {

    ClockDialStateless()
    {
        // Seconds
        ClockHand( 0.9f, 2, Color.Green)
        // Minutes
        ClockHand( 0.7f, 5, Color.Red)
        // Hours
        ClockHand( 0.4f, 10, Color.Black)
    }

}

@Composable
fun ClockDialStateless(
    dialColor: Color = Color.White,
    ticksColor: Color = Color.Black,
    content: @Composable() () -> Unit
){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(ratio = 1f)
            .clip(CircleShape)
            .background(dialColor)
            .border(
                width = 2.dp,
                color = ticksColor,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    )
    {

        // Ticks
        for (i in 0..11){
            Tick(i,ticksColor)
        }

        // Dial (cover ticks)
        Box(
            Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .aspectRatio(ratio = 1f)
                .clip(CircleShape)
                .background(dialColor)
        )

        // Ticks numbers
        for (i in 12 downTo 1){
            TickNumber(i-12, 12, "$i")
        }


        // Content
        content()

    }
}

@Composable
fun Tick(position: Int, color: Color = Color.Black){
    Box(modifier = Modifier
        .fillMaxHeight()
        .width(2.dp)
        .rotate(360f / 12 * position)
        .clip(RectangleShape)
        .background(color)
    )
}

@Composable
fun TickNumber(
    position: Int,
    maxPosition: Int,
    text: String,
    color: Color = Color.Black
){
    val rotation = 360f / maxPosition * position

    fun GetFW(): FontWeight?
    {
        var fw: FontWeight? = null
        if(position % 3 == 0){
            fw = FontWeight.Bold
        }
        return fw
    }

    Column ( modifier = Modifier
        .fillMaxHeight()
        .rotate(rotation),
        verticalArrangement = Arrangement.Top) {

        Text(
            modifier = Modifier
                .rotate(-rotation)
                .padding(10.dp),
            text = text,
            color = color,
            fontSize = 27.sp,
            textAlign = TextAlign.Center,
            fontWeight = GetFW()
        )

    }

}

@Composable
fun ClockHand(
    heightPercentage: Float = 0.3f,
    width: Int = 10,
    color: Color = Color.Black,
    modifier: Modifier = Modifier
){
    val shape = RoundedCornerShape((width*0.5).dp)
    Column(
        modifier = modifier
            .fillMaxHeight(heightPercentage)
            .width((width).dp)
            .offset(0.dp, 5.dp)) {
        val totalHeight = IntrinsicSize.Max;
        Box(modifier = modifier
            .weight(0.5f) // Weight to split the total height
            .fillMaxWidth()
            .clip(shape)
            .background(color)
        )
        Box(modifier = modifier
            .weight(0.5f) // Weight to split the total height
            .fillMaxWidth()
            .clip(shape)
            .background(Color.Transparent)
        )
    }

}

@Preview(showBackground = true, device = "id:pixel_5")
@Composable
fun DefaultPreview() {
    ComposeClockTheme {
        ClockStateful()
    }
}