package ca.allanwang.kau.sample

import android.os.Bundle
import ca.allanwang.kau.internal.KauBaseActivity
import ca.allanwang.kau.logging.KL
import ca.allanwang.kau.permissions.PERMISSION_ACCESS_COARSE_LOCATION
import ca.allanwang.kau.permissions.PERMISSION_ACCESS_FINE_LOCATION
import ca.allanwang.kau.permissions.PERMISSION_CAMERA
import ca.allanwang.kau.permissions.kauRequestPermissions
import ca.allanwang.kau.swipe.SWIPE_EDGE_LEFT
import ca.allanwang.kau.swipe.kauSwipeOnCreate
import ca.allanwang.kau.swipe.kauSwipeOnDestroy
import ca.allanwang.kau.utils.fullLinearRecycler
import ca.allanwang.kau.utils.startActivitySlideOut
import ca.allanwang.kau.utils.toast
import ca.allanwang.kau.utils.withAlpha
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter

/**
 * Created by Allan Wang on 2017-06-12.
 *
 * Activity for animations
 * Now also showcases permissions
 */
class AnimActivity : KauBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val adapter = FastItemAdapter<PermissionCheckbox>()
        setContentView(fullLinearRecycler(adapter).apply { setBackgroundColor(KPrefSample.bgColor.withAlpha(255)) })

        adapter.add(listOf(
                PERMISSION_ACCESS_COARSE_LOCATION,
                PERMISSION_ACCESS_FINE_LOCATION,
                PERMISSION_CAMERA
        ).map { PermissionCheckbox(it) })
        adapter.withOnClickListener { _, _, item, _ ->
            KL.d("Perm Click")
            kauRequestPermissions(item.permission) {
                granted, _ ->
                toast("${item.permission} enabled: $granted")
                adapter.notifyAdapterDataSetChanged()
            }
            true
        }
        kauSwipeOnCreate {
            edgeFlag = SWIPE_EDGE_LEFT
        }
    }

    override fun onDestroy() {
        kauSwipeOnDestroy()
        super.onDestroy()
    }

    override fun onBackPressed() {
        startActivitySlideOut(MainActivity::class.java)
    }

}