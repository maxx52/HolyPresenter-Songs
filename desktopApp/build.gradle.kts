plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutinesSwing)
    implementation(libs.compose.uiToolingPreview)
    implementation("org.holypresenter:platform-api:0.3.0")
    implementation("org.holypresenter:platform-ui:0.5.0")
    implementation(libs.androidx.material3.desktop)
    implementation(libs.androidx.material3.jvmstubs)
    implementation(libs.androidx.runtime.desktop)
    implementation(libs.androidx.foundation.layout.desktop)
    implementation(libs.kotlinxSerializationJson)
}

tasks.withType<Jar>().configureEach {
    archiveBaseName.set("songs")
    archiveVersion.set("")
}

val holyPresenterModulesDir =
    file("D:/Idea/HolyPresenter/desktopApp/modules")

val cleanOldPlatformUi by tasks.registering(Delete::class) {
    description = "Copies the Songs module into HolyPresenter"
    delete(
        fileTree(holyPresenterModulesDir) {
            include("platform-ui-*.jar")
        }
    )
}

val installModule by tasks.registering(Copy::class) {
    description = "Copies the Songs module into HolyPresenter"

    dependsOn(cleanOldPlatformUi)

    from(
        tasks.named<Jar>("jar")
            .flatMap { it.archiveFile }
    )

    from(configurations.runtimeClasspath) {
        include("platform-ui-*.jar")
    }

    into(holyPresenterModulesDir)
}

tasks.named<Jar>("jar") {
    finalizedBy(installModule)
}