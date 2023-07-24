import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jooq.meta.jaxb.Logging
import org.jooq.meta.jaxb.Property
import org.jooq.meta.jaxb.ForcedType

plugins {
	id("org.springframework.boot") version "3.1.1"
	id("io.spring.dependency-management") version "1.1.0"
	id("nu.studer.jooq") version "8.2"
	kotlin("jvm") version "1.8.22"
	kotlin("plugin.spring") version "1.8.22"
}

group = "com.application"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-jooq:3.1.0")
	implementation("org.springframework.boot:spring-boot-starter-jdbc:3.0.4")
	implementation("mysql:mysql-connector-java:8.0.33")
	jooqGenerator("mysql:mysql-connector-java:8.0.33")
	implementation("org.springframework.boot:spring-boot-starter-web:3.1.0")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.2")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation ("org.jooq:jooq:3.18.5")
	implementation("org.springframework.boot:spring-boot-starter-validation:3.0.4")
	implementation("jakarta.validation:jakarta.validation-api:3.0.2")
	testImplementation("org.springframework.boot:spring-boot-starter-test:3.1.0")
	testImplementation ("org.mockito.kotlin:mockito-kotlin:5.0.0")
}

jooq {
	version.set("3.18.4")

	configurations {
		create("main") {
			jooqConfiguration.apply {
				logging = Logging.WARN
				jdbc.apply {
					driver = "com.mysql.cj.jdbc.Driver"
					url = "jdbc:mysql://localhost:13306/bookshelf?autoReconnect=true&useSSL=false&characterEncoding=UTF-8"
					user = "user"
					password = "docker"
					properties = listOf(
						Property().apply {
							key = "PAGE_SIZE"
							value = "2048"
						}
					)
				}
				generator.apply {
					name = "org.jooq.codegen.DefaultGenerator"
					database.apply {
						name = "org.jooq.meta.mysql.MySQLDatabase"
						forcedTypes = listOf(
							ForcedType().apply {
								name = "varchar"
								includeExpression = ".*"
								includeTypes = "JSONB?"
							},
							ForcedType().apply {
								name = "varchar"
								includeExpression = ".*"
								includeTypes = "INET"
							}
						)
					}
					generate.apply {
						isDeprecated = false
						isRecords = false
						isImmutablePojos = false
						isFluentSetters = false
					}
					target.apply {
						packageName = "com.application.books.infrastructure.repository"
						//directory = "src/generated/jooq"
					}
					strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
				}
			}
		}
	}
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
