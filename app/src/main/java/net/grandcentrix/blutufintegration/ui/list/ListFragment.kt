package net.grandcentrix.blutufintegration.ui.list

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import net.grandcentrix.blutufintegration.R
import net.grandcentrix.blutufintegration.data.model.DeviceUiState
import net.grandcentrix.blutufintegration.data.model.Resource
import net.grandcentrix.blutufintegration.data.model.State
import net.grandcentrix.blutufintegration.databinding.FragmentMainBinding
import net.grandcentrix.blutufintegration.view.FullScreenErrorView

class ListFragment : Fragment(), DevicesAdapter.OnClickActions {

    private val viewModel: ListViewModel by viewModels()

    lateinit var binding: FragmentMainBinding

    lateinit var adapter: DevicesAdapter

    val identifier: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        setHasOptionsMenu(true)

        binding = FragmentMainBinding.inflate(layoutInflater, container, false)

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

        viewModel.uiModel.observe(viewLifecycleOwner, { uiModel -> updateUi(uiModel) })
        viewModel.selectedDevice.observe(viewLifecycleOwner, { device -> updateDevice(device) })

        return binding.root
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

    private fun updateUi(uiModel: Resource<List<DeviceUiState>>?) {
        when (uiModel) {
            is Resource.Scanning -> {
                binding.progressBar.isVisible = true
            }
            is Resource.Complete -> {
                binding.progressBar.isVisible = false
            }
            is Resource.Success -> {
                adapter.setItems(uiModel.data)
            }
            is Resource.Error -> {

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

    override fun onStop() {
        super.onStop()
        Log.e("sami","list onStop")
    }

    override fun onStart() {
        super.onStart()
        Log.e("sami","list onStart")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("sami","list onDestroy")
    }
}
