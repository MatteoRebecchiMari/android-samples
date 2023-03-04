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
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import java.util.*
import java.util.Collections.rotate
import java.util.concurrent.Flow

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ClockClockPage()
        }
    }
}

@Preview(showBackground = true, device = "id:pixel_5")
@Composable
fun DefaultPreview() {
    ClockClockPage()
}


@Composable
fun ClockClockPage() {

    ComposeClockTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            Column() {
                Box(Modifier.padding(10.dp)){
                    ClockStateful()
                }
            }
        }
    }

}

@Composable
fun ClockStateful() {

    // Kotlin like Flutter (Dart)
    // has removed the "new" keyword
    val timer: Timer = Timer()

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
        for (i in 0..5){
            Tick(i,ticksColor)
        }

        // Dial (cover ticks)
        /*Box(
            Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .aspectRatio(ratio = 1f)
                .clip(CircleShape)
                .background(dialColor)
        )*/

        // Ticks numbers
        for (i in 12 downTo 1){
            TickNumber(i-12, 12, "$i")
        }


        // Content
        content()

    }
}

@Composable
fun Tick(position: Int, color: Color = Color.Black,
         modifier: Modifier = Modifier){

    val width = 5;
    val height: Float = if(position % 3 == 0) 12f else 8f;

    val shape = RoundedCornerShape((width*0.5).dp)
    Column(
        modifier = modifier
            .fillMaxHeight(1f)
            .width((width).dp)
            .rotate(360f / 12 * position)
            .padding(0.dp,4.dp)
    ) {

        Box(modifier = modifier
            //.weight(0.5f) // Weight to split the total height
            .fillMaxWidth()
            .height((height).dp)
            .width((width).dp)
            .clip(shape)
            .background(color)
        )
        Box(modifier = modifier
            .weight(1f) // Weight to split the total height
            .fillMaxWidth()
            .clip(shape)
            .background(Color.Transparent)
        )
        Box(modifier = modifier
            //.weight(0.5f) // Weight to split the total height
            .fillMaxWidth()
            .height((height).dp)
            .width((width).dp)
            .clip(shape)
            .background(color)
        )
    }

}

@Composable
fun TickNumber(
    position: Int,
    maxPosition: Int,
    text: String,
    color: Color = Color.Black
){
    val rotation = 360f / maxPosition * position


    Column ( modifier = Modifier
        .fillMaxHeight()
        .rotate(rotation),
        verticalArrangement = Arrangement.Top) {

        Box(modifier = Modifier
            .height(14.dp)
            .background(Color.Transparent)
        )

        // Ternary operator:
        // like C# float fontSize = (position % 3 == 0) ? 27f*1.3f : 27*1f;
        val bigger = 27f*1.3f;
        val smaller = 27*1f;
        val fontSize: Float = if(position % 3 == 0) bigger else smaller;
        val fontWeight: FontWeight? = if(position % 3 == 0) FontWeight.Bold else null;

        Text(
            modifier = Modifier
                .rotate(-1*rotation)
                .padding(0.dp),
            text = text,
            color = color,
            fontSize = (fontSize).sp,
            textAlign = TextAlign.Center,
            fontWeight = fontWeight
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

class Timer() {

    val seconds = MutableSharedFlow<Int>();
    val minutes = MutableSharedFlow<Int>();
    val hours = MutableSharedFlow<Int>();

    init {
        runBlocking {
            launch {
                val calendar = Calendar.getInstance()
                seconds.emit(calendar.get(Calendar.SECOND))
                minutes.emit(calendar.get(Calendar.MINUTE))
                hours.emit(calendar.get(Calendar.HOUR))
            }
        }
    }

    private var isRunning: Boolean = false;
    private var timerJob: Job? = null

    // runBlocking method blocks the current thread for waiting
    // while coroutineScope just suspends

    //private fun startTimer() = runBlocking {
    private fun startTimer() = runBlocking {

        timerJob = launch {

            println("Timer started")

            isRunning = true

            while (isRunning){

                // Wait 1 second
                delay(1000)

                val calendar = Calendar.getInstance()
                seconds.emit(calendar.get(Calendar.SECOND))
                minutes.emit(calendar.get(Calendar.MINUTE))
                hours.emit(calendar.get(Calendar.HOUR))

            }

            println("Timer stopped")
        }

    }
    
    fun start(): Boolean {

        if(isRunning){
            return false
        }

        startTimer()

        return true;

    }

    suspend fun stop(): Boolean {

        if(timerJob == null){
            return false
        }

        isRunning = false;

        timerJob!!.join()

        return true;

    }

}