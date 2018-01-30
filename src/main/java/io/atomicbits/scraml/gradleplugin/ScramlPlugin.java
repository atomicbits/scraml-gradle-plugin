package io.atomicbits.scraml.gradleplugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;
<<<<<<< HEAD
=======
import org.gradle.api.tasks.SourceSetContainer;

import java.util.*;
>>>>>>> release/0.8.0-M1


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
<<<<<<< HEAD
        for (Task task : project.getTasksByName("compileJava", false)) {
            task.dependsOn(scramlTask);
=======
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
>>>>>>> release/0.8.0-M1
        }

//        Map<Project, Set<Task>> projectsWithTasks = project.getAllTasks(false);
//        for (Set<Task> taskSet : projectsWithTasks.values()) {
//            for (Task task : taskSet) {
//                setDependsOn(task, project, scramlTask);
//            }
//        }

        // Add the generated source output dir to the Java source directories.
        SourceSet mainSourceSet =
                project.getConvention().getPlugin(JavaPluginConvention.class).getSourceSets().getByName(SourceSet.MAIN_SOURCE_SET_NAME);
        mainSourceSet.getJava().srcDir(project.relativePath(scramlTask.getOutputDirectory()));
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


}
