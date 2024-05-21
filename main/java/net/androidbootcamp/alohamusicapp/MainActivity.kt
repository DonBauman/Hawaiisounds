package net.androidbootcamp.alohamusicapp

import android.media.MediaPlayer
import android.os.Bundle
import androidx.compose.material3.Text
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.compose.ui.layout.ContentScale
import net.androidbootcamp.alohamusicapp.ui.theme.AlohaMusicAppTheme
import java.io.IOException
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Color

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AlohaMusicAppTheme {
                SplashScreen()
            }
        }
    }

    @Composable
    fun SplashScreen() {
        var showSplash by remember { mutableStateOf(true) }

        LaunchedEffect(key1 = true) {
            delay(5000) // Delay for 5 seconds
            showSplash = false
        }

        if (showSplash) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Image(
                    painter = painterResource(id = R.drawable.hawaii),
                    contentDescription = "Hawaii Background",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop // Ensures the image covers the entire screen
                )
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Sounds of Hawaii",
                        fontSize = 30.sp,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        } else {
            MusicPlayer()
        }
    }


    @Composable
    fun MusicPlayer() {
        val context = LocalContext.current
        var playing by remember { mutableStateOf(false) }
        var currentTrack by remember { mutableStateOf(MusicTrack.Ukulele) }
        var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

        DisposableEffect(currentTrack) {
            // Initialize MediaPlayer with the current track
            mediaPlayer?.release() // Release any previous MediaPlayer
            mediaPlayer = MediaPlayer.create(context, currentTrack.resourceId).apply {
                setOnCompletionListener {
                    it.release()
                }
                setOnErrorListener { mp, what, extra ->
                    Log.e("MediaPlayer", "Error occurred: What $what, Extra $extra")
                    true
                }
            }
            onDispose {
                mediaPlayer?.release()
                mediaPlayer = null
            }
        }

        LaunchedEffect(playing, mediaPlayer) {
            if (playing) {
                mediaPlayer?.start()
            } else {
                mediaPlayer?.pause()
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SongButton(
                "Play Ukulele Song",
                R.drawable.manukulele,
                onClick = {
                    if (currentTrack != MusicTrack.Ukulele) {
                        currentTrack = MusicTrack.Ukulele
                    }
                    playing = !playing
                }
            )
            SongButton(
                "Play Drums Song",
                R.drawable.guydrums,
                onClick = {
                    if (currentTrack != MusicTrack.Drums) {
                        currentTrack = MusicTrack.Drums
                    }
                    playing = !playing
                }
            )
        }
    }


    @Composable
    fun SongButton(text: String, imageRes: Int, onClick: () -> Unit) {
        Card(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.clickable(onClick = onClick)
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = text,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.height(180.dp).fillMaxWidth()
                )
                Button(
                    onClick = onClick,
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                ) {
                    Text(text, fontSize = 18.sp)
                }
            }
        }
    }


    enum class MusicTrack(val resourceId: Int) {
        Ukulele(R.raw.ukulele),
        Drums(R.raw.drums)
    }

}