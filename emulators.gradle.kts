/*
 * Copyright (c) 2020 - 2021 TomTom N.V. All rights reserved.
 *
 * This software is the proprietary copyright of TomTom N.V. and its subsidiaries and may be
 * used for internal evaluation purposes or commercial use strictly subject to separate
 * licensee agreement between you and TomTom. If you are the licensee, you are only permitted
 * to use this Software in accordance with the terms of your license agreement. If you are
 * not the licensee then you are not authorised to use this software in any manner and should
 * immediately return it to TomTom N.V.
 */

import com.android.ddmlib.AndroidDebugBridge
import com.android.ddmlib.CollectingOutputReceiver
import com.android.ddmlib.FileListingService
import com.android.ddmlib.FileListingService.FileEntry
import com.android.ddmlib.IDevice
import com.android.ddmlib.SyncService.getNullProgressMonitor
import com.android.ddmlib.logcat.LogCatMessage
import com.android.sdklib.devices.Abi
import com.google.gson.GsonBuilder
import com.tomtom.ivi.buildsrc.environment.Versions
import com.tomtom.ivi.buildsrc.extensions.getOrDefault
import com.tomtom.ivi.buildsrc.extensions.gradleAssert
import com.tomtom.navui.emulators.EmulatorsExtension
import com.tomtom.navui.emulators.dsl.AvdInfo
import com.tomtom.navui.emulators.dsl.DeviceProfile
import com.tomtom.navui.emulators.tasks.CreateEmulatorsTask
import com.tomtom.navui.emulators.tasks.StartEmulatorsTask
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Emulator tests setup.
 *
 * This file configures emulators to use for development and testing. It creates Android Virtual
 * Devices (AVDs) which can be used both on CI and for development.
 *
 * The count of emulators (e.g. `-Pemulators=3`) is only used when starting up emulators (with the
 * `startEmulators` task), since the same AVD is used to start multiple identical instances.
 *
 * TODO(IVI-3538): Create Gradle plugin.
 */

apply(plugin = "com.tomtom.navui.emulators-plugin")

// Emulators are Android AVD instances.
val indigoDevelopmentEmulatorName = "IndiGO_Emulator"
val indigoHeadlessEmulatorName = "IndiGO_Headless_Emulator"
// Device profiles are AVD hardware and software platform definitions.
val indigoDeviceProfileName = "IndiGO_Test_Device"

val emulatorTargetApi = Versions.COMPILE_SDK
val emulatorMinimumApi = Versions.MIN_SDK
val defaultEmulatorImage = "system-images;android-$emulatorTargetApi;android-automotive;x86_64"

val extras = rootProject.extra
val emulatorCount = extras.getOrDefault("emulators", "1").toString().toInt()
val emulatorImage = System.getenv("ANDROID_EMULATOR_IMAGE") ?: defaultEmulatorImage
val emulatorOutputDirectory = extras.get("testOutputDirectory") as File
val avdDirectory = File(
    extras.getOrDefault(
        "emulatorDirectory",
        "${project.gradle.gradleUserHomeDir.parent}/.android/avd"
    ).toString()
)
gradleAssert(avdDirectory.exists() || avdDirectory.mkdirs()) {
    "Unable to create AVD directory '${avdDirectory.absolutePath}'."
}

// Ensure multiple test tasks can run concurrently.
tasks.withType<Test> {
    maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2)
        .takeIf { it > 0 } ?: emulatorCount
}

// Simplify configuration of the emulators plugin.
internal val emulatorsExtension = project.extensions.getByType(EmulatorsExtension::class.java)
fun emulators(action: Action<EmulatorsExtension>) {
    action.execute(emulatorsExtension)
}

emulators {
    outputDir = emulatorOutputDirectory

    maxConcurrentStart = emulatorCount
    startDelay = 10
    startTimeout = 300
    nrOfRetries = 1
    waitForFirstEmulator = false

    deviceProfiles {
        create(indigoDeviceProfileName) { configureIndigoDeviceProfile() }
    }

    avds {
        create(indigoHeadlessEmulatorName) {
            configureIndigoEmulatorInstance(isHeadless = true)
        }
    }
}

