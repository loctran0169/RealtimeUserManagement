package vn.edu.uit.realtimeuseraccountmanager.Utils

import android.text.TextUtils
import android.util.Patterns
import java.util.regex.Pattern

interface StringUtils {
    fun validateName(name: String): Boolean {
        val regex = "^[A-Z][a-zA-Z]{3,}(?: [A-Z][a-zA-Z]*){0,2}\$";
        val p = Pattern.compile(regex);
        if (name == null) {
            return false;
        }
        val matcher = p.matcher(name);
        return matcher.matches();
    }

    fun isEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}