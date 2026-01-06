package hu.bme.ait.clubhub.ui.screen.writeposts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import hu.bme.ait.clubhub.data.Post


sealed interface WritePostUiState {
    object Init : WritePostUiState
    object LoadingPostUpload : WritePostUiState
    object PostUploadSuccess : WritePostUiState
    data class ErrorDuringPostUpload(val error: String?) : WritePostUiState
}


class WritePostsViewModel : ViewModel() {

    private var _markerPosition = mutableStateOf<LatLng?>(null)


    fun getMarkerPosition(): LatLng? {
        return _markerPosition.value
    }

    fun setMarkerPosition(latLng: LatLng) {
        _markerPosition.value = latLng
    }

    fun clearMarker() {
        _markerPosition.value = null
    }

    companion object {
        const val COLLECTION_POSTS = "posts"
    }

    var writePostUiState: WritePostUiState
            by mutableStateOf(WritePostUiState.Init)

    private lateinit var auth: FirebaseAuth

    init {
        auth = Firebase.auth
    }

    fun uploadPost(title: String, postBody: String, times: String) {
        writePostUiState = WritePostUiState.LoadingPostUpload

        val formattedDate = java.text.SimpleDateFormat(
            "yyyy-MM-dd",
            java.util.Locale.getDefault()
        ).format(java.util.Date())

        val myPost = Post(
            postTitle = title,
            postBody = postBody,
            meetingTimes = times,
            uid = auth.currentUser!!.uid,
            author = auth.currentUser!!.email!!,
            postDate = formattedDate,  // human-readable string
            latitude = _markerPosition.value!!.latitude,
            longitude = _markerPosition.value!!.longitude
        )

        val postsCollection = FirebaseFirestore.getInstance().collection(COLLECTION_POSTS)
        postsCollection.add(myPost)
            .addOnSuccessListener {
                writePostUiState = WritePostUiState.PostUploadSuccess
            }
            .addOnFailureListener {
                writePostUiState = WritePostUiState.ErrorDuringPostUpload(it.message)
            }
    }
}