plugins {
	java
	idea
	id("org.springframework.boot") version "3.2.3"
	id("io.spring.dependency-management") version "1.1.4"
	id("org.springdoc.openapi-gradle-plugin") version "1.8.0"
}

// libraries versions
val versionSpringDocOpenAPI by extra { "2.3.0" }
val versionH2 by extra { "2.2.224" }
val versionLombok by extra { "1.18.30" }

group = "pl.robloj"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
	mavenCentral()
}

rootProject.afterEvaluate {
	val forkedSpringBootRun = project.tasks.named("forkedSpringBootRun")
	forkedSpringBootRun.configure {
		doNotTrackState("See https://github.com/springdoc/springdoc-openapi-gradle-plugin/issues/102")
	}
}

dependencies {
	implementation("com.h2database:h2:$versionH2")
	implementation("org.projectlombok:lombok:$versionLombok")
	annotationProcessor("org.projectlombok:lombok:$versionLombok")
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-web")
//	implementation("org.springframework.boot:spring-boot-starter-hateoas")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-data-rest")
//	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$versionSpringDocOpenAPI")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-api:$versionSpringDocOpenAPI")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

idea {
	module {
		isDownloadJavadoc = true
		isDownloadSources = true
	}
}