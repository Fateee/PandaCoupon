package com.panda.coupon.router;

import android.content.Context;

import com.lzh.nonview.router.RouterConfiguration;
import com.lzh.nonview.router.module.RouteCreator;
import com.panda.coupon.utils.ConstantUtils;

/**
 * 对Router框架做二次封装。利于在组件化环境下方便使用：
 * @see <a href="Router">https://github.com/yjfnypeu/Router</a>
 */
public class JMRouteManager {
    private static final String TAG = "JMRouteManager";
    private static JMRouteManager manager = new JMRouteManager();
    public static JMRouteManager get() {
        return manager;
    }

    private boolean inited = false;

    public void init(Context context) {
        if (inited) {
            return;
        }
        inited = true;

        loadRouteRulesIfExist();

        initRouteBaseConfig(context);
    }


    private void initRouteBaseConfig(Context context) {
        // 添加路由规则。
        RouterConfiguration.get().setInterceptor(DefaultInterceptor.get());
    }

    /**
     * 通过反射加载通过Router框架生成的路由映射表。此处会加载各个组件中通过运行时注解生成的路由表
     */
    private void loadRouteRulesIfExist() {
        String[] packs = ConstantUtils.PACK.PACKAGES;
        String clzNameRouteRules = ".RouterRuleCreator";
        for (String pack : packs) {
            try {
                Class<?> creator = Class.forName(pack + clzNameRouteRules);
                RouteCreator instance = (RouteCreator) creator.newInstance();
                RouterConfiguration.get().addRouteCreator(instance);
            } catch (Exception ignore) {
                ignore.printStackTrace();
            }
        }
    }
}
