package ca.allanwang.kau

import groovy.util.Node
import groovy.util.XmlParser
import org.gradle.api.GradleException
import org.gradle.api.Project
import java.io.File

/**
 * Given an xml of the format
 *
 * <?xml version="1.0" encoding="utf-8"?>
 *  <resources>
 *  <version title="v0.1" />
 *  <item text="Initial Changelog" />
 *  <item text="Bullet point here" />
 *  <item text="More points" />
 *  <item text="" /> <!-- this one is empty and therefore ignored -->
 * </resources>
 *
 * Outputs a changelog in markdown format
 */
open class ChangelogGenerator(private val project: Project) {

    class ChangelogException(message: String) : GradleException(message)

    private fun fail(message: String): Nothing =
        throw ChangelogException(message)

    class ChangelogEntry(val version: String, val items: Array<String>)

    private fun Node.forEachNode(action: (Node) -> Unit) {
        children().forEach {
            action(it as Node)
        }
    }

    fun read(inputUri: String): List<ChangelogEntry> {
        val input = File(inputUri)
        if (!input.exists()) {
            fail("Could not generate changelog from ${input.absolutePath}")
        }

        val parser = XmlParser().parse(inputUri)

        val entries: MutableList<ChangelogEntry> = mutableListOf()
        var version: String? = null
        val items: MutableList<String> = mutableListOf()

        fun addEntry() {
            version?.also { v ->
                entries.add(ChangelogEntry(v, items.toTypedArray()))
                items.clear()
            }
        }

        parser.depthFirst().mapNotNull { it as? Node }.forEach { n ->
            when (n.name()) {
                "version" -> {
                    addEntry()
                    version = n.attribute("title")?.toString() ?: ""
                }
                "item" -> {
                    n.attribute("text")?.toString()?.takeIf(String::isNotBlank)?.let {
                        items.add(it)
                    }
                }
            }
        }
        addEntry()
        return entries
    }

    @JvmOverloads
    fun generate(inputUri: String, outputUri: String = "${project.rootDir}/docs/Changelog.md") {
        val entries = read(inputUri)
        val output = File(outputUri)
        if (output.exists()) {
            if (output.isDirectory) {
                fail("Cannot save changelog at directory ${output.absolutePath}")
            }
            if (output.isFile && !output.delete()) {
                fail("Could not delete changelog at ${output.absolutePath}")
            }
        } else {
            output.parentFile.mkdirs()
        }

        if (!output.createNewFile()) {
            fail("Could not create changelog file at ${output.absolutePath}")
        }
        val markdown = buildString {
            append("# Changelog\n")
            entries.forEach { e ->
                append("\n## ${e.version}\n")
                e.items.forEach {
                    append("* $it\n")
                }
            }
        }
        output.writeText(markdown)
        println("Generated changelog at ${output.absolutePath}")
    }
}