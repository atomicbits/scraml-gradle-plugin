package io.atomicbits.scraml.gradleplugin;

import com.android.build.gradle.BaseExtension;
import com.android.build.gradle.api.AndroidSourceSet;
import io.atomicbits.scraml.gradleplugin.util.AndroidUtils;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.UnknownDomainObjectException;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;

import java.util.*;


/**
 * Created by peter on 13/10/17.
 */
public class ScramlPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getExtensions().create(ScramlExtension.name, ScramlExtension.class, project);
        GenerateScramlCode scramlTask = project.getTasks().create(GenerateScramlCode.name, GenerateScramlCode.class);
        scramlTask.setDescription("Generates REST client code from a RAML model.");
        // depend the compile task on the codegen task
        // compileJava & compileTestJava: plain Java project
        // javaPreCompileDebug and javaPreCompileRelease: Java android project

        List<String> tasksToDependOn = Arrays.asList(
                "build", "compileJava", "compileTestJava", "javaPreCompileDebug", "javaPreCompileRelease",
                "compileDebugJavaWithJavac", "compileReleaseJavaWithJavac"
        );
        for (String dependingTaskName : tasksToDependOn) {
            for (Task task : project.getTasksByName(dependingTaskName, false)) {
                task.dependsOn(scramlTask);
            }
        }

//        Map<Project, Set<Task>> projectsWithTasks = project.getAllTasks(false);
//        for (Set<Task> taskSet : projectsWithTasks.values()) {
//            for (Task task : taskSet) {
//                setDependsOn(task, project, scramlTask);
//            }
//        }

        // Add the generated source output dir to the Java source directories.
        if (AndroidUtils.isAndroidProject(project)) {
            BaseExtension androidLibraryOrAppExtension = (BaseExtension) project.getProperties().get("android");
            try {
                AndroidSourceSet androidMainSourceSet =
                        androidLibraryOrAppExtension.getSourceSets().getByName(SourceSet.MAIN_SOURCE_SET_NAME);
                androidMainSourceSet.getJava().srcDirs(project.relativePath(scramlTask.getOutputDirectory()));
            } catch (UnknownDomainObjectException | NoSuchElementException exc) {
                // ignore, resourceDir will remain null
            }
        } else {
            SourceSetContainer sourceSets = (SourceSetContainer) project.getProperties().get(GenerateScramlCode.SOURCE_SETS_PROPERTY);
            if (sourceSets != null) {
                try {
                    sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
                            .getJava()
                            .srcDir(project.relativePath(scramlTask.getOutputDirectory()));
                } catch (UnknownDomainObjectException | NoSuchElementException exc) {
                    // ignore, resourceDir will remain null
                }
            }
        }
    }

    private boolean isTaskDependentOnScramlTask(String taskName) {
        String taksNameLowerCase = taskName.toLowerCase();
        return (taksNameLowerCase.contains("java") && taksNameLowerCase.contains("compile")) || taksNameLowerCase.equals("build");
    }

    private void setDependsOn(Task task, Project project, GenerateScramlCode scramlTask) {

        String taskName = task.getName();
        System.out.println("Task name: " + taskName);

        if (isTaskDependentOnScramlTask(taskName)) {
            for (Task javaCompileTask : project.getTasksByName(taskName, false)) {
                javaCompileTask.dependsOn(scramlTask);
            }
        }

        for (Object potentialTask : task.getDependsOn()) {
            System.out.println("Potential task is: " + potentialTask.getClass().getCanonicalName());
            if (potentialTask instanceof Task) {
                setDependsOn((Task) potentialTask, project, scramlTask);
            }
        }

    }


    // Public resources:
    // - - - - - - - - -
    // https://docs.gradle.org/current/userguide/userguide_single.html
    // https://guides.gradle.org/implementing-gradle-plugins/
    // https://discuss.gradle.org/t/compile-classes-from-generated-resources-before-java-compilation/20007/2
    // https://docs.gradle.org/current/userguide/build_lifecycle.html
    // https://github.com/andreas-schilling/joda-beans-gradle-plugin
    // ConfigurableFileCollection: https://docs.gradle.org/current/javadoc/org/gradle/api/Project.html#files(java.lang.Object...)
    // IncrementalTaskInputs: https://docs.gradle.org/current/dsl/org.gradle.api.tasks.incremental.IncrementalTaskInputs.html
    // Java Plugin: https://docs.gradle.org/current/userguide/java_plugin.html
    //      see SourceSet properties: resources.srcDirs
    // Gradle generated source compilation:
    //      https://discuss.gradle.org/t/how-to-use-gradle-with-generated-sources/9401/5
    //  better:
    //      https://discuss.gradle.org/t/compile-classes-from-generated-resources-before-java-compilation/20007/2
    // Android gradle plugin example:
    //      https://github.com/google/protobuf-gradle-plugin/blob/master/src/main/groovy/com/google/protobuf/gradle/ProtobufPlugin.groovy
    //


}
