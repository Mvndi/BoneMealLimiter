plugins {
    `java-library`
    `maven-publish`
    checkstyle // Ensures correctly formatted code
    pmd // Code quality checks
    id("xyz.jpenilla.run-paper") version "2.3.1" // Paper server for testing/hotloading JVM
}

group = "net.mvndicraft"
version = "1.0.0-SNAPSHOT"
description = "TODO describe your plugin"
java.sourceCompatibility = JavaVersion.VERSION_21
var mainMinecraftVersion = "1.21.4"

var mvndiUser: String? = project.findProperty("mvndi.user") as String? ?: System.getenv("MVNDI_MVN_USER")
var mvndiPassword: String? = project.findProperty("mvndi.key") as String? ?: System.getenv("MVNDI_MVN_KEY")

val mvndiRemoteSnapshots = repositories.maven("https://repo.mvndicraft.net/repository/maven-snapshots/") {
    name = "Mvndi"
    credentials.username = mvndiUser
    credentials.password = mvndiPassword
}

val mvndiRemoteReleases = repositories.maven("https://repo.mvndicraft.net/repository/maven-releases/") {
    name = "Mvndi"
    credentials.username = mvndiUser
    credentials.password = mvndiPassword
}

repositories {
    mavenLocal()
    mavenCentral()

    // Paper
    maven("https://repo.papermc.io/repository/maven-public/")

    // Mvndi
    mvndiRemoteSnapshots
    mvndiRemoteReleases
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:$mainMinecraftVersion-R0.1-SNAPSHOT")

    testImplementation("org.junit.jupiter:junit-jupiter:5.11.0")
    testImplementation("com.github.seeseemelk:MockBukkit-v1.21:3.107.0")
}

checkstyle {
    toolVersion = "10.12.4"
    configFile = rootProject.file("config/checkstyle/checkstyle.xml")
}

pmd {
    isConsoleOutput = true
    toolVersion = "7.0.0"
    rulesMinimumPriority = 5
    ruleSets = listOf("category/java/errorprone.xml", "category/java/bestpractices.xml")
}

publishing {
    val repo = if (project.version.toString().contains("SNAPSHOT")) mvndiRemoteSnapshots else mvndiRemoteReleases
    repositories.add(repo)
    publications.create<MavenPublication>("maven") {
        from(components["java"])
        artifactId = "templateplugin"
    }
}

tasks {
    processResources {
        val props = mapOf(
            "name" to project.name,
            "version" to project.version,
            "description" to project.description,
        )
        inputs.properties(props)
        filesMatching("paper-plugin.yml") {
            expand(props)
        }
    }

    runServer {
        minecraftVersion("$mainMinecraftVersion")
    }

    test {
        useJUnitPlatform()
    }
}

@Suppress("UnstableApiUsage")
tasks.withType(xyz.jpenilla.runtask.task.AbstractRun::class) {
    javaLauncher = javaToolchains.launcherFor {
        vendor = JvmVendorSpec.JETBRAINS
        languageVersion = JavaLanguageVersion.of(21)
    }
    jvmArgs("-XX:+AllowEnhancedClassRedefinition")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}
