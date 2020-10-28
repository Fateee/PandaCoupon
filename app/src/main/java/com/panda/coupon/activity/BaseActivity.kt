package com.panda.coupon.activity

import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.panda.coupon.R

import kotlinx.android.synthetic.main.activity_main.*

open class BaseActivity : AppCompatActivity() {


    override fun setContentView(layoutResID: Int) {
        if (!hideToolbar()) {
            super.setContentView(R.layout.activity_main)
            dynamicAddContentLayout(layoutResID)
        } else {
            super.setContentView(layoutResID)
        }
    }
    private fun dynamicAddContentLayout(layoutId: Int) {
        if (layout_coordinator !is CoordinatorLayout) {
            throw IllegalArgumentException("CoordinatorLayout must be root")
        }
        val mContentView = layoutInflater.inflate(layoutId, null)
//        val layoutParams = mContentView.layoutParams
        //layoutParams.setBehavior(new AppBarLayout.ScrollingViewBehavior());
        content_layout.addView(mContentView)
        setSupportActionBar(toolbar)
    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.menu_main, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        return when (item.itemId) {
//            R.id.action_settings -> true
//            else -> super.onOptionsItemSelected(item)
//        }
//    }

    open fun hideToolbar(): Boolean {
        return true
    }

    fun setPageTitle(title: String) {
        setTitle(title)
    }

}
