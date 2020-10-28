package com.panda.coupon.router;

import com.lzh.nonview.router.module.ActionRouteRule;
import com.lzh.nonview.router.module.ActivityRouteRule;
import com.lzh.nonview.router.module.RouteCreator;
import com.lzh.nonview.router.module.RouteRule;
import com.panda.coupon.activity.ScanCodeActivity;
import com.panda.coupon.activity.WebViewActivity;

import java.util.HashMap;
import java.util.Map;

public class RouterRuleCreator implements RouteCreator {
    @Override
    public Map<String, ActivityRouteRule> createActivityRouteRules() {
        Map<String, ActivityRouteRule> routes = new HashMap<>();
        routes.put(RouteSchema.WEB_VIEW, new ActivityRouteRule(WebViewActivity.class)
                .addParam("url", RouteRule.STRING));
        routes.put(RouteSchema.PAGE_SCAN,new ActivityRouteRule(ScanCodeActivity.class));
        return routes;
    }

    @Override
    public Map<String, ActionRouteRule> createActionRouteRules() {
        Map<String, ActionRouteRule> routes = new HashMap<>();
        return routes;
    }
}
