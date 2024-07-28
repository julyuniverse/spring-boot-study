tasks.withType<Test> {
    useJUnitPlatform()
    enabled = false // 테스트 태스크 비활성화
}

/* plain.jar 생성 방지 */
tasks.getByName<Jar>("jar") {
    enabled = false
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}