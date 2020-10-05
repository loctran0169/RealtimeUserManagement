package vn.edu.uit.realtimeuseraccountmanager.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chauthai.swipereveallayout.SwipeRevealLayout
import com.chauthai.swipereveallayout.ViewBinderHelper
import kotlinx.android.synthetic.main.activity_main.*
import vn.edu.uit.realtimeuseraccountmanager.R
import vn.edu.uit.realtimeuseraccountmanager.User
import vn.edu.uit.realtimeuseraccountmanager.ui.fragments.main.FragmentMainDirections

class AdapterUser(val context: Context, var items: List<User>) : RecyclerView.Adapter<AdapterUser.ViewHolder>() {

    private val viewBinderHelper = ViewBinderHelper()

    init {
        viewBinderHelper.setOpenOnlyOne(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_recycleview_user, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        viewBinderHelper.bind(holder.swiperevealUserItem, position.toString())

        Glide.with(holder.img_avatar_user_item).load(item.avatar).error(R.drawable.ic_no_avatar).into(holder.img_avatar_user_item)
        holder.textview_name_user_item.text = item.name
        holder.textview_email_user_item.text = item.email

        holder.btn_delete.setOnClickListener {
            val action = FragmentMainDirections.actionFragmentMainToFragmentProfile(item.id, context.resources.getString(R.string.delete))
            (context as FragmentActivity).frmMain.findNavController().navigate(action)
            viewBinderHelper.closeLayout(position.toString())
        }

        holder.layout_item_user.setOnClickListener {
            val action = FragmentMainDirections.actionFragmentMainToFragmentProfile(item.id!!, context.resources.getString(R.string.edit))
            (context as FragmentActivity).frmMain.findNavController().navigate(action)
        }
    }

    fun updateData(newItems: List<User>) {
        items = newItems;
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val swiperevealUserItem: SwipeRevealLayout = view.findViewById(R.id.swipeReveal_user_item)
        val btn_delete = view.findViewById<ImageButton>(R.id.btn_delete)
        val img_avatar_user_item = view.findViewById<ImageView>(R.id.img_avatar_user_item)
        val img_status_user = view.findViewById<ImageView>(R.id.img_status_user) // status if user online
        val layout_item_user = view.findViewById<LinearLayout>(R.id.layout_item_user)

        val textview_name_user_item = view.findViewById<TextView>(R.id.textview_name_user_item)
        val textview_email_user_item = view.findViewById<TextView>(R.id.textview_email_user_item)
    }
}