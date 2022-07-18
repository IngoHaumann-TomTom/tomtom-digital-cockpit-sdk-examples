---
title: Configure the Runtime Deployment of the IVI System
---

Android allows the running of Android services in processes separate from the main
application/activity process(es). The TomTom IndiGO platform uses Android services for IVI service 
hosts and therefore it is possible to isolate the IVI service host instances too, see 
[IVI Services](/tomtom-indigo/documentation/development/ivi-services) for more details about IVI 
service hosts. The main advantage of process isolation is that when a process unexpectedly crashes 
(and restarts), this only affects the service host instances running in that process and not all 
other services and does not affect the UI.

The default IVI application configuration deploys each service host implementation in a separate
process. It is also possible to run multiple IVI service hosts in one process, to limit the impact of 
the Binder IPC and reduce the (limited) overhead of each process. The IVI services provided by TomTom
can be rearranged, default implementations can be replaced by new ones and services can be removed 
from the deployment.

An IVI service host can be deployed to multiple runtime deployments. This allows multiple
instances of the service host to run in separate processes.

## IVI instance overview

A vehicle may have multiple infotainment screens. Each infotainment screen is an IVI instance. There
is at least a single default IVI instance, typically the one associated with the center stack. A
single system may run multiple IVI instances that all need access to service hosts which are
specific to each IVI instance and service hosts that are accessible by all IVI instances.

## Runtime deployment configuration overview

There are two aspects of an IVI runtime configuration:

