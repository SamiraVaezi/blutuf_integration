package net.grandcentrix.blutufintegration.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import net.grandcentrix.blutuf.core.api.Service
import net.grandcentrix.blutufintegration.R
import net.grandcentrix.blutufintegration.data.model.DeviceUiState
import net.grandcentrix.blutufintegration.databinding.FragmentServiceBinding

class ServiceFragment() : Fragment() {

    private lateinit var binding: FragmentServiceBinding
    private var adapter: ServicesAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentServiceBinding.inflate(layoutInflater, container, false)

        adapter = ServicesAdapter()
        binding.recycler.adapter = adapter
        binding.recycler.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                RecyclerView.VERTICAL
            )
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    fun updateUi(services: ArrayList<Service>) {
        Log.e("sami", "update : " + services.size)
        adapter?.setItems(services)
    }

}