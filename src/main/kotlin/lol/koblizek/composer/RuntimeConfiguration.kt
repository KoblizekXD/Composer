package lol.koblizek.composer

class RuntimeConfiguration {
    private val resources: ArrayList<String> = ArrayList()
    private val toRemove: ArrayList<String> = ArrayList()
    private val moveToRoot: ArrayList<String> = ArrayList()

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
}