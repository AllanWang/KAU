package ca.allanwang.kau.sample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import ca.allanwang.kau.utils.startActivitySlideOut

/**
 * Created by Allan Wang on 2017-06-12.
 *
 * Empty Activity for animations
 */
class AnimActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sample)
    }

    override fun onBackPressed() {
        startActivitySlideOut(MainActivity::class.java)
    }
}