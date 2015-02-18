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

import com.codenvy.api.core.ConflictException;
import com.codenvy.api.core.ForbiddenException;
import com.codenvy.api.core.ServerException;
import com.codenvy.api.core.UnauthorizedException;
import com.codenvy.api.core.util.LineConsumerFactory;
import com.codenvy.api.project.server.FolderEntry;
import com.codenvy.api.project.server.ProjectImporter;
import com.codenvy.commons.lang.IoUtil;
import com.codenvy.vfs.impl.fs.LocalPathResolver;
import com.codenvy.vfs.impl.fs.VirtualFileImpl;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @author Roman Nikitenko
 */
@Singleton
public class LocalProjectImporter implements ProjectImporter {

    private final LocalPathResolver localPathResolver;

    @Inject
    public LocalProjectImporter(LocalPathResolver localPathResolver) {
        this.localPathResolver = localPathResolver;
    }

    @Override
    public String getId() {
        return "local";
    }

    @Override
    public boolean isInternal() {
        return false;
    }

    @Override
    public String getDescription() {
        return "Import project from local file system by path.";
    }

    /** {@inheritDoc} */
    @Override
    public ImporterCategory getCategory() {
        return ImporterCategory.SOURCE_CONTROL;
    }

    @Override
    public void importSources(FolderEntry baseFolder, String location, Map<String, String> parameters)
            throws ForbiddenException, ConflictException, UnauthorizedException, IOException, ServerException {
        importSources(baseFolder, location, parameters, LineConsumerFactory.NULL);
    }

    @Override
    public void importSources(FolderEntry baseFolder, String location, Map<String, String> parameters, LineConsumerFactory consumerFactory)
            throws IOException, ServerException {
        try {
            final String localPath = localPathResolver.resolve((VirtualFileImpl)baseFolder.getVirtualFile());
            final File projectDir = new File(localPath);
            final File source = new File(location);
            IoUtil.copy(source, projectDir, IoUtil.ANY_FILTER);
        } catch (IOException e) {
            throw new ServerException(
                    "Your project cannot be imported. The issue is either from a incorrect path, or file system corruption. " +
                    "Please contact support for assistance.", e);
        }
    }
}
