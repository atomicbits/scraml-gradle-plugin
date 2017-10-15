package io.atomicbits.scraml.gradleplugin;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

/**
 * Created by peter on 13/10/17.
 */
public class GenerateScramlCode extends DefaultTask {

    final static String name = "generateScraml";

    public GenerateScramlCode() {
        setDescription("Generates REST client code from a RAML model.");
    }

    @TaskAction
    public void generate() {
        System.out.println("Hello Scraml!!!");

        // getProject();


        /**

         compileJava
         src/main/java

         processResources
         src/main/resources

         compileTestJava
         src/test/java

         processTestResources
         src/test/resources

         "
         Assuming that the initial build is successful (i.e. the build task and its dependencies complete without error), changes to
         files in, or the addition/remove of files from, the locations listed above will initiate a new build. If a change is made to
         a Java source file in src/main/java, the build will fire and all tasks will be scheduled. Gradle’s incremental build support
         ensures that only the tasks that are actually affected by the change are executed.

         If the change to the main Java source causes compilation to fail, subsequent changes to the test source in src/test/java will
         not initiate a new build. As the test source depends on the main source, there is no point building until the main source has
         changed, potentially fixing the compilation error. After each build, only the inputs of the tasks that actually executed will
         be monitored for changes.

         Continuous build is in no way coupled to compilation. It works for all types of tasks. For example, the processResources task
         copies and processes the files from src/main/resources for inclusion in the built JAR. As such, a change to any file in this
         directory will also initiate a build.
         " -- https://docs.gradle.org/current/userguide/userguide_single.html

         */



    }

    private ScramlExtension getScramlExtension() {
        return (ScramlExtension) getProject().getExtensions().getByName(ScramlExtension.name);
    }

}
