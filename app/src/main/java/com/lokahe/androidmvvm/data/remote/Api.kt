package com.lokahe.androidmvvm.data.remote

object Api {
    const val URL = "https://api.backendless.com/"
    private const val APP_ID = "TODO Replace with your own values"
    private const val RESET_API_KEY = "TODO Replace with your own values"
    private const val APP_KEY = "$APP_ID/$RESET_API_KEY"

    const val REGISTER = "$APP_KEY/users/register"
    const val LOGIN = "$APP_KEY/users/login"
    const val VERIFY_TOKEN = "$APP_KEY/users/isvalidusertoken/{token}"
    const val LOGOUt = "$APP_KEY/users/logout"
    const val UPDATE_PROPERTY = "$APP_KEY/users/{user-id}"
    const val DATA_USERS = "$APP_KEY/data/Users" //?where=email%3D'<EMAIL>'
    const val DATA_POSTS = "$APP_KEY/data/Posts" //
    const val UPLOAD_FILE = "$APP_KEY/files"


}