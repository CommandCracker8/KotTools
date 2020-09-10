plugins {
    java
    kotlin("jvm") version "1.4.0"
}

group = "world.cepi.kotlintools"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven(url = "https://papermc.io/repo/repository/maven-public/")
}

dependencies {
    implementation("com.destroystokyo.paper:paper-api:1.16.2-R0.1-SNAPSHOT")
    compile(kotlin("stdlib"))
    compile(kotlin("reflect"))
    testImplementation(kotlin("test-junit5"))
}

configurations.compile.get().isTransitive = false

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks {
    withType(JavaCompile::class) {
        options.isIncremental = true
    }

    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }

    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }

    "build" {
        dependsOn(fatJar)
    }
}

val fatJar = task("fatJar", type = Jar::class) {
    baseName = "${project.name}-fat"
    from(configurations.compileClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    with(tasks["jar"] as CopySpec)
}