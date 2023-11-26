package dev.lazygarde.watering.ui.screen.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startForegroundService
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import dev.lazygarde.watering.databinding.FragmentHomeBinding
import dev.lazygarde.watering.section.sensordata.SensorDataModel
import dev.lazygarde.watering.service.WateringService
import dev.lazygarde.watering.ui.MainViewModel
import dev.lazygarde.watering.ui.dialog.SpeechToTextDialog
import kotlinx.coroutines.launch
import java.util.Locale

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var textToSpeech: TextToSpeech
    private var sensorData = SensorDataModel()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        textToSpeech = TextToSpeech(requireContext()) {}
        textToSpeech.language = Locale.ENGLISH
//        startService()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.auto.collect {
                binding.scAuto.isChecked = it
            }
        }

        binding.scAuto.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setAuto(isChecked)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.waterPump.collect {
                binding.scWaterPump.isChecked = it
            }
        }
        binding.root.setOnTouchListener { view, motionEvent ->
            childFragmentManager.fragments.forEach {
                if (it.isVisible && it is SpeechToTextDialog) {
                    it.dismiss()
                    return@setOnTouchListener false
                }
            }
            return@setOnTouchListener true
        }

        binding.scWaterPump.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setWaterPump(isChecked)
        }

        binding.animationView.setOnClickListener {
            SpeechToTextDialog().apply {
                onSpeechToTextResult = {
                    textToSpeech.speak(
                        viewModel.getAnswerFromSpeech(it),
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        null
                    )

                }
            }.show(childFragmentManager, "SpeechToTextDialog")
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.sensorData.collect {
                sensorData = it
                binding.tvTemperatureValue.text = it.temperature.toString() + "°C"
                binding.tvHumidityValue.text = it.humidity.toString() + "%"
                binding.tvSoilMoistureValue.text = it.soilMoisture.toString() + "%"
            }
        }
        setUpThreshold()
    }

    private fun setUpThreshold() {
        binding.apply {
            editTextHumidity.setText(viewModel.humidityThrehold.value.toString())
            editTextHumidity.doAfterTextChanged {
                if (it.toString().isNotEmpty()) {
                    viewModel.setHumidityThrehold(it.toString().toDouble())
                }
            }
            editTextTemperature.setText(viewModel.temperatureThrehold.value.toString())
            editTextTemperature.doAfterTextChanged {
                if (it.toString().isNotEmpty()) {
                    viewModel.setTemperatureThrehold(it.toString().toDouble())
                }
            }
            editTextSoilMoisture.setText(viewModel.soilMoistureThrehold.value.toString())
            editTextSoilMoisture.doAfterTextChanged {
                if (it.toString().isNotEmpty()) {
                    viewModel.setSoilMoistureThrehold(it.toString().toDouble())
                }
            }
        }
    }

    private fun startService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (ContextCompat.checkSelfPermission(activity ?: return, Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity ?: return, arrayOf(Manifest.permission.FOREGROUND_SERVICE), 0)
            }
        }

    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == 0) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(activity, WateringService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(activity ?: return, intent)
                } else {
                }
            } else {
                // Permission denied
            }
        }
    }

}