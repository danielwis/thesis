package tld.domain.me;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

class JarPackager {
    private File sourcePath;

    public JarPackager(File sourcePath) throws IOException {
        if (!sourcePath.exists())
            throw new IOException("Unable to locate " + sourcePath);

        this.sourcePath = sourcePath;
    }

    public void extractTo(File destDir) throws IOException {
        // sourcePath is a .jar, extract it to destDir
        try (JarFile jf = new JarFile(sourcePath)) {
            Enumeration<JarEntry> entries = jf.entries();

            if (entries != null) {
                if (!destDir.isDirectory()) {
                    if (!destDir.mkdir()) {
                        throw new IOException("Failed to create root directory at " + destDir);
                    }
                }

                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    File entryFile = new File(destDir, entry.getName());
                    if (entry.isDirectory()) {
                        if (!entryFile.mkdir()) {
                            throw new IOException("Failed to create inner directory at " + entryFile);
                        }
                    } else {
                        try (InputStream in = jf.getInputStream(entry);
                                OutputStream out = new FileOutputStream(entryFile)) {
                            in.transferTo(out);
                        }
                    }
                }
            }
        }
    }

    public void createAt(File jarName) throws IOException {
        // sourcePath is a directory, package it as a .jar at destPath
        if (jarName.exists())
            throw new IOException("File or directory " + jarName + " already exists");

        try (JarOutputStream os = new JarOutputStream(
                new BufferedOutputStream(new FileOutputStream(jarName)))) {
            // we just put slashes in filenames to make directories (?)
            // so we have a "root path" and a file name which should be enough
            // stackoverflow "how can i zip a complete directory with all subfoldres in
            // Java"
            Files.walkFileTree(sourcePath.toPath(), new SimpleFileVisitor<Path>() {
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    String relPath = sourcePath.toPath().relativize(file).toString();
                    System.out.println("Found file: " + relPath);
                    os.putNextEntry(new JarEntry(relPath));
                    try (FileInputStream in = new FileInputStream(file.toFile())) {
                        in.transferTo(os);
                    }
                    os.closeEntry();
                    return FileVisitResult.CONTINUE;
                }

                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    String relPath = sourcePath.toPath().relativize(dir).toString() + "/";

                    // .jar files don't include the root ("/")
                    if (dir.equals(sourcePath.toPath()))
                        return FileVisitResult.CONTINUE;

                    System.out.println("Found directory: " + relPath);
                    os.putNextEntry(new JarEntry(relPath));
                    os.closeEntry();
                    return FileVisitResult.CONTINUE;
                }
            });
        }

    }
}
