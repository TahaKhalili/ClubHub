package hu.bme.ait.clubhub.ui.screen.posts

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import hu.bme.ait.clubhub.data.Post
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import hu.bme.ait.clubhub.R

private enum class Screen {
    POSTS,
    MAP
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostsScreen(
    viewModel: PostsViewModel = viewModel(),
    onNewPostsClick: () -> Unit = {},
    onPostClick: (String) -> Unit = {}
) {
    val postListState = viewModel.postsList().collectAsState(
        initial = MessagesUIState.Init
    )
    val context = LocalContext.current
    var currentScreen by remember { mutableStateOf(Screen.POSTS) }
    val uiSettings by remember { mutableStateOf(MapUiSettings(zoomControlsEnabled = true, zoomGesturesEnabled = true)) }
    val mapProperties by remember {
        mutableStateOf(
            MapProperties(
                mapType = MapType.NORMAL,
                isTrafficEnabled = false,
                mapStyleOptions = MapStyleOptions.loadRawResourceStyle(context, R.raw.mapstyle)
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(stringResource(R.string.clubs_forum), color = Color.White)
                        Spacer(modifier = Modifier.width(20.dp))
                        Text(stringResource(R.string.add_your_activity_location), color = Color.Gray, fontSize = 15.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black
                ),
                actions = {
                    IconButton(
                        onClick = { onNewPostsClick() }
                    ) {
                        Icon(
                            Icons.Outlined.Add,
                            contentDescription = stringResource(R.string.info),
                            tint = Color.White
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar (
                containerColor = Color.Black
            ){
                IconButton(onClick = {currentScreen = Screen.POSTS},
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.AutoMirrored.Filled.List,
                        contentDescription = stringResource(R.string.posts),
                        tint = if (currentScreen == Screen.POSTS) Color.White else Color.Gray)
                }
                IconButton(onClick = {currentScreen = Screen.MAP},
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.Map,
                        contentDescription = stringResource(R.string.map),
                        tint = if (currentScreen == Screen.MAP) Color.White else Color.Gray
                    )
                }
            }
        },
        containerColor = Color.Gray
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            when (postListState.value) {
                is MessagesUIState.Init -> {
                    Text(stringResource(R.string.init))
                }
                is MessagesUIState.Loading -> {
                    CircularProgressIndicator()
                }
                is MessagesUIState.Error -> {
                    Text(stringResource(R.string.error))
                }
                is MessagesUIState.Success -> {
                    val postsWithId = (postListState.value as MessagesUIState.Success).postList
                    when (currentScreen) {
                        Screen.POSTS -> {
                            LazyColumn {
                                items(postsWithId) { postsWithId ->
                                    PostCard(
                                        post = postsWithId.post,
                                        onRemoveItem = { viewModel.deletePost(postsWithId.postId) },
                                        currentUserId = FirebaseAuth.getInstance().currentUser!!.uid,
                                        onPostClick = {onPostClick(postsWithId.postId)}
                                    )
                                }
                            }
                        }
                        Screen.MAP -> {
                            val cameraPositionState = rememberCameraPositionState {
                                position = CameraPosition.fromLatLngZoom(LatLng(47.4979, 19.0402), 10f)
                            }
                            GoogleMap(
                                modifier = Modifier.fillMaxSize(),
                                cameraPositionState = cameraPositionState,
                                uiSettings = uiSettings,
                                properties = mapProperties
                            ) {
                                postsWithId.forEach { postWithId ->
                                    val post = postWithId.post
                                    val postId = postWithId.postId
                                    if (post.latitude != null && post.longitude != null) {
                                        Marker(
                                            state = MarkerState(
                                                position = LatLng(
                                                    post.latitude!!,
                                                    post.longitude!!
                                                )
                                            ),
                                            title = post.postTitle,
                                            snippet = post.postBody,
                                            onInfoWindowClick = {onPostClick(postId)}
                                        )
                                    }
                                }
                            }
                        }
                    }

                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostCard(
    post: Post,
    onRemoveItem: () -> Unit = {},
    currentUserId: String = "",
    onPostClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Card(
        colors = CardDefaults.cardColors(
//            containerColor = Color(0xFFB0B0B0) // Grey card color
            containerColor = Color.Black
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        ),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable(onClick = onPostClick)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {

            val context = LocalContext.current
            val uiSettings by remember { mutableStateOf(MapUiSettings(zoomControlsEnabled = true, zoomGesturesEnabled = true)) }
            val mapProperties by remember {
                mutableStateOf(
                    MapProperties(
                        mapType = MapType.NORMAL,
                        isTrafficEnabled = false,
                        mapStyleOptions = MapStyleOptions.loadRawResourceStyle(context, R.raw.mapstyle)
                    )
                )
            }

            val cameraPositionState = rememberCameraPositionState {
                val target = if (post.latitude != null && post.longitude != null) {
                    LatLng(post.latitude!!, post.longitude!!)
                } else {
                    LatLng(47.4979, 19.0402) // Budapest fallback
                }
                position = CameraPosition.fromLatLngZoom(target, 12f)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp) // fixed height
                    .clip(RoundedCornerShape(5.dp))
                    .border(2.dp, Color.DarkGray, RoundedCornerShape(5.dp))
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    uiSettings = uiSettings,
                    properties = mapProperties
                ) {
                    if (post.latitude != null && post.longitude != null) {
                        val markerState = remember { MarkerState(LatLng(post.latitude!!, post.longitude!!)) }
                        Marker(
                            state = markerState,
                            title = post.postTitle,
                            snippet = stringResource(
                                R.string.lat_lng,
                                post.latitude ?: 0.0,
                                post.longitude ?: 0.0
                            )
                        )
                    }
                } 
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(8.dp)
                    .verticalScroll(scrollState)
            ) {
                Column {
                    Text(text = stringResource(R.string.date_posted) + post.postDate, style = MaterialTheme.typography.bodySmall, color = Color.White)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = post.postTitle, fontSize = 25.sp, color = Color.White)
                    Text(text = post.postBody, fontSize = 15.sp, color = Color.White)
                }
            }

            if (currentUserId == post.uid) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = stringResource(R.string.delete),
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 8.dp)
                        .clickable { onRemoveItem() },
                    tint = Color.Red
                )
            }
        }
    }
}