package io.atomicbits.scraml.gradleplugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.UnknownDomainObjectException;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;

import java.util.NoSuchElementException;


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
        for (Task task : project.getTasksByName("compileJava", false)) {
            task.dependsOn(scramlTask);
        }
        // Add the generated source output dir to the Java source directories.
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
