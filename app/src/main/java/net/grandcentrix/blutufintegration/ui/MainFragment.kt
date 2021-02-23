package net.grandcentrix.blutufintegration.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import net.grandcentrix.blutufintegration.MainViewModel
import net.grandcentrix.blutufintegration.R
import net.grandcentrix.blutufintegration.data.model.DeviceUiState
import net.grandcentrix.blutufintegration.data.model.Resource
import net.grandcentrix.blutufintegration.data.model.State
import net.grandcentrix.blutufintegration.databinding.FragmentMainBinding
import net.grandcentrix.blutufintegration.view.FullScreenErrorView

class MainFragment : Fragment(), DevicesAdapter.OnClickActions {

    private val viewModel: MainViewModel by activityViewModels()
    lateinit var binding: FragmentMainBinding
    lateinit var adapter: DevicesAdapter

    var isScanning: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false);


        setHasOptionsMenu(true)

        binding = FragmentMainBinding.inflate(layoutInflater, container, false)

        adapter = DevicesAdapter(clickListener = this)
        binding.recycler.adapter = adapter
        binding.recycler.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                RecyclerView.VERTICAL
            )
        )

        binding.statusContainer.switchBle.setOnClickListener {
            viewModel.bluetoothStateChange(binding.statusContainer.switchBle.isChecked)
        }
        binding.swipToRefresh.setOnRefreshListener {
            if (!isScanning) {
//                viewModel.startScan()
                scan()
            }
            binding.swipToRefresh.isRefreshing = false
        }

//        viewModel.uiModel.observe(viewLifecycleOwner, { uiModel -> updateUi(uiModel) })

        lifecycleScope.launchWhenStarted {
            viewModel.bleStateFlow.collect { enable -> updateBluetoothState(enable) }
        }

        scan()

        return binding.root
    }

    @ExperimentalCoroutinesApi
    private fun scan() {
        lifecycleScope.launch {
            viewModel.startScanTest().collect {
                adapter.setItems(it)
            }
        }
    }

    private fun updateBluetoothState(enable: Boolean) {
        binding.statusContainer.switchBle.isChecked = enable
        binding.errorFrame.isVisible = !enable

        if (!enable) {
            binding.errorFrame.removeAllViews()
            binding.errorFrame.addView(
                FullScreenErrorView.newInstanceDisabledBluetooth(
                    requireContext()
                )
            )
        }
    }

    private fun updateUi(uiModel: Resource<List<DeviceUiState>>?) {
        when (uiModel) {
            is Resource.Scanning -> {
                isScanning = true
                binding.progressBar.isVisible = true
            }
            is Resource.Complete -> {
                isScanning = false
                binding.progressBar.isVisible = false
            }
            is Resource.Success -> {
                adapter.setItems(uiModel.data)
                binding.statusContainer.state.text = getStateTitle(uiModel.data)
            }
            is Resource.Error -> {

            }
        }
    }

    private fun getStateTitle(list: List<DeviceUiState>): String {
        var title = getString(R.string.disconnected)
        list.forEachIndexed { _, deviceUiState ->
            deviceUiState.takeIf { it.state == State.CONNECTED }?.let {
                title = getString(R.string.connected_to, it.device.name ?: it.device.identifier)
            }

            deviceUiState.takeIf { it.state == State.CONNECTING }?.let {
                title = getString(R.string.connecting_to, it.device.name ?: it.device.identifier)
            }
        }
        return title
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.refresh) {
            if (!isScanning) {
//                viewModel.startScan()
                scan()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onConnectClicked(device: DeviceUiState) {
        viewModel.connectDevice(device.device.identifier)
    }

    override fun onDisconnectClicked(device: DeviceUiState) {
        viewModel.disconnectDevice(device.device.identifier)
    }

    override fun onItemClicked(device: DeviceUiState) {
        val action = MainFragmentDirections.actionMainDetail(device)
        findNavController().navigate(action)
    }
}
