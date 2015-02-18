/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.localimporter.server;

import com.codenvy.api.core.notification.EventService;
import com.codenvy.api.core.util.LineConsumer;
import com.codenvy.api.core.util.LineConsumerFactory;
import com.codenvy.api.project.server.FileEntry;
import com.codenvy.api.project.server.FolderEntry;
import com.codenvy.api.project.server.ProjectImporter;
import com.codenvy.api.user.server.dao.Profile;
import com.codenvy.api.user.server.dao.UserProfileDao;
import com.codenvy.api.vfs.server.VirtualFileSystem;
import com.codenvy.api.vfs.server.VirtualFileSystemRegistry;
import com.codenvy.commons.env.EnvironmentContext;
import com.codenvy.commons.lang.IoUtil;
import com.codenvy.commons.user.UserImpl;
import com.codenvy.vfs.impl.fs.LocalFileSystemProvider;
import com.codenvy.vfs.impl.fs.WorkspaceHashLocalFSMountStrategy;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.multibindings.Multibinder;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Roman Nikitenko
 */
public class LocalProjectImporterTest {
    @Mock
    private UserProfileDao       userProfileDao;
    private File                 fsRoot;
    private File                 repo;
    private VirtualFileSystem    vfs;
    private LocalProjectImporter localProjectImporter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        // Bind components
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                Multibinder<ProjectImporter> projectImporterMultibinder = Multibinder.newSetBinder(binder(), ProjectImporter.class);
                projectImporterMultibinder.addBinding().to(LocalProjectImporter.class);
            }
        });

        // Init virtual file system
        File target = new File(Thread.currentThread().getContextClassLoader().getResource(".").toURI()).getParentFile();
        fsRoot = new File(target, "fs-root");
        Assert.assertTrue(fsRoot.mkdirs());
        VirtualFileSystemRegistry registry = new VirtualFileSystemRegistry();
        WorkspaceHashLocalFSMountStrategy mountStrategy = new WorkspaceHashLocalFSMountStrategy(fsRoot, fsRoot);
        LocalFileSystemProvider vfsProvider = new LocalFileSystemProvider("my_vfs", mountStrategy, new EventService(), null, registry);
        registry.registerProvider("my_vfs", vfsProvider);
        vfs = registry.getProvider("my_vfs").newInstance(URI.create(""));

        // set current user
        EnvironmentContext.getCurrent().setUser(new UserImpl("codenvy", "codenvy", null, Arrays.asList("workspace/developer"), false));

        // rules for mock
        Map<String, String> profileAttributes = new HashMap<>();
        profileAttributes.put("firstName", "Codenvy");
        profileAttributes.put("lastName", "Codenvy");
        profileAttributes.put("email", "codenvy@codenvy.com");
        Mockito.when(userProfileDao.getById("codenvy"))
               .thenReturn(new Profile().withId("codenvy").withUserId("codenvy").withAttributes(profileAttributes));

        // init source repository
        repo = new File(target, "projectForImport");
        Assert.assertTrue(repo.mkdirs());
        Assert.assertTrue(new File(repo, "src").mkdirs());
        try (BufferedWriter w = Files.newBufferedWriter(new File(repo, "src/hello.c").toPath(), Charset.forName("UTF-8"))) {
            w.write("#include <stdio.h>\n\n");
            w.write("int main()\n");
            w.write("{\n");
            w.write("    printf(\"Hello world!\\n\");\n");
            w.write("    return 0;\n");
            w.write("}\n");
        }
        try (BufferedWriter w = Files.newBufferedWriter(new File(repo, "README").toPath(), Charset.forName("UTF-8"))) {
            w.write("test local importer");
        }
        localProjectImporter = injector.getInstance(LocalProjectImporter.class);
    }

    static class SystemOutLineConsumer implements LineConsumer {
        @Override
        public void writeLine(String line) throws IOException {
            System.out.println(line);
        }

        @Override
        public void close() throws IOException {
        }
    }

    static class SystemOutLineConsumerFactory implements LineConsumerFactory {
        @Override
        public LineConsumer newLineConsumer() {
            return new SystemOutLineConsumer();
        }
    }

    @After
    public void tearDown() throws Exception {
        Assert.assertTrue(IoUtil.deleteRecursive(fsRoot));
        Assert.assertTrue(IoUtil.deleteRecursive(repo));
    }

    @Test
    public void testImport() throws Exception {
        FolderEntry folder = new FolderEntry("my-vfs", vfs.getMountPoint().getRoot().createFolder("project"));
        localProjectImporter
                .importSources(folder, repo.getAbsolutePath(), Collections.<String, String>emptyMap(),
                               new SystemOutLineConsumerFactory());
        Assert.assertNotNull(folder.getChild("src"));
        Assert.assertNotNull(folder.getChild("src/hello.c"));
        Assert.assertNotNull(folder.getChild("README"));
        FileEntry readme = (FileEntry)folder.getChild("README");
        Assert.assertEquals("test local importer", new String(readme.contentAsBytes()));
    }
}
