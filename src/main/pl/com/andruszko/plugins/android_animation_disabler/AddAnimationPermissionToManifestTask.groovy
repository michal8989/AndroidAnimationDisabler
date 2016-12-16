package pl.com.andruszko.plugins.android_animation_disabler

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApplicationVariant
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class AddAnimationPermissionToManifestTask extends DefaultTask {

    private final AppExtension android;
    public ApplicationVariant variant;

    public AddAnimationPermissionToManifestTask() {
        android = project.extensions.getByType(AppExtension.class);
    }

    void copyAndReplaceText(File source, File dest) {
        String output = source.text.replace('<!-- ANIMATION_SCALE_INJECTION_PLACE -->', '<uses-permission android:name="android.permission.SET_ANIMATION_SCALE"/>');
        dest.write(output);
        if (output.contains('SET_ANIMATION_SCALE')) {
            project.logger.info("SUCCESS the SET_ANIMATION_SCALE permission for $variant.name")
        } else {
            project.logger.info("FAILURE the SET_ANIMATION_SCALE permission for $variant.name")
        }
    }

    @TaskAction
    void taskAction() {
        // Override Data in Manifest - This can be done using different Manifest files for each flavor, this way there's no need to modify the manifest
        project.logger.info("Adding the SET_ANIMATION_SCALE permission for $variant.name")

        variant.outputs.each { output ->
            File manifest = output.processManifest.manifestOutputFile;
            copyAndReplaceText(manifest, manifest);
        }
    }
}
