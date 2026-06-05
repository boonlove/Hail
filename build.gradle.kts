// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.compose.compiler) apply false
}

val androidMinSdkVersion by extra(23)
val androidTargetSdkVersion by extra(36)
val androidCompileSdkVersion by extra(36)
val androidApplicationId by extra("com.aistra.hail")
val androidVersionName by extra(getVersionName())
val androidVersionCode by extra(getVersionCode())

fun getGitCommitCount(): Int {
    val process = Runtime.getRuntime().exec(arrayOf("git", "rev-list", "--count", "HEAD"))
    return process.inputStream.bufferedReader().use { it.readText().trim().toInt() }
}

fun getGitDescribe(): String {
    val process = Runtime.getRuntime().exec(arrayOf("git", "describe", "--tags", "--abbrev=0"))
    val result = process.inputStream.bufferedReader().use { it.readText().trim() }
    return result.ifEmpty { "v1.0.0" }
}

fun getVersionCode(): Int {
    val commitCount = getGitCommitCount()
    return commitCount
}

fun getVersionName(): String {
    return getGitDescribe().removePrefix("v")
}
