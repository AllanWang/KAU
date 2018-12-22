package ca.allanwang.kau.internal

import androidx.appcompat.app.AppCompatActivity
import ca.allanwang.kau.permissions.kauOnRequestPermissionsResult

/**
 * Created by Allan Wang on 2017-08-01.
 *
 * Base activity for any activity that would have extended [AppCompatActivity]
 *
 * Ensures that some singleton methods are called.
 * This is simply a convenience class;
 * you can always copy and paste this to your own class.
 */
abstract class KauBaseActivity : AppCompatActivity() {

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        kauOnRequestPermissionsResult(permissions, grantResults)
    }
}