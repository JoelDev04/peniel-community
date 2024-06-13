package id.jostudios.penielcommunity.Models.FirebaseModels

import id.jostudios.penielcommunity.Enums.Groups
import id.jostudios.penielcommunity.Enums.Permissions
import id.jostudios.penielcommunity.Enums.Roles

data class UserModel(
    var id: Long,
    var name: String,

    var displayName: String,

    var bornDate: Long = 0,
    var phoneNumber: String = "",
    var emailAddress: String = "",

    var permissions: MutableList<Permissions> = mutableListOf(Permissions.ViewApp, Permissions.ViewFeed),

    var role: Roles = Roles.Guest,
    var group: MutableList<Groups> = mutableListOf(Groups.Jemaat),

    var profilePicture: String = "blank.png"
)