/**
 * Creates and starts up the development emulator.
 *
 * The AVD made for CI doesn't have a window and has a few more limitations that make it unusable
 * in development. This task creates one that fixes those problems.
 *
 * Do not use this task along with others when invoking Gradle.
 */
tasks.create("createDevelopmentEmulator") {
    doFirst {
        logger.warn("Creating development emulator with image: '$defaultEmulatorImage'")
        // Just for this run, replace the CI AVD with the development AVD; otherwise the former
        // will be created and started together with the latter.
        val createEmulatorsTask =
            project.tasks.named(CreateEmulatorsTask.TASK_NAME).get() as CreateEmulatorsTask
        val startEmulatorsTask =
            project.tasks.named(StartEmulatorsTask.TASK_NAME).get() as StartEmulatorsTask

        listOf(
            AvdInfo(indigoDevelopmentEmulatorName).apply {
                configureIndigoEmulatorInstance(isHeadless = false)
            }
        ).let {
            createEmulatorsTask.avds = it
            startEmulatorsTask.avds = it
        }
    }
}
/**
 * Internal task.
 *
 * Starts the development emulator. Can not be invoked on the command line.
 */
tasks.create("startDevelopmentEmulator") {
    dependsOn(":createDevelopmentEmulator")
    mustRunAfter(":createDevelopmentEmulator")
    doLast {
        val userAvdConfigFile = File(avdDirectory, "$indigoDevelopmentEmulatorName.ini")
        userAvdConfigFile.writeText(
                """
            avd.ini.encoding=UTF-8
            path=${avdDirectory.absolutePath}/$indigoDevelopmentEmulatorName.avd
            path.rel=avd/$indigoDevelopmentEmulatorName.avd
            target=android-$emulatorTargetApi
            """.trimIndent()
        )

        // Apply necessary configuration changes. All settings modified here are not available via
        // the emulators plugin.
        val qemuConfigFile = File(avdDirectory, "$indigoDevelopmentEmulatorName.avd/config.ini")
        val config = qemuConfigFile.readText()
                // The AVD manager won't recognize an unknown device whose configuration contains
                // the 'hw.device.name' key. Delete it to make Android Studio consider it.
                .replace(Regex("hw.device.name=.*"), "") +
                // Enable input from hardware keyboards, to be able to type from the host.
                "hw.keyboard=yes\n"
        qemuConfigFile.writeText(config)
    }
    finalizedBy(":startEmulators")
}

/**
 * Retrieves logs from all connected devices at the same time.
 */
tasks.create("deleteDevelopmentEmulator") {
    dependsOn(":killEmulators")
    doLast {
        File(avdDirectory, "$indigoDevelopmentEmulatorName.ini").delete()
        File(avdDirectory, "$indigoDevelopmentEmulatorName.avd").deleteRecursively()
    }
}

/**
 * Forcibly terminates any running emulator instance.
 */
tasks.create("killEmulators") {
    dependsOn(":stopEmulators")
    doLast {
        exec {
            executable = "pkill"
            args = listOf("-f", "@$indigoDevelopmentEmulatorName")
            standardOutput = ByteArrayOutputStream()
            errorOutput = ByteArrayOutputStream()
            isIgnoreExitValue = true
        }
        exec {
            executable = "pkill"
            args = listOf("-f", "@$indigoHeadlessEmulatorName")
            standardOutput = ByteArrayOutputStream()
            errorOutput = ByteArrayOutputStream()
            isIgnoreExitValue = true
        }
    }
}

/**
 * Retrieves logs from all connected devices at the same time.
 * The logs will be placed in the current NavTest directory inside the project root directory, in a
 * `deviceLogs` subdirectory, which will contain one sub-directory per device named as that
 * device's serial number.
 */
