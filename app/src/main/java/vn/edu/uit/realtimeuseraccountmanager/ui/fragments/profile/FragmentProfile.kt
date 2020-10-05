package vn.edu.uit.realtimeuseraccountmanager.ui.fragments.profile

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_profile.*
import vn.edu.uit.realtimeuseraccountmanager.R
import vn.edu.uit.realtimeuseraccountmanager.User
import vn.edu.uit.realtimeuseraccountmanager.Utils.IManagement
import vn.edu.uit.realtimeuseraccountmanager.Utils.StringUtils
import vn.edu.uit.realtimeuseraccountmanager.Utils.TimeUtils
import vn.edu.uit.realtimeuseraccountmanager.databinding.FragmentProfileBinding
import java.util.*

class FragmentProfile : Fragment(), FragmentProfileListener, OnBackPressedListener, IManagement, StringUtils, TimeUtils {
    private val safeArgs: FragmentProfileArgs by navArgs()
    private val REQUEST_CODE_PICK_PHOTO = 123

    private val adapterGender: ArrayAdapter<String> by lazy {
        ArrayAdapter(requireContext(), R.layout.item_gender, getListGender())
    }
    private val viewModel: ViewModelProfile by lazy {
        ViewModelProviders.of(this@FragmentProfile, ViewModelProfileFactory(safeArgs.userId)).get(ViewModelProfile::class.java)
    }
    private var isEditInformation = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentProfileBinding.inflate(inflater, container, false)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        binding.lifecycleOwner = this@FragmentProfile
        binding.listener = this@FragmentProfile
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTask(safeArgs.task!!)
        edittext_gender.setAdapter(adapterGender)

