package ca.allanwang.kau

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

class KauPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.extensions.run {
            create<Versions>("kau")
            create<Dependencies>("kauDependency")
            create<Plugins>("kauPlugin")
            create<ChangelogGenerator>("kauChangelog", project)
        }
    }
}