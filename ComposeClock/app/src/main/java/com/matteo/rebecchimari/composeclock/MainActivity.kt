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
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.matteo.rebecchimari.composeclock.ui.theme.ComposeClockTheme
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.*

class MainActivity : ComponentActivity() {

    // Kotlin like Flutter (Dart)
    // has removed the "new" keyword
    val timerVM: TimerVM = TimerVM()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ClockClockPage(timerVM)
        }
    }

    override fun onStart() {
        super.onStart()
        timerVM.start()
    }

    override fun onPause() {
        super.onPause()
        timerVM.stop()
    }

}

@Preview(showBackground = true, device = "id:pixel_5")
@Composable
fun DefaultPreview() {
    ClockClockPage(TimerVM())
}


@Composable
fun ClockClockPage(timerVM: TimerVM) {

    ComposeClockTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            Column() {
                Box(Modifier.padding(10.dp)){
                    ClockStateful(timerVM)
                }
            }
        }
    }

}

@Composable
fun ClockStateful(timerVM: TimerVM) {

    val secondsState: State<Int> = timerVM.seconds.collectAsState(initial = 0)
    val minuteState: State<Int> = timerVM.minutes.collectAsState(initial = 0)
    val hoursState: State<Int> = timerVM.hours.collectAsState(initial = 0)

    ClockDialStateless()
    {
        // Seconds
        ClockHand((360f/60f * secondsState.value),  0.9f, 2, Color.Green)
        // Minutes
        ClockHand((360f/60f * minuteState.value), 0.6f, 5, Color.Red)
        // Hours
        ClockHand((360f/12f * (hoursState.value % 12)), 0.4f, 10, Color.Black)
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
            .rotate((360f / 12f) * (position))
            .padding(0.dp, 4.dp)
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
    val rotation = 0f //(360f / maxPosition) * position

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
                .rotate(-1 * rotation)
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
    rotation: Float,
    heightPercentage: Float = 0.3f,
    width: Int = 10,
    color: Color = Color.Black,
    modifier: Modifier = Modifier
){
    val shape = RoundedCornerShape((width*0.5).dp)
    Column(
        modifier = modifier
            .rotate(rotation)
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

class TimerVM : ViewModel() {

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
    private fun startTimer() {

        timerJob = viewModelScope.launch {

            println("Timer started")

            isRunning = true

            while (isRunning){

                // Wait 1 second
                delay(1000)

                val calendar = Calendar.getInstance()

                val s: Int = calendar.get(Calendar.SECOND)
                val m: Int = calendar.get(Calendar.MINUTE)
                val h: Int = calendar.get(Calendar.HOUR)

                seconds.emit(s)
                minutes.emit(m)
                hours.emit(h)

                println("Hours: $h")
                println("Minutes: $m")
                println("Seconds: $s")

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

     fun stop(): Boolean {

        if(timerJob == null){
            return false
        }

        viewModelScope.launch {

            isRunning = false;
            timerJob!!.join()

        }

        return true;

    }

}