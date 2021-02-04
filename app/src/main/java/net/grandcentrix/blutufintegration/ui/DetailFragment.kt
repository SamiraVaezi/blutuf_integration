package net.grandcentrix.blutufintegration.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import net.grandcentrix.blutuf.core.api.Data
import net.grandcentrix.blutuf.core.api.Device
import net.grandcentrix.blutufintegration.R
import net.grandcentrix.blutufintegration.data.model.DeviceUiState
import net.grandcentrix.blutufintegration.data.model.Resource
import net.grandcentrix.blutufintegration.data.model.State
import net.grandcentrix.blutufintegration.databinding.FragmentDetailBinding
import java.util.*

class DetailFragment : Fragment() {

    val args: DetailFragmentArgs by navArgs()

    private val sharedViewModel: MainViewModel by activityViewModels()

    lateinit var binding: FragmentDetailBinding

    lateinit var currectDevice: DeviceUiState

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailBinding.inflate(layoutInflater, container, false)

        currectDevice = args.device
        initiateViews(currectDevice)

        sharedViewModel.uiModel.observe(viewLifecycleOwner, { uiModel -> updateUi(uiModel) })

        return binding.root
    }

    private fun updateUi(uiModel: Resource<List<DeviceUiState>>?) {
        when (uiModel) {
            is Resource.Success -> {
                uiModel.data.find { deviceUiState -> deviceUiState.device.identifier == currectDevice.device.identifier }
                    ?.let {
                        currectDevice.state = it.state
                        Log.e("sami","bonding : "+it.bonding?.name)
                        updateState(it)
                    }
            }
            else -> {
            }
        }
    }

    private fun initiateViews(deviceUiState: DeviceUiState) {
        binding.name.text = getString(R.string.device_name, deviceUiState.device.name ?: "Unknown")
        binding.identifier.text =
            getString(R.string.device_identifier, deviceUiState.device.identifier)

        binding.description.text = getDescription(deviceUiState.device)

        binding.btnConnect.setOnClickListener { sharedViewModel.connectDevice(deviceUiState.device.identifier) }
        binding.btnDisconnect.setOnClickListener { sharedViewModel.disconnectDevice(deviceUiState.device.identifier) }
        binding.btnBond.setOnClickListener { createBond() }
        updateState(deviceUiState)
    }

    private fun createBond() {
        if(currectDevice.state != State.CONNECTED) {
            Snackbar.make(binding.root, "Device not connected!", Snackbar.LENGTH_LONG).show()
        } else {
            sharedViewModel.createBond(currectDevice.device.identifier)
        }
    }

    private fun updateState(deviceUiState: DeviceUiState) {
        binding.btnConnect.isVisible = deviceUiState.state == State.DISCONNECTED
        binding.btnDisconnect.isVisible = deviceUiState.state == State.CONNECTED
        binding.progressBar.isVisible = deviceUiState.state == State.CONNECTING

    }

    private fun getDescription(device: Device): String {
        val parsedAdvertisementData = device.advertisementData
        val rawParsed = parsedAdvertisementData.records.joinToString(separator = "") {
            """
Len: ${it.length.toString(16)}
Type: ${it.type.toString(16)}
Raw: ${it.data.toDisplayableString()}

            """.trimIndent()
        }

        val parsedAD = """

Rssi: ${device.rssi}
Flags: ${parsedAdvertisementData.flags}
Name: ${parsedAdvertisementData.deviceName}
TxPowerLevel: ${parsedAdvertisementData.txPowerLevel}

$rawParsed
        """.trimIndent()

        return device.advertisementData.rawData.toDisplayableString() + parsedAD
    }

    private fun Data.toDisplayableString(): String {
        return (this.buffer.array() ?: byteArrayOf()).toDisplayableString()
    }

    private fun ByteArray.toDisplayableString(): String {
        return this.joinToString(
            prefix = "[",
            postfix = "]"
        ) { (it.toInt() and 0xff).toString(16).padStart(2, '0').toUpperCase(Locale.US) }
    }

}