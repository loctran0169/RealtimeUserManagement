package vn.edu.uit.realtimeuseraccountmanager.data.model

import android.net.Uri

/*
* data response when upload image to firebase storage
* */
data class UploadImageResponse(
    var status: Boolean,
    var uri: Uri?
)