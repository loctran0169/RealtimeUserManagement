package vn.edu.uit.realtimeuseraccountmanager.Utils

interface IManagement {
    val ADD: String
        get() = "add"
    val EDIT: String
        get() = "edit"
    val DELETE: String
        get() = "delete"
    val FOLDER_UPLOAD: String
        get() = "images"
    val USERS_UPLOAD_FOLDER: String
        get() = "users"
}