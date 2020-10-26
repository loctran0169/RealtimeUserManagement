package vn.edu.uit.realtimeuseraccountmanager.ui.fragments.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main.*
import vn.edu.uit.realtimeuseraccountmanager.R
import vn.edu.uit.realtimeuseraccountmanager.User
import vn.edu.uit.realtimeuseraccountmanager.Utils.IManagement
import vn.edu.uit.realtimeuseraccountmanager.databinding.FragmentMainBinding
import vn.edu.uit.realtimeuseraccountmanager.ui.MainViewModel
import java.util.*
import kotlin.collections.ArrayList

class FragmentMain : Fragment(), FragmentMainListener, IManagement {

    private val viewModel: MainViewModel by lazy {
        ViewModelProviders.of(this@FragmentMain)
            .get(MainViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentMainBinding.inflate(inflater, container, false)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        binding.listener = this@FragmentMain
        binding.viewModel = viewModel
        binding.lifecycleOwner = this@FragmentMain
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerViewUser()

        searchview_name.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrEmpty())
                    searchInListUser(newText)
                else
                    viewModel.listUser.value = viewModel.users
                return true
            }
        })

        viewModel.listUser.observe(this@FragmentMain.viewLifecycleOwner, Observer {

        })

        viewModel.isLoadingListUser.observe(this@FragmentMain.viewLifecycleOwner, Observer {
            if (it)
                hideBackgroundLoading()
        })
    }

    private fun initRecyclerViewUser() {
        recyclerView_user.run {
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun searchInListUser(name: String) {
        val list = ArrayList<User>()
        for (i in viewModel.listUser.value!!) {
            if (i.name!!.toLowerCase(Locale.ROOT).contains(name))
                list.add(i)
        }
        viewModel.listUser.value = list
    }

    override fun onButtonAddPressed(view: View) {
        frmMain.findNavController().navigate(FragmentMainDirections.actionFragmentMainToFragmentProfile("", resources.getString(R.string.add)))
    }

    private fun showBackgroundLoading(message: String?) {
        view_wait_for_loading.visibility = View.VISIBLE
        textview_message.text = message
    }

    private fun hideBackgroundLoading() {
        view_wait_for_loading.visibility = View.GONE
    }
}