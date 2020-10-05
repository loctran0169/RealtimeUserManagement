package vn.edu.uit.realtimeuseraccountmanager.ui.fragments.profile

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import vn.edu.uit.realtimeuseraccountmanager.User
import vn.edu.uit.realtimeuseraccountmanager.Utils.IManagement
import vn.edu.uit.realtimeuseraccountmanager.data.model.UploadImageResponse
import java.io.File
import java.util.*

class ViewModelProfileFactory(private val user_id: String?) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ViewModelProfile(user_id) as T
    }
}

class ViewModelProfile(user_id: String?) : ViewModel(), IManagement {
    var photoPath: Uri? = null
    var userData = MutableLiveData<User?>().apply { value = null }
    var uploadImageResponse = MutableLiveData<UploadImageResponse?>().apply { value = null }
    var saveUserResponse = MutableLiveData<Boolean?>().apply { value = null }
    var deleteUserResponse = MutableLiveData<Boolean?>().apply { value = null }

    private val refUser = FirebaseDatabase.getInstance().reference.child(USERS_UPLOAD_FOLDER)

    fun loadUser(user_id: String) {
        val ref = FirebaseDatabase.getInstance().reference.child(USERS_UPLOAD_FOLDER).child(user_id)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if (user != null) {
                    userData.value = user
                }
            }
        })
    }

    fun updateImageToFirebaseStorage(path: Uri) {
        val ref = FirebaseStorage.getInstance().getReference("/$FOLDER_UPLOAD/${File(path.toString()).name}")
        ref.putFile(path)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener {
                    uploadImageResponse.value = UploadImageResponse(true, it)
                }
            }
            .addOnFailureListener {
                uploadImageResponse.value = UploadImageResponse(false, null)
                println(it.stackTrace)
            }
    }

    fun saveUserToFirebaseDataBase(user: User, urlImage: String?) {
        val uuid = if (user.id == null) UUID.randomUUID().toString() else user.id
        val ref = FirebaseDatabase.getInstance().getReference("/$USERS_UPLOAD_FOLDER/$uuid")
        ref.setValue(User(uuid, user.name, user.birthday, user.gender, user.address, user.email))
        user.run {
            id = uuid
            if (urlImage != null) avatar = urlImage
        }
        ref.setValue(user)
            .addOnSuccessListener {
                saveUserResponse.value = true
            }
            .addOnFailureListener {
                saveUserResponse.value = false
                println(it.stackTrace)
            }
    }

    fun deleteUserToFirebaseDataBase(user_id: String) {
        refUser.child(user_id)
            .removeValue()
            .addOnSuccessListener {
                deleteUserResponse.value = true
            }
            .addOnFailureListener {
                deleteUserResponse.value = false
                println(it.stackTrace)
            }
    }
}