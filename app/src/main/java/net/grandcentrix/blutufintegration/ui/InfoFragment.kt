package net.grandcentrix.blutufintegration.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.grandcentrix.blutuf.core.api.Data
import net.grandcentrix.blutuf.core.api.Device
import net.grandcentrix.blutufintegration.data.model.DeviceUiState
import net.grandcentrix.blutufintegration.databinding.FragmentInfoBinding
import java.util.*

class InfoFragment(private val deviceUiState: DeviceUiState) : Fragment() {

    private lateinit var binding: FragmentInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentInfoBinding.inflate(layoutInflater, container, false)

        binding.description.text = getDescription(deviceUiState.device)

        return binding.root
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