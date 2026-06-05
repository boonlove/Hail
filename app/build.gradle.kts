import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
}

val androidMinSdkVersion: Int by rootProject.extra
val androidTargetSdkVersion: Int by rootProject.extra
val androidCompileSdkVersion: Int by rootProject.extra
val androidApplicationId: String by rootProject.extra
val androidVersionName: String by rootProject.extra
val androidVersionCode: Int by rootProject.extra

fun getSigningConfig(key: String): String? {
    val properties = Properties()
    val keystorePropertiesFile = rootProject.file("keystore.properties")
    if (keystorePropertiesFile.exists()) {
        try {
            properties.load(keystorePropertiesFile.inputStream())
            return properties.getProperty(key)
        } catch (e: Exception) {
            println("Warning: 无法加载 keystore.properties 文件: ${e.message}")
        }
    }
    return null
}

android {
    namespace = "com.aistra.hail"
    compileSdk = androidCompileSdkVersion

    defaultConfig {
        applicationId = androidApplicationId
        minSdk = androidMinSdkVersion
        targetSdk = androidTargetSdkVersion
        versionCode = androidVersionCode
        versionName = androidVersionName

        ndk {
            abiFilters += listOf("arm64-v8a")
        }
    }

    signingConfigs {
        val storeFilePath = getSigningConfig("storeFile").toString()
        val storePassword = getSigningConfig("storePassword")
        val keyAlias = getSigningConfig("keyAlias")
        val keyPassword = getSigningConfig("keyPassword")
        val hasSigning = rootProject.file(storeFilePath).exists() && storePassword != null && keyAlias != null && keyPassword != null
        if (hasSigning) {
            create("release") {
                this.storeFile = rootProject.file(storeFilePath)
                this.storePassword = storePassword
                this.keyAlias = keyAlias
                this.keyPassword = keyPassword
                enableV1Signing = true
                enableV2Signing = true
                enableV3Signing = true
                enableV4Signing = false
            }
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            signingConfig = signingConfigs.findByName("release")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.findByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
    }
    kotlin {
        jvmToolchain(21)
    }
    androidResources {
        generateLocaleConfig = true
        // Do not compress the dex files, so the apk can be imported as a privileged app
        noCompress += "dex"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
        compose = true
    }
    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }
}

base {
    archivesName.set(
        "Hail_v${androidVersionName}_${androidVersionCode}"
    )
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.ui.tooling.preview)
    debugImplementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.biometric.ktx)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.preference.ktx)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.pinyin4j)
    implementation(libs.material)
    implementation(libs.insetter)
    implementation(libs.shizuku.api)
    implementation(libs.shizuku.provider)
    implementation(libs.dhizuku.api)
    implementation(libs.appiconloader)
    implementation(libs.compose.preference)
    implementation(libs.commons.text)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.hiddenapibypass)
    compileOnly(libs.xposed)
}
