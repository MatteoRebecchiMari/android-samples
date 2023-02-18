package com.matteo.rebecchimari.composeclock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.matteo.rebecchimari.composeclock.ui.theme.ComposeClockTheme

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
    Box(modifier = Modifier.padding(10.dp),
        contentAlignment = Alignment.Center
        ) {
        ClockDialStateless()
        ClockHandHour()
    }
}

@Composable
fun ClockDialStateless(){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(ratio = 1f)
            .clip(CircleShape)
            .background(Color.Green)
            .border(
                width = 2.dp,
                color = Color.Black,
                shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        // Ticks
        for (i in 0..11){
            Tick(i)
        }
        // Dial (cover ticks)
        Box(
            Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .aspectRatio(ratio = 1f)
                .clip(CircleShape)
                .background(Color.Green)
        )
    }
}

@Composable
fun Tick(position: Int){
    Box(modifier = Modifier
        .fillMaxHeight()
        .width(2.dp)
        .rotate(360f / 12 * position)
        .clip(RectangleShape)
        .background(Color.Black)
    )
}

@Composable
fun ClockHandHour(modifier: Modifier = Modifier){
    Box(modifier = modifier
        .fillMaxHeight(0.3f)
        .width(10.dp)
        .clip(RectangleShape)
        .background(Color.Black)
    )
}

@Preview(showBackground = true, device = "id:pixel_5")
@Composable
fun DefaultPreview() {
    ComposeClockTheme {
        ClockStateful()
    }
}