dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("software.amazon.awssdk:ses:2.26.24")
}

/* plain.jar 생성 방지 */
tasks.getByName<Jar>("jar") {
    enabled = false
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = true
    mainClass.set("com.springbootstudy.aws.ses.AwsSesApplication")
}
