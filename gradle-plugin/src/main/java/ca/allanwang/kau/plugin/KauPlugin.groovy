package ca.allanwang.kau.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class KauPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        println("Hello KAU")
        project.extensions.create("kauDependencies", Dependencies)
    }

    class Dependencies {
        String test = 'asdf'
    }
}