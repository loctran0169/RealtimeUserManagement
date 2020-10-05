package vn.edu.uit.realtimeuseraccountmanager.ui.fragments.profile

import android.view.View

interface FragmentProfileListener{
    fun onBackPressed(view : View)
    fun onAcceptPress(view : View)
    fun onBirthDayPress(view : View)
    fun onGenderPress(view : View)
    fun onChoosePhotoPressed(view : View)
}
 interface OnBackPressedListener {
    fun onBackPressed()
}