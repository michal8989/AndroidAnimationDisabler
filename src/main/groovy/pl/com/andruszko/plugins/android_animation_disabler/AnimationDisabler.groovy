package pl.com.andruszko.plugins.android_animation_disabler

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApplicationVariant
import org.gradle.api.Plugin
import org.gradle.api.Project

class AnimationDisablerPlugin implements Plugin<Project> {


    @Override
    void apply(Project project) {

        project.plugins.withId('com.android.application') {
            AppExtension android = project.extensions.getByType(AppExtension);

            android.applicationVariants.all { ApplicationVariant variant ->
                def task = project.tasks.findByName("assemble${variant.name.capitalize()}AndroidTest".toString());

                if (task != null) {
                    // Define disable animations from command line task
                    DisableAnimationTask disableAnimationTask = (DisableAnimationTask) project.task("disable${variant.name.capitalize()}Animations",
                            type: DisableAnimationTask,
                            dependsOn: "install${variant.name.capitalize()}"
                    )
                    disableAnimationTask.variant = variant

                    // Adding task dependency, assemble will run only when we disable animations.
                    project.tasks
                            .findByName("assemble${variant.name.capitalize()}AndroidTest".toString())
                            .dependsOn "disable${variant.name.capitalize()}Animations"


                    // Add premission to manifest
                    AddAnimationPermissionToManifestTask addAnimationPermissionToManifestTask = (AddAnimationPermissionToManifestTask) project.task("add${variant.name.capitalize()}AnimationScaleManifestPremission",
                            type: AddAnimationPermissionToManifestTask,
                            dependsOn: "process${variant.name.capitalize()}Manifest"
                    )
                    addAnimationPermissionToManifestTask.variant = variant

                    // We are adding premission to Manifest only when it is generated and ready to make any changes.
                    project.tasks
                            .findByName("generate${variant.name.capitalize()}Sources".toString())
                            .dependsOn "add${variant.name.capitalize()}AnimationScaleManifestPremission"
                }
            }
        }

    }
}
