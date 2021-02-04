package net.grandcentrix.blutufintegration.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import net.grandcentrix.blutufintegration.R
import net.grandcentrix.blutufintegration.databinding.ViewFullScreenErrorBinding

class FullScreenErrorView(context: Context, attrs: AttributeSet?) :
    ConstraintLayout(context, attrs) {


    constructor(context: Context) : this(context, null)

    companion object {

        fun newInstanceDisabledBluetooth(context: Context) = FullScreenErrorView(context).apply {
            setTitle(context.getString(R.string.bluetooth_disable_error_title))
            setText(context.getString(R.string.bluetooth_disable_error_text))
            setImage(R.drawable.ble_disable)
        }
    }

    private var binding: ViewFullScreenErrorBinding =
        ViewFullScreenErrorBinding.inflate(LayoutInflater.from(context), this, true)

    private fun setTitle(title: String) {
        binding.title.text = title
    }

    private fun setText(text: String) {
        binding.text.text = text
    }

    private fun setImage(resourceId: Int) {
        binding.image.setImageResource(resourceId)
    }
}