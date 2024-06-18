package id.jostudios.penielcommunity.Objects

import com.google.firebase.auth.FirebaseUser
import id.jostudios.penielcommunity.Models.FirebaseModels.CredentialModel
import id.jostudios.penielcommunity.Models.FirebaseModels.UserModel

object GlobalState {
    public var isAuth: Boolean = false;
    public var isLogin: Boolean = false;

    public var currentCredential: CredentialModel? = null;
    public var currentUser: UserModel? = null;
    public var firebaseUser: FirebaseUser? = null;

    public var token: String = "";
}