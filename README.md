# Composer  
Composer is a build system for Compose API, allowing easy way 
to gain access to Minecraft Server source code.
The project is currently running on yarn mappings, 
as they're currently the most complete mappings.  

### Installation  
There's currently no way to install the plugin :(  

### Usage  
Here's a sample Gradle build file:  

```kotlin
// build.gradle.kts
import lol.koblizek.composer.runtimeConfig
import lol.koblizek.composer.minecraft

runtimeConfig {
    // Use a server JAR file which is actually located in the wrapper JAR for decompilation
    extractAndUseInstead("META-INF/versions/1.19.4/server-1.19.4.jar")
    // Set default folder for decompiled code
    decompileIn(project.sourceSets.main.java.srcDir.iterator().next())
}

dependencies {
    // Selects which Minecraft version download
    minecraft("1.19.4")
}
```

### Contributing  
You might see that I'm doing this all alone and my code isn't good all the times
you can contribute to the project, it'd help me a lot and save some time.
Just fork the repository, make changes and create a pull request, it's not that hard!