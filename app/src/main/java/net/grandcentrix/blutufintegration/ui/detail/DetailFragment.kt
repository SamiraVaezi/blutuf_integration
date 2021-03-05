package net.grandcentrix.blutufintegration.ui.detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.material.tabs.TabLayoutMediator
import net.grandcentrix.blutufintegration.R
import net.grandcentrix.blutufintegration.data.model.DeviceUiState
import net.grandcentrix.blutufintegration.data.model.State
import net.grandcentrix.blutufintegration.databinding.FragmentDetailBinding
import net.grandcentrix.blutufintegration.ui.InfoFragment
import net.grandcentrix.blutufintegration.ui.ServiceFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class DetailFragment : Fragment() {

    private val args: DetailFragmentArgs by navArgs()

    private val viewModel: DetailViewModel by viewModel() {
        parametersOf(args.deviceIdentifier)
    }

    private lateinit var binding: FragmentDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailBinding.inflate(layoutInflater, container, false)

        viewModel.uiModel.observe(viewLifecycleOwner, { uiModel -> updateUi(uiModel) })
        viewModel.selectedDevice.observe(viewLifecycleOwner, { device -> updateState(device) })

        return binding.root
    }

    private fun setupViewPager(device: DeviceUiState) {
        val fragments = listOf(InfoFragment(device), ServiceFragment())

        val titles = listOf(
            getString(R.string.tab_title_info),
            getString(R.string.tab_title_service),
        )

        binding.viewPager.adapter = DetailViewPagerAdapter(this, fragments)
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = titles[position]
            binding.viewPager.setCurrentItem(tab.position, true)
        }.attach()
    }

    private fun updateUi(device: DeviceUiState) {
        binding.name.text = getString(R.string.device_name, device.device.name ?: "Unknown")
        binding.identifier.text =
            getString(R.string.device_identifier, device.device.identifier)

        setupViewPager(device)
        updateState(device)
    }

    private fun updateService(deviceUiState: DeviceUiState) {
//        (((binding.viewPager.adapter as DeviceDetailViewPagerAdapter).getFragment(1)) as ServiceFragment).updateUi(
//            deviceUiState.services
//        )
    }

    private fun initiateViews(deviceUiState: DeviceUiState) {
        binding.name.text = getString(R.string.device_name, deviceUiState.device.name ?: "Unknown")
        binding.identifier.text =
            getString(R.string.device_identifier, deviceUiState.device.identifier)

        binding.btnConnect.setOnClickListener { viewModel.onConnectClicked(deviceUiState) }
        binding.btnDisconnect.setOnClickListener { viewModel.onDisconnectClicked(deviceUiState) }
        binding.btnBond.setOnClickListener { createBond() }
        updateState(deviceUiState)
    }

    private fun createBond() {
        /*if (currectDevice.state != State.CONNECTED) {
            Snackbar.make(binding.root, "Device not connected!", Snackbar.LENGTH_LONG).show()
        } else {
        }*/
    }

    private fun updateState(deviceUiState: DeviceUiState?) {
        deviceUiState?.let {
            binding.btnConnect.isVisible = deviceUiState.state == State.DISCONNECTED
            binding.btnDisconnect.isVisible = deviceUiState.state == State.CONNECTED
            binding.progressBar.isVisible = deviceUiState.state == State.CONNECTING

            /*binding.btnBond.text = deviceUiState.bonding?.name ?: "BOND"
            binding.btnBond.visibility = when (deviceUiState.bonding) {
                Bonding.State.BONDING -> View.INVISIBLE
                else -> View.VISIBLE
            }*/
            binding.progressBarBoning.isVisible = !binding.btnBond.isVisible
        }
    }
}
