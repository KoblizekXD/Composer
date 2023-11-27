package lol.koblizek.composer

import java.io.File
import java.io.Serializable
import java.net.URI
import java.net.URL

class RuntimeConfiguration : Serializable {
    internal val resources: ArrayList<String> = ArrayList()
    internal val toRemove: ArrayList<String> = ArrayList()
    internal val moveToRoot: ArrayList<String> = ArrayList()
    internal var decompilationSource: String? = null
    internal var resourcesSource: String? = null
    internal var useInstead: String? = null

    internal lateinit var dataSources: DataSources
    internal lateinit var remapOptions: RemapOptions
    internal lateinit var decompileOptions: DecompileOptions

    fun areDataSourcesInitialized(): Boolean = ::dataSources.isInitialized
    fun areRemapOptionsInitialized(): Boolean = ::remapOptions.isInitialized
    fun areDecompOptionsInitialized(): Boolean = ::decompileOptions.isInitialized

    /**
     * Sources used for downloading required data
     */
    class DataSources {
        enum class Side {
            CLIENT, SERVER
        }

        /**
         * URL to the game's main JAR file
         */
        lateinit var game: String

        /**
         * URL to the game's json file containing libraries etc.
         */
        lateinit var gameJson: String

        /**
         * URL to the mappings file
         */
        lateinit var mappings: String

        /**
         * Game Side, either client or server
         */
        lateinit var side: Side

        internal lateinit var newMappings: File.() -> File

        fun isSideInitialized(): Boolean = ::side.isInitialized
        fun isMappingsInitialized(): Boolean = ::mappings.isInitialized
        fun isGameJsonInitialized(): Boolean = ::gameJson.isInitialized
        fun isGameInitialized(): Boolean = ::game.isInitialized
        fun doUseAltMappingFile(): Boolean = ::newMappings.isInitialized

        fun isEverythingInitialized(): Boolean {
            return isGameInitialized() && isSideInitialized() && isGameJsonInitialized() && isMappingsInitialized()
        }

        /**
         * Allows to manipulate with the default downloaded mapping file,
         * before it's passed to any other task. This can help in cases,
         * where mappings are delivered in Jar archives(Fabric mappings).
         *
         * @param file default mapping file downloaded, returns the new mapping file
         */
        fun editMappings(file: File.() -> File) {
            newMappings = file
        }
    }

    fun sources(src: DataSources.() -> Unit) {
        val temp = DataSources()
        src(temp)
        dataSources = temp
    }

    class RemapOptions {
        enum class MappingsProvider {
            SRG, TINYV2, MOJMAPS
        }

        /**
         * Types of mappings downloaded, will be used to parse
         */
        lateinit var mappings: MappingsProvider

        /**
         * Target namespace from the parsed mappings
         */
        lateinit var nms: String

        /**
         * Whether apply NeoForge's ART
         */
        var applyArt: Boolean = false

        fun isNmsInitialized(): Boolean = ::nms.isInitialized
        fun isMappingsInitialized(): Boolean = ::mappings.isInitialized
    }

    fun remap(opt: RemapOptions.() -> Unit) {
        val temp = RemapOptions()
        opt(temp)
        remapOptions = temp
    }

    class DecompileOptions {
        /**
         * Directory where the game should be decompiled,
         * if not initialized, game will not be decompiled and just be added to classpath
         */
        lateinit var targetDir: String

        /**
         * Whether create another directory for comparing and creating patches, ignored if
         * targetDir is uninitialized
         */
        var createPatchSources: Boolean = false

        fun isTargetDirInitialized(): Boolean = ::targetDir.isInitialized
    }

    fun decompile(opt: DecompileOptions.() -> Unit) {
        val temp = DecompileOptions()
        opt(temp)
        decompileOptions = temp
    }

    data class PostDecompilationWorkflow(val srcDirectory: File) {}
}