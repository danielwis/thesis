package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.objectweb.asm.*;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Expected at least two arguments: 'read FILENAME' or 'generate INFILE OUTFILE'");
            System.exit(1);
        }

        if (args[0].equals("read")) {
            String inFile = args[1].endsWith(".class") ? args[1] : args[1] + ".class";
            var mp = new MetadataPrinter();
            try {
                Path path = Paths.get(inFile);
                byte[] bytes = Files.readAllBytes(path);
                System.out.println("Size of " + inFile + ": " + bytes.length);

                ClassReader cr = new ClassReader(bytes);
                cr.accept(mp, new Attribute[] { new MavenPackageAttribute(""), new MavenSHAAttribute("") }, 0);
            } catch (IOException e) {
                System.out.println("Unable to find class " + inFile);
            }
        } else if (args[0].equals("generate")) {
            if (args.length < 3) {
                System.err.println("Expected three arguments: 'generate INFILE OUTFILE'");
                System.exit(1);
            }

            String inFile = args[1].endsWith(".class") ? args[1] : args[1] + ".class";
            try {
                Path path = Paths.get(inFile);
                byte[] bytes = Files.readAllBytes(path);
                if (bytes == null)
                    System.out.println("null");

                String outFile = args[2].endsWith(".class") ? args[2] : args[2] + ".class";
                var a = new MetadataAdder(bytes, outFile);
                a.add();
            } catch (IOException e) {
                Path currentRelativePath = Paths.get("");
                String path = currentRelativePath.toAbsolutePath().toString();
                System.out.println("Unable to find target class " + inFile + " in " + path);
            }
        }
    }
}
