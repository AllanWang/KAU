import org.gradle.api.Plugin
import org.gradle.api.Project

class KauPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        println("Hello KAU")
        project.extensions.create("kauDependencies", Dependencies::class.java)
    }

    class Dependencies {
        val test = "1.2.3"
    }
}