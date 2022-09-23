/*
 * Copyright © 2022 TomTom NV. All rights reserved.
 *
 * This software is the proprietary copyright of TomTom NV and its subsidiaries and may be
 * used for internal evaluation purposes or commercial use strictly subject to separate
 * license agreement between you and TomTom NV. If you are the licensee, you are only permitted
 * to use this software in accordance with the terms of your license agreement. If you are
 * not the licensee, you are not authorized to use this software in any manner and should
 * immediately return or destroy it.
 */


/**
 * Note:
 * This plugin configuration file is intended for TomTom internal use and not relevant
 * to an external developer
 * By default, this file is not included in the SDK packaging: it is deleted by CI scripting
 */
buildscript {
    repositories {
        gradlePluginPortal()
    }
    dependencies {
        classpath("com.gradle:gradle-enterprise-gradle-plugin:3.8.1")
    }
}

// We want Gradle to publish task execution analysis to gradle enterprise server.
// This can further be utilised to improve build and test lifecycle
// https://docs.gradle.com/enterprise/gradle-plugin
pluginManager.apply(com.gradle.enterprise.gradleplugin.GradleEnterprisePlugin::class.java)

val isCiBuild = System.getenv("BUILD_BUILDNUMBER") != null

gradleEnterprise {
    server = "https://gradle-poc.tomtomgroup.com"
    buildScan {
        publishAlways()
        tag(if (isCiBuild) "CI" else "LOCAL")
        capture {
            isTaskInputFiles = true
        }
        isUploadInBackground = ! isCiBuild
        if (isCiBuild) {
            val baseUrl = System.getenv("SYSTEM_TEAMFOUNDATIONCOLLECTIONURI")
            val project = System.getenv("SYSTEM_TEAMPROJECT")
            val buildId = System.getenv("BUILD_BUILDID")
            val buildNumber = System.getenv("BUILD_BUILDNUMBER")

            val buildUrl = "$baseUrl$project/_build/results?buildId=$buildId"
            link("Build Url", buildUrl)
            value("Build Number", buildNumber)
        }
    }
}

buildCache {
    local {
        // Local cache is for local developent only.
        isEnabled = !isCiBuild
    }

    remote<HttpBuildCache> {
        // Remote cache is for CI builds only (as it requires corp network and password).
        isEnabled = isCiBuild
        isPush = isCiBuild
        url = uri("https://gradlecache-ivi.tomtomgroup.com/cache/")
        credentials {
            username = "ci"
            password = System.getenv("GRADLE_REMOTE_CACHE_PWD")
        }
    }
}
