package dev.lazygarde.watering.setup.remoteconfig

import com.google.firebase.remoteconfig.FirebaseRemoteConfig

object RemoteConfig {

    val baseUrl: String
        get() = getData("https://api.openweathermap.org/data/2.5/", "base_url") as String

    val minTemperature: Double
        get() = getData(0.2, "min_temperature") as Double


    private fun getData(defaultValue: Any, key: String) : Any {
        var ans: Any = defaultValue
        FirebaseRemoteConfig.getInstance().fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                try {
                    ans = when (ans) {
                        is String -> FirebaseRemoteConfig.getInstance().getString(key)
                        is Boolean -> FirebaseRemoteConfig.getInstance().getBoolean(key)
                        is Double -> FirebaseRemoteConfig.getInstance().getDouble(key)
                        is Long -> FirebaseRemoteConfig.getInstance().getLong(key)
                        else -> defaultValue
                    }
                } catch (_: Exception) {
                }
            }
        }
        return ans
    }

}
