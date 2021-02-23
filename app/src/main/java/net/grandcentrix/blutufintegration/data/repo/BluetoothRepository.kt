package net.grandcentrix.blutufintegration.data.repo

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.util.Log
import net.grandcentrix.blutufintegration.data.model.DefaultDevice


object BluetoothRepository {

    fun deviceSupportsBluetooth(context: Context): Boolean {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager?
        return bluetoothManager?.adapter != null
    }

    fun enableBluetooth(enable: Boolean) {
        val adapter = BluetoothAdapter.getDefaultAdapter()
        when(enable) {
            true -> adapter.enable()
            false -> adapter.disable()
        }
    }

    fun test( context: Context){
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter

        Log.e("sami","advertisment: "+ bluetoothAdapter.isMultipleAdvertisementSupported)
    }


    fun startScan(
        context: Context,
        onDeviceAppeared: ((net.grandcentrix.blutuf.core.api.Device) -> kotlin.Unit)?
    ): kotlin.Unit {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter
        /*bluetoothAdapter.bondedDevices.forEach{
            Log.e("sami", " bonded : "+it.name)
        }*/

        val callback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                super.onScanResult(callbackType, result)
                val device = DefaultDevice.fromScanResult(result!!)
                onDeviceAppeared?.invoke(device)
                Log.e("sami", "scanned :${device.name} : ${device.identifier}")
            }

            override fun onBatchScanResults(results: MutableList<ScanResult>?) {
                super.onBatchScanResults(results)
                Log.e("sami", "onBatchScanResults:${results.toString()}")
            }

            override fun onScanFailed(errorCode: Int) {
                super.onScanFailed(errorCode)
                Log.e("sami", "onScanFailed: $errorCode")
            }

        }

        bluetoothAdapter.bluetoothLeScanner.startScan(
            listOf(ScanFilter.Builder().build()),
            ScanSettings.Builder().build(),
            callback )

    }
}