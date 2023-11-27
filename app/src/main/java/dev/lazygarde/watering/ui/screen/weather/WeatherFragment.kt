package dev.lazygarde.watering.ui.screen.weather

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import dev.lazygarde.watering.R
import dev.lazygarde.watering.databinding.FragmentWeatherBinding
import dev.lazygarde.watering.ui.MainViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class WeatherFragment : Fragment() {

    private lateinit var binding: FragmentWeatherBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.weatherStatus.collect {
                if (it != "") {
                    binding.tvWeather.text = it
                    if (it == "Rain") {
                        Glide.with(this@WeatherFragment).load(R.drawable.icon_rain)
                            .into(binding.ivWeather)
                    } else if (it == "Clear") {
                        Glide.with(this@WeatherFragment).load(R.drawable.icon_clear_day)
                            .into(binding.ivWeather)
                    } else if (it == "Overcast") {
                        Glide.with(this@WeatherFragment).load(R.drawable.icon_cloudy_day)
                            .into(binding.ivWeather)
                    } else {
                        Glide.with(this@WeatherFragment).load(R.drawable.icon_cloudy_sunny_day)
                            .into(binding.ivWeather)
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.sensorData.collect {
                binding.tvTemperature.text = it.temperature.toString() + "°C"
                binding.tvHumidity.text = it.humidity.toString() + "%"
            }
        }
        binding.tvDate.text = getCurrentTime()

    }

    private fun getCurrentTime(): String {
        val currentTime = Calendar.getInstance().time
        val sdf = SimpleDateFormat("EEEE, d MMMM", Locale.getDefault())
        return sdf.format(currentTime)
    }

}