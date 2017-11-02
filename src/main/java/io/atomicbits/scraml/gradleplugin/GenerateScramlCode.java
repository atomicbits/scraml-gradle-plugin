package io.atomicbits.scraml.gradleplugin;

import com.android.build.gradle.BaseExtension;
import com.android.build.gradle.api.AndroidSourceSet;
import io.atomicbits.scraml.generator.ScramlGenerator;
import io.atomicbits.scraml.gradleplugin.util.AndroidUtils;
import io.atomicbits.scraml.gradleplugin.util.ListUtils;
import io.atomicbits.scraml.gradleplugin.util.StringUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.UnknownDomainObjectException;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.api.tasks.TaskAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;

/**
 * Created by peter on 13/10/17.
 */
public class GenerateScramlCode extends DefaultTask {

    private final Logger logger = LoggerFactory.getLogger("scraml");

    final static String name = "generateScraml";
    static final String SOURCE_SETS_PROPERTY = "sourceSets";

    @TaskAction
    public void generate() {

        ScramlExtension scramlExtension = getScramlExtension();
        String ramlApi = scramlExtension.getRamlApi();
        String apiPackage = scramlExtension.getApiPackage();
        String licenseKey = scramlExtension.getLicenseKey();
        String classHeader = scramlExtension.getClassHeader();
        String platform = getPlatform();
        String resourceDirectory = findResourceDirectory();
        File outputDir = getOutputDirectory();

        logger.debug("ramlApi: " + ramlApi);
        logger.debug("apiPackage: " + apiPackage);
        logger.debug("licenseKey: " + licenseKey);
        logger.debug("classHeader: " + classHeader);
        logger.debug("resourceDirectory: " + resourceDirectory);
        logger.debug("outputDirectory: " + outputDir.getAbsolutePath());

        if (!StringUtils.isNullOrEmpty(ramlApi) && !StringUtils.isNullOrEmpty(resourceDirectory)) {
            System.out.println("Generating Scraml client API");

            File ramlBaseDir;
            File ramlSource;
            if (isTopLevel(resourceDirectory)) {
                ramlBaseDir = new File(resourceDirectory);
                ramlSource = new File(ramlBaseDir, ramlApi);
            } else {
                ramlBaseDir = new File(getProject().getProjectDir(), resourceDirectory);
                ramlSource = new File(ramlBaseDir, ramlApi);
            }

            String[] apiPackageAndClass = packageAndClassFromRamlPointer(ramlApi, apiPackage);
            String apiPackageName = apiPackageAndClass[0];
            String apiClassName = apiPackageAndClass[1];

            Map<String, String> generatedFiles;
            try {
                if (Platform.SCALA_PLAY.toLowerCase().equals(platform.toLowerCase())) {
                    generatedFiles =
                            ScramlGenerator.generateScalaCode(
                                    ramlSource.toURI().toURL().toString(),
                                    apiPackageName,
                                    apiClassName,
                                    licenseKey,
                                    classHeader
                            );
                } else { // default
                    generatedFiles =
                            ScramlGenerator.generateJavaCode(
                                    ramlSource.toURI().toURL().toString(),
                                    apiPackageName,
                                    apiClassName,
                                    licenseKey,
                                    classHeader
                            );
                }
            } catch (MalformedURLException | NullPointerException e) {
                feedbackOnException(ramlBaseDir, ramlApi, ramlSource);
                throw new RuntimeException("Could not generate RAML client.", e);
            }

            outputDir.mkdirs();
            // ToDo: delete everything below outputDirAsFile recursively

            try {
                for (Map.Entry<String, String> entry : generatedFiles.entrySet()) {
                    String filePath = entry.getKey();
                    String content = entry.getValue();
                    File fileInDst = new File(outputDir, filePath);
                    fileInDst.getParentFile().mkdirs();
                    FileWriter writer = new FileWriter(fileInDst);
                    writer.write(content);
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Could not generate RAML client.", e);
            }


        } else {
            logger.debug("No Scraml client API generated in project " + getProject().getDisplayName() +
                    " because there is no ramlApi set, or the resourceDirectory could not be found.");
        }

    }

    @OutputDirectory
    public File getOutputDirectory() {
        String outputDirectory = findOutputDirectory();
        File outputBaseDirAsFile;
        if (isTopLevel(outputDirectory)) {
            outputBaseDirAsFile = new File(outputDirectory);
        } else {
            outputBaseDirAsFile = new File(getProject().getProjectDir(), outputDirectory);
        }
        return new File(outputBaseDirAsFile, "generated-sources/scraml");
    }


    private ScramlExtension getScramlExtension() {
        return (ScramlExtension) getProject().getExtensions().getByName(ScramlExtension.name);
    }

    private String findOutputDirectory() {
        String outputDirectory = getScramlExtension().getOutputDirectory();
        if (outputDirectory == null) {
            outputDirectory = getProject().getBuildDir().getAbsolutePath();
        }
        return outputDirectory;
    }

    private String findResourceDirectory() {
        String resourceDir = getScramlExtension().getResourceDirectory();
        if (resourceDir == null) {
            if (AndroidUtils.isAndroidProject(getProject())) {
                BaseExtension androidLibraryOrAppExtension = (BaseExtension) getProject().getProperties().get("android");
                AndroidSourceSet androidMainSourceSet =
                        androidLibraryOrAppExtension.getSourceSets().getByName(SourceSet.MAIN_SOURCE_SET_NAME);
                // androidMainSourceSet.getJava().setSrcDirs()
                resourceDir =
                        androidMainSourceSet
                                .getResources()
                                .getSourceDirectoryTrees()
                                .iterator().next()
                                .getDir().getAbsolutePath();
            } else {
                SourceSetContainer sourceSets = (SourceSetContainer) getProject().getProperties().get(SOURCE_SETS_PROPERTY);
                // sourceSets.stream().forEach(sourceSet -> {
                //     logger.debug("Sourceset: " + sourceSet.getName());
                // });
                if (sourceSets != null) {
                    try {
                        SourceDirectorySet resources =
                                sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
                                        .getResources();
//                        resources.getSrcDirTrees().stream().forEach(directoryTree -> {
//                            logger.debug("resource directory: " + directoryTree.getDir().getAbsolutePath());
//                        });
                        resourceDir =
                                resources.getSrcDirTrees().iterator().next()
                                        .getDir()
                                        .getAbsolutePath();
                    } catch (UnknownDomainObjectException | NoSuchElementException exc) {
                        // ignore, resourceDir will remain null
                    }
                }
            }
        }
        logger.debug("Resource dir is: " + resourceDir);
        return resourceDir != null ? resourceDir : "";
    }

    private Boolean isTopLevel(String directory) {
        return !StringUtils.isNullOrEmpty(directory) &&
                (directory.startsWith("/") || directory.contains(":\\") || directory.contains(":/"));
    }

    private String getPlatform() {

        ScramlExtension scramlExtension = getScramlExtension();
        String platform = scramlExtension.getPlatform();
        String language = scramlExtension.getLanguage();

        if (StringUtils.isDefined(platform)) {
            if ("scala".equals(platform.toLowerCase())) {
                return Platform.SCALA_PLAY;
            } else if ("java".equals(platform.toLowerCase())) {
                return Platform.JAVA_JACKSON;
            } else {
                return platform;
            }
        } else if (StringUtils.isDefined(language)) {
            if ("scala".equals(language.toLowerCase())) {
                return Platform.SCALA_PLAY;
            } else if ("java".equals(language.toLowerCase())) {
                return Platform.JAVA_JACKSON;
            } else {
                return language;
            }
        } else {
            return Platform.JAVA_JACKSON;
        }
    }


    private String escape(char ch) {
        return "\\Q" + ch + "\\E";
    }

    private String[] packageAndClassFromRamlPointer(String pointer, String apiPackageName) {
        String[] parts = pointer.split(escape('/'));
        if (parts.length == 1) {
            String packageName;
            if (StringUtils.isNullOrEmpty(apiPackageName))
                packageName = "io.atomicbits";
            else
                packageName = apiPackageName;
            return new String[]{packageName, cleanFileName(parts[0])};
        } else {
            String className = cleanFileName(parts[parts.length - 1]);
            List<String> firstParts = Arrays.asList(parts).subList(0, parts.length - 1); // toIndex is exclusive
            String packageName;
            if (StringUtils.isNullOrEmpty(apiPackageName))
                packageName = ListUtils.mkString(firstParts, ".");
            else
                packageName = apiPackageName;
            return new String[]{packageName, className};
        }
    }

    private String cleanFileName(String fileName) {
        String[] nameSplit = fileName.split(escape('.'));
        String withOutExtension;
        if (nameSplit.length == 0) {
            withOutExtension = fileName;
        } else {
            withOutExtension = nameSplit[0];
        }

        // capitalize after special characters and drop those characters along the way
        List<Character> dropChars = Arrays.asList('-', '_', '+', ' ');
        String cleanedDropChars = withOutExtension;
        for (Character dropChar : dropChars) {
            List<String> items = removeEmpty(Arrays.asList(cleanedDropChars.split(escape(dropChar))));
            List<String> capitalized = new ArrayList<>();
            for (String item : items) {
                capitalized.add((capitalize(item)));
            }
            cleanedDropChars = ListUtils.mkString(capitalized, "");
        }

        // capitalize after numbers 0 to 9, but keep the numbers
        List<Character> numbers = Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9');
        // Make sure we don't drop the occurrences of numbers at the end by adding a space and removing it later.
        String cleanedNumbers = cleanedDropChars + " ";
        for (Character number : numbers) {
            List<String> items = Arrays.asList(cleanedNumbers.split(escape(number))); // it's important NOT to remove the empty strings here
            List<String> capitalized = new ArrayList<>();
            for (String item : items) {
                capitalized.add((capitalize(item)));
            }
            cleanedNumbers = ListUtils.mkString(capitalized, number.toString());
        }

        // final cleanup of all strange characters
        return cleanedNumbers.replaceAll("[^A-Za-z0-9]", "").trim();
    }

    private String capitalize(String dirtyName) {
        char[] chars = dirtyName.toCharArray();
        if (chars.length > 0) {
            chars[0] = Character.toUpperCase(chars[0]);
        }
        return new String(chars);
    }


    private List<String> removeEmpty(List<String> items) {
        List<String> emptied = new ArrayList<>();
        for (String item : items) {
            if (!item.isEmpty()) {
                emptied.add(item);
            }
        }
        return emptied;
    }

    private void feedbackOnException(File ramlBaseDir,
                                     String ramlPointer,
                                     File ramlSource) {
        System.out.println(
                "Exception during RAMl parsing, possibly caused by a wrong RAML path.\n" +
                        "Are you sure the following values are correct (non-null)?\n\n" +
                        "- - - - - - - - - - - - - - - - - - - - - - -\n" +
                        "RAML base path: " + ramlBaseDir + "\n" +
                        "RAML relative path: " + ramlPointer + "\n" +
                        "RAML absolute path" + ramlSource + "\n" +
                        "- - - - - - - - - - - - - - - - - - - - - - -\n\n" +
                        "In case the relative path is wrong or null, check your project settings and" +
                        "make sure the 'scramlRamlApi in scraml in Compile' value points to the main" +
                        "raml file in your project's (or module's) resources directory."
        );
    }


}
