plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutinesSwing)
    implementation(libs.compose.uiToolingPreview)
    implementation("org.holypresenter:platform-api:0.1.0")
    implementation("org.holypresenter:platform-ui:0.1.0")
    implementation(libs.androidx.material3.desktop)
    implementation(libs.androidx.material3.jvmstubs)
}

tasks.withType<Jar>().configureEach {
    archiveBaseName.set("songs")
    archiveVersion.set("")
}

val holyPresenterModulesDir = file("D:/Idea/HolyPresenter/desktopApp/modules")

tasks.register<Copy>("installModule") {
    description = "Copies the Songs module into HolyPresenter"
    from(tasks.named<Jar>("jar").flatMap { it.archiveFile })
    from(configurations.runtimeClasspath) {
        include("platform-ui-0.1.0.jar")
    }
    into(holyPresenterModulesDir)
}

tasks.named("jar") {
    finalizedBy("installModule")
}