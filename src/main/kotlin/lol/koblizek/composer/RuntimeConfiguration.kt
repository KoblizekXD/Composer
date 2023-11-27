package lol.koblizek.composer

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
    }

    fun decompile(opt: DecompileOptions.() -> Unit) {
        val temp = DecompileOptions()
        opt(temp)
        decompileOptions = temp
    }
}