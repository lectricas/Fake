plugins {
    kotlin("jvm") version "1.7.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.yaml:snakeyaml:1.30")
    implementation("commons-cli:commons-cli:1.5.0")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(platform("org.junit:junit-bom:5.8.0"))
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")
}

tasks {
    create<Jar>("FakeJar") {
        from(sourceSets["main"].output)

        manifest {
            attributes["Main-Class"] = "presentation.FakeCLI"
        }

        archiveBaseName.set("Fake")
        archiveVersion.set("")

        from(configurations.compileClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        doLast {
            copy {
                from(archiveFile)
                into(File(project.rootDir, "out"))
            }
        }
    }
}

tasks.test {
    useJUnitPlatform()
}
