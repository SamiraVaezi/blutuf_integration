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
import com.google.android.material.tabs.TabLayoutMediator
import net.grandcentrix.blutuf.core.api.Bonding
import net.grandcentrix.blutuf.core.api.Data
import net.grandcentrix.blutuf.core.api.Device
import net.grandcentrix.blutufintegration.MainViewModel
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

        setupViewPager()

        sharedViewModel.uiModel.observe(viewLifecycleOwner, { uiModel -> updateUi(uiModel) })

        return binding.root
    }

    private fun setupViewPager() {
        val fragments = listOf(InfoFragment(currectDevice), ServiceFragment())

        val titles = listOf(
            getString(R.string.tab_title_info),
            getString(R.string.tab_title_service),
        )

        binding.viewPager.adapter = DeviceDetailViewPagerAdapter(this, fragments)
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = titles[position]
            binding.viewPager.setCurrentItem(tab.position, true)
        }.attach()
    }

    private fun updateUi(uiModel: Resource<List<DeviceUiState>>?) {
        when (uiModel) {
            is Resource.Success -> {
                uiModel.data.find { deviceUiState -> deviceUiState.device.identifier == currectDevice.device.identifier }
                    ?.let {
                        currectDevice.state = it.state
                        updateState(it)
                        updateService(it)

                    }
            }
            else -> {
            }
        }
    }

    private fun updateService(deviceUiState: DeviceUiState) {
        (((binding.viewPager.adapter as DeviceDetailViewPagerAdapter).getFragment(1)) as ServiceFragment).updateUi(
            deviceUiState.services
        )
    }

    private fun initiateViews(deviceUiState: DeviceUiState) {
        binding.name.text = getString(R.string.device_name, deviceUiState.device.name ?: "Unknown")
        binding.identifier.text =
            getString(R.string.device_identifier, deviceUiState.device.identifier)

        binding.btnConnect.setOnClickListener { sharedViewModel.connectDevice(deviceUiState.device.identifier) }
        binding.btnDisconnect.setOnClickListener { sharedViewModel.disconnectDevice(deviceUiState.device.identifier) }
        binding.btnBond.setOnClickListener { createBond() }
        updateState(deviceUiState)


    }

    private fun createBond() {
        if (currectDevice.state != State.CONNECTED) {
            Snackbar.make(binding.root, "Device not connected!", Snackbar.LENGTH_LONG).show()
        } else {
            sharedViewModel.createBond(currectDevice.device.identifier)
        }
    }

    private fun updateState(deviceUiState: DeviceUiState) {
        binding.btnConnect.isVisible = deviceUiState.state == State.DISCONNECTED
        binding.btnDisconnect.isVisible = deviceUiState.state == State.CONNECTED
        binding.progressBar.isVisible = deviceUiState.state == State.CONNECTING

        binding.btnBond.text = deviceUiState.bonding?.name ?: "BOND"
        binding.btnBond.visibility = when (deviceUiState.bonding) {
            Bonding.State.BONDING -> View.INVISIBLE
            else -> View.VISIBLE
        }
        binding.progressBarBoning.isVisible = !binding.btnBond.isVisible
    }


}