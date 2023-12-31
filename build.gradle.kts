plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("xyz.jpenilla.run-paper") version "2.2.2"
    id("maven-publish")
}


group = "it.einjojo.smpengine"
version = "1.6.4"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.2-R0.1-SNAPSHOT")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")
    implementation("com.zaxxer:HikariCP:5.1.0")

    compileOnly("org.projectlombok:lombok:1.18.20")
    annotationProcessor("org.projectlombok:lombok:1.18.20")


}

//utf8


tasks {

    //utf 8
    compileJava {
        options.encoding = "UTF-8"
    }

    //jpenillia
    runServer {
        minecraftVersion("1.20.4")
    }

    shadowJar {
        relocate("com.zaxxer.hikari", "it.einjojo.smpengine.database.hikari")
        relocate("com.github.benmanes.caffeine", "it.einjojo.smpengine.cache.caffeine")
    }

    assemble {
        dependsOn(shadowJar)
    }

    processResources {
        filesMatching("**/*.yml") {
            expand("version" to project.version)
        }


    }

}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "Wandoria"
            url = uri("https://repo.wandoria.net/jojo/")
            credentials {
                username = project.findProperty("USERNAME")?.toString() ?: ""
                password = project.findProperty("PASSWORD")?.toString() ?: ""
            }
        }
    }
}