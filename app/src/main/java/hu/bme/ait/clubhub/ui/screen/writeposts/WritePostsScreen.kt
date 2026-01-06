package hu.bme.ait.clubhub.ui.screen.writeposts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import hu.bme.ait.clubhub.R


@Composable
fun WritePostsScreen(
    modifier: Modifier = Modifier,
    viewModel: WritePostsViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    LaunchedEffect(viewModel.writePostUiState) {
        if(viewModel.writePostUiState is WritePostUiState.PostUploadSuccess){
            onNavigateBack()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(10.dp)
    ) {
        val context = LocalContext.current

        val cameraState = rememberCameraPositionState {
            CameraPosition.fromLatLngZoom(
                LatLng(47.4979, 19.0402), 10f
            )
        }
        var uiSettings by remember {
            mutableStateOf(
                MapUiSettings(
                    zoomControlsEnabled = true,
                    zoomGesturesEnabled = true
                )
            )
        }
        var mapProperties by remember {
            mutableStateOf(
                MapProperties(
                    mapType = MapType.NORMAL,
                    isTrafficEnabled = false,
                    mapStyleOptions = MapStyleOptions.loadRawResourceStyle(
                        context,
                        R.raw.mapstyle
                    )
                )
            )
        }

        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(
                LatLng(47.4979, 19.0402),
                12f
            )
        }

        GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            cameraPositionState = cameraPositionState,
            uiSettings = uiSettings,
            properties = mapProperties,
            onMapClick = { latLng ->
                viewModel.setMarkerPosition(latLng)
                val cameraPostion = CameraPosition.Builder()
                    .target(latLng)
                    .build()
                cameraState.move(
                    CameraUpdateFactory.newCameraPosition(cameraPostion)
                )
            }
        ) {
            viewModel.getMarkerPosition()?.let { position ->
                Marker(
                    state = MarkerState(position = position),
                    title = stringResource(R.string.selected_location)
                )
            }
        }

        var postTitle by remember { mutableStateOf("") }
        var postBody by remember { mutableStateOf("") }
        var postTimes by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(10.dp)
        ) {
            Text(stringResource(R.string.add_the_location_to_your_activity_above), color = Color.White)

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = postTitle,
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(color = Color.White),
                label = { Text(text = stringResource(R.string.post_title), color = Color.White) },
                onValueChange = { postTitle = it }
            )
            OutlinedTextField(
                value = postBody,
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(color = Color.White),
                label = { Text(text = stringResource(R.string.post_body), color = Color.White) },
                onValueChange = { postBody = it },
                minLines = 5,
                maxLines = 13,
            )
            OutlinedTextField(
                value = postTimes,
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(color = Color.White),
                label = { Text(text = stringResource(R.string.enter_the_meeting_times), color = Color.White) },
                onValueChange =  {postTimes = it}
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedButton(
                    onClick = {
                        val marker = viewModel.getMarkerPosition()
                        if(marker == null) {
                            android.widget.Toast.makeText(
                                context,
                                context.getString(R.string.please_select_a_location_on_the_map_before_sending_your_post), //this works even with error
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            viewModel.uploadPost(postTitle, postBody, postTimes)
                        }
                    }
                ) {
                    Text(stringResource(R.string.send), color = Color.White)
                }
                OutlinedButton(onClick = {onNavigateBack()}) {
                    Text(stringResource(R.string.cancel), color = Color.White)
                }
            }

            when (viewModel.writePostUiState) {
                is WritePostUiState.LoadingPostUpload -> CircularProgressIndicator()
                is WritePostUiState.PostUploadSuccess -> Text(text = stringResource(R.string.post_uploaded), color = Color.White)
                is WritePostUiState.ErrorDuringPostUpload ->
                    Text(
                        text = "${(viewModel.writePostUiState as WritePostUiState.ErrorDuringPostUpload).error}",
                        color = Color.White
                    )
                else -> {}
            }
        }
    }
}