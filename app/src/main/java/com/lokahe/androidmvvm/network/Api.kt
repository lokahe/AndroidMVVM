package com.lokahe.androidmvvm.network

object Api {
    const val URL = "https://api.backendless.com/"
    const val APP_ID = "TODO Replace with your own values"
    const val RESET_API_KEY = "TODO Replace with your own values"

    const val REGISTER = "$APP_ID/$RESET_API_KEY/users/register"
    const val LOGIN = "$APP_ID/$RESET_API_KEY/users/login"
    const val VERIFY_TOKEN = "$APP_ID/$RESET_API_KEY/users/isvalidusertoken/{token}"
    const val LOGOUt = "$APP_ID/$RESET_API_KEY/users/logout"
    const val UPDATE_PROPERTY = "$APP_ID/$RESET_API_KEY/users/{user-id}"
    const val DATA_USERS = "$APP_ID/$RESET_API_KEY/data/Users" //?where=email%3D'<EMAIL>'
    const val DATA_POSTS = "$APP_ID/$RESET_API_KEY/data/Posts" //

}