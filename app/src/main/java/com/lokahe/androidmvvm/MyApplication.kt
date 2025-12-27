package com.lokahe.androidmvvm

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.HiltAndroidApp
import java.lang.ref.WeakReference
import android.os.Build.VERSION.SDK_INT as API
import android.os.Build.VERSION_CODES as APIS

@HiltAndroidApp
class MyApplication : Application() {
    val TAG = "MyApplication"

    companion object {
        val liveActivities = mutableSetOf<WeakReference<Activity>>()


        // 1. CHANGE: Use lateinit var, do NOT instantiate it here.
        lateinit var application: MyApplication
            private set // Optional: Prevent external classes from overwriting it

        @JvmStatic
        fun <T : Activity> findActivity(clazz: Class<T>): T? {
            val iterator = liveActivities.iterator()
            while (iterator.hasNext()) {
                val activity = iterator.next().get()
                if (activity == null) {
                    iterator.remove() // Clean up dead references
                } else if (clazz.isInstance(activity)) {
                    return clazz.cast(activity)
                }
            }
            return null
        }
    }

    override fun onCreate() {
        super.onCreate()

        // 2. ASSIGN: Capture the valid context here
        application = this

        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                liveActivities.add(WeakReference(activity))
                Log.d(
                    TAG,
                    "onActivityCreated: ${activity.javaClass.simpleName} - ${liveActivities.forEach { it.get() }} "
                )
            }

            override fun onActivityDestroyed(activity: Activity) {
                // Remove the WeakReference pointing to the destroyed activity
                if (API >= APIS.N) {
                    liveActivities.removeIf { it.get() == activity || it.get() == null }
                } else {
                    val iterator = liveActivities.iterator()
                    while (iterator.hasNext()) {
                        val ref = iterator.next()
                        val act = ref.get()
                        if (act == null || act == activity) {
                            iterator.remove()
                        }
                    }
                }
                Log.d(
                    TAG,
                    "onActivityDestroyed: ${activity.javaClass.simpleName} -  ${liveActivities.forEach { it.get() }} "
                )
            }

            // Optional: implement other lifecycle methods if needed
            override fun onActivityStarted(activity: Activity) {
                Log.d(
                    TAG, "onActivityStarted: ${activity.javaClass.simpleName}" +
                            if (activity is AppCompatActivity) "${activity.lifecycle.currentState}" else ""
                )
            }

            override fun onActivityResumed(activity: Activity) {
                Log.d(
                    TAG,
                    "onActivityResumed: ${activity.javaClass.simpleName}" +
                            if (activity is AppCompatActivity) "${activity.lifecycle.currentState}" else ""
                )
            }

            override fun onActivityPaused(activity: Activity) {
                Log.d(
                    TAG,
                    "onActivityPaused: ${activity.javaClass.simpleName}" +
                            if (activity is AppCompatActivity) "${activity.lifecycle.currentState}" else ""
                )
            }

            override fun onActivityStopped(activity: Activity) {
                Log.d(
                    TAG,
                    "onActivityStopped: ${activity.javaClass.simpleName}" +
                            if (activity is AppCompatActivity) "${activity.lifecycle.currentState}" else ""
                )
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
                Log.d(
                    TAG,
                    "onActivitySaveInstanceState: ${activity.javaClass.simpleName}" +
                            if (activity is AppCompatActivity) "${activity.lifecycle.currentState}" else ""
                )
            }
        })
    }
}