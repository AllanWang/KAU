package ca.allanwang.kau

import org.gradle.api.Plugin
import org.gradle.api.Project

class KauPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.extensions.create("kau", Versions)
        project.extensions.create("kauPlugin", Plugins)
        project.extensions.create("kauDependency", Dependencies)
        project.extensions.create("kauChangelog", ChangelogGenerator, project)
    }

}