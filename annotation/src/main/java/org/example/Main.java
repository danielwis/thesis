package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    static String filePath = "Test.class";

    public static void main(String[] args) {
        try {
            Path path = Paths.get(filePath);
            byte[] bytes = Files.readAllBytes(path);
            if (bytes == null)
                System.out.println("null");
            System.out.println("length of " + filePath + ": " + bytes.length);

            var a = new MetadataAdder(bytes);
            a.add();
        } catch (IOException e) {
            Path currentRelativePath = Paths.get("");
            String path = currentRelativePath.toAbsolutePath().toString();
            System.out.println("Unable to find target class " + filePath + " in " + path);
        }
    }
}
