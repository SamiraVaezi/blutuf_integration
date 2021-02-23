package net.grandcentrix.blutufintegration.data.model

import android.bluetooth.le.ScanRecord
import android.bluetooth.le.ScanResult
import android.util.SparseArray
import net.grandcentrix.blutuf.core.api.AdRecord
import net.grandcentrix.blutuf.core.api.AdvertisementData
import net.grandcentrix.blutuf.core.api.Data
import net.grandcentrix.blutuf.core.api.Device
import java.nio.ByteBuffer

data class DefaultDevice(
    override val identifier: String,
    override val name: String?,
    override val rssi: Int,
    private val scanRecord: ScanRecord?
) : Device {

    companion object {

        fun fromScanResult(scanResult: ScanResult): DefaultDevice {
            return DefaultDevice(
                scanResult.device!!.address,
                scanResult.device!!.name,
                scanResult.rssi,
                scanResult.scanRecord
            )
        }
    }

    override val advertisementData: AdvertisementData by lazy {
        DefaultAdvertisementData(scanRecord)
    }

    override fun equals(other: Any?): Boolean {
        return identifier == (other as? DefaultDevice)?.identifier
    }

    override fun hashCode(): Int {
        return identifier.hashCode()
    }
}

class DefaultData(override val buffer: ByteBuffer) : Data {
    companion object {

        fun fromByteArray(bytes: ByteArray): DefaultData {
            return DefaultData(ByteBuffer.wrap(bytes))
        }
    }
}

data class DefaultAdRecord(override val type: Int, override val length: Int, override val data: Data) : AdRecord

data class DefaultAdvertisementData(private val scanRecord: ScanRecord?) : AdvertisementData {

    override val isValid: Boolean
        get() = scanRecord != null

    override val flags: Int
        get() = scanRecord?.advertiseFlags ?: 0

    override val deviceName: String
        get() = scanRecord?.deviceName ?: ""

    override val manufacturerSpecificData: Map<Int, Data> by lazy<Map<Int, Data>> {
        val data = scanRecord?.manufacturerSpecificData ?: SparseArray()
        val result = mutableMapOf<Int, Data>()

        for (i in 0 until data.size()) {
            val manufacturerId = data.keyAt(i)
            val rawData = data.valueAt(i)
            result[manufacturerId] = DefaultData.fromByteArray(rawData ?: byteArrayOf())
        }

        result
    }

    override fun manufacturerSpecificDataById(manufacturerId: Int): Data? {
        val rawData = scanRecord?.getManufacturerSpecificData(manufacturerId) ?: byteArrayOf()
        return DefaultData.fromByteArray(rawData)
    }

    override val serviceData: Map<String, Data> by lazy<Map<String, Data>> {
        scanRecord?.serviceData?.map { entry ->
            entry.key.uuid.toString() to DefaultData.fromByteArray(entry.value)
        }?.toMap() ?: emptyMap()
    }

    override val txPowerLevel: Int
        get() = scanRecord?.txPowerLevel ?: 0

    override val records: List<AdRecord> by lazy<List<AdRecord>> {
        val rawData = scanRecord?.bytes ?: byteArrayOf()
        val result = mutableListOf<AdRecord>()

        var pos = 0
        while (pos < rawData.size) {
            val len = rawData[pos].toInt() and 0xff
            if (len > 0) {
                val type = rawData[pos + 1].toInt() and 0xff
                val dataSlice = rawData.slice(pos + 2..pos + len)
                result += DefaultAdRecord(
                    type,
                    len,
                    DefaultData.fromByteArray(dataSlice.toByteArray())
                )
            }
            pos += 1 + len
        }

        result
    }

    override val rawData: Data by lazy<Data> { DefaultData.fromByteArray(scanRecord?.bytes ?: byteArrayOf()) }
}

fun ByteArray?.toByteBuffer(): ByteBuffer {
    if (this == null) {
        return ByteBuffer.wrap(byteArrayOf())
    }
    return ByteBuffer.wrap(this)
}
