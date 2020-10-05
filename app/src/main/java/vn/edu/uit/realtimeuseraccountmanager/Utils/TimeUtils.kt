package vn.edu.uit.realtimeuseraccountmanager.Utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

interface TimeUtils {

    fun dateTimeToTimeStampFromString(time: String): Long {
        return SimpleDateFormat("dd/MM/yyyy").parse(time).time
    }

    fun getDateString(time: Long): String {
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        return simpleDateFormat.format(time)
    }
}