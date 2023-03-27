import com.example.assignment.network.RetrofitInstance
import com.example.assignment.utils.BASE_URL


class AppRepository {

    suspend fun getCurrentWeather(url: String) =
        RetrofitInstance.apiClient.getCurrentWeather(url)
       // RetrofitInstance.apiClient.getCurrentWeather(cityName,units, defaultLang, apiKey!!)


}