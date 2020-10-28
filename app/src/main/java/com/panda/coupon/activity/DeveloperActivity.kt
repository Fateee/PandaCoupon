package com.panda.coupon.activity

import android.os.Bundle
import com.panda.coupon.R
import com.panda.coupon.utils.ConstantUtils
import com.panda.coupon.utils.JMToast
import com.panda.coupon.utils.SpUtil
import kotlinx.android.synthetic.main.activity_developer.*
import kotlin.system.exitProcess


class DeveloperActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_developer)
        setPageTitle("开发者选项")
        var isRd = SpUtil.getInstace().getBoolean(ConstantUtils.KEY_URL_RD,false)
        if (isRd) {
            rg.check(R.id.test)
        } else {
            rg.check(R.id.product)
        }
        var selectId = rg.checkedRadioButtonId
        rg.setOnCheckedChangeListener { group, checkedId ->
            if (selectId == checkedId) return@setOnCheckedChangeListener
            when (checkedId) {
                R.id.product -> SpUtil.getInstace().save(ConstantUtils.KEY_URL_RD,false)
                R.id.test -> SpUtil.getInstace().save(ConstantUtils.KEY_URL_RD,true)
            }
            JMToast.show("即将退出，请重新打开app以切换环境。")
            rg.postDelayed({
                exitProcess(0)
            },3000)
        }
    }

    override fun hideToolbar() = false
}
