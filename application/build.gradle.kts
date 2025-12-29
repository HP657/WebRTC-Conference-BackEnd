plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("jvm")
    kotlin("plugin.spring")
}


dependencies {
    implementation(project(":signaling"))
    implementation(project(":infrastructure"))
    implementation("org.springframework.boot:spring-boot-starter-websocket")
}
