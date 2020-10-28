package com.panda.coupon.utils;


import org.jetbrains.annotations.Nullable;

/**
 * Created by congxiany on 18/3/5.
 */

public class ConstantUtils {

    public static String PRODUCT_URL = "http://bop.pandayoufu.com/";
    public static String RD_URL = "http://bop-test.pandayoufu.com/";

    public static final String TYPE_SETTINGS = "settings";
    public static final String TYPE_USER = "user";

    public static final String KEY_USER_UID_HEAD = "head_";
    public static final String KEY_LOCK_UID_HEAD = "lock_head_";
    public static final String KEY_USER_UID = "uid";
    public static final String KEY_USER_SSID = "sid";
    public static final String KEY_USER_ENV = "currentEnv";
    public static final String KEY_RN_ENV = "env";
    public static final String KEY_RN_API_ENV = "apiEnv";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_API_ENV = "key_api_env";
    public static final String KEY_API_LOGIN_INFO = "key_api_login_info";
    public static final String KEY_API_ENV_COUNTRY = "key_api_env_country";
    public static final String KEY_CURRENT_COUNTRY = "key_current_country";
    public static final String KEY_LOGIN_ENV_ENABLE = "login_env_enable";
    public static final String KEY_USER_EMAIL = "key_user_email";
    //    public static final String KEY_ACCESS_TOKEN = "key_access_token";
    public static final String KEY_REFRESH_TOKEN = "key_refresh_token";
    public static final String KEY_TAB_BAR = "tabbar_v3_2_7_1";
    public static final String KEY_WORK_SPACE = "work_space";
    //    public static final String KEY_LOCK_STRING = "key_lock_string";
//    public static final String KEY_LOCK_UID_STRING = "key_lock_sid_string";
    public static final String KEY_LOCK_LAST_TIME = "key_lock_last_time";
    public static final String KEY_FIRST_SHOW_WORKSPACE_TOP = "key_first_show_workspace_top";
    public static final String KEY_LOCK_TIMES = "key_lock_times";
    public static final String KEY_IS_REFRESH_TOKEN = "key_is_refresh_token";
    public static final String COOKIE = "cookie";
    public static final String KEY_DEVICE_ID = "key_device_id";
    public static final String LOGIN_NOTICE = "login_notice";//登录
    public static final String SCAN_RESULT_NOTICE = "scan_result";//扫描结果
    public static final String LOCK_VIEW_STATE_CHANGE = "lock_view_state_change";//登录
    public static final String DISMISS_RN = "dismiss_rn";//dismiss
    public static final String CHOOSE_LOCATION = "choose_location";//地图选择位置

    public static final String HIDE_BOTTOM_NOTICE = "hide_bottom_notice";//隐藏底部bar
    public static final String EXIT_APP = "exit_app";//隐藏底部bar
    public static final String DIS_CLICKABLE = "dis_clickable";//隐藏底部bar
    public static final String JUMP_NEW_WEB = "jump_new_web";//跳转新webview页面
    public static final String GO_TO_LOGIN = "go_to_login";//登录请求
    public static final String REFRESH_TAB_INDICATION = "refresh_tab_indication";//刷新底部红点请求
    public static final String SWITCH_TAB = "switch_tab";
    public static final String KEY_HTTP_CAPTURE = "http_capture";
    public static final String UPDATE_WATERMARK = "update_watermark";
    public static final String KEY_WATERMARK = "watermark";
    public static final String KEY_HOME_GUIDE_DISPLAYED = "key_home_guide_displayed"; //是否展示过新手引导
    public static final String KEY_HOME_GUIDE_IS_MAINTAINER = "key_home_guide_is_maintainer"; //是否展示新手引导 1不展示

    public static final String ACTION_REPLACE_URL = "ankerboxmanager.WebViewActivity.replace"; //替换webview url

    public static final int CODE_SHARE_ERROR = 41;//分享失败

    public static String[] SHARE_TITLES = {"微信好友", "朋友圈", "QQ好友", "QQ空间", "新浪微博", "复制链接"};
//    public static int[] SHARE_IMGS = {R.drawable.share_weixin_friend, R.drawable.share_weixin_friend_zone, R.drawable.share_qq, R.drawable.share_qq_zone, R.drawable.share_weibo, R.drawable.share_link};
//    public static String[] SHARE_TYPES = {SsoShareType.WEIXIN_FRIEND, SsoShareType.WEIXIN_FRIEND_ZONE, SsoShareType.QQ_FRIEND, SsoShareType.QQ_ZONE, SsoShareType.WEIBO_TIME_LINE, SsoShareType.COPY_LINK};

    public static final String KEY_TODO_MAP_FULL_SELECTED = "key_todo_map_full_selected"; //是否选中满电池热力图
    public static final String KEY_TODO_MAP_LACK_SELECTED = "key_todo_map_lack_selected"; //是否选中缺电池热力图






    ///////////
    public static final int CODE_SUCC = 0;//接口正常

    public static final int CODE_COMPATIBLE = -110;//不支持该接口

    public static final int CODE_LOGIN_CANCEL = 11;//取消登录

    public static final int CODE_CAMERA_CANCEL = 21;//取消扫码

    public static final int CODE_CAMERA_ERROR = 22;//相机错误

    public static final int CODE_CAMERA_NO_PERMISSION = 23;//无相机权限

    public static final int CODE_LOCATION_ERROR = 31;//定位失败

    public static final String TYPE_SCAN = "scan";

    public static final String TYPE_SCAN_INPUT = "input";

    public static final String TYPE_SCAN_All = "all";

    public static final String ACTION_SET_PAGETITLE = "onSetPageTitleAction";

    public static final String ACTION_LOGOUT = "onLogoutAction";

    public static final String ACTION_REACT = "onReactAction";

    public static final String KEY_URL_RD = "URL_RD";

    public static final class PACK {
        public static final String MAIN = "com.panda.coupon";
        /**
         * JMRouteManager中使用此数组数据来加载所有的路由映射表。
         */
        public static final String[] PACKAGES = new String[] {MAIN};
    }
}
