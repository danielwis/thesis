package tld.domain.me;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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

    public void createAt(File destPath) {
        // sourcePath is a directory, package it as a .jar at destPath
    }
}
