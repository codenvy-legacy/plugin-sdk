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
package com.codenvy.api.deploy;

import com.codenvy.api.auth.AuthenticationService;
import com.codenvy.api.auth.SecureRandomTokenGenerator;
import com.codenvy.api.auth.TokenExtractor;
import com.codenvy.api.auth.TokenGenerator;
import com.codenvy.api.auth.TokenManager;
import com.codenvy.api.auth.UserProvider;
import com.codenvy.api.builder.BuilderAdminService;
import com.codenvy.api.builder.BuilderSelectionStrategy;
import com.codenvy.api.builder.BuilderService;
import com.codenvy.api.builder.LastInUseBuilderSelectionStrategy;
import com.codenvy.api.builder.internal.SlaveBuilderService;
import com.codenvy.api.runner.LastInUseRunnerSelectionStrategy;
import com.codenvy.api.runner.RunnerAdminService;
import com.codenvy.api.runner.RunnerSelectionStrategy;
import com.codenvy.api.runner.RunnerService;
import com.codenvy.api.runner.internal.SlaveRunnerService;
import com.codenvy.api.user.server.UserProfileService;
import com.codenvy.api.user.server.UserService;
import com.codenvy.api.workspace.server.WorkspaceService;
import com.codenvy.everrest.CodenvyAsynchronousJobPool;
import com.codenvy.ide.env.DummyUserProvider;
import com.codenvy.ide.env.DymmyHttpSessionTokenExtractor;
import com.codenvy.inject.DynaModule;
import com.codenvy.vfs.impl.fs.LocalFileSystemRegistryPlugin;
import com.google.inject.AbstractModule;

import org.everrest.core.impl.async.AsynchronousJobPool;
import org.everrest.core.impl.async.AsynchronousJobService;
import org.everrest.guice.PathKey;

/** @author andrew00x */
@DynaModule
public class ApiModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(WorkspaceService.class);

        bind(LocalFileSystemRegistryPlugin.class);

        bind(BuilderSelectionStrategy.class).toInstance(new LastInUseBuilderSelectionStrategy());
        bind(BuilderService.class);
        bind(BuilderAdminService.class);
        bind(SlaveBuilderService.class);

        bind(RunnerSelectionStrategy.class).toInstance(new LastInUseRunnerSelectionStrategy());
        bind(RunnerService.class);
        bind(RunnerAdminService.class);
        bind(SlaveRunnerService.class);

        bind(UserService.class);
        bind(UserProfileService.class);
        bind(UserProvider.class).to(DummyUserProvider.class);
        bind(TokenExtractor.class).to(DymmyHttpSessionTokenExtractor.class);
        bind(AuthenticationService.class);
        bind(TokenManager.class);
        bind(TokenGenerator.class).to(SecureRandomTokenGenerator.class);


        bind(AsynchronousJobPool.class).to(CodenvyAsynchronousJobPool.class);
        bind(new PathKey<>(AsynchronousJobService.class, "/async/{ws-id}")).to(AsynchronousJobService.class);

        install(new com.codenvy.generator.archetype.ArchetypeGeneratorModule());

        install(new com.codenvy.api.core.rest.CoreRestModule());
        install(new com.codenvy.api.analytics.AnalyticsModule());
        install(new com.codenvy.api.project.server.BaseProjectModule());
        install(new com.codenvy.api.builder.internal.BuilderModule());
        install(new com.codenvy.api.runner.internal.RunnerModule());
        install(new com.codenvy.api.vfs.server.VirtualFileSystemModule());
        install(new com.codenvy.vfs.impl.fs.VirtualFileSystemFSModule());
    }
}
