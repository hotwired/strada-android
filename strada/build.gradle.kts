plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlinx-serialization")
    id("maven-publish")
    id("signing")
}

val libVersionName by extra(version as String)
val libraryName by extra("Strada Android")
val libraryDescription by extra("Create fully native Android controls, driven by your web app")

val publishedGroupId by extra("dev.hotwire")
val publishedArtifactId by extra("strada")

val siteUrl by extra("https://github.com/hotwired/strada-android")
val gitUrl by extra("https://github.com/hotwired/strada-android.git")

val licenseType by extra("MIT License")
val licenseUrl by extra("https://github.com/hotwired/strada-android/blob/main/LICENSE")

val developerId by extra("basecamp")
val developerEmail by extra("androidteam@basecamp.com")

val isSonatypeRelease by extra(project.hasProperty("sonatype"))

repositories {
    google()
    mavenCentral()
}

android {
    compileSdk = 34
    testOptions.unitTests.isIncludeAndroidResources = true
    testOptions.unitTests.isReturnDefaultValues = true

    defaultConfig {
        minSdk = 26
        targetSdk = 34
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Define ProGuard rules for this android library project. These rules will be applied when
        // a consumer of this library sets 'minifyEnabled true'.
        consumerProguardFiles("proguard-consumer-rules.pro")
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            setProguardFiles(listOf(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"))
        }
    }

    sourceSets {
        named("main")  { java { srcDirs("src/main/kotlin") } }
        named("test")  { java { srcDirs("src/test/kotlin") } }
    }

    namespace = "dev.hotwire.strada"

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("include" to listOf("*.jar"), "dir" to "libs")))

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
    implementation("androidx.lifecycle:lifecycle-common:2.7.0")

    testImplementation("junit:junit:4.13.2")
    testImplementation("androidx.test:core:1.5.0")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("androidx.lifecycle:lifecycle-runtime-testing:2.7.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("org.robolectric:robolectric:4.11.1")
    testImplementation("org.mockito:mockito-core:5.2.0")
    testImplementation("com.nhaarman:mockito-kotlin:1.6.0")
}

tasks {
    // Only sign Sonatype release artifacts
    withType<Sign>().configureEach {
        onlyIf { isSonatypeRelease }
    }
}

// Sign Sonatype published release artifacts
if (isSonatypeRelease) {
    signing {
        val keyId = System.getenv("GPG_KEY_ID")
        val secretKey = System.getenv("GPG_SECRET_KEY")
        val password = System.getenv("GPG_PASSWORD")

        useInMemoryPgpKeys(keyId, secretKey, password)

        setRequired({ gradle.taskGraph.hasTask("publish") })
        sign(publishing.publications)
    }
}

// Publish to GitHub Packages via:
//   ./gradlew -Pversion=<version> clean build publish
//   https://github.com/orgs/hotwired/packages?repo_name=strada-android
// Publish to Maven Central via:
//   ./gradlew -Psonatype -Pversion=<version> clean build publish
//   https://search.maven.org/artifact/dev.hotwire/strada
publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = publishedGroupId
            artifactId = publishedArtifactId
            version = libVersionName

            pom {
                name.set(libraryName)
                description.set(libraryDescription)
                url.set(siteUrl)

                licenses {
                    license {
                        name.set(licenseType)
                        url.set(licenseUrl)
                    }
                }
                developers {
                    developer {
                        id.set(developerId)
                        name.set(developerId)
                        email.set(developerEmail)
                    }
                }
                scm {
                    url.set(gitUrl)
                }
            }

            // Applies the component for the release build variant
            afterEvaluate {
                from(components["release"])
            }
        }
    }
    repositories {
        if (isSonatypeRelease) {
            maven {
                url = uri("https://s01.oss.sonatype.org/content/repositories/releases/")

                credentials {
                    username = System.getenv("SONATYPE_USER")
                    password = System.getenv("SONATYPE_PASSWORD")
                }
            }
        } else {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/hotwired/strada-android")

                credentials {
                    username = System.getenv("GITHUB_ACTOR")
                    password = System.getenv("GITHUB_TOKEN")
                }
            }
        }
    }
}
