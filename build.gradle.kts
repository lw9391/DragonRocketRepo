plugins {
    id("java-library")
}

group = "io.dragon"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.25.3")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    archiveBaseName = "Dragon-rockets-library";
    archiveVersion = version.toString()
}