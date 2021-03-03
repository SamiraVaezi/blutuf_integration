package net.grandcentrix.blutufintegration.ui.launch

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import net.grandcentrix.blutuf.core.api.BluetoothDisabledError
import net.grandcentrix.blutuf.core.api.FineLocationPermissionMissingError
import net.grandcentrix.blutuf.core.api.MissingPrecondition
import net.grandcentrix.blutufintegration.R
import net.grandcentrix.blutufintegration.databinding.FragmentLaunchBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val REQUEST_CODE_PERMISSION_LOCATION = 1

class LaunchFragment : Fragment() {

    lateinit var binding: FragmentLaunchBinding

    private val viewModel: LaunchViewModel by viewModel()

    private val permissions =
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
        )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        binding = FragmentLaunchBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.conditions.observe(viewLifecycleOwner) { conditions -> updateUi(conditions) }
    }

    private fun updateUi(conditions: List<MissingPrecondition>) {
        Log.e("sami", "error: ${conditions.size}")

        binding.checkboxPermission.isChecked = true
        binding.checkboxBle.isChecked = true
        updateStartBtn(false)

        conditions.forEach {
            when (it) {
                is FineLocationPermissionMissingError -> {
                    binding.checkboxPermission.isChecked = false
                    updateStartBtn(needPermission = true)
                }
                is BluetoothDisabledError -> {
                    binding.checkboxBle.isChecked = false
                }
                else -> {
                }
            }
        }
    }

    private fun updateStartBtn(needPermission: Boolean) {
        binding.btnStart.isEnabled = true
        if (needPermission) {
            binding.btnStart.setText(R.string.request_permission)
            binding.btnStart.setOnClickListener { requestPermissions() }
        } else {
            binding.btnStart.setText(R.string.start_app)
            binding.btnStart.setOnClickListener {
                findNavController().navigate(R.id.action_launch_to_main)
            }
        }
    }

    private fun showPermissionError() {
        Snackbar.make(binding.root, R.string.error_permission_needed, Snackbar.LENGTH_LONG).show()
    }

    private fun requestPermissions() {
        requestPermissions(permissions, REQUEST_CODE_PERMISSION_LOCATION)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                binding.checkboxPermission.isChecked = true
                updateStartBtn(false)
            } else {
                showPermissionError()
            }
        }
    }
}