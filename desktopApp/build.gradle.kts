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
    implementation("org.jetbrains.compose.runtime:runtime:1.11.1")
    implementation("org.jetbrains.compose.material3:material3:1.9.0")
    implementation("org.holypresenter:platform-ui:0.1.0")
}

        tasks.withType<Jar>().configureEach {
            archiveBaseName.set("songs")
            archiveVersion.set("")
        }

val holyPresenterModulesDir = file("D:/Idea/HolyPresenter/desktopApp/modules")

tasks.register<Copy>("installModule") {
    description = ""
    dependsOn(tasks.named("jar"))

    from(tasks.named<Jar>("jar").flatMap { it.archiveFile })

    into(holyPresenterModulesDir)
}