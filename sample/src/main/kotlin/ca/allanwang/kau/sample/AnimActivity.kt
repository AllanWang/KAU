package ca.allanwang.kau.sample

import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import ca.allanwang.kau.logging.KL
import ca.allanwang.kau.permissions.*
import ca.allanwang.kau.utils.fullLinearRecycler
import ca.allanwang.kau.utils.startActivitySlideOut
import ca.allanwang.kau.utils.toast
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter

/**
 * Created by Allan Wang on 2017-06-12.
 *
 * Activity for animations
 * Now also showcases permissions
 */
class AnimActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val adapter = FastItemAdapter<PermissionCheckbox>()
        setContentView(fullLinearRecycler(adapter))
        adapter.add(listOf(
                PERMISSION_ACCESS_COARSE_LOCATION,
                PERMISSION_ACCESS_FINE_LOCATION
        ).map { PermissionCheckbox(it) })
        adapter.withOnClickListener { _, _, item, _ ->
            KL.d("Perm Click")
            kauRequestPermissions(item.permission) {
                granted, deniedPerm ->
                toast("${item.permission} enabled: $granted")
                adapter.notifyAdapterDataSetChanged()
            }
            true
        }
    }

    override fun onBackPressed() {
        startActivitySlideOut(MainActivity::class.java)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        kauOnRequestPermissionsResult(permissions, grantResults)
    }
}