tasks.create("pullLogs") {
    val dropboxFolder = "/data/system/dropbox"
    doLast {
        AndroidDebugBridge.initIfNeeded(false)
        val bridge = AndroidDebugBridge.createBridge(10000, TimeUnit.MILLISECONDS)
        gradleAssert(bridge.devices.isNotEmpty()) {
            "There are no connected devices to pull logs from!"
        }

        bridge.devices.forEach { avd ->
            val targetDir = File(rootProject.projectDir, "deviceLogs/${avd.serialNumber}/")
            gradleAssert(targetDir.exists() || targetDir.mkdirs()) {
                "Unable to create device logs directory '${targetDir.absolutePath}'."
            }
            // Before pulling the dropbox folder, get rid of the irrelevant files.
            avd.runShell(
                "rm -rf " +
                    "$dropboxFolder/system_server_strictmode* " +
                    "$dropboxFolder/system_app_strictmode* " +
                    "$dropboxFolder/keymaster*"
            )
            avd.pullDirectory(
                dropboxFolder,
                targetDir
            )

            // Clean-up the dropbox after pulling it from a connected device.
            avd.runShell("rm -r /data/anr/* /data/tombstones/* $dropboxFolder/*")
        }
    }
}

fun convertPackageListToMap(key: String, output: String): Map<String, String> {
    gradleAssert(output.isNotEmpty()) {
        "Unable to retrieve $key."
    }
    val listOfPackages = mutableListOf<String>()
    output.lines().forEach { line ->
        val keyValue = line.split(':', limit = 2).map { it -> it.trim() }
        if ((keyValue.size >= 2) and (keyValue[0] == "package")) {
            listOfPackages.add(keyValue[1])
        }
    }
    val dictionaryOfValues = mutableMapOf<String, String>()
    dictionaryOfValues.put("packages", listOfPackages.joinToString())
    gradleAssert(dictionaryOfValues.isNotEmpty()) {
        "Empty list found for packages."
    }
    return dictionaryOfValues
}

fun convertKeyValueToMap(key: String, output: String): Map<String, String> {
    gradleAssert(output.isNotEmpty()) {
        "Unable to retrieve $key."
    }
    val dictionaryOfValues = mutableMapOf<String, String>()
    output.lines().forEach { line ->
        val keyValue = line.split(':', limit = 2).map { it -> it.trim().trim('[').trim(']') }
        if (keyValue.size >= 2) {
            dictionaryOfValues.put(keyValue[0], keyValue[1])
        }
    }
    gradleAssert(dictionaryOfValues.isNotEmpty()) {
        "Empty values found for $key."
    }
    return dictionaryOfValues
}

/**
 * Retrieves metadata from all connected devices at the same time.
 * The metadata will be placed in the current NavTest directory inside the project root directory, in a
 * `deviceLogs` subdirectory, which will contain one sub-directory per device named as that
 * device's serial number.
 */
tasks.create("pullMetadata") {
    doLast {
        AndroidDebugBridge.initIfNeeded(false)
        val bridge = AndroidDebugBridge.createBridge(10000, TimeUnit.MILLISECONDS)
        gradleAssert(bridge.devices.isNotEmpty()) {
            "There are no connected devices to pull metadata from!"
        }

        bridge.devices.forEach { avd ->
            val jsonMetadata = mutableMapOf<String, Map<String, String>>()
            val targetDir = File(rootProject.projectDir, "deviceLogs/${avd.serialNumber}/")
            gradleAssert(targetDir.exists() || targetDir.mkdirs()) {
                "Unable to create device metadata directory '${targetDir.absolutePath}'."
            }

            jsonMetadata["density"] = convertKeyValueToMap("density", avd.runShell("wm density"))
            jsonMetadata["size"] = convertKeyValueToMap("size", avd.runShell("wm size"))
            jsonMetadata["properties"] = convertKeyValueToMap("getprop", avd.runShell("getprop"))
            jsonMetadata["package list"] =
                convertKeyValueToMap("pm list packages", avd.runShell("pm list packages"))

            val jsonString: String = GsonBuilder().setPrettyPrinting().create().toJson(jsonMetadata)
            File(targetDir, "device-metadata.json").writeText(jsonString)
        }
    }
}

/**
 * This ensures that only one type of emulator is launched at the same time.
 */
