package com.panda.coupon.router;

/**
 * Created by congxiany on 18/3/7.
 */

public class RouteSchema {
    public static final String PREFIX_ROUTE_PD = "pd://";

    public static final String PREFIX_PANDA = "panda://";

    public static final String MIDDLE_PAGE = "page/";

    public static final String MIDDLE_JSBRIDGE = "jsbridge/";

    public static final String mid_action = "action/";

    public static final String PAGE_SCAN = PREFIX_ROUTE_PD + MIDDLE_PAGE + "scan";//扫码页

    public static final String WEB_VIEW = PREFIX_ROUTE_PD + MIDDLE_PAGE + "webview";//扫码页

    public static final String PDF_VIEW = PREFIX_ROUTE_PD + MIDDLE_PAGE + "pdfView";//扫码页

    public static final String LOGIN = PREFIX_ROUTE_PD + MIDDLE_PAGE + "login";//扫码页

    public static final String LOCK_VIEW = PREFIX_ROUTE_PD + MIDDLE_PAGE + "lockView";//扫码页

    public static final String LOCK_VIEW_SETTING = PREFIX_ROUTE_PD + MIDDLE_PAGE + "lockViewSetting";//扫码页

    public static final String LOCK_VIEW_ITEM_MAIN = PREFIX_ROUTE_PD + MIDDLE_PAGE + "lockViewItemMain";//扫码页

    public static final String CHOOSE_LOCATION = PREFIX_ROUTE_PD + MIDDLE_PAGE + "ChooseLocation";//地图选择位置

    public static final String TODO_MAP_VIEW = PREFIX_PANDA + MIDDLE_PAGE + "operation_workspace_map";//运维待办地图页面

    public static final String TODO_MAP_DEVICE_MOVE_VIEW = PREFIX_PANDA + MIDDLE_PAGE + "operation_workspace_map_diff";//运维待办位移柜机页面

    public static final String PAGE_PREVIEW_FILES = PREFIX_ROUTE_PD + MIDDLE_PAGE + "preview_files";//图片预览页

    /******action***/
    public static final String ACTION_UPDATE = mid_action + "app_update";
    /**
     * 分享
     */
    public static final String share_action = mid_action + "share";
    /*************************************jsbridge****************************************************/

    public static final String JS_SET_PAGE_TITLE = MIDDLE_JSBRIDGE + "setTitle";
    public static final String JS_SCAN_CODE = MIDDLE_JSBRIDGE + "qrcode";
    public static final String JS_JUMP = MIDDLE_JSBRIDGE + "jump";
    public static final String JS_APP_COLD_START = MIDDLE_JSBRIDGE + "appColdStart";

    public static final String JS_GET_USER_INFO = MIDDLE_JSBRIDGE + "get_user_info";

    public static final String JS_GET_LOCATION = MIDDLE_JSBRIDGE + "get_location";

    public static final String JS_NAVIGATE = MIDDLE_JSBRIDGE + "navigate";

    public static final String JS_MAKE_PHONE = MIDDLE_JSBRIDGE + "make_phone_call";

    public static final String JS_CONNECT_WIFI = MIDDLE_JSBRIDGE + "connect_wifi";

    public static final String JS_GET_WIFI_LIST = MIDDLE_JSBRIDGE + "get_wifi_list";

    public static final String JS_DECRYPT = MIDDLE_JSBRIDGE + "decrypt_password";

    public static final String JS_TOOGLE_BAR = MIDDLE_JSBRIDGE + "toggle_bottom_bar";//切换底栏

    public static final String JS_OPTION_BUTTON = MIDDLE_JSBRIDGE + "set_option_button";//设置右上角按钮

    public static final String JS_OPTION_BUTTON_CLICK = MIDDLE_JSBRIDGE + "option_button_click";//监听右上角点击

    public static final String JS_TOOGLE_NAVIGATION_BAR = MIDDLE_JSBRIDGE + "toggle_navigation_bar";//切换导航栏

    public static final String JS_TOGGLE_BACK_BUTTON = MIDDLE_JSBRIDGE + "toggle_back_button";//切换后退按钮

    public static final String JS_BLUETOOTH_OPEN = MIDDLE_JSBRIDGE + "open_bluetooth_adapter";//开启蓝牙模块

    public static final String JS_BLUETOOTH_CLOSE = MIDDLE_JSBRIDGE + "close_bluetooth_adapter";//关闭蓝牙模块

    public static final String JS_BLUETOOTH_STATUS = MIDDLE_JSBRIDGE + "get_bluetooth_adapter_state";//获取蓝牙状态

    public static final String JS_BLUETOOTH_STATUS_LISTENER = MIDDLE_JSBRIDGE + "bluetooth_adapter_state_change";//监听蓝牙状态变化

    public static final String JS_BLUETOOTH_START_DISCOVERY = MIDDLE_JSBRIDGE + "start_bluetooth_devices_discovery";//开始搜寻蓝牙设备

    public static final String JS_BLE_START_DISCOVERY = MIDDLE_JSBRIDGE + "start_ble_bluetooth_devices_discovery";//开始搜寻BLE蓝牙设备

    public static final String JS_BLUETOOTH_STOP_DISCOVERY = MIDDLE_JSBRIDGE + "stop_bluetooth_devices_discovery";//停止搜寻蓝牙设备

    public static final String JS_BLE_STOP_DISCOVERY = MIDDLE_JSBRIDGE + "stop_ble_bluetooth_devices_discovery";//停止搜寻蓝牙设备

    public static final String JS_BLUETOOTH_DEVICES = MIDDLE_JSBRIDGE + "get_bluetooth_devices";//搜索到的蓝牙设备

    public static final String JS_CLOSE_CONTAINER = MIDDLE_JSBRIDGE + "close_h5_container";

    public static final String JS_GET_APP_INFO = MIDDLE_JSBRIDGE + "get_app_info";

    public static final String JS_LOGOUT = MIDDLE_JSBRIDGE + "logout";

    public static final String JS_SET_STORAGE = MIDDLE_JSBRIDGE + "set_storage";

    public static final String JS_GET_STORAGE = MIDDLE_JSBRIDGE + "get_storage";

    public static final String JS_REPORT_ERROR = MIDDLE_JSBRIDGE + "report_error";

    public static final String JS_GESTURE_PASSWORD = MIDDLE_JSBRIDGE + "gesture_password";

    public static final String JS_CHOOSE_IMAGE = MIDDLE_JSBRIDGE + "choose_image";

    public static final String JS_ON_CHOOSE_IMAGE_UPLOAD = MIDDLE_JSBRIDGE + "on_choose_image_upload";

    public static final String JS_ON_IMAGE_UPLOADING = MIDDLE_JSBRIDGE + "on_image_uploading";

    public static final String JS_ON_DELETE_UPLOADING = MIDDLE_JSBRIDGE + "delete_uploading_img";

    public static final String JS_ON_RETRY_IMG_UPLOAD = MIDDLE_JSBRIDGE + "retry_img_upload";

    public static final String JS_UPDATE_APP = MIDDLE_JSBRIDGE + "update_app";

    public static final String JS_PULL_REFRESH = MIDDLE_JSBRIDGE + "pull_refresh";

    public static final String JS_BLUETOOTH_DEVICE_FOUND_LISTENER = MIDDLE_JSBRIDGE + "bluetooth_device_found";//搜索到蓝牙设备的监听

    public static final String JS_BLUETOOTH_DEVICE_STATUS = MIDDLE_JSBRIDGE + "get_connected_bluetooth_devices";//依据UUID获取设备状态

    public static final String JS_BLUETOOTH_OPEN_BLE_CONNECTION = MIDDLE_JSBRIDGE + "create_ble_connection";//连接低功耗蓝牙设备

    public static final String JS_BLUETOOTH_CLOSE_BLE_CONNECTION = MIDDLE_JSBRIDGE + "close_ble_connection";//断开连接低功耗蓝牙设备

    public static final String JS_BLUETOOTH_BLE_CONNECTION_STATUS = MIDDLE_JSBRIDGE + "ble_connection_state_change";//监听低功耗蓝牙连接状态改变

    public static final String JS_BLUETOOTH_BLE_DEVICES = MIDDLE_JSBRIDGE + "get_ble_device_services";//获取蓝牙设备所有服务

    public static final String JS_BLUETOOTH_BLE_DEVICE_CHARACTER = MIDDLE_JSBRIDGE + "get_ble_device_characteristics";//获取蓝牙服务的特征值

    public static final String JS_BLUETOOTH_READ_BLE_DEVICE_CHARACTER = MIDDLE_JSBRIDGE + "read_ble_characteristic_value";//读取蓝牙服务的特征值

    public static final String JS_BLUETOOTH_WRITE_BLE_DEVICE_CHARACTER = MIDDLE_JSBRIDGE + "write_ble_characteristic_value";//写入蓝牙服务的特征值

    public static final String JS_BLUETOOTH_NOTIFY_BLE_DEVICE_CHARACTER = MIDDLE_JSBRIDGE + "notify_ble_characteristic_value_change";//订阅蓝牙服务的特征值

    public static final String JS_BLUETOOTH_BLE_DEVICE_CHARACTER_CHANGE = MIDDLE_JSBRIDGE + "ble_characteristic_value_change";//监听蓝牙服务的特征值变化

    public static final String JS_CHOOSE_LOCATION = MIDDLE_JSBRIDGE + "choose_location"; //地图选择位置

    public static final String HOME_PAGE_DEVICE_INFO = "ankerboxmanager://page/device_scan";

    public static final String HOME_PAGE_WORK_SPACE = "ankerboxmanager://page/workspace";

    public static final String PAGE_TO_RN = "ankerboxmanager://rn";

    public static final String MSG_TYPE_LIST = PREFIX_PANDA + MIDDLE_PAGE + "message_list";//消息中心

    public static final String MSG_LIST = PREFIX_PANDA + MIDDLE_PAGE + "messages";//某种消息的列表

    public static final String JS_REFRESH_TAB_INDICATION = MIDDLE_JSBRIDGE + "update_tabbar_badge";//刷新home tab 红点

    public static final String JS_SET_BUTTON_BACK = MIDDLE_JSBRIDGE + "set_back_button";//返回

    public static final String JS_LOCAL_NOTIFICATION = MIDDLE_JSBRIDGE + "local_notification";//本地通知

    public static final String JS_SHOW = MIDDLE_JSBRIDGE + "show";//本地通知

    public static final String JS_HIDE = MIDDLE_JSBRIDGE + "hide";//本地通知

    public static final String HOME_PAGE_MINE = "ankerboxmanager://page/mine";

    public static final String SWITCH_HOME_TAB = "ankerboxmanager://page/tab";

    public static final String share_js = MIDDLE_JSBRIDGE + "share";

    public static final String PREVIEW_FILES = MIDDLE_JSBRIDGE + "preview_files";

    public static final String LOCAL_FILE_SCHEME = PREFIX_PANDA + "localImgPath";

}
