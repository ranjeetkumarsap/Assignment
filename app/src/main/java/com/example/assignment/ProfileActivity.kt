package com.example.assignment


import AppRepository
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.LottieAnimationView
import com.example.assignment.databinding.ActivityProfileBinding
import com.example.assignment.model.CurrentWeather
import com.example.assignment.model.CurrentWeatherResponse
import com.example.assignment.utils.*
import com.example.assignment.viewmodel.ProfileViewModel
import com.example.assignment.viewmodel.ViewModelProviderFactory
import java.util.*


class ProfileActivity : AppCompatActivity() {
    lateinit var binding: ActivityProfileBinding
    lateinit var tvUsername: TextView
    lateinit var tvbio: TextView
    private lateinit var ivProfile: ImageView
    lateinit var profileViewModel: ProfileViewModel
    lateinit var appRepository: AppRepository
    private var apiKey: String? = null
    private val defaultLang = "en"
    lateinit  var temp_text_view :TextSwitcher
    lateinit  var humidity_text_view :TextSwitcher
    lateinit  var description_text_view :TextSwitcher
    lateinit  var wind_text_view :TextSwitcher
    lateinit  var animation_view :LottieAnimationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // setContentView(R.layout.activity_profile)
            binding = ActivityProfileBinding.inflate(layoutInflater)
            setContentView(binding.root)
        val actionBar: ActionBar? = supportActionBar
        actionBar!!.setDisplayUseLogoEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)

        val strName=intent.getStringExtra("Name")
        val strBio=intent.getStringExtra("Bio")
        val strUri=intent.getStringExtra("Uri")



        tvUsername = findViewById(R.id.tvUsername)
        tvbio = findViewById(R.id.tvbio)
        ivProfile = findViewById(R.id.profile_circular_image)

        temp_text_view  =findViewById(R.id.temp_text_view)
        description_text_view  = findViewById(R.id.description_text_view)
        humidity_text_view  = findViewById(R.id.humidity_text_view)
        wind_text_view  = findViewById(R.id.wind_text_view)
        animation_view  = findViewById(R.id.animation_view)

        setupTextSwitchers()

        if (strUri!=null){
            try {
                val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(strUri))
                ivProfile.setImageBitmap(bitmap)
            }catch (e:Exception){
               e.printStackTrace()
            }

        }


        tvUsername.text=strName
        tvbio.text=strBio

        appRepository = AppRepository()
        val factory = ViewModelProviderFactory(application, appRepository)
        profileViewModel = ViewModelProvider(this, factory).get(ProfileViewModel::class.java)

        apiKey = resources.getString(R.string.open_weather_map_api)

        initObserve()
        requestWeather("Kolkata",false)

       binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // on below line we are checking
                // if query exist or not.
                if (query!!.length>0) {
                    requestWeather(query,false)
                } else {
                    // if query is not present we are displaying
                    // a toast message as no  data found..
                    requestWeather("Kolkata",false)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // if query text is change in that case we
                // are filtering our adapter with
                // new text on below line.
                return false
            }
        })

    }

    private fun requestWeather(cityName: String, isSearch: Boolean) {
            getCurrentWeather(cityName, isSearch)
    }

    private fun getCurrentWeather(cityName: String, isSearch: Boolean) {
        apiKey = resources.getString(R.string.open_weather_map_api)
        profileViewModel.getCurrentWeather(cityName, UNITS, defaultLang, apiKey!!)

    }

    fun initObserve() {
        profileViewModel.currentWeatherLiveData.observe(this, androidx.lifecycle.Observer {
            if (it.cod==200) {
                Log.d("Inside Success==>", it.toString())
                binding.cityNameTextView.setText(
                    java.lang.String.format(
                        "%s, %s",
                        it.name,
                        it.sys.country
                    )
                )

                storeCurrentWeather(it)
            } else {
                Toast.makeText(this, "Some issue!" , Toast.LENGTH_SHORT).show()
            }
        })
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun storeCurrentWeather(response: CurrentWeatherResponse) {
        var currentWeather: CurrentWeather = CurrentWeather()
        currentWeather.setTemp(response.main.temp)
        currentWeather.setHumidity(response.main.humidity)
        currentWeather.setDescription(response.weather.get(0).description)
        currentWeather.setMain(response.weather.get(0).main)
        currentWeather.setWeatherId(response.weather.get(0).id)
        currentWeather.setWindDeg(response.wind.deg)
        currentWeather.setWindSpeed(response.wind.speed)
        currentWeather.setStoreTimestamp(System.currentTimeMillis())
        showStoredCurrentWeather(currentWeather)
    }

    private fun showStoredCurrentWeather(currentWeather: CurrentWeather) {
        //val currentWeather: CurrentWeather = data[0]


        temp_text_view.setText(
            java.lang.String.format(
                Locale.getDefault(),
                "%.0f°",
                currentWeather.getTemp()
            )
        )
        description_text_view.setText(
            getWeatherStatus(
                currentWeather.getWeatherId(),
                isRTL(this@ProfileActivity)
            )
        )
        humidity_text_view.setText(
            java.lang.String.format(
                Locale.getDefault(), "%d%%", currentWeather.getHumidity()
            )
        )
        wind_text_view.setText(
            java.lang.String.format(
                Locale.getDefault(),
                resources.getString(R.string.wind_unit_label),
                currentWeather.getWindSpeed()
            )
        )
        animation_view.setAnimation(
            getWeatherAnimation(
                currentWeather.getWeatherId()
            )
        )
        animation_view.playAnimation()
    }

    private fun setupTextSwitchers() {
        binding.tempTextView.setFactory(
            TextViewFactory(
                this@ProfileActivity,
                R.style.TempTextView,
                true,
                Typeface.DEFAULT
            )
        )
        binding.tempTextView.setInAnimation(
            this@ProfileActivity,
            R.anim.slide_in_right
        )
        binding.tempTextView.setOutAnimation(
            this@ProfileActivity,
            R.anim.slide_out_left
        )
        binding.descriptionTextView.setFactory(
            TextViewFactory(
                this@ProfileActivity,
                R.style.DescriptionTextView,
                true,
                Typeface.DEFAULT
            )
        )
        binding.descriptionTextView.setInAnimation(
            this@ProfileActivity,
            R.anim.slide_in_right
        )
        binding.descriptionTextView.setOutAnimation(
            this@ProfileActivity,
            R.anim.slide_out_left
        )
        binding.humidityTextView.setFactory(
            TextViewFactory(
                this@ProfileActivity,
                R.style.HumidityTextView,
                false,
                Typeface.DEFAULT
            )
        )
        binding.humidityTextView.setInAnimation(
            this@ProfileActivity,
            R.anim.slide_in_bottom
        )
        binding.humidityTextView.setOutAnimation(
            this@ProfileActivity,
            R.anim.slide_out_top
        )
        binding.windTextView.setFactory(
            TextViewFactory(
                this@ProfileActivity,
                R.style.WindSpeedTextView,
                false,
                Typeface.DEFAULT
            )
        )
        binding.windTextView.setInAnimation(
            this@ProfileActivity,
            R.anim.slide_in_bottom
        )
        binding.windTextView.setOutAnimation(
            this@ProfileActivity,
            R.anim.slide_out_top
        )
    }


}