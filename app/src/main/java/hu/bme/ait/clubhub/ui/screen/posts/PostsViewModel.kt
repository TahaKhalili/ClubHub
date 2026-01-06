package hu.bme.ait.clubhub.ui.screen.posts

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import hu.bme.ait.clubhub.data.Post
import hu.bme.ait.clubhub.data.PostWithId
import hu.bme.ait.clubhub.ui.screen.writeposts.WritePostsViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

sealed interface MessagesUIState {
    object Init : MessagesUIState
    object Loading : MessagesUIState
    data class Success(val postList: List<PostWithId>) : MessagesUIState
    data class Error(val error: String?) : MessagesUIState
}

class PostsViewModel: ViewModel() {

    fun postsList() = callbackFlow {
        val snapshotListener = Firebase.firestore.collection(
            WritePostsViewModel.COLLECTION_POSTS)
            .orderBy("postDate", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                val response = if (snapshot != null) {
                    val postList = snapshot.toObjects(Post::class.java)
                    val postWithIdList = mutableListOf<PostWithId>()

                    postList.forEachIndexed { index, post ->
                        postWithIdList.add(
                            PostWithId(
                                snapshot.documents[index].id, post))
                    }

                    MessagesUIState.Success(postWithIdList)

                } else {
                    MessagesUIState.Error(e?.localizedMessage)
                }

                trySend(response)
            }
        awaitClose {
            snapshotListener.remove()
        }
    }

    fun deletePost(postId: String) {
        FirebaseFirestore.getInstance().collection(
            WritePostsViewModel.COLLECTION_POSTS
        ).document(postId).delete()
    }
}