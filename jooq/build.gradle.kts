import nu.studer.gradle.jooq.JooqEdition

plugins {
	java
	id("org.springframework.boot") version "3.4.5"
	id("io.spring.dependency-management") version "1.1.7"
	id("nu.studer.jooq") version "10.1"
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
	implementation("org.springframework.boot:spring-boot-starter-jooq")
	jooqGenerator("org.jooq:jooq-meta-extensions")
	jooqGenerator("com.mysql:mysql-connector-j")
	runtimeOnly("com.mysql:mysql-connector-j")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:mysql")
}

dependencyManagement {
	dependencies {
		dependency("org.jooq:jooq-meta-extensions:${dependencyManagement.importedProperties["jooq.version"]}")
	}
}

tasks.named<Test>("test") {
	useJUnitPlatform()
}

jooq {
	version = dependencyManagement.importedProperties["jooq.version"]
	edition = JooqEdition.OSS

	configurations {
		create("main") {
			generateSchemaSourceOnCompilation = true

			jooqConfiguration.apply {
				generator.apply {
					database.apply {
						name = "org.jooq.meta.extensions.ddl.DDLDatabase"
						properties.add(
							org.jooq.meta.jaxb.Property().apply {
								key = "scripts"
								value = "src/main/resources/mysql-schema.sql"
							}
						)
					}
					target.apply {
						packageName = "zin.rashidi.boot.jooq"
					}
					strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
				}
			}
		}
	}
}
