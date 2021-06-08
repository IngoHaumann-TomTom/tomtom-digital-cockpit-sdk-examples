package com.tomtom.ivi.example.serviceapi.account

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Wrapper for string value that protects the value from being stored in the logs.
 */
@Parcelize
data class SensitiveString(val value: String) : Parcelable {
    override fun toString(): String = "-HIDDEN-"
}
