package vn.edu.uit.realtimeuseraccountmanager.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import vn.edu.uit.realtimeuseraccountmanager.User
import vn.edu.uit.realtimeuseraccountmanager.Utils.IManagement
import java.util.*

class MainViewModel : ViewModel(), IManagement {
    val refUser = FirebaseDatabase.getInstance().reference.child(USERS_UPLOAD_FOLDER)

    var listUser = MutableLiveData<List<User>>().apply { value = mutableListOf() }
    var isLoadingListUser = MutableLiveData<Boolean>().apply { value = false }

    init {
        loadListUser()
    }

    private fun loadListUser() {
        refUser.keepSynced(true)
        refUser.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val list = ArrayList<User>()
                for (i in snapshot.children) {
                    val user = i.getValue(User::class.java)
                    user?.let { list.add(it) }
                }
                listUser.value = list
                isLoadingListUser.value = true
            }

        })
    }
}