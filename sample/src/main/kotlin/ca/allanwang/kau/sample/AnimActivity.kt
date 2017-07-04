package ca.allanwang.kau.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import ca.allanwang.kau.utils.fullLinearRecycler
import ca.allanwang.kau.utils.startActivitySlideOut
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
        val adapter = FastItemAdapter<KP>
        val recycler = fullLinearRecycler {

        }
        setContentView(R.layout.sample)
    }

    override fun onBackPressed() {
        startActivitySlideOut(MainActivity::class.java)
    }
}