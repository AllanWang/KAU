package ca.allanwang.kau

import org.gradle.api.GradleException
import org.gradle.api.Project

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
class ChangelogGenerator {

    static class ChangelogException extends GradleException {
        ChangelogException(String message) {
            super(message)
        }
    }

    private Project project

    ChangelogGenerator(Project project) {
        this.project = project
    }

    private static void fail(String message) {
        throw new ChangelogException(message)
    }

    final void generate(String inputUri, String outputUri = "$project.rootDir/docs/Changelog.md") {
        def input = new File(inputUri)
        if (!input.exists())
            fail("Could not generate changelog from ${input.absolutePath}")

        def output = new File(outputUri)

        if (output.exists()) {
            if (output.isDirectory())
                fail("Cannot save changelog at directory ${output.absolutePath}")


            if (output.isFile() && !output.delete())
                fail("Could not delete changelog at ${output.absolutePath}")
        } else {
            output.parentFile.mkdirs()
        }

        if (!output.createNewFile())
            fail("Could not create changelog file at ${output.absolutePath}")

        def parsedProjectXml = (new XmlParser()).parse(inputUri)
        def sw = new StringWriter()
        sw.append("# Changelog\n")
        parsedProjectXml.depthFirst().each {
            switch (it.name()) {
                case "version":
                    sw.append("\n## ${it.@title}\n")
                    break
                case "item":
                    if (it.@text?.trim())
                        sw.append("* ${it.@text}\n")
            }
        }
        output.write(sw.toString())
        println("Generated changelog at ${output.absolutePath}")
    }

}