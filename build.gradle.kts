plugins {
    kotlin("multiplatform") version "1.6.20"
    id("com.android.library")
    id("io.codearte.nexus-staging") version "0.30.0"
    `maven-publish`
    signing
    id("org.jetbrains.kotlin.plugin.parcelize") version "1.6.20"
}

val libraryVersionPrefix: String by project
group = "com.ditchoom"
version = "$libraryVersionPrefix.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
}

kotlin {
    android {
        publishLibraryVariants("release")
    }
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        testRuns["test"].executionTask.configure {
            useJUnit()
        }
    }
    js {
        binaries.executable()
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
            }
        }
        nodejs{ }
    }
    macosX64()
    linuxX64()
    ios()
    iosSimulatorArm64()

    watchos()
    tvos()
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("com.ditchoom:buffer:1.0.81")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            kotlin.srcDir("src/androidMain/java")
        }
        val androidRelease by getting {
            kotlin.srcDir("src/androidMain/java")
        }
        val jvmMain by getting {
            kotlin.srcDir("src/commonNoAndroid/kotlin")
        }
        val jsMain by getting {
            kotlin.srcDir("src/commonNoAndroid/kotlin")
        }
        val macosX64Main by getting {
            kotlin.srcDir("src/commonNoAndroid/kotlin")
        }
        val linuxX64Main by getting {
            kotlin.srcDir("src/commonNoAndroid/kotlin")
        }
        val iosMain by getting {
            kotlin.srcDir("src/commonNoAndroid/kotlin")
        }
        val iosSimulatorArm64Main by getting {
            kotlin.srcDir("src/commonNoAndroid/kotlin")
        }
        val iosArm64Main by getting {
            kotlin.srcDir("src/commonNoAndroid/kotlin")
        }
        val watchosMain by getting {
            kotlin.srcDir("src/commonNoAndroid/kotlin")
        }
        val tvosMain by getting {
            kotlin.srcDir("src/commonNoAndroid/kotlin")
        }
    }
}

android {
    compileSdkVersion(31)
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].java.srcDir("src/androidMain/kotlin")
    defaultConfig {
        minSdkVersion(1)
        targetSdkVersion(31)
    }
    lintOptions {
        isQuiet = true
        isAbortOnError =  false
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

val javadocJar: TaskProvider<Jar> by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

System.getenv("GITHUB_REPOSITORY")?.let {
    signing {

        useInMemoryPgpKeys("56F1A973", System.getenv("GPG_SECRET"), System.getenv("GPG_SIGNING_PASSWORD"))
        sign(publishing.publications)
    }


    val ossUser = System.getenv("SONATYPE_NEXUS_USERNAME")
    val ossPassword = System.getenv("SONATYPE_NEXUS_PASSWORD")

    val publishedGroupId: String by project
    val libraryName: String by project
    val libraryDescription: String by project
    val siteUrl: String by project
    val gitUrl: String by project
    val licenseName: String by project
    val licenseUrl: String by project
    val developerOrg: String by project
    val developerName: String by project
    val developerEmail: String by project
    val developerId: String by project

    val libraryVersion = if (System.getenv("GITHUB_RUN_NUMBER") != null) {
        "$libraryVersionPrefix${System.getenv("GITHUB_RUN_NUMBER")}"
    } else {
        "${libraryVersionPrefix}0-SNAPSHOT"
    }

    project.group = publishedGroupId
    project.version = libraryVersion

    publishing {
        publications.withType(MavenPublication::class) {
            groupId = publishedGroupId
            version = libraryVersion

            artifact(tasks["javadocJar"])

            pom {
                name.set(libraryName)
                description.set(libraryDescription)
                url.set(siteUrl)

                licenses {
                    license {
                        name.set(licenseName)
                        url.set(licenseUrl)
                    }
                }
                developers {
                    developer {
                        id.set(developerId)
                        name.set(developerName)
                        email.set(developerEmail)
                    }
                }
                organization {
                    name.set(developerOrg)
                }
                scm {
                    connection.set(gitUrl)
                    developerConnection.set(gitUrl)
                    url.set(siteUrl)
                }
            }
        }

        repositories {
            maven("https://oss.sonatype.org/service/local/staging/deploy/maven2/") {
                name = "sonatype"
                credentials {
                    username = ossUser
                    password = ossPassword
                }
            }
        }
    }

    nexusStaging {
        username = ossUser
        password = ossPassword
        packageGroup = publishedGroupId
    }
}
