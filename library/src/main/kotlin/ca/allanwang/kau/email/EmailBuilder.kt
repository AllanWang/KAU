package ca.allanwang.kau.email

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.support.annotation.StringRes
import ca.allanwang.kau.R
import ca.allanwang.kau.logging.KL
import ca.allanwang.kau.utils.isAppInstalled
import ca.allanwang.kau.utils.string


/**
 * Created by Allan Wang on 2017-06-20.
 */
class EmailBuilder(@StringRes val emailId: Int, @StringRes val subjectId: Int) {
    var message: String = "Write here."
    var deviceDetails: Boolean = true
    var appInfo: Boolean = true
    var footer: String? = null
    private val packages: MutableList<Package> = mutableListOf()

    fun checkPackage(packageName: String, appName: String) = packages.add(Package(packageName, appName))

    data class Package(val packageName: String, val appName: String)

    fun execute(context: Context) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + context.string(emailId)))
        intent.putExtra(Intent.EXTRA_SUBJECT, context.string(subjectId))
        val emailBuilder = StringBuilder()
        emailBuilder.append(message).append("\n\n")
        if (deviceDetails)
            emailBuilder.append("\nOS Version: ").append(System.getProperty("os.version")).append("(").append(Build.VERSION.INCREMENTAL).append(")")
                    .append("\nOS API Level: ").append(Build.VERSION.SDK_INT)
                    .append("\nDevice: ").append(Build.DEVICE)
                    .append("\nManufacturer: ").append(Build.MANUFACTURER)
                    .append("\nModel (and Product): ").append(Build.MODEL).append(" (").append(Build.PRODUCT).append(")")
                    .append("\n")

        if (appInfo) {
            try {
                val appInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                emailBuilder.append("\nApp Version Name: ").append(appInfo.versionName)
                        .append("\nApp Version Code: ").append(appInfo.versionCode).append("\n")
            } catch (e: PackageManager.NameNotFoundException) {
                KL.e("EmailBuilder packageInfo not found")
            }
        }

        packages.forEach {
            if (context.isAppInstalled(it.packageName))
                emailBuilder.append(String.format("\n%s is installed", it.appName))
        }

        if (footer != null)
            emailBuilder.append("\n\n").append(footer)

        intent.putExtra(Intent.EXTRA_TEXT, emailBuilder.toString())
        context.startActivity(Intent.createChooser(intent, context.resources.getString(R.string.kau_send_via)))
    }
}

fun Context.sendEmail(@StringRes emailId: Int, @StringRes subjectId: Int, builder: EmailBuilder.() -> Unit = {}) {
    EmailBuilder(emailId, subjectId).apply {
        builder()
        execute(this@sendEmail)
    }
}