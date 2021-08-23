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

import com.android.build.gradle.api.VersionedVariant
import com.tomtom.ivi.buildsrc.environment.ProjectAbis
import com.tomtom.navtest.extensions.buildVariants
import groovy.lang.GroovyObject
import org.jfrog.gradle.plugin.artifactory.ArtifactoryPluginUtil
import org.jfrog.gradle.plugin.artifactory.dsl.ArtifactoryPluginConvention
import org.jfrog.gradle.plugin.artifactory.dsl.PublisherConfig

plugins.apply("com.jfrog.artifactory")
plugins.apply("maven-publish")

val isApplicationProject: Boolean by extra

group = "com.tomtom.ivi"

// Register publications
extensions.getByType(PublishingExtension::class.java).apply {
    publications {
        if (isApplicationProject) {
            project.buildVariants.configureEach {
                val variantName = name
                val versionName = (this as VersionedVariant).versionName

                if (variantName !in listOf("release", "debug")) {
                    return@configureEach
                }

                val publication = findByName("apk") as MavenPublication? ?: create(
                    "apk",
                    MavenPublication::class.java
                ) {
                    groupId = project.group.toString()
                    artifactId = "products_indigo_examples"
                    version = versionName
                }

                ProjectAbis.enabledAbis.forEach { abi ->
                    val output = File(
                        rootProject.file(
                            "../builds/${variantName}"
                        ),
                        "${project.name}-${abi}-${variantName}.apk"
                    )

                    publication.artifact(output) {
                        extension = "${abi}.apk"
                        classifier = variantName
                    }
                }
            }
        }
        create<MavenPublication>("exampleAppDocs") {
            artifact(File(rootProject.projectDir, "../example-app-docs.tar.gz")) {
                extension = "tar.gz"
            }
            groupId = "com.tomtom.ivi"
            version = findProperty("iviVersion") as String
            artifactId = "example-app-docs"
        }
    }
}


fun Project.artifactory(configure: ArtifactoryPluginConvention.() -> Unit): Unit =
    configure(project.convention.getPluginByName("artifactory"))

artifactory {
    setContextUrl(rootProject.extra.get("artifactoryRepo") as String)

    publish(delegateClosureOf<PublisherConfig> {
        repository(delegateClosureOf<GroovyObject> {
            setProperty("repoKey", "ivi-maven")
            if (project.hasProperty("publishUsername")) {
                setProperty("username", properties["publishUsername"].toString())
                setProperty("password", properties["publishPassword"].toString())
            }
        })
        defaults(delegateClosureOf<GroovyObject> {
            invokeMethod("publications", arrayOf("apk, exampleAppDocs"))
            setProperty("publishArtifacts", true)
            setProperty("publishPom", true)
        })
    })
    if (project.hasProperty("proxyHost") &&
        project.hasProperty("proxyPort")
    ) {
        val rootClientConfig =
            ArtifactoryPluginUtil.getArtifactoryConvention(project).clientConfig
        rootClientConfig.proxy.host = properties["proxyHost"].toString()
        rootClientConfig.proxy.port = properties["proxyPort"].toString().toInt()
        if (project.hasProperty("proxyUsername") &&
            project.hasProperty("proxyPassword")
        ) {
            rootClientConfig.proxy.username = properties["proxyUsername"].toString()
            rootClientConfig.proxy.password = properties["proxyPassword"].toString()
        }
    }
}