- The [`globalDeployments`](#globaldeployments), which are relevant to the entire system.
- The [`multipleInstanceDeployments`](#multipleinstancedeployments), which relate to specific IVI
  instances.

### `globalDeployments`

The [`RuntimeConfigurator`](TTIVI_INDIGO_GRADLEPLUGINS_API).`globalDeployments()` will contain the
service hosts that host IVI Service APIs that are relevant for the whole application. These service
hosts will only have one instance available system-wide.

In production, there should be only one runtime deployment created inside the `globalDeployments`
configuration: the
[`RuntimeDeploymentIdentifier`](TTIVI_INDIGO_GRADLEPLUGINS_API)`.globalRuntime`. However, it is
possible to define multiple runtimes. This can be useful, for instance when creating integration
tests, in order to test how certain functionality behaves under different deployment configurations.
The example below shows two global deployment configurations for the `connectionTestServiceHosts`.
The `OwnProcessDeployment` creates an instance of `connectionTestServiceHosts` which runs in its own
process, and `MainProcessDeployment` creates an instance in the main process. This deployment could
be useful to test if a workflow behaves properly when deployed in its own process or just the main
one.

__build.gradle.kts:__

```kotlin
import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.IviServiceHostConfig
import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.RuntimeDeploymentIdentifier
import com.tomtom.ivi.platform.gradle.api.framework.config.ivi

val connectionTestServiceHosts = IviServiceHostConfig(...)

ivi {
    application {
        enabled = true
        ...
        runtime {
            globalDeployments {
                create(RuntimeDeploymentIdentifier.globalRuntime)
                create("OwnProcessDeployment") {
                    autoRegister = false
                    deployServiceHosts(connectionTestServiceHosts).asBinderHost()
                }
                create("MainProcessDeployment") {
                    autoRegister = false
                    deployServiceHosts(connectionTestServiceHosts).asBinderHost()
                        .inMainProcess()
                }
            }
        }
    }
}
```

### `multipleInstanceDeployments`

The [`RuntimeConfigurator`](TTIVI_INDIGO_GRADLEPLUGINS_API).`multipleInstanceDeployments()` will
contain service hosts that are relevant for a given IVI instance. Multiple IVI instances can be
configured in one deployment. This means, that a given process will run as many instances of a
service host as there are IVI instances configured.

In production, there can be multiple runtimes created inside the `multipleInstanceDeployments`
configuration. This can be useful to increase the reliability of the system, since it will enable
the isolation of service hosts. In the example below there will be one process spawned with an
instance of `accountServiceHost` associated with the `passengerIviInstance`, and another process
with an instance of `accountServiceHost` associated with the `accountServiceHost`. Therefore, if one
of them crashes there will be no impact on the other.

__build.gradle.kts:__

```kotlin
import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.IviServiceHostConfig
import com.tomtom.ivi.platform.gradle.api.framework.config.ivi

val accountServiceHost = IviServiceHostConfig(...)

ivi {
    application {
        enabled = true
        ...
        runtime {
            multipleInstanceDeployments {
                create("DriverDeployment") {
                    iviInstances = listOf(driverIviInstance)
                    deployServiceHost(accountServiceHost)
                }
                create("PassengerDeployment") {
                    iviInstances = listOf(passengerIviInstance)
                    deployServiceHost(accountServiceHost)
                }
            }
        }
    }
}
```

## How to extend the default runtime deployment

In the main application build script, you can override the default runtime deployment. For example:

__build.gradle.kts:__

```kotlin
import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.IviServiceHostConfig
import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.RuntimeDeploymentIdentifier
import com.tomtom.ivi.platform.gradle.api.framework.config.ivi

apply(from = rootProject.file("iviservicehosts.gradle.kts"))

val accountsServiceHosts: List<IviServiceHostConfig> by project.extra

ivi {
    application {
        enabled = true
        ...
        runtime {
            globalDeployments {
                // Create the "Global" runtime deployment.
                create(RuntimeDeploymentIdentifier.globalRuntime) {
                    // Apply the default runtime deployments. This deploys each IVI service host
                    // implementation in a separate process.
                    applyDefaultDeployments(all())

                    // Deploy the `accountsServiceHosts` in the same process.
                    deployServiceHosts(inList(accountsServiceHosts))
                        .withProcessName("account")
                }
            }
        }
    }
}
```

The above example uses the default deployment configuration and configures the
`accountsServiceHosts` to run in the same process.

## How to replace a default service host

The default IVI service hosts can be found inside the module
[com.tomtom.ivi.platform.gradle.api.defaults.config](TTIVI_INDIGO_GRADLEPLUGINS_API) and in the 
`api_appsuitedefaults_*` modules. It is possible to replace a default service host, for instance to 
deploy a new implementation of an IVI service API. The example below replaces the default 
`contactsServiceHost` with a custom `cloudContactsServiceHost`.

__build.gradle.kts:__

```kotlin
import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.IviServiceHostConfig
import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.RuntimeDeploymentIdentifier
import com.tomtom.ivi.platform.gradle.api.defaults.config.contactsServiceHost
import com.tomtom.ivi.platform.gradle.api.framework.config.ivi

val cloudContactsServiceHost = IviServiceHostConfig(...)

ivi {
    application {
        enabled = true
        services {
            removeHost(contactsServiceHost)
            addHost(cloudContactsServiceHost)
        }
        globalRuntime {
             create(RuntimeDeploymentIdentifier.global) {
                 applyDefaultDeployments(all())
                 deployServiceHost(cloudContactsServiceHost)
             }
        }
    }
}
```

## How to add an IVI instance

A vehicle may have multiple infotainment screens. Each infotainment screen is an IVI instance.

To add an IVI instance, it needs to be created in the main application build script, and needs to
be mapped to a runtime deployment. The following example defines two IVI instances, the
"CenterStack" instance and the "Passenger" instance, and maps each IVI instance to its own runtime
deployment.

__build.gradle.kts:__

```kotlin
import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.IviInstanceIdentifier
import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.RuntimeDeploymentIdentifier
import com.tomtom.ivi.platform.gradle.api.defaults.config.mainMenuFrontend
import com.tomtom.ivi.platform.gradle.api.framework.config.ivi

val centerStackIviInstance = IviInstanceIdentifier("CenterStack")
val passengerIviInstance = IviInstanceIdentifier("Passenger")

ivi {
    application {
        enabled = true
        iviInstances {
            // Create the "CenterStack" IVI instance with the default frontends and menu items.
            create(centerStackIviInstance) {
                applyGroups { includeDefaultGroups() }
            }
            // Create the "Passenger" IVI instance. In this example only the `mainMenuFrontend` is
            // added.
            create(passengerIviInstance) {
                frontends {
                    add(mainMenuFrontend, ...)
                }
            }
        }
        runtime {
            globalDeployments {
                // Create "Global" runtime deployment to deploy all global IVI service hosts.
                create(RuntimeDeploymentIdentifier.globalRuntime) {
                    deployServiceHosts(all())
                }
            }
            multipleInstanceDeployments {
                // Create "CenterStack" runtime deployment to deploy all service hosts for the
                // "CenterStack" IVI instances.
                create(RuntimeDeploymentIdentifier("CenterStackRuntime")) {
                    iviInstances = listOf(centerStackIviInstance)
                    deployServiceHosts(all())
                }
                // Create "Passenger" runtime deployment to deploy all service hosts for the
                // "Passenger" IVI instances.
                create(RuntimeDeploymentIdentifier("PassengerRuntime")) {
                    iviInstances = listOf(passengerIviInstance)
                    deployServiceHosts(all())
                }
            }
        }
    }
}
```

The above configuration results in running all IVI service host instances in their own process.

__Note:__ It is also possible to map multiple IVI instances to the same runtime deployment. In this
case the IVI service host instances of these IVI instances will run in the same process. Another
option is to selectively deploy services across deployments.

To use an IVI instance, an Android Activity needs to be bound to an IVI instance. The Android
manifest entry for the activity must define a metadata entry with the name
`com.tomtom.ivi.platform.framework.api.product.activity.IVI_INSTANCE` and the value of the name of
IVI instance. The activity must subclass the [`IviActivity`](TTIVI_INDIGO_API) class. To use the
default system UI use [`DefaultActivity`](TTIVI_INDIGO_API) as the base class.

__AndroidManifest.xml:__

```xml
<activity
    android:name=".PassengerActivity"
    android:label="@string/ttivi_passenger_activity_label"
    android:launchMode="singleTask"
    android:windowSoftInputMode="adjustPan">

    <intent-filter>
        <action android:name="com.tomtom.ivi.product.standalone.indigo.PASSENGER" />
        <category android:name="android.intent.category.DEFAULT" />
    </intent-filter>

    <meta-data
        android:name="com.tomtom.ivi.platform.framework.api.product.activity.IVI_INSTANCE"
        android:value="Passenger" />
</activity>
```

The above example defines a `PassengerActivity` that is associate to the `Passenger` IVI Instance.

## How to run an Android service in the same process as an IVI service host

Standard Android services (not IVI service hosts) are not managed by the IVI platform in any way.
The IVI build config only allows an Android service to be deployed in a configurable process name.
For instance, it is possible to deploy an Android service in the same process as an IVI service
host without the need to hard-code the process name of the Android service in an
`AndroidManifest.xml` file.

__build.gradle.kts:__

```kotlin
import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.AndroidServiceConfig
import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.IviServiceHostConfig
import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.RuntimeDeploymentIdentifier
import com.tomtom.ivi.platform.gradle.api.framework.config.ivi

val androidService = AndroidServiceConfig("com....Service")
val someServiceHost = IviServiceHostConfig(...)

ivi {
    application {
        enabled = true
        services {
            addHost(someServiceHost)
        }
        globalRuntime {
             create(RuntimeDeploymentIdentifier.global) {
                 applyDefaultDeployments(all())
                 deployServiceHost(someServiceHost)
                 deployAndroidService(androidService).inSameProcessAs(someServiceHost)
             }
        }
    }
}
```

The above example deploys `com....Service` in the same process as `someServiceHost`.

__Note:__ The IVI build config does not manage the Gradle dependencies to include the referenced
Android service into the build.

## How to run a broadcast receiver in the same process as an IVI service host

Android broadcast receivers are not managed by the IVI platform in any way. The IVI build config
only allows a broadcast receiver to be deployed in a configurable process name. For instance, it
is possible to deploy a broadcast receiver in the same process as an IVI service host without the
need to hard-code the process name of the broadcast receiver in an `AndroidManifest.xml` file.

__build.gradle.kts:__

```kotlin
import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.BroadcastReceiverConfig
import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.IviServiceHostConfig
import com.tomtom.ivi.platform.gradle.api.common.iviapplication.config.RuntimeDeploymentIdentifier
import com.tomtom.ivi.platform.gradle.api.framework.config.ivi

val broadcastReceiver = BroadcastReceiverConfig("com....BroadcastReceiver")
val someServiceHost = IviServiceHostConfig(...)

ivi {
    application {
        enabled = true
        services {
            addHost(someServiceHost)
        }
        globalRuntime {
             create(RuntimeDeploymentIdentifier.global) {
                 applyDefaultDeployments(all())
                 deployServiceHost(someServiceHost)
                 deployBroadcastReceiver(broadcastReceiver).inSameProcessAs(someServiceHost)
             }
        }
    }
}
```

The above example deploys `com....BroadcastReceiver` in the same process as `someServiceHost`.

__Note:__ The IVI build config does not manage the Gradle dependencies to include the referenced
broadcast receiver into the build.

