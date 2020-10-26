package vn.edu.uit.realtimeuseraccountmanager.ui.adapter

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import vn.edu.uit.realtimeuseraccountmanager.User

@BindingAdapter("bind:data")
fun setItems(view: RecyclerView, data: List<User>?) {
    if (view.adapter == null)
        view.adapter = AdapterUser(view.context, data ?: emptyList())
    else
        (view.adapter as AdapterUser).updateData(data ?: emptyList())
}