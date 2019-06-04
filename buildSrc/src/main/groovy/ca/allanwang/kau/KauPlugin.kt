package ca.allanwang.kau

import org.gradle.api.Plugin
import org.gradle.api.Project

class KauPlugin : Plugin<Project> {

    override fun apply( project: Project) {
        project.extensions.create("kau", Versions::class)
        project.extensions.create("kauPlugin", Plugins::class)
        project.extensions.create("kauDependency", Dependencies::class)
//        project.extensions.create("kauChangelog", ChangelogGenerator, project)
    }

}