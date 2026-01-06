package hu.bme.ait.clubhub.ui.screen.signin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await


sealed interface SigninUiState {
    object Init: SigninUiState
    object Loading: SigninUiState
    object RegisterSuccess: SigninUiState
    object SigninSuccess: SigninUiState
    data class Error(val errorMessage: String?): SigninUiState
}

class SigninViewModel : ViewModel() {

    var signinUiState: SigninUiState by mutableStateOf(SigninUiState.Init)

    private lateinit var auth: FirebaseAuth

    init {
        auth = Firebase.auth
    }

    fun registerUser(email: String, password: String) {
        signinUiState = SigninUiState.Loading
        try {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    signinUiState = SigninUiState.RegisterSuccess
                }
                .addOnFailureListener {
                    signinUiState = SigninUiState.Error(it.localizedMessage)
                }


        } catch (e:Exception) {
            signinUiState = SigninUiState.Error(e.localizedMessage)
            e.printStackTrace()
        }

    }

    suspend fun signinUser(email: String, password: String) : AuthResult? {
        signinUiState = SigninUiState.Loading
        try {
            val result = auth.signInWithEmailAndPassword(email,password).await()
            if (result.user != null) {
                signinUiState = SigninUiState.SigninSuccess
            } else {
                signinUiState = SigninUiState.Error("Sign-in failed")
            }

            return result
        } catch (e: Exception) {
            signinUiState = SigninUiState.Error(e.localizedMessage)
            e.printStackTrace()

            return null
        }
    }

}