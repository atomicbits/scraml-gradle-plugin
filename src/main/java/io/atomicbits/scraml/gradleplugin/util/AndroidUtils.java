package io.atomicbits.scraml.gradleplugin.util;

import com.android.build.gradle.BaseExtension;
import org.gradle.api.Project;

/**
 * Created by peter on 2/11/17.
 */
public class AndroidUtils {

    static final String ANDROID_PROJECT_PROPERTY = "android";

    public static boolean isAndroidProject(Project project) {
        if (project.hasProperty(ANDROID_PROJECT_PROPERTY)) {
            BaseExtension androidLibraryOrAppExtension = (BaseExtension) project.getProperties().get("android");
            return androidLibraryOrAppExtension.getSourceSets().size() > 0;
        }
        return false;
    }

}
