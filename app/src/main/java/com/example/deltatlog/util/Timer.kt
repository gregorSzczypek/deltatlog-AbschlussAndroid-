package com.example.deltatlog.util//import android.os.CountDownTimer

class Timer {
    var startTime: Long = 0
    var endTime: Long = 0
    var isRunning: Boolean = false

    fun start() {
        startTime = System.currentTimeMillis()
        isRunning = true
    }

    fun stop() {
        endTime = System.currentTimeMillis()
        isRunning = false
    }

    fun elapsed(): Long {
        return if (isRunning) {
            System.currentTimeMillis() - startTime
        } else {
            endTime - startTime
        }
    }
}
