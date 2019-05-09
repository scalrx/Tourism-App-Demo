package io.github.scalrx.floridatourism

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import io.github.scalrx.floridatourism.StoreAdapter.StoreViewHolder


import java.util.ArrayList

class StoreAdapter(private val entries: ArrayList<StoreEntry>, private val onStoreListener: OnStoreListener) : RecyclerView.Adapter<StoreViewHolder>() {
    class StoreViewHolder(itemView: View, var onStoreListener: OnStoreListener) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var logo: ImageView
        var address: TextView
        var phoneNumber: TextView

        init {
            logo = itemView.findViewById(R.id.storeLogo) as ImageView
            address = itemView.findViewById(R.id.addressLine)
            phoneNumber = itemView.findViewById(R.id.phoneLine)

            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            onStoreListener.onStoreClick(adapterPosition)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): StoreViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.store_entry, viewGroup, false)
        return StoreViewHolder(view, onStoreListener)
    }

    override fun onBindViewHolder(storeViewHolder: StoreViewHolder, i: Int) {
        val entry = entries[i]
        storeViewHolder.logo.setImageBitmap(entry.image)
        storeViewHolder.address.text = entry.address
        storeViewHolder.phoneNumber.text = entry.phoneNumber
    }

    override fun getItemCount(): Int {
        return entries.size
    }

    interface OnStoreListener {
        fun onStoreClick(i: Int)
    }
}