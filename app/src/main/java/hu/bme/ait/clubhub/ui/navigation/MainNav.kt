package hu.bme.ait.clubhub.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object LoadingRoute: NavKey

@Serializable
data object SigninRoute: NavKey

@Serializable
data object PostsRoute: NavKey

@Serializable
data object WritePostsRoute: NavKey

@Serializable
data class ClubDetailRoute (val postId: String): NavKey