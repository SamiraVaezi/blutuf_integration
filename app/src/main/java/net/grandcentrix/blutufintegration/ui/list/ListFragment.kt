package net.grandcentrix.blutufintegration.ui.list

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import net.grandcentrix.blutufintegration.R
import net.grandcentrix.blutufintegration.data.model.DeviceUiState
import net.grandcentrix.blutufintegration.data.model.ProcessState
import net.grandcentrix.blutufintegration.databinding.FragmentMainBinding
import net.grandcentrix.blutufintegration.ui.view.FullScreenErrorView
import org.koin.androidx.viewmodel.ext.android.viewModel

class ListFragment : Fragment(), DevicesAdapter.OnClickActions {

    private val viewModel: ListViewModel by viewModel()

    private lateinit var binding: FragmentMainBinding

    private lateinit var adapter: DevicesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        setHasOptionsMenu(true)

        binding = FragmentMainBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = DevicesAdapter(clickListener = this)
        binding.recycler.adapter = adapter
        binding.recycler.addItemDecoration(
            DividerItemDecoration(requireContext(), RecyclerView.VERTICAL)
        )

        binding.statusContainer.switchBle.setOnClickListener {
//            viewModel.bluetoothStateChange(binding.statusContainer.switchBle.isChecked)
        }

        binding.swipToRefresh.setOnRefreshListener {
            viewModel.startScan()
            binding.swipToRefresh.isRefreshing = false
        }

        viewModel.uiModel.observe(viewLifecycleOwner) { uiModel -> updateUi(uiModel) }
        viewModel.selectedDevice.observe(viewLifecycleOwner) { device -> updateDevice(device) }
        viewModel.scanState.observe(viewLifecycleOwner) { isScanning -> updateScanState(isScanning) }
    }

    private fun updateDevice(device: DeviceUiState?) {
        device?.let {
            adapter.updateItem(device)
            binding.statusContainer.state.text = device.getStateTitle(requireContext())
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

    private fun updateScanState(isScanning: Boolean) {
        binding.progressBar.isVisible = isScanning
    }

    private fun updateUi(uiModel: ProcessState<List<DeviceUiState>>) {
        when (uiModel) {
            is ProcessState.Scanning -> {
                binding.progressBar.isVisible = true
            }
            is ProcessState.Complete -> {
                binding.progressBar.isVisible = false
            }
            is ProcessState.Success -> {
                adapter.setItems(uiModel.data)
            }
            is ProcessState.Error -> {
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.refresh) {
            viewModel.startScan()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onConnectClicked(device: DeviceUiState) {
        viewModel.onConnectClicked(device)
    }

    override fun onDisconnectClicked(device: DeviceUiState) {
        viewModel.onDisconnectClicked(device)
    }

    override fun onItemClicked(identifier: String) {
        val action = ListFragmentDirections.actionMainDetail(identifier)
        findNavController().navigate(action)
    }
}