tasks.getByPath(":startEmulators").doLast {
    val bridge = AndroidDebugBridge.createBridge(10000, TimeUnit.MILLISECONDS)
    val runningEmulatorTypes = bridge.devices.distinctBy { it.avdName }
    gradleAssert(runningEmulatorTypes.size <= 1) {
        "Multiple types of emulator are running concurrently:\n" +
            " ${runningEmulatorTypes.joinToString { it.name }}\n" +
            "This is not recommended, as it will make some tests fail."
    }
}

/**
 * Pulls a full directory tree out of a connected device.
 *
 * [targetDir] will contain the full directory tree specified in [directory]. If given
 * `directory = "/sdcard, targetDir = /tmp`, files will be pulled into `/tmp/sdcard/`.
 *
 * @param directory String with the remote directory path to be downloaded.
 * @param targetDir File object describing the local download directory.
 */
fun IDevice.pullDirectory(directory: String, targetDir: File) {
    val targetDirWithSourceTree = File(targetDir, directory)
    gradleAssert(targetDirWithSourceTree.exists() || targetDirWithSourceTree.mkdirs()) {
        "Unable to create pull target directory '${targetDirWithSourceTree.absolutePath}'."
    }

    logger.info("Pulling directory '$directory' from device: $this")
    val messages: MutableList<LogCatMessage> = mutableListOf()
    val receiver = com.android.ddmlib.logcat.LogCatReceiverTask(this)
    receiver.addLogCatListener { messages.addAll(it) }

    try {
        syncService.pull(
            arrayOf(FileEntry(null, directory, FileListingService.TYPE_DIRECTORY, false)),
            targetDir.absolutePath,
            getNullProgressMonitor()
        )
    } catch (ex: Exception) {
        logger.warn("Exception during pull: '${ex.message}' - Logcat of transfer follows:")
        messages.forEach {
            logger.warn("> $it")
        }
    }
}

/**
 * Default device profile configuration for emulators to test IndiGO product.
 */
fun DeviceProfile.configureIndigoDeviceProfile() {
    manufacturer = "TomTom"
    playstore = false
    hardware {
        screen {
            size = "xlarge"
            diagonalLength = 10.50
            pixelDensity = "280dpi"
            screenRatio = "long"
            xDimension = 2560
            yDimension = 1600
            touch {
                multiTouch = "jazz-hands"
                mechanism = "finger"
                screenType = "capacitive"
            }
        }
        networking += setOf("Bluetooth", "Wifi")
        sensors += setOf(
            "Accelerometer",
            "Barometer",
            "Compass",
            "GPS",
            "Gyroscope"
        )
        mic = true
        keyboard = "nokeys"
        nav = "nonav"
        ram = "3072MB"
        buttons = "hard"
        internalStorage = "2GB"
        abi += setOf(Abi.X86_64.toString())
        cpu = "Generic CPU"
        gpu = "Generic GPU"
        powerType = "plugged-in"
        sdcard = false
    }
    software {
        apiLevel = "$emulatorMinimumApi-"
        liveWallpaperSupport = false
        glVersion = "2.0"
        statusBar = false
        glExtensions += setOf(
            "GL_EXT_blend_minmax",
            "GL_EXT_debug_marker",
            "GL_EXT_discard_framebuffer",
            "GL_EXT_multi_draw_arrays",
            "GL_EXT_multisampled_render_to_texture",
            "GL_EXT_occlusion_query_boolean",
            "GL_EXT_read_format_bgra",
            "GL_EXT_robustness",
            "GL_EXT_shader_texture_lod",
            "GL_EXT_shadow_samplers",
            "GL_EXT_sRGB",
            "GL_EXT_texture_format_BGRA8888",
            "GL_EXT_texture_rg",
            "GL_EXT_texture_storage",
            "GL_EXT_texture_type_2_10_10_10_REV",
            "GL_IMG_multisampled_render_to_texture",
            "GL_IMG_program_binary",
            "GL_IMG_read_format",
            "GL_IMG_shader_binary",
            "GL_IMG_texture_compression_pvrtc",
            "GL_IMG_texture_format_BGRA8888",
            "GL_IMG_texture_npot",
            "GL_NV_fence",
            "GL_OES_compressed_ETC1_RGB8_texture",
            "GL_OES_compressed_paletted_texture",
            "GL_OES_depth24",
            "GL_OES_depth_texture",
            "GL_OES_depth_texture_cube_map",
            "GL_OES_EGL_image",
            "GL_OES_EGL_image_external",
            "GL_OES_egl_sync",
            "GL_OES_EGL_sync",
            "GL_OES_element_index_uint",
            "GL_OES_fbo_render_mipmap",
            "GL_OES_fragment_precision_high",
            "GL_OES_get_program_binary",
            "GL_OES_mapbuffer",
            "GL_OES_packed_depth_stencil",
            "GL_OES_required_internalformat",
            "GL_OES_rgb8_rgba8",
            "GL_OES_standard_derivatives",
            "GL_OES_surfaceless_context",
            "GL_OES_texture_3D",
            "GL_OES_texture_float",
            "GL_OES_texture_half_float",
            "GL_OES_texture_half_float_linear",
            "GL_OES_texture_npot",
            "GL_OES_vertex_array_object",
            "GL_OES_vertex_half_float",
            "GL_OES_vertex_type_10_10_10_2"
        )
        bluetoothProfiles += setOf(
            "A2DP",
            "AVDTP",
            "AVRCP",
            "GATT",
            "HFP",
            "HSP",
            "PBA",
            "PBAP",
            "SDAP"
        )
    }
    states {
        create("Landscape") {
            description = "The device in landscape orientation"
            screenOrientation = "land"
            keyboardState = "keyshidden"
            navState = "navhidden"
            isDefault = true
        }
    }
}

