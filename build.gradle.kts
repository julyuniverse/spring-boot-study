plugins {
    `java-library`
    id("org.springframework.boot") version "3.3.2"
    id("io.spring.dependency-management") version "1.1.6"
}

configure(listOf(project(":aws:ses"), project(":social-login"))) {
    apply(plugin = "java-library")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")

    group = "com.springbootstudy"
    version = "1.0.0"

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(17)
        }
    }

    configurations {
        compileOnly {
            extendsFrom(configurations.annotationProcessor.get())
        }

        all {
            // console msg: Standard Commons Logging discovery in action with spring-jcl: please remove commons-logging.jar from classpath in order to avoid potential conflicts
            // 잠재적인 충돌을 피하기 위해서 commons-logging.jar 제거하기.
            exclude("commons-logging", "commons-logging")
        }
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        implementation("org.springframework.boot:spring-boot-starter")
        compileOnly("org.projectlombok:lombok")
        annotationProcessor("org.projectlombok:lombok")
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        enabled = false // 테스트 태스크 비활성화
    }

    springBoot {
        buildInfo()
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    enabled = false // 테스트 태스크 비활성화
}

tasks.getByName<Jar>("jar") {
    enabled = false // plain.jar 생성 방지
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}
