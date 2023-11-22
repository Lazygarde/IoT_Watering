package dev.lazygarde.watering.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.lazygarde.watering.databinding.ItemSensorDataBinding
import dev.lazygarde.watering.section.sensordata.SensorDataModel

class SensorDataAdapter: RecyclerView.Adapter<SensorDataAdapter.ViewHolder>() {

    private var sensorDataList: List<SensorDataModel> = listOf()
    inner class ViewHolder(private val binding: ItemSensorDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(sensorDataModel: SensorDataModel) {
            binding.apply {
                tvTemperature.text = sensorDataModel.temperature.toString()
                tvHumidity.text = sensorDataModel.humidity.toString()
                tvTime.text = sensorDataModel.time.toString()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSensorDataBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return sensorDataList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(sensorDataList[position])
    }

    fun updateData(newList: List<SensorDataModel>) {
        sensorDataList = newList
        notifyDataSetChanged()
    }
}