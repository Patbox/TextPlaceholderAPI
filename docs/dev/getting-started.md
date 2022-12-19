# Getting Started

To begin, you need to add Nucleoid's maven to your build file.

=== "Groovy"

    ```groovy title="build.gradle"
    repositories {
        // There might be other repos there too, just add it at the end
        maven { 
            url "https://maven.nucleoid.xyz/"
            name "Nucleoid"
        }
    }
    ```

===+ "Kotlin"

    ```kotlin title="build.gradle.kts"
    repositories {
        // There might be other repos there too, just add it at the end
        maven("https://maven.nucleoid.xyz/") { name = "Nucleoid" }
    }
    ```

Then you just declare it as dependency!

!!! info inline end

	[![Latest Version](https://img.shields.io/maven-metadata/v?color=blue&label=version&metadataUrl=https%3A%2F%2Fmaven.nucleoid.xyz%2Feu%2Fpb4%2Fplaceholder-api%2Fmaven-metadata.xml&style=for-the-badge)](https://maven.nucleoid.xyz/eu/pb4/placeholder-api/)

    You just need to replace `[VERSION]` with version you want to use (which should be usually the latest available).
    For list of version names, you can check [maven](https://maven.nucleoid.xyz/eu/pb4/placeholder-api/)

=== "Groovy"

    ```groovy title="build.gradle"
    dependencies {
        // You will have other dependencies here too
        
        modImplementation include("eu.pb4:placeholder-api:[VERSION]")
    }
    ```

===+ "Kotlin"

    ```kotlin title="build.gradle.kts"
    dependencies {
        // You will have other dependencies here too

        modImplementation(include("eu.pb4:placeholder-api:[VERSION]"))
    }
    ```

This will also include it in yours mods, so users won't need to download it separately.

You just need to replace `[VERSION]` with the version you want to use (which should be usually the latest available).
For list of version names, you can check [Maven Repository](https://maven.nucleoid.xyz/eu/pb4/placeholder-api/)
