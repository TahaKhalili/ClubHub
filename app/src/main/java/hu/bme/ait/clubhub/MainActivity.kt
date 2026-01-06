package hu.bme.ait.clubhub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.scene.rememberSceneSetupNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import hu.bme.ait.clubhub.ui.navigation.ClubDetailRoute
import hu.bme.ait.clubhub.ui.navigation.LoadingRoute
import hu.bme.ait.clubhub.ui.navigation.PostsRoute
import hu.bme.ait.clubhub.ui.navigation.SigninRoute
import hu.bme.ait.clubhub.ui.navigation.WritePostsRoute
import hu.bme.ait.clubhub.ui.screen.club.ClubDetailScreen
import hu.bme.ait.clubhub.ui.screen.loading.LoadingScreen
import hu.bme.ait.clubhub.ui.screen.signin.SigninScreen
import hu.bme.ait.clubhub.ui.screen.posts.PostsScreen
import hu.bme.ait.clubhub.ui.screen.writeposts.WritePostsScreen
import hu.bme.ait.clubhub.ui.theme.ClubHubTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClubHubTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavGraph(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun NavGraph(modifier: Modifier) {
    val backStack = rememberNavBackStack(LoadingRoute)

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        onBack = {backStack.removeLastOrNull()},
        entryDecorators = listOf(
            rememberSceneSetupNavEntryDecorator(),
            rememberSavedStateNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider  = entryProvider {
            entry<LoadingRoute> {
                LoadingScreen({
                    backStack.add(SigninRoute)
                })
            }
            entry<SigninRoute> {
                SigninScreen(
                    onSigninSuccess = {
                        backStack.add(PostsRoute)
                    }
                )
            }
            entry<PostsRoute> {
                PostsScreen(
                    onNewPostsClick = {
                        backStack.add(WritePostsRoute)
                    },
                    onPostClick = { postId ->
                        backStack.add(ClubDetailRoute(postId))
                    }
                )
            }
            entry<WritePostsRoute> {
                WritePostsScreen(
                    onNavigateBack = {
                        backStack.removeLastOrNull()
                    }
                )
            }
            entry<ClubDetailRoute> { entry ->
                ClubDetailScreen(
                    onNavigateBack = {
                        backStack.removeLastOrNull()
                    },
                    postId = entry.postId
                )
            }
        }
    )
}