/**
 * Creates an AVD configuration with headless option.
 */
fun AvdInfo.configureIndigoEmulatorInstance(isHeadless: Boolean) {
    packagePath = emulatorImage
    nrOfInstances = emulatorCount
    device = indigoDeviceProfileName
    path = File(avdDirectory, "$name.avd")
    cores = 4
    memory = 3072
    partitionSize = 1536
    acceleration = "on"
    gpuMode = if (isHeadless) "swiftshader_indirect" else "host"
    noSkin = true
    noWindow = isHeadless
    noBootAnim = isHeadless
    noCache = isHeadless
    noSnapStorage = isHeadless
    wipeData = isHeadless
    readOnly = isHeadless
    root = true

    postBoot {
        if (isHeadless) {
            // Disable error dialogs such as ANR to prevent pop-ups on top of the product. This is
            // done as soon as possible to have effect on possible errors during configuration.
            runShell("settings put global hide_error_dialogs 1")
            // Disable the Alexa Client Auto Service in the headless emulator, since it's not used.
            runShell("pm disable --user 10 com.amazon.alexaautoclientservice")
        }

        // Set the density to match the value of the "samsung-galaxy_tab_s5e_for_indigo" tablet
        runShell("wm density 288")

        // Increase logcat buffer size to 16M
        runShell("logcat -G 16M")

        // Disable mobile data
        runShell("svc data disable")
        runShell("settings put global wifi_scan_always_enabled 0")

        // Enable wifi
        runShell("svc wifi enable")

        // Disable location
        runShell("settings put secure location_providers_allowed -gps")

        // Disable automatic date/time & configure time-format to 24 hours
        runShell("settings put global auto_time 0")
        runShell("settings put global auto_time_zone 0")
        runShell("settings put system time_12_24 24")

        // Disable ART bytecode verification, as it slows down execution of tests.
        runShell("settings put global art_verifier_verify_debuggable 0")

        // Disable the calendar app, as it can crash.
        runShell("pm disable com.android.calendar")

        // Set time
        val formatter = DateTimeFormatter.ofPattern("MMddHHmmyyy.ss")
        val dateTime = LocalDateTime.now().withHour(12).withMinute(0).withSecond(0)
        val dateArgument = dateTime.format(formatter)
        runShell("date $dateArgument")
        runShell("am broadcast -a android.intent.action.TIME_SET")

        // Disable peek notifications to avoid pop-ups on top of the product.
        runShell("settings put global heads_up_notifications_enabled 0")
    }
}

fun IDevice.runShell(command: String): String {
    val receiver = CollectingOutputReceiver()
    executeShellCommand(command, receiver)
    return receiver.output
}
