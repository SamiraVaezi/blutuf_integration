package net.grandcentrix.blutufintegration.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import net.grandcentrix.blutuf.core.api.Characteristic
import net.grandcentrix.blutuf.core.api.Service
import net.grandcentrix.blutufintegration.databinding.ListRowServiceBinding
import java.util.ArrayList

class ServicesAdapter(
    private var items: MutableList<Any> = mutableListOf(),
) : RecyclerView.Adapter<ServicesAdapter.ViewHolder>() {

    fun setItems(services: MutableList<Any>) {
        items = services
        notifyDataSetChanged()
    }

    fun getItem(position: Int) = items[position]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServicesAdapter.ViewHolder {
        val view = ListRowServiceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServicesAdapter.ViewHolder, position: Int) {
        val item = items[position] as Characteristic

        holder.binding.btnRead.isVisible = false
        holder.binding.btnWrite.isVisible = false

        holder.binding.text.text = item.identifier.toString()
        if (item.isReadable) {
            holder.binding.btnRead.run {
                isVisible = true

            }
        }

        if (item.isWritable) {
            holder.binding.btnWrite.run {
                isVisible = true
            }
        }
    }

    override fun getItemCount() = items.size
    fun setItems(services: ArrayList<Service>) {
        items.addAll(services)
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ListRowServiceBinding) :
        RecyclerView.ViewHolder(binding.root)

}