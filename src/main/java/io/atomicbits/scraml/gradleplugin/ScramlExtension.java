package io.atomicbits.scraml.gradleplugin;

import org.gradle.api.Project;
import org.gradle.api.provider.PropertyState;

/**
 * Created by peter on 13/10/17.
 */
public class ScramlExtension {

    private final PropertyState<String> ramlApi;
    private final PropertyState<String> apiPackage;
    private final PropertyState<String> language;
    private final PropertyState<String> platform;
    private final PropertyState<String> resourceDirectory;
    private final PropertyState<String> outputDirectory;
    private final PropertyState<String> licenseKey;
    private final PropertyState<String> classHeader;
    private final PropertyState<String> singleSourceFile;

    static final String name = "scraml";

    public ScramlExtension(Project project) {
        this.ramlApi = project.property(String.class);
        this.apiPackage = project.property(String.class);
        this.language = project.property(String.class);
        this.platform = project.property(String.class);
        this.resourceDirectory = project.property(String.class);
        this.outputDirectory = project.property(String.class);
        this.licenseKey = project.property(String.class);
        this.classHeader = project.property(String.class);
        this.singleSourceFile = project.property(String.class);

    }

    public PropertyState<String> getRamlApiProvider() {
        return this.ramlApi;
    }

    public String getRamlApi() {
        if (this.ramlApi.isPresent()) {
            return this.ramlApi.get();
        } else {
            return null;
        }
    }

    public void setRamlApi(String ramlApi) {
        this.ramlApi.set(ramlApi);
    }

    public PropertyState<String> getApiPackageProvider() {
        return this.apiPackage;
    }

    public String getApiPackage() {
        if (this.apiPackage.isPresent()) {
            return this.apiPackage.get();
        } else {
            return null;
        }
    }

    public void setApiPackage(String apiPackage) {
        this.apiPackage.set(apiPackage);
    }

    public PropertyState<String> getLanguageProvider() {
        return this.language;
    }

    public String getLanguage() {
        if (this.language.isPresent()) {
            return this.language.get();
        } else {
            return null;
        }
    }

    public void setLanguage(String language) {
        this.language.set(language);
    }

    public PropertyState<String> getPlatformProvider() {
        return this.platform;
    }

    public String getPlatform() {
        if (this.platform.isPresent()) {
            return this.platform.get();
        } else {
            return null;
        }
    }

    public void setPlatform(String platform) {
        this.platform.set(platform);
    }

    public PropertyState<String> getResourceDirectoryProvider() {
        return this.resourceDirectory;
    }

    public String getResourceDirectory() {
        if (this.resourceDirectory.isPresent()) {
            return this.resourceDirectory.get();
        } else {
            return null;
        }
    }

    public void setResourceDirectory(String resourceDirectory) {
        this.resourceDirectory.set(resourceDirectory);
    }

    public PropertyState<String> getOutputDirectoryProvider() {
        return this.outputDirectory;
    }

    public String getOutputDirectory() {
        if (this.outputDirectory.isPresent()) {
            return this.outputDirectory.get();
        } else {
            return null;
        }
    }

    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory.set(outputDirectory);
    }

    public PropertyState<String> getLicenseKeyProvider() {
        return this.licenseKey;
    }

    public String getLicenseKey() {
        if (this.licenseKey.isPresent()) {
            return this.licenseKey.get();
        } else {
            return null;
        }
    }

    public void setLicenseKey(String licenseKey) {
        this.licenseKey.set(licenseKey);
    }

    public PropertyState<String> getClassHeaderProvider() {
        return this.classHeader;
    }

    public String getClassHeader() {
        if (this.classHeader.isPresent()) {
            return this.classHeader.get();
        } else {
            return null;
        }
    }

    public void setClassHeader(String classHeader) {
        this.classHeader.set(classHeader);
    }


    public PropertyState<String> getSingleSourceFileProvider() {
        return this.singleSourceFile;
    }

    public String getSingleSourceFile() {
        if (this.singleSourceFile.isPresent()) {
            return this.singleSourceFile.get();
        } else {
            return null;
        }
    }

    public void setSingleSourceFile(String singleSourceFile) {
        this.singleSourceFile.set(singleSourceFile);
    }

}
