package lol.koblizek.composer

import java.io.Serializable

class RuntimeConfiguration : Serializable {
    internal val resources: ArrayList<String> = ArrayList()
    internal val toRemove: ArrayList<String> = ArrayList()
    internal val moveToRoot: ArrayList<String> = ArrayList()
    internal var decompilationSource: String? = null
    internal var resourcesSource: String? = null
    internal var useInstead: String? = null

    /**
     * Marks file or directory in source directory as a resource.
     * The resource will be moved in the next game configuration or when prompted.
     *
     * @param name name of the file/directory to be marked and moved
     */
    fun markAsResource(name: String) {
        resources.add(name)
    }

    fun remove(name: String) {
        toRemove.add(name)
    }

    fun moveToRoot(relPath: String) {
        moveToRoot.add(relPath)
    }

    fun decompileIn(sourcePath: String) {
        decompilationSource = sourcePath
    }

    fun putResourcesIn(sourcePath: String) {
        resourcesSource = sourcePath
    }

    fun extractAndUseInstead(relativeZipLocation: String) {
        useInstead = relativeZipLocation
    }

    fun minecraft(version: String) {
        ComposerPlugin.version = version
    }
}