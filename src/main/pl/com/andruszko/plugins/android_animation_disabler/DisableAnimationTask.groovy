package pl.com.andruszko.plugins.android_animation_disabler

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApplicationVariant
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

import static com.thisisglobal.plugins.gradle.android.instrumentation.animation_disabler.ProcessHelper.runWithLogger

class DisableAnimationTask extends DefaultTask {

    private final AppExtension android
    public ApplicationVariant variant

    DisableAnimationTask() {
        android = project.extensions.getByType(AppExtension.class);
    }

    @TaskAction
    void taskAction() {
        project.logger.info("INVOKING ADB: " + [getAdb(), 'devices'])
        [getAdb(), 'devices'].execute().text.eachLine { line ->
            def matcher = line =~ /^(.*)\tdevice/
            if (matcher) {
                grantAnimationForDevice(matcher[0][1])
            }
        }
    }

    def grantAnimationForDevice(String deviceId) {
        def adb = getAdb()
        int returnCode = runWithLogger("$adb -s $deviceId shell pm grant $variant.applicationId android.permission.SET_ANIMATION_SCALE", project.logger, null, null);
        if (returnCode != 0) {
            throw new GradleException("Could not grant permission to disable animations");
        }
    }

    def getAdb() {
        android.getAdbExe().toString()
    }
}
