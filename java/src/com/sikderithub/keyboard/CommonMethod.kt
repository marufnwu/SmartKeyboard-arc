package com.sikderithub.keyboard

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Insets
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.Transformation
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.annotation.ColorInt
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


object CommonMethod {

    var ytChannel: String? = null
    var fbPage: String? = null
    var accountAge : String? = null



    fun disableScreenCapture(activity: Activity){
        activity.window.setFlags(WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE)
    }


    fun getCurrentTimeUsingFormat(format: String,locale: Locale): String {
        val dateFormat: SimpleDateFormat = SimpleDateFormat(format, locale)
        return dateFormat.format(Date()).toString()
    }

    fun getCurrentTime(): String {
        val date = Date()
        return date.time.toString()
    }


    fun getHoursDifBetweenToTime(startTime: String, endTime: String): String? {
        return try {
            val date1 = startTime.toLong()
            val date2 = endTime.toLong()
            val difference = date2 - date1
            val differenceDates = difference / (60 * 60 * 1000)
            differenceDates.toString()
        } catch (exception: Exception) {
            null
        }
    }
    fun getHoursDifBetweenToTime(startTime: Long, endTime: Long): Long {
        val date1 = startTime.toLong()
        val date2 = endTime.toLong()
        val difference = date1-date2
        val d = TimeUnit.MILLISECONDS.toHours(difference)
        Log.d("c", d.toString())
        return d
    }

    fun getMinDifBetweenToTime(startTime: Long, endTime: Long): Long {
        val date1 = startTime.toLong()
        val date2 = endTime.toLong()
        val difference = date1-date2
        val d = TimeUnit.MILLISECONDS.toMinutes(difference)
        Log.d("c", d.toString())
        return d
    }

    fun getSecDifBetweenToTime(startTime: Long, endTime: Long): Long {
        val date1 = startTime.toLong()
        val date2 = endTime.toLong()
        val difference = date1-date2
        val d = TimeUnit.MILLISECONDS.toSeconds(difference)
        return d
    }


    fun getMinuteDifBetweenToTime(startTime: String, endTime: String): String? {
        return try {
            val date1 = startTime.toLong()
            val date2 = endTime.toLong()
            val difference = date2 - date1
            val differenceDates = difference / (60 * 1000)
            differenceDates.toString()
        } catch (exception: Exception) {
            null
        }
    }

    fun getMimeTypeFromUrl(url: String): String? {
        var type: String?=null
        val typeExtension: String=MimeTypeMap.getFileExtensionFromUrl(url)
        if (!typeExtension.isNullOrEmpty()) {
            type=MimeTypeMap.getSingleton().getMimeTypeFromExtension(typeExtension)
        }
        return type
    }


    fun openAppLink(context: Context) {
        val appPackageName: String=context.applicationContext.packageName
        try {
            val appIntent: Intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            appIntent.setPackage("com.android.vending")
            context.startActivity(appIntent)
        } catch (e: ActivityNotFoundException) {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }

    fun shareAppLink(context: Context) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=${context.applicationContext.packageName}")
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        context.startActivity(shareIntent)
    }

    fun openConsoleLink(context: Context,consoleId: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_VIEW
            data=Uri.parse("https://play.google.com/store/apps/developer?id=$consoleId")
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        context.startActivity(shareIntent)
    }


    fun openVideo(context: Context, videoId: String) {
        val appIntent: Intent= Intent(Intent.ACTION_VIEW,Uri.parse("vnd.youtube:$videoId"))
        try {
            context.startActivity(appIntent)
        } catch (e: Exception) {
            val webIntent: Intent= Intent(Intent.ACTION_VIEW,Uri.parse("http://www.youtube.com/watch?v=$videoId"))
            context.startActivity(Intent.createChooser(webIntent,"Choose one:"))
        }
    }

    fun haveInternet(connectivityManager: ConnectivityManager?): Boolean {
        return when {
            connectivityManager==null -> {
                false
            }
            Build.VERSION.SDK_INT >= 23 -> {
                val network = connectivityManager.activeNetwork
                val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || capabilities.hasTransport(
                    NetworkCapabilities.TRANSPORT_WIFI
                )
            }
            else -> {
                (connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!.isAvailable
                        && connectivityManager.activeNetworkInfo!!.isConnected)
            }
        }
    }

