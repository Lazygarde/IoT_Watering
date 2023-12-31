package dev.lazygarde.watering.ui.screen.home

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import dev.lazygarde.watering.databinding.FragmentHomeBinding
import dev.lazygarde.watering.section.sensordata.SensorDataModel
import dev.lazygarde.watering.section.weather.RetrofitHelper
import dev.lazygarde.watering.section.weather.WeatherApi
import dev.lazygarde.watering.ui.MainViewModel
import dev.lazygarde.watering.ui.dialog.SpeechToTextDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var textToSpeech : TextToSpeech
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
        textToSpeech = TextToSpeech(requireContext()){}
        textToSpeech.language = Locale.ENGLISH

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

        binding.scWaterPump.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setWaterPump(isChecked)
        }

        binding.animationView.setOnClickListener {
            SpeechToTextDialog().apply {
                onSpeechToTextResult = {
                    textToSpeech.speak(viewModel.getAnswerFromSpeech(it), TextToSpeech.QUEUE_FLUSH, null, null)

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
    }


}