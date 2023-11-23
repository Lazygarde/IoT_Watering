package dev.lazygarde.watering.ui.screen.home

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import dev.lazygarde.watering.databinding.FragmentHomeBinding
import dev.lazygarde.watering.section.sensordata.SensorDataModel
import dev.lazygarde.watering.ui.MainViewModel
import dev.lazygarde.watering.ui.MyAxisFormatter
import dev.lazygarde.watering.ui.adapter.SensorDataAdapter
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var sensorDataAdapter: SensorDataAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
    }

    private fun setUpView() {

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        sensorDataAdapter = SensorDataAdapter()


        binding.rvSensorData.apply {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = sensorDataAdapter
        }
        val temperatureEntries = mutableListOf<Entry>()
        val temperatureDataSet = LineDataSet(temperatureEntries, "Temperature").apply {
            setDrawCircles(true)
            setCircleColor(Color.RED)
            circleRadius = 5f
            setDrawCircleHole(false)
            setDrawValues(false)
            this.color = Color.RED
            lineWidth = 2f
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawFilled(true)
            fillColor = Color.RED
        }
        val temperatureLineData = LineData(temperatureDataSet)
        binding.lineTemperatureChart.data = temperatureLineData

        val humidityEntries = mutableListOf<Entry>()
        val humidityDataSet = LineDataSet(humidityEntries, "Humidity").apply {
            setDrawCircles(true)
            setCircleColor(Color.BLUE)
            circleRadius = 5f
            setDrawCircleHole(false)
            setDrawValues(false)
            this.color = Color.BLUE
            lineWidth = 2f
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawFilled(true)
            fillColor = Color.BLUE
        }
        val humidityLineData = LineData(humidityDataSet)
        binding.lineHumidityChart.data = humidityLineData

        binding.btnPushData.setOnClickListener {
            viewModel.addSensorData(
                SensorDataModel(
                    id = System.currentTimeMillis(),
                    temperature = ((15..25).random()).toDouble(),
                    humidity = ((40..60).random()).toDouble(),
                    time = System.currentTimeMillis()
                )
            )
        }
        binding.btnClearData.setOnClickListener {
            viewModel.clearData()
        }

        lifecycleScope.launch {
            viewModel.sensorDataList.collect {
                Log.d("12312312321321312", "sensorDataList: $it")
                updateLineChart(
                    binding.lineTemperatureChart,
                    temperatureEntries,
                    temperatureDataSet,
                    it,
                    SensorDataModel::temperature
                )
                updateLineChart(
                    binding.lineHumidityChart,
                    humidityEntries,
                    humidityDataSet,
                    it,
                    SensorDataModel::humidity
                )
            }
        }
    }

    private fun updateLineChart(
        chart: LineChart,
        entries: MutableList<Entry>,
        dataSet: LineDataSet,
        sensorData: List<SensorDataModel>,
        valueSelector: (SensorDataModel) -> Double
    ) {
        entries.clear()
        val firstTime =
            if (sensorData.isNotEmpty()) sensorData[0].time else System.currentTimeMillis()
        entries.addAll(sensorData.map {
            val secondsSinceFirst = (it.time - firstTime) / 1000
            Entry(secondsSinceFirst.toFloat(), valueSelector(it).toFloat())
        })
        dataSet.notifyDataSetChanged()
        chart.data.notifyDataChanged()
        chart.notifyDataSetChanged()
        chart.apply {
            description.isEnabled = false
            setTouchEnabled(true)
            setPinchZoom(true)
            setDrawGridBackground(true)
            animateX(1000, Easing.EaseInOutQuad)
            legend.apply {
                verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
                orientation = Legend.LegendOrientation.HORIZONTAL
                setDrawInside(false)
            }
            axisRight.isEnabled = false
            axisLeft.apply {
                setDrawGridLines(true)
                axisMinimum = 0f
            }
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                valueFormatter = MyAxisFormatter(firstTime)
                // Set the maximum number of visible entries
                setLabelCount(5, true)
            }
            // Move view to the latest data point
            moveViewToX(data.entryCount.toFloat())
            invalidate()
        }
    }

}