plugins {
    id("java")
    application
}

group = "org.pinhead"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

application {
    mainClass = "org.pinhead.Main"
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
    dependsOn("cleanTest")

    testLogging {
        events ("passed", "skipped", "failed")
    }

}