    fun getScreenWidth(activity: Activity): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = activity.windowManager.currentWindowMetrics
            val insets: Insets = windowMetrics.windowInsets
                .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            windowMetrics.bounds.width() - insets.left - insets.right
        } else {
            val displayMetrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.widthPixels
        }
    }

    fun getScreenHeight(activity: Activity): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = activity.windowManager.currentWindowMetrics
            val insets: Insets = windowMetrics.windowInsets
                .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            windowMetrics.bounds.width() - insets.left - insets.right
        } else {
            val displayMetrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.heightPixels
        }
    }


    fun dpToPix(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,context.resources.displayMetrics).toInt()
    }

    fun subStringFromString(value: String, length: Int): String {
        return value.substring(0,value.length-length)
    }


    fun openWhatsapp(ctx:Context, whatsAppBtn:View, mobile:String){
       whatsAppBtn.setOnClickListener {
            try {
                val msg = ""
                ctx.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://api.whatsapp.com/send?phone=$mobile&text=$msg")
                    )
                )
            } catch (e: java.lang.Exception) {
                Toast.makeText(
                    ctx,
                    "WhatsApp not Installed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun openLink(context: Context, url:String?){

        if(url==null){
            return
        }


        val linkHost = Uri.parse(url).host
        val uri = Uri.parse(url)
        if (linkHost == null) {
            return
        }
        if (linkHost == "play.google.com") {
            val appId = uri.getQueryParameter("id")
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("market://details?id=$appId")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } else if (linkHost == "www.youtube.com") {
            try {
                val intent = Intent(Intent.ACTION_VIEW, uri)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.setPackage("com.google.android.youtube")
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                context.startActivity(intent)
            }catch (e : Exception){
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                context.startActivity(intent)
            }
        }
        else if (url != null && (url.startsWith("http://") || url.startsWith(
                "https://"
            ))
        ) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    fun keepScreenOn(ctx: Context){
        (ctx as Activity).window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    fun isVideoAppInstalled(context: Context): Boolean {
        val uri = "com.lmp.video"
        val pm = context.packageManager
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
            return true
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return false
    }

    fun openMediaPlayerApp(ctx:Context){
        val intent =  Intent(android.content.Intent.ACTION_VIEW);

        intent.data = Uri.parse("https://play.google.com/store/apps/details?id=com.lmp.video");

        ctx.startActivity(intent);
    }

    fun getLotteryMiddle(number:String): String{
        return number.toCharArray(number.length-4, (number.length-4)+2).joinToString("")
    }

    fun  getLast2digit(number:String) : String{
        return number.toCharArray(number.length-2, (number.length)).joinToString("")
    }


    fun currentVersionCode(): Int {
        return BuildConfig.VERSION_CODE
    }



    @SuppressLint("HardwareIds")
    fun deviceId(context: Context) : String {
        val id: String = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )

        return id

    }

    fun generateMiddleSerial() : MutableList<String> {
        val list = mutableListOf<String>()
        for (n in 0..100){
            if(n<10){
                list.add("0$n")
            }else{
                list.add("$n")
            }
        }

        return list
    }

    fun expand(v: View) {
        val matchParentMeasureSpec =
            View.MeasureSpec.makeMeasureSpec((v.parent as View).width, View.MeasureSpec.EXACTLY)
        val wrapContentMeasureSpec =
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        v.measure(matchParentMeasureSpec, wrapContentMeasureSpec)
        val targetHeight = v.measuredHeight

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.layoutParams.height = 1
        v.visibility = View.VISIBLE
        val a: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                v.layoutParams.height =
                    if (interpolatedTime == 1f) ViewGroup.LayoutParams.WRAP_CONTENT else (targetHeight * interpolatedTime).toInt()
                v.requestLayout()
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        // Expansion speed of 1dp/ms
        a.duration = (targetHeight / v.context.resources.displayMetrics.density).toInt().toLong()
        v.startAnimation(a)
    }

    fun collapse(v: View) {
        val initialHeight = v.measuredHeight
        val a: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                if (interpolatedTime == 1f) {
                    v.visibility = View.GONE
                } else {
                    v.layoutParams.height =
                        initialHeight - (initialHeight * interpolatedTime).toInt()
                    v.requestLayout()
                }
            }

            override fun willChangeBounds(): Boolean {
                return true
            }
        }

        // Collapse speed of 1dp/ms
        a.duration = (initialHeight / v.context.resources.displayMetrics.density).toInt().toLong()
        v.startAnimation(a)
    }

     fun viewGoneAnimator(view: View) {
        view.animate()
            .alpha(0f)
            .setDuration(300)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    view.visibility = View.GONE
                }
            })
    }

     fun viewVisibleAnimator(view: View) {
        view.animate()
            .alpha(1f)
            .setDuration(300)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    view.visibility = View.VISIBLE
                }
            })
    }

    @ColorInt
    fun getContrastColor(@ColorInt color: Int): Int {
        // Counting the perceptive luminance - human eye favors green color...
        val a: Double = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
        val d: Int
        d = if (a < 0.5) {
            0 // bright colors - black font
        } else {
            255 // dark colors - white font
        }
        return Color.rgb(d, d, d)
    }

    fun getOppositeColor(color: Int): Int {
        // Extract the RGB components from the color integer
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)

        // Calculate the complementary color by subtracting each component from 255
        val oppositeRed = 255 - red
        val oppositeGreen = 255 - green
        val oppositeBlue = 255 - blue

        // Combine the RGB components into a single color integer
        return Color.rgb(oppositeRed, oppositeGreen, oppositeBlue)
    }

}