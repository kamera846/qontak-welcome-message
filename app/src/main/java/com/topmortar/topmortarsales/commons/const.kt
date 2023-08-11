package com.topmortar.topmortarsales.commons

import android.widget.Toast

// Services
const val BASE_URL = "https://saleswa.topmortarindonesia.com/" // Production
//const val BASE_URL = "https://dev-saleswa.topmortarindonesia.com/" // Development

const val RESPONSE_STATUS_OK = "ok"
const val RESPONSE_STATUS_EMPTY = "empty"
const val RESPONSE_STATUS_FAIL = "fail"

const val GET_CONTACT = "contacts.php"
const val EDIT_CONTACT = "contacts.php"
const val SEND_MESSAGE = "messages.php"
const val SEARCH_CONTACT = "contactsSearch.php"
const val GET_CITY = "city.php"
const val ADD_CITY = "city.php"
const val AUTH = "auth.php"
const val GET_USERS = "users.php"
const val DETAIL_USER = "users.php"
const val ADD_USERS = "users.php"
const val REQUEST_OTP = "reqOtp.php"
const val VERIFY_OTP = "verifyOtp.php"
const val UPDATE_PASSWORD = "updatePassword.php"

// Request Code
const val ACTIVITY_REQUEST_CODE = "activity_request_code"
const val MAIN_ACTIVITY_REQUEST_CODE = 111
const val DETAIL_ACTIVITY_REQUEST_CODE = 222
const val MANAGE_USER_ACTIVITY_REQUEST_CODE = 333

// Tag Log
const val TAG_RESPONSE_CONTACT = "TAG RESPONSE CONTACT"
const val TAG_RESPONSE_MESSAGE = "TAG RESPONSE MESSAGE"
const val TAG_ACTION_MAIN_ACTIVITY = "TAG ACTION MAIN ACTIVITY"

// Global
const val TOAST_LONG = Toast.LENGTH_LONG
const val TOAST_SHORT = Toast.LENGTH_SHORT
const val EMPTY_FIELD_VALUE = "Not set"

// Props
const val CONST_CONTACT_ID = "const_contact_id"
const val CONST_OWNER = "const_owner"
const val CONST_LOCATION = "const_location"
const val CONST_PHONE = "const_phone"
const val CONST_NAME = "const_name"
const val CONST_MESSAGE = "const_message"
const val CONST_BIRTHDAY = "const_birthday"
const val CONST_MAPS = "const_maps"
const val CONST_STATUS = "const_status"
const val CONST_ADDRESS = "const_address"
const val CONST_USER_ID = "const_user_id"
const val CONST_USER_LEVEL = "const_user_level"
const val CONST_FULL_NAME = "const_full_name"

// Status
const val LOGGED_IN = true
const val LOGGED_OUT = false
const val SEARCH_OPEN = "search_open"
const val SEARCH_CLOSE = "search_close"
const val SEARCH_CLEAR = "search_clear"
const val SYNC_NOW = "sync_now"
const val STATUS_CONTACT_DATA = "data"
const val STATUS_CONTACT_PASSIVE = "passive"
const val STATUS_CONTACT_ACTIVE = "active"
const val STATUS_CONTACT_BLACKLIST = "blacklist"

// User Kind
const val USER_KIND_ADMIN = "user_kind_admin"
const val USER_KIND_SALES = "user_kind_sales"
const val AUTH_LEVEL_ADMIN = "admin"
const val AUTH_LEVEL_SALES = "sales"

// DUMMY
const val DUMMY_ADMIN_USERNAME = "topmortar"
const val DUMMY_ADMIN_PASSWORD = "admintopmortar123"
const val DUMMY_SALES_USERNAME = "topmortarsales"
const val DUMMY_SALES_PASSWORD = "topmortar123"