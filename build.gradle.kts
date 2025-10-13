plugins {
    `java-library`
    id("io.github.goooler.shadow") version "8.1.8"
    `maven-publish`
    checkstyle // Ensures correctly formatted code
    pmd // Code quality checks
    id("org.sonarqube") version "6.0.1.5171" // Advanced code quality checks
    id("xyz.jpenilla.run-paper") version "2.3.1" // Paper server for testing/hotloading JVM
    id("io.papermc.hangar-publish-plugin") version "0.1.3"
    id("com.modrinth.minotaur") version "2.+" // cf https://github.com/modrinth/minotaur
}

group = "net.mvndicraft"
version = "1.0.13"
description = "Limit the bone meal use with config."
java.sourceCompatibility = JavaVersion.VERSION_21
var mainMinecraftVersion = "1.21.10"
val supportedMinecraftVersions = "1.20 - 1.21.10"


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

sonar {
  properties {
    property("sonar.projectKey", "Mvndi_BoneMealLimiter_c0328ae7-6dc7-421e-a6fa-eb713d1b2f68")
    property("sonar.projectName", project.name)
    property("sonar.host.url", "https://mvndisonar.formiko.fr")
  }
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
    group = "documentation"
    description = "Displays the version."
    doLast {
        println("${project.version}")
    }
}

tasks.register("echoReleaseName") {
    group = "documentation"
    description = "Displays the release name."
    doLast {
        println("${project.version} [${supportedMinecraftVersions}]")
    }
}

val extractChangelog = tasks.register("extractChangelog") {
    group = "documentation"
    description = "Extracts the changelog for the current project version from CHANGELOG.md, including the version header."

    val changelog = project.objects.property(String::class)
    outputs.upToDateWhen { false }

    doLast {
        val version = project.version.toString()
        val changelogFile = project.file("CHANGELOG.md")

        if (!changelogFile.exists()) {
            println("CHANGELOG.md not found.")
            changelog.set("No changelog found.")
            return@doLast
        }

        val lines = changelogFile.readLines()
        val entries = mutableListOf<String>()
        var foundVersion = false

        for (line in lines) {
            when {
                // Include the version line itself
                line.trim().equals("# $version", ignoreCase = true) -> {
                    foundVersion = true
                    entries.add(line)
                }
                // Stop collecting at the next version header
                foundVersion && line.trim().startsWith("# ") -> break
                // Collect lines after the version header
                foundVersion -> entries.add(line)
            }
        }

        val result = if (entries.isEmpty()) {
            "Update to $version."
        } else {
            entries.joinToString("\n").trim()
        }

        // println("Changelog for version $version:\n$result")
        changelog.set(result)
    }

    // Make changelog accessible from other tasks
    extensions.add(Property::class.java, "changelog", changelog)
}

tasks.register("echoLatestVersionChangelog") {
    group = "documentation"
    description = "Displays the latest version change."

    dependsOn(tasks.named("extractChangelog"))

    doLast {
        println((extractChangelog.get().extensions.getByName("changelog") as Property<String>).get())
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
        changelog.set(
            extractChangelog.map {
                (it.extensions.getByName("changelog") as Property<String>).get()
            }
        )
        platforms {
            register(io.papermc.hangarpublishplugin.model.Platforms.PAPER) {
                url = "https://github.com/Mvndi/"+project.name+"/releases/download/"+versionString+"/"+project.name+"-"+versionString+".jar"

                // Set platform versions from gradle.properties file
                val versions: List<String> = supportedMinecraftVersions.replace(" ", "").split(",")
                platformVersions.set(versions)
            }
        }
    }
}

// Do an array of game versions from supportedMinecraftVersions
fun expandMinecraftVersions(range: String): List<String> {
    val latestPatches = mapOf("1.20" to 6, "1.21" to 10)

    fun String.toMinorAndPatch() = split('.').let {
        if (it.size == 2) it.joinToString(".") to 0 else "${it[0]}.${it[1]}" to it[2].toInt()
    }

    val (startMinor, startPatch) = range.split(" - ")[0].trim().toMinorAndPatch()
    val (endMinor, endPatch) = range.split(" - ")[1].trim().toMinorAndPatch()

    return generateSequence(startMinor) { current ->
        val (major, minor) = current.split('.').map { it.toInt() }
        if (current == endMinor) null else "%d.%d".format(major, minor + 1)
    }.flatMap { minor ->
        val from = if (minor == startMinor) startPatch else 0
        val to = if (minor == endMinor) endPatch else latestPatches[minor] ?: 0
        (from..to).map { if (it == 0) minor else "$minor.$it" }
    }.toList()
}

tasks.register("echoSupportedMinecraftVersions") {
    group = "documentation"
    description = "Displays the supported Minecraft versions."
    doLast {
        println("${expandMinecraftVersions(supportedMinecraftVersions).joinToString(", ")}")
    }
}


modrinth {
    token.set(System.getenv("MODRINTH_TOKEN")) // Remember to have the MODRINTH_TOKEN environment variable set or else this will fail - just make sure it stays private!
    projectId.set("${project.name.lowercase()}") // This can be the project ID or the slug. Either will work!
    versionNumber.set("${project.version}") // You don't need to set this manually. Will fail if Modrinth has this version already
    versionType.set("release") // This is the default -- can also be `beta` or `alpha`
    // uploadFile.set(tasks.jar) // With Loom, this MUST be set to `remapJar` instead of `jar`!
    uploadFile.set(layout.buildDirectory.dir("libs").get().asFile.absolutePath + "/${project.name}-${project.version}.jar")
    gameVersions.addAll(expandMinecraftVersions(supportedMinecraftVersions)) // Must be an array, even with only one version
    loaders.addAll("paper", "folia", "purpur", "spigot", "bukkit") // Must also be an array
    changelog.set(
        extractChangelog.map {
            (it.extensions.getByName("changelog") as Property<String>).get()
        }
    )
    syncBodyFrom = rootProject.file("README.md").readText()
}

tasks.named("modrinth") {
    dependsOn(tasks.named("modrinthSyncBody"))
}
