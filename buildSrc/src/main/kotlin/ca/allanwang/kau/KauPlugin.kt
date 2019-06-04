package ca.allanwang.kau

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

class KauPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.extensions.create<Versions>("kau")
        project.extensions.create<Plugins>("kauPlugin")
        project.extensions.create<Dependencies>("kauDependency")
        project.extensions.create<ChangelogGenerator>("kauChangelog", project)
    }
}