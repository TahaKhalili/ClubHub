package hu.bme.ait.clubhub.ui.screen.club

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import hu.bme.ait.clubhub.data.Post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// Sealed interface to represent the UI states
sealed interface ClubDetailUiState {
    object Loading : ClubDetailUiState
    data class Success(val post: Post) : ClubDetailUiState
    data class Error(val message: String?) : ClubDetailUiState
}

class ClubDetailViewModel(
    postId : String // The ViewModel receives the screen object
) : ViewModel() {

    private val _uiState = MutableStateFlow<ClubDetailUiState>(ClubDetailUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        // Use the postId from the screen object to fetch data
        fetchClubDetails(postId)
    }

    private fun fetchClubDetails(postId: String) {
        viewModelScope.launch {
            _uiState.value = ClubDetailUiState.Loading
            try {
                val document = FirebaseFirestore.getInstance()
                    .collection("posts")
                    .document(postId)
                    .get()
                    .await()

                val post = document.toObject(Post::class.java)
                if (post != null) {
                    _uiState.value = ClubDetailUiState.Success(post)
                } else {
                    _uiState.value = ClubDetailUiState.Error("Post not found.")
                }
            } catch (e: Exception) {
                _uiState.value = ClubDetailUiState.Error(e.localizedMessage)
            }
        }
    }
}