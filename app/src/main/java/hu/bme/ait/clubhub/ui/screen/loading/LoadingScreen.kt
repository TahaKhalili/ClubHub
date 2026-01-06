package hu.bme.ait.clubhub.ui.screen.loading

import android.os.Build.VERSION.SDK_INT
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import hu.bme.ait.clubhub.R
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds


@Composable
fun LoadingScreen(
    onTimeout : () -> Unit,
){
    LaunchedEffect(Unit) {
        delay(2.seconds)
        onTimeout()
    }

    val context = LocalContext.current
    val gifEnabledLoader = ImageLoader.Builder(context)
        .components {
            if ( SDK_INT >= 28 ) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }.build()

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        AsyncImage(
            modifier = Modifier.fillMaxSize(),
//            contentScale = ContentScale.Crop,
//            model = "https://media.giphy.com/media/v1.Y2lkPWVjZjA1ZTQ3MjRnaW5hMGhyZ3B2d3JpdmFsb3lhN3poY3AwMTRoczEyd2F3czFvaiZlcD12MV9naWZzX3NlYXJjaCZjdD1n/TKa7fQzChHylCQ89to/giphy.gif",
            model = "https://media.giphy.com/media/v1.Y2lkPTc5MGI3NjExa2VsMjM0aHNqOHd4aDUydTZzaXFqbjFhY2RlNG5qa3hjOTBzZjJ6cSZlcD12MV9zdGlja2Vyc19zZWFyY2gmY3Q9cw/TON1o1UFKu75yLOPY2/giphy.gif",
            imageLoader = gifEnabledLoader,
            contentDescription = stringResource(R.string.loadingicon)
        )

    }

}