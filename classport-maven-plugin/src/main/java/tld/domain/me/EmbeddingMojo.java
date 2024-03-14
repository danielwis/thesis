package tld.domain.me;

import org.apache.maven.artifact.Artifact;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import tld.domain.me.MetadataAdder;
import tld.domain.me.JarPackager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Set;

// TODO:
//  - The LifecyclePhase might need changing (?)
//      See https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html
//  - Change the ResolutionScope?
//      See https://maven.apache.org/plugin-tools/apidocs/org/apache/maven/plugins/annotations/ResolutionScope.html
@Mojo(name = "classport", defaultPhase = LifecyclePhase.COMPILE, requiresDependencyResolution = ResolutionScope.COMPILE)
public class EmbeddingMojo
        extends AbstractMojo {
    /**
     * Gives access to the Maven project information.
     */
    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    MavenProject project;

    public void execute() throws MojoExecutionException, MojoFailureException {
        Set<Artifact> afs = project.getArtifacts();
        getLog().info("Found " + afs.size() + " artefacts.");
        for (Artifact a : afs) {
            try {
                JarPackager pkgr = new JarPackager(a.getFile());
                File tmpdir = Files.createTempDirectory("classport").toFile();
                getLog().info("Extracting " + a.getFile() + " to " + tmpdir);
                pkgr.extractTo(tmpdir);
                getLog().info("Extraction complete");
                pkgr = new JarPackager(tmpdir);
                pkgr.createAt(new File("/tmp/" + a.getArtifactId()));

                /*
                for file in files:
                    var m = new MetadataAdder(file);
                    m.add(metadata)

                pkgr.createAt(originalPath);
                */
            } catch (IOException e) {
                System.err.println(e);
            }

            getLog().info("\033[1mFound dependency: \033[0m" + a.getFile()
                    + "\n\t" + "- Group: " + a.getGroupId()
                    + "\n\t" + "- Artifact: " + a.getArtifactId()
                    + "\n\t" + "- Version: " + a.getVersion()
                    + "\n\t" + "- URL: " + (a.getRepository() == null ? "<unknown>"
                            : a.getRepository().getUrl())
                    + "\n\t" + "- Download URL: " + a.getDownloadUrl()
                    + "\n\t" + "- Constructed (best guess) URL: "
                    + "TODO (get repo URL, add params)");
        }
    }
}
