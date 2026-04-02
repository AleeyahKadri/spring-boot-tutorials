import org.springframework.boot.gradle.plugin.SpringBootPlugin
import org.gradle.api.tasks.testing.Test
import org.gradle.testing.jacoco.plugins.JacocoCoverageReport
import org.gradle.testing.jacoco.tasks.JacocoReport

plugins {
    id("java")
    id("jacoco-report-aggregation")
    id("org.springframework.boot") version "3.4.5" apply false
    id("io.spring.dependency-management") version "1.1.7"
    id("org.sonarqube") version "6.2.0.5505"
}

group = "zin.rashidi.boot"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    subprojects.forEach { p ->
        implementation(project(":${p.name}"))
    }
}

dependencies {
    implementation(platform(SpringBootPlugin.BOM_COORDINATES))
}

reporting {
    reports {
        named<JacocoCoverageReport>("testCodeCoverageReport") {
            testSuiteName = "test"
        }
    }
}

sonar {
    properties {
        property("sonar.projectKey", "rashidi_spring-boot-tutorials")
        property("sonar.organization", "rashidi-github")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.coverage.jacoco.xmlReportPaths", "${layout.buildDirectory.get()}/reports/jacoco/testCodeCoverageReport/testCodeCoverageReport.xml")
    }
}

subprojects {
    apply {
        plugin("jacoco")
    }

    plugins.withId("java") {
        tasks.named<Test>("test") {
            finalizedBy(tasks.named("jacocoTestReport"))
        }
    }
}

tasks.named("check") {
    dependsOn(tasks.named<JacocoReport>("testCodeCoverageReport"))
}
