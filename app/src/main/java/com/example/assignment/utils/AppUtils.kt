package com.example.assignment.utils

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.*
import android.media.Ringtone
import android.widget.Toast
import androidx.core.os.ConfigurationCompat
import com.example.assignment.R
import java.util.*

var ringTone : Ringtone ?= null

fun setLocale(activity: Activity, languageCode: String?) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)
    val resources: Resources = activity.resources
    val config: Configuration = resources.configuration
    config.setLocale(locale)
    resources.updateConfiguration(config, resources.displayMetrics)
}

fun showToast(context: Context , msg : String){
    Toast.makeText(context, msg , Toast.LENGTH_LONG).show()
}

fun getRoundedCornerBitmap(bitmap: Bitmap, pixels: Int): Bitmap? {
    val output = Bitmap.createBitmap(
        bitmap.width, bitmap
            .height, Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(output)
    val color = -0xbdbdbe
    val paint = Paint()
    val rect = Rect(0, 0, bitmap.width, bitmap.height)
    val rectF = RectF(rect)
    val roundPx = pixels.toFloat()
    paint.setAntiAlias(true)
    canvas.drawARGB(0, 0, 0, 0)
    paint.setColor(color)
    canvas.drawRoundRect(rectF, roundPx, roundPx, paint)
    paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_IN))
    canvas.drawBitmap(bitmap, rect, rect, paint)
    return output
}


fun getWeatherStatus(weatherCode: Int, isRTL: Boolean): String? {
    if (weatherCode / 100 == 2) {
        return if (isRTL) {
            WEATHER_STATUS_PERSIAN.get(0)
        } else {
            WEATHER_STATUS.get(0)
        }
    } else if (weatherCode / 100 == 3) {
        return if (isRTL) {
            WEATHER_STATUS_PERSIAN.get(1)
        } else {
            WEATHER_STATUS.get(1)
        }
    } else if (weatherCode / 100 == 5) {
        return if (isRTL) {
            WEATHER_STATUS_PERSIAN.get(2)
        } else {
            WEATHER_STATUS.get(2)
        }
    } else if (weatherCode / 100 == 6) {
        return if (isRTL) {
            WEATHER_STATUS_PERSIAN.get(3)
        } else {
            WEATHER_STATUS.get(3)
        }
    } else if (weatherCode / 100 == 7) {
        return if (isRTL) {
            WEATHER_STATUS_PERSIAN.get(4)
        } else {
            WEATHER_STATUS.get(4)
        }
    } else if (weatherCode == 800) {
        return if (isRTL) {
            WEATHER_STATUS_PERSIAN.get(5)
        } else {
            WEATHER_STATUS.get(5)
        }
    } else if (weatherCode == 801) {
        return if (isRTL) {
            WEATHER_STATUS_PERSIAN.get(6)
        } else {
            WEATHER_STATUS.get(6)
        }
    } else if (weatherCode == 803) {
        return if (isRTL) {
            WEATHER_STATUS_PERSIAN.get(7)
        } else {
            WEATHER_STATUS.get(7)
        }
    } else if (weatherCode / 100 == 8) {
        return if (isRTL) {
            WEATHER_STATUS_PERSIAN.get(8)
        } else {
            WEATHER_STATUS.get(8)
        }
    }
    return if (isRTL) {
        WEATHER_STATUS_PERSIAN.get(4)
    } else {
        WEATHER_STATUS.get(4)
    }
}


fun getWeatherAnimation(weatherCode: Int): Int {
    if (weatherCode / 100 == 2) {
        return R.raw.storm_weather
    } else if (weatherCode / 100 == 3) {
        return R.raw.rainy_weather
    } else if (weatherCode / 100 == 5) {
        return R.raw.rainy_weather
    } else if (weatherCode / 100 == 6) {
        return R.raw.snow_weather
    } else if (weatherCode / 100 == 7) {
        return R.raw.unknown
    } else if (weatherCode == 800) {
        return R.raw.clear_day
    } else if (weatherCode == 801) {
        return R.raw.few_clouds
    } else if (weatherCode == 803) {
        return R.raw.broken_clouds
    } else if (weatherCode / 100 == 8) {
        return R.raw.cloudy_weather
    }
    return R.raw.unknown
}

fun isRTL(context: Context): Boolean {
    val locale = ConfigurationCompat.getLocales(context.resources.configuration)[0]
    val directionality = Character.getDirectionality(locale!!.displayName[0]).toInt()
    return directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT.toInt() || directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC.toInt()
}