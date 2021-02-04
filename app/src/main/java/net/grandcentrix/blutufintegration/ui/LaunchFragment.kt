package net.grandcentrix.blutufintegration.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import net.grandcentrix.blutufintegration.R
import net.grandcentrix.blutufintegration.databinding.FragmentLaunchBinding
import net.grandcentrix.blutufintegration.data.repo.BluetoothRepository

private const val REQUEST_CODE_PERMISSION_LOCATION = 1


class LaunchFragment : Fragment() {

    @VisibleForTesting
    lateinit var binding: FragmentLaunchBinding

    private val permissions =
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH_ADMIN)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        binding = FragmentLaunchBinding.inflate(layoutInflater, container, false)

        if (anyRequirementsNeeded()) {
            when {
                !binding.checkboxBle.isChecked -> showError()
                !binding.checkboxPermission.isChecked -> updateStartBtn(true)
            }
        } else {
            updateStartBtn(false)
        }

        return binding.root
    }

    private fun anyRequirementsNeeded(): Boolean = listOf(
        isBluetoothSupported(),
        checkPermissions()
    ).any { checkResult -> checkResult == CheckResult.REQUIRED }

    @VisibleForTesting
    fun isBluetoothSupported(): CheckResult {
        val result = when (BluetoothRepository.deviceSupportsBluetooth(requireContext())) {
            true -> CheckResult.PASSED
            else -> CheckResult.REQUIRED
        }
        binding.checkboxBle.isChecked = result == CheckResult.PASSED
        return result
    }

    @VisibleForTesting
    fun checkPermissions(): CheckResult {
        val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            permissions.any {
                checkSelfPermission(requireContext(), it) !=
                        PackageManager.PERMISSION_GRANTED
            }
        ) {
            CheckResult.REQUIRED
        } else
            CheckResult.PASSED
        binding.checkboxPermission.isChecked = result == CheckResult.PASSED
        return result
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

    private fun showError() {
        /*Snackbar.make(binding.root, R.string.error_ble_not_supported, Snackbar.LENGTH_LONG)
            .setAction("Exit") { requireActivity().finish() }.show()*/
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

    enum class CheckResult {
        PASSED,
        REQUIRED
    }
}