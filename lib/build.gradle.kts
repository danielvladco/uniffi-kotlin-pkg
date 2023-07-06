plugins {
    id("org.jetbrains.kotlin.jvm") version "1.8.10"

    `java-library`
    // Added the maven-publish plugin
    `maven-publish`
}

repositories {
   // Use Maven Central for resolving dependencies.
   mavenCentral()   
   // Use local repository for testing
   mavenLocal()
}

dependencies {
  // Add the JNA. No need to manually include the jar: https://github.com/java-native-access/jna
  implementation("net.java.dev.jna:jna:5.8.0")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

// Inlude the shared libraries.
val libsDir = File("libs")
tasks.withType<Jar> {
    from(libsDir) { include("**/*.so") }
    from(libsDir) { include("**/*.dylib") }
}

publishing {
   publications {
    // Github packages
	create<MavenPublication>("gpr") {
        from(components["java"])
        pom {
		// Artifact coordinates and info. To be setup for production.
		groupId = "uniffi"
        artifactId = "zcash"
		version = "0.0.0"
                description.set("The librustzcash Kotlin FFI binding")
                url.set("https://github.com/eigerco/uniffi-zcash-lib")
                licenses {
                    license {
                        name.set("The MIT License")
                        url.set("https://github.com/eigerco/uniffi-zcash-lib/blob/main/LICENSE")
                    }
                }
           }
	}
   }
   repositories {
     maven {
        name = "GitHubPackages"
        // url = uri(System.getenv("KOTLIN_REGISTRY_URL")) // https://example.com/repository/maven
        url = uri("https://maven.pkg.github.com/eigerco/uniffi-zcash-lib")
        isAllowInsecureProtocol = true // uncomment this for testing.
        credentials {
            // username = System.getenv("KOTLIN_REGISTRY_USERNAME") // Use "token" as the username for API token authentication
            // password = System.getenv("KOTLIN_REGISTRY_PASSWORD")
            username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
            password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
        }
     }
  }
}