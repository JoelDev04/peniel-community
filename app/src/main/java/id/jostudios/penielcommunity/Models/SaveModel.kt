package id.jostudios.penielcommunity.Models

import id.jostudios.penielcommunity.Models.FirebaseModels.CredentialModel
import id.jostudios.penielcommunity.Models.FirebaseModels.UserModel

data class SaveModel(
    var token: String = "",
    var user: UserModel? = null,
    var credential: CredentialModel? = null,
    var sessionID: String = ""
);
