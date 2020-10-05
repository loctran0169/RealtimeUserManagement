package vn.edu.uit.realtimeuseraccountmanager

import com.google.firebase.database.IgnoreExtraProperties
import java.io.Serializable

/*
* gender 0 = male, 1 = female
* */
@IgnoreExtraProperties
data class User(
    var id: String? = null,
    var name: String? = null,
    var birthday: Long? = null,
    var gender: Int? = null,
    var address: String? = null,
    var email: String? = null,
    var avatar: String? = null,
    val status: Boolean? = false
) : Serializable