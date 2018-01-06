package ca.allanwang.kau.email

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.support.annotation.StringRes
import android.util.DisplayMetrics
import ca.allanwang.kau.R
import ca.allanwang.kau.logging.KL
import ca.allanwang.kau.utils.installerPackageName
import ca.allanwang.kau.utils.isAppInstalled
import ca.allanwang.kau.utils.string
import ca.allanwang.kau.utils.toast


/**
 * Created by Allan Wang on 2017-06-20.
 */
class EmailBuilder(val email: String, val subject: String) {
    var message: String = "Write here."
    var deviceDetails: Boolean = true
    var appInfo: Boolean = true
    var footer: String? = null
    private val pairs: MutableMap<String, String> = mutableMapOf()
    private val packages: MutableList<Package> = mutableListOf()
    private val attachments: ArrayList<Uri> = arrayListOf()

    fun checkPackage(packageName: String, appName: String) = packages.add(Package(packageName, appName))

    fun addItem(key: String, value: String) = pairs.put(key, value)

    fun addAttachment(uri: Uri) = attachments.add(uri)

    var extras: Intent.() -> Unit = {}

    data class Package(val packageName: String, val appName: String)

    fun getIntent(context: Context): Intent {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("mailto:$email"))
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        val emailBuilder = StringBuilder()
        emailBuilder.append(message).append("\n\n")
        if (deviceDetails) {
            val deviceItems = mutableMapOf(
                    "OS Version" to "${System.getProperty("os.version")} (${Build.VERSION.INCREMENTAL})",
                    "OS SDK" to Build.VERSION.SDK_INT,
                    "Device (Manufacturer)" to "${Build.DEVICE} (${Build.MANUFACTURER})",
                    "Model (Product)" to "${Build.MODEL} (${Build.PRODUCT})",
                    "Package Installer" to (context.installerPackageName ?: "None")
            )
            if (context is Activity) {
                val metric = DisplayMetrics()
                context.windowManager.defaultDisplay.getMetrics(metric)
                deviceItems.put("Screen Dimensions", "${metric.widthPixels} x ${metric.heightPixels}")
            }
            deviceItems.forEach { (k, v) -> emailBuilder.append("$k: $v\n") }
        }
        if (appInfo) {
            try {
                val appInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                emailBuilder.append("\nApp: ").append(context.packageName)
                        .append("\nApp Version Name: ").append(appInfo.versionName)
                        .append("\nApp Version Code: ").append(appInfo.versionCode).append("\n")
            } catch (e: PackageManager.NameNotFoundException) {
                KL.e { "EmailBuilder packageInfo not found" }
            }
        }

        if (packages.isNotEmpty()) emailBuilder.append("\n")
        packages.forEach {
            if (context.isAppInstalled(it.packageName))
                emailBuilder.append(String.format("\n%s is installed", it.appName))
        }

        if (pairs.isNotEmpty()) emailBuilder.append("\n")
        pairs.forEach { (k, v) -> emailBuilder.append("$k: $v\n") }

        if (footer != null)
            emailBuilder.append("\n").append(footer)

        intent.putExtra(Intent.EXTRA_TEXT, emailBuilder.toString())
        return intent
    }

    /**
     * Create the intent and send the request when possible
     * If a stream uri is added, it will automatically be flagged to pass on read permissions
     */
    fun execute(context: Context) {
        val intent = getIntent(context)
        intent.extras()
        val packageName = intent.resolveActivity(context.packageManager)?.packageName
                ?: return context.toast(R.string.kau_error_no_email, log = true)

        if (attachments.isNotEmpty()) {
            attachments.forEach { context.grantUriPermission(packageName, it, Intent.FLAG_GRANT_READ_URI_PERMISSION) }
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, attachments)
        }

        context.startActivity(Intent.createChooser(intent, context.string(R.string.kau_send_via)))
    }
}

fun Context.sendEmail(@StringRes emailId: Int, @StringRes subjectId: Int, builder: EmailBuilder.() -> Unit = {})
        = sendEmail(string(emailId), string(subjectId), builder)


fun Context.sendEmail(email: String, subject: String, builder: EmailBuilder.() -> Unit = {}) {
    EmailBuilder(email, subject).apply {
        builder()
        execute(this@sendEmail)
    }
}