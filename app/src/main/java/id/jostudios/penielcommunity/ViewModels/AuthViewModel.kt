package id.jostudios.penielcommunity.ViewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import id.jostudios.penielcommunity.Helpers.AuthHelper
import id.jostudios.penielcommunity.Helpers.FirebaseHelper
import id.jostudios.penielcommunity.Objects.GlobalState
import id.jostudios.penielcommunity.Objects.System
import id.jostudios.penielcommunity.Helpers.EncryptorHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthViewModel: ViewModel() {
    private var authToken: MutableStateFlow<String> = MutableStateFlow("");

    // Getter \\
    public fun getAuthToken() = authToken.asStateFlow();

    // Setter \\
    public fun setAuthToken(token: String) {
        authToken.value = token;
        System.debug("Setting token : ${token}");
    }

    public fun validateToken(context: Context): Boolean {
        val decrypted = EncryptorHelper.Decrypt(authToken.value);

        if (decrypted == null) {
            //System.setToast(context, "Error while parsing token!");
            System.dialogMessageBox(context, "Error!", "Error while parsing token!");
            return false;
        }

        val tokenModel = AuthHelper.parseToken(decrypted);

        if (tokenModel == null) {
            //System.setToast(context, "Token is invalid!");
            System.dialogMessageBox(context, "Error!", "Token is invalid!");
            return false;
        }

        FirebaseHelper.initializeDB(context, tokenModel);

        GlobalState.token = authToken.value;
        GlobalState.isAuth = true;

        return true;
    }
}