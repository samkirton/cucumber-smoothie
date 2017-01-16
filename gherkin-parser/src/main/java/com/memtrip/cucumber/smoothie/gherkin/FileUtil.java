package com.memtrip.cucumber.smoothie.gherkin;

import com.memtrip.cucumber.smoothie.Log;

import javax.annotation.processing.Filer;
import javax.tools.JavaFileObject;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Scanner;

class FileUtil {

    private Filer filer;

    FileUtil(Filer filer) {
        this.filer = filer;
    }

    String getFile(String projectRootFolderName, String filePath) {

        StringBuilder result = new StringBuilder();

        String projectRootFilePath = getProjectPath(projectRootFolderName);
        String resolvedFilePath = projectRootFilePath + filePath;

        File file = new File(resolvedFilePath);

        try (Scanner scanner = new Scanner(file)) {

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                result.append(line).append("\n");
            }

            scanner.close();

        } catch (IOException e) {
            Log.error("Could not find file: " + resolvedFilePath);
        }

        return result.toString();
    }

    private String getProjectPath(String projectName) {

        try {

            JavaFileObject pathResolver = filer.createSourceFile("_" + System.currentTimeMillis() + ".tmp");
            String generatedProjectPath = pathResolver.toUri().toString();
            File generatedProjectFile = new File(new URI(generatedProjectPath));

            return appendEndSlash(getProjectPath(generatedProjectFile, projectName));

        } catch (Exception e) {
            Log.error("Cucumber smoothie could not resolve the project path, " +
                    "please check your file system permissions.");
        }

        return null;
    }

    private String appendEndSlash(String projectPath) {

        if (projectPath != null && !projectPath.endsWith("/")) {
            projectPath += "/";
        }

        return projectPath;
    }

    private String getProjectPath(File generatedFile, String projectName) {

        if (generatedFile.getParentFile() != null) {
            if (generatedFile.getParentFile().getName().equals(projectName)) {
                return generatedFile.getParentFile().getAbsolutePath();
            } else {
                return getProjectPath(generatedFile.getParentFile(), projectName);
            }
        }

        return null;
    }
}