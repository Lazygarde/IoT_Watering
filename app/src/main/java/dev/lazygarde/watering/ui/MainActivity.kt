package dev.lazygarde.watering.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import dev.lazygarde.watering.databinding.ActivityMainBinding
import dev.lazygarde.watering.ui.adapter.SensorDataAdapter
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var sensorDataAdapter: SensorDataAdapter
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        sensorDataAdapter = SensorDataAdapter(

        )


        binding.rvSensorData.apply {
            layoutManager =
                LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
            adapter = sensorDataAdapter
        }

        lifecycleScope.launch {
            viewModel.sensorDataList.collect {
                sensorDataAdapter.updateData(it)
            }
        }
    }
}