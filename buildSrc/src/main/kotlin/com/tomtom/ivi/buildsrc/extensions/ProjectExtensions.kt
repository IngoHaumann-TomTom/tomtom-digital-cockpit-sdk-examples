/*
 * Copyright (c) 2020 - 2020 TomTom N.V. All rights reserved.
 *
 * This software is the proprietary copyright of TomTom N.V. and its subsidiaries and may be
 * used for internal evaluation purposes or commercial use strictly subject to separate
 * licensee agreement between you and TomTom. If you are the licensee, you are only permitted
 * to use this Software in accordance with the terms of your license agreement. If you are
 * not the licensee then you are not authorised to use this software in any manner and should
 * immediately return it to TomTom N.V.
 */

package com.tomtom.ivi.buildsrc.extensions

import com.android.build.gradle.TestedExtension
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

val Project.android: TestedExtension
    get() = extensions.getByType(TestedExtension::class.java)

fun Project.android(action: Action<TestedExtension>) =
    action.execute(android)

fun TestedExtension.kotlinOptions(action: Action<KotlinJvmOptions>) =
    action.execute((this as ExtensionAware).extensions.getByType(KotlinJvmOptions::class.java))

/**
 * Retrieves a value from a project's `gradle.properties` files.
 */
fun Project.getGradleProperty(key: String, default: Boolean) =
    properties[key]?.toString()?.toBoolean() ?: default