        viewModel.uploadImageResponse.observe(this@FragmentProfile.viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it != null) {
                viewModel.uploadImageResponse.value = null
                if (it.status) {
                    showBackgroundLoading(resources.getString(R.string.saving_database))
                    viewModel.saveUserToFirebaseDataBase(getUserInformation(), it.uri.toString())
                } else {
                    hideBackgroundLoading()
                    Toast.makeText(requireContext(), resources.getString(R.string.upload_image_failure), Toast.LENGTH_LONG).show()
                }
            }
        })

        viewModel.saveUserResponse.observe(this@FragmentProfile.viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it != null) {
                hideBackgroundLoading()
                viewModel.saveUserResponse.value = null

                if (safeArgs.task == resources.getString(R.string.edit)) {// when edit user response
                    if (it) {
                        viewModel.photoPath = null
                        Toast.makeText(requireContext(), resources.getString(R.string.edit_user_success), Toast.LENGTH_LONG).show()
                        disableAllFieldProfile()
                        btn_accept.text = resources.getString(R.string.edit)
                    } else
                        Toast.makeText(requireContext(), resources.getString(R.string.edit_user_fail), Toast.LENGTH_LONG).show()
                } else { // when add user response
                    if (it) {
                        viewModel.photoPath = null
                        Toast.makeText(requireContext(), resources.getString(R.string.insert_user_success), Toast.LENGTH_LONG).show()
                        requireActivity().onBackPressed()
                    } else
                        Toast.makeText(requireContext(), resources.getString(R.string.insert_user_fail), Toast.LENGTH_LONG).show()
                }
            }
        })

        viewModel.deleteUserResponse.observe(this@FragmentProfile.viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it != null) {
                viewModel.deleteUserResponse.value = null
                hideBackgroundLoading()
                if (it) {
                    Toast.makeText(requireContext(), resources.getString(R.string.delete_user_success), Toast.LENGTH_LONG).show()
                    requireActivity().onBackPressed()
                } else
                    Toast.makeText(requireContext(), resources.getString(R.string.delete_user_fail), Toast.LENGTH_LONG).show()
            }
        })

        viewModel.userData.observe(this@FragmentProfile.viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it != null) {
                setUserInformation(it)
                hideBackgroundLoading()
            }
        })

    }

    override fun onBackPressed(view: View) {
        requireActivity().onBackPressed()
    }

    override fun onAcceptPress(view: View) {
        hideKeyBoard()
        when (safeArgs.task) {
            resources.getString(R.string.add) -> {
                if (checkFieldProfile()) {
                    showBackgroundLoading(resources.getString(R.string.uploading_image))
                    viewModel.updateImageToFirebaseStorage(viewModel.photoPath!!)
                }
            }
            resources.getString(R.string.edit) -> {
                if (isEditInformation) {
                    showBackgroundLoading(resources.getString(R.string.saving_database))
                    if (viewModel.photoPath == null) {// if not select photo
                        viewModel.saveUserToFirebaseDataBase(getUserInformation(), viewModel.userData.value?.avatar)
                    } else {
                        viewModel.updateImageToFirebaseStorage(viewModel.photoPath!!)
                    }
                    isEditInformation = false
                } else {
                    enableAllFieldProfile()
                    btn_accept.text = resources.getString(R.string.save)
                    isEditInformation = true
                }
            }
            resources.getString(R.string.delete) -> {
                AlertDialog.Builder(requireContext())
                    .setNegativeButton(resources.getString(R.string.no)) { _, i ->
                    }
                    .setPositiveButton(resources.getString(R.string.yes)) { _, i ->
                        showBackgroundLoading(resources.getString(R.string.deleteing))
                        viewModel.deleteUserToFirebaseDataBase(safeArgs.userId!!)
                    }.setTitle(resources.getString(R.string.delete_user))
                    .setMessage(resources.getString(R.string.message_delete_user))
                    .create().show()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBirthDayPress(view: View) {
        hideKeyBoard()
        val cal = Calendar.getInstance()
        DatePickerDialog(requireContext(),
            R.style.DatePickerTheme,
            DatePickerDialog.OnDateSetListener { _, year, month, day ->

                //set value date to edit text
                edittext_birthday.setText("$day/${month + 1}/$year")

            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
            .show()
    }

    override fun onGenderPress(view: View) {
        hideKeyBoard()
        edittext_gender.setAdapter(ArrayAdapter(requireContext(), R.layout.item_gender, getListGender()))
        edittext_gender.showDropDown()
    }

    override fun onChoosePhotoPressed(view: View) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_PICK_PHOTO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_PICK_PHOTO && resultCode == Activity.RESULT_OK && data != null) {
            viewModel.photoPath = data.data
            try {
                viewModel.photoPath?.let {
                    if (Build.VERSION.SDK_INT < 28) {
                        val bitmap = MediaStore.Images.Media.getBitmap(
                            requireActivity().contentResolver,
                            viewModel.photoPath
                        )
                        Glide.with(img_profile_avatar).load(bitmap).into(img_profile_avatar)
                    } else {
                        val source = ImageDecoder.createSource(requireActivity().contentResolver, viewModel.photoPath!!)
                        val bitmap = ImageDecoder.decodeBitmap(source)
                        Glide.with(img_profile_avatar).load(bitmap).into(img_profile_avatar)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getListGender(): List<String> {
        return listOf(resources.getString(R.string.male), resources.getString(R.string.female))
    }

    private fun getUserInformation(): User {
        return User(id = if (safeArgs.userId.isNullOrEmpty()) null else safeArgs.userId,
            name = edittext_name.text.toString(),
            birthday = dateTimeToTimeStampFromString(edittext_birthday.text.toString()),
            gender = if (edittext_gender.text.toString() == resources.getString(R.string.male)) 0 else 1,
            address = edittext_address.text.toString(),
            email = edittext_email.text.toString()
        )
    }

    private fun setUserInformation(user: User) {
        Glide.with(img_profile_avatar).load(user.avatar).error(R.drawable.ic_no_avatar).into(img_profile_avatar)
        edittext_name.setText(user.name)
        edittext_birthday.setText(getDateString(user.birthday!!))
        edittext_gender.setText(if (user.gender!! == 0) resources.getString(R.string.male) else resources.getString(R.string.female))
        edittext_address.setText(user.address)
        edittext_email.setText(user.email)
    }

    private fun hideKeyBoard() {
        val imm = this@FragmentProfile.context?.getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(this@FragmentProfile.activity?.currentFocus?.windowToken, 0)
    }

    //only check null all field
    private fun checkFieldProfile(): Boolean {
        when {
            viewModel.photoPath == null -> {
                Toast.makeText(requireContext(), resources.getString(R.string.empty_image), Toast.LENGTH_LONG).show()
                return false
            }
            edittext_name.isNullOrEmpty() -> {
                Toast.makeText(requireContext(), resources.getString(R.string.empty_name), Toast.LENGTH_LONG).show()
                return false
            }
            edittext_birthday.isNullOrEmpty() -> {
                Toast.makeText(requireContext(), resources.getString(R.string.empty_birthday), Toast.LENGTH_LONG).show()
                return false
            }
            edittext_gender.isNullOrEmpty() -> {
                Toast.makeText(requireContext(), resources.getString(R.string.empty_gender), Toast.LENGTH_LONG).show()
                return false
            }
            edittext_address.isNullOrEmpty() -> {
                Toast.makeText(requireContext(), resources.getString(R.string.empty_address), Toast.LENGTH_LONG).show()
                return false
            }
            edittext_email.isNullOrEmpty() -> {
                Toast.makeText(requireContext(), resources.getString(R.string.empty_email), Toast.LENGTH_LONG).show()
                return false
            }
            !isEmail(edittext_email.text.toString()) -> {
                Toast.makeText(requireContext(), resources.getString(R.string.wrong_email_format), Toast.LENGTH_LONG).show()
                return false
            }
        }
        return true
    }

    private fun EditText.isNullOrEmpty(): Boolean {
        return this.text.isNullOrEmpty()
    }

    private fun setTask(task: String) {
        btn_accept.text = task
        when (task) {
            resources.getString(R.string.add) -> {

            }
            resources.getString(R.string.edit) -> {
                disableAllFieldProfile()
                showBackgroundLoading(resources.getString(R.string.loading));
                viewModel.loadUser(safeArgs.userId!!)
            }
            resources.getString(R.string.delete) -> {
                disableAllFieldProfile()
                showBackgroundLoading(resources.getString(R.string.loading));
                viewModel.loadUser(safeArgs.userId!!)
            }
        }
    }

    private fun disableAllFieldProfile() {
        img_profile_avatar.isEnabled = false
        edittext_name.isFocusable = false
        edittext_birthday.isEnabled = false
        edittext_gender.isEnabled = false
        edittext_address.isFocusable = false
        edittext_email.isFocusable = false
    }

    private fun enableAllFieldProfile() {
        img_profile_avatar.isClickable = true
        img_profile_avatar.isEnabled = true

        edittext_name.isFocusable = true
        edittext_name.isFocusableInTouchMode = true

        edittext_birthday.isEnabled = true

        edittext_gender.isEnabled = true

        edittext_address.isFocusable = true
        edittext_address.isFocusableInTouchMode = true

        edittext_email.isFocusable = true
        edittext_email.isFocusableInTouchMode = true
    }

    private fun showBackgroundLoading(message: String?) {
        view_wait_for_loading.visibility = View.VISIBLE
        textview_message.text = message
    }

    private fun hideBackgroundLoading() {
        view_wait_for_loading.visibility = View.GONE
    }

    override fun onBackPressed() {
        requireActivity().onBackPressed()
    }

}