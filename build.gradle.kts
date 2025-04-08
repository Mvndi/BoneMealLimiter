import io.papermc.hangarpublishplugin.model.Platforms

plugins {
    `java-library`
    id("io.github.goooler.shadow") version "8.1.7"
    `maven-publish`
    checkstyle // Ensures correctly formatted code
    pmd // Code quality checks
    id("xyz.jpenilla.run-paper") version "2.3.1" // Paper server for testing/hotloading JVM
    id("io.papermc.hangar-publish-plugin") version "0.1.3"
}

group = "net.mvndicraft"
version = "1.0.1"
description = "Limit the bone meal use with config."
java.sourceCompatibility = JavaVersion.VERSION_21
var mainMinecraftVersion = "1.21.4"
val supportedMinecraftVersions = "1.20 - 1.21.4"


repositories {
    mavenLocal()
    mavenCentral()

    // Paper
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.aikar.co/content/groups/aikar/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:$mainMinecraftVersion-R0.1-SNAPSHOT")

    implementation("org.bstats:bstats-bukkit:3.1.0")
    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")

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
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks {
    shadowJar {
        val prefix = "${project.group}.lib"
        sequenceOf(
            "co.aikar",
            "org.bstats"
        ).forEach { pkg ->
            relocate(pkg, "$prefix.$pkg")
        }

        archiveFileName.set("${project.name}-${project.version}.jar")
    }
    assemble {
        dependsOn(shadowJar)
    }
    processResources {
        val props = mapOf(
            "name" to project.name,
            "version" to project.version,
            "description" to project.description,
            "apiVersion" to "1.20"
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

tasks.register("echoVersion") {
    doLast {
        println("${project.version}")
    }
}

tasks.register("echoReleaseName") {
    doLast {
        println("${project.version} [${supportedMinecraftVersions}]")
    }
}

val versionString: String = version as String
val isRelease: Boolean = !versionString.contains("SNAPSHOT")

hangarPublish { // ./gradlew publishPluginPublicationToHangar
    publications.register("plugin") {
        version.set(project.version as String)
        channel.set(if (isRelease) "Release" else "Snapshot")
        id.set(project.name)
        apiKey.set(System.getenv("HANGAR_API_TOKEN"))
        platforms {
            register(Platforms.PAPER) {
                url = "https://github.com/Mvndi/"+project.name+"/releases/download/1.6.14/"+project.name+"-"+versionString+".jar"

                // Set platform versions from gradle.properties file
                val versions: List<String> = supportedMinecraftVersions.replace(" ", "").split(",")
                platformVersions.set(versions)
            }
        }
    }
}