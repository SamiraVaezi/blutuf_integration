package net.grandcentrix.blutufintegration.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import net.grandcentrix.blutuf.core.api.Device
import net.grandcentrix.blutufintegration.R
import net.grandcentrix.blutufintegration.data.model.DeviceUiState
import net.grandcentrix.blutufintegration.data.model.State
import net.grandcentrix.blutufintegration.databinding.ListRowDeviceBinding

class DevicesAdapter(
    private var items: List<DeviceUiState> = emptyList(),
    private val clickListener: OnClickActions
) : RecyclerView.Adapter<DevicesAdapter.ViewHolder>() {

    interface OnClickActions {
        fun onConnectClicked(device: DeviceUiState)
        fun onDisconnectClicked(device: DeviceUiState)
        fun onItemClicked(device: DeviceUiState)
    }

    fun setItems(devices: List<DeviceUiState>) {
        items = devices
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ListRowDeviceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val device = items[position]
        holder.binding.name.text = device.device.name ?: device.device.identifier

        holder.updateButton(device.state)
        holder.binding.buttonContainer.btnConnect.setOnClickListener { clickListener.onConnectClicked(device) }
        holder.binding.buttonContainer.btnDisconnect.setOnClickListener { clickListener.onDisconnectClicked(device) }
        holder.itemView.setOnClickListener { clickListener.onItemClicked(device) }
    }

    override fun getItemCount() = items.size

    inner class ViewHolder(val binding: ListRowDeviceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun updateButton(state: State) {
            binding.buttonContainer.btnConnect.isVisible = state == State.DISCONNECTED
            binding.buttonContainer.btnDisconnect.isVisible = state == State.CONNECTED
            binding.buttonContainer.progressBar.isVisible = state == State.CONNECTING
        }
    }
}