/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.tutorials.client.wizard;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.BuildersDescriptor;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.api.project.shared.dto.RunnersDescriptor;
import com.codenvy.ide.api.event.OpenProjectEvent;
import com.codenvy.ide.api.projecttype.wizard.ProjectWizard;
import com.codenvy.ide.api.wizard.AbstractWizardPage;
import com.codenvy.ide.collections.Jso;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.extension.maven.client.wizard.MavenPomServiceClient;
import com.codenvy.ide.extension.maven.shared.MavenAttributes;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.StringUnmarshaller;
import com.codenvy.ide.rest.Unmarshallable;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Evgen Vidolob
 */
@Singleton
public class ExtensionPagePresenter extends AbstractWizardPage implements ExtensionPageView.ActionDelegate {

    private ExtensionPageView      view;
    private ProjectServiceClient   projectServiceClient;
    private DtoFactory             dtoFactory;
    private DtoUnmarshallerFactory dtoUnmarshallerFactory;
    private EventBus               eventBus;
    private MavenPomServiceClient  pomReaderClient;

    @Inject
    public ExtensionPagePresenter(ExtensionPageView view,
                                  ProjectServiceClient projectServiceClient,
                                  MavenPomServiceClient pomReaderClient,
                                  DtoFactory dtoFactory,
                                  DtoUnmarshallerFactory dtoUnmarshallerFactory,
                                  EventBus eventBus) {
        super("Maven project settings", null);
        this.view = view;
        this.projectServiceClient = projectServiceClient;
        this.pomReaderClient = pomReaderClient;
        this.dtoFactory = dtoFactory;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.eventBus = eventBus;
        view.setDelegate(this);
    }

    @Nullable
    @Override
    public String getNotice() {
        return null;
    }

    @Override
    public boolean isCompleted() {
        boolean isArtifactIdCompleted = !view.getArtifactId().equals("");
        boolean isGroupIdCompleted = !view.getGroupId().equals("");
        boolean isVersionFieldCompleted = !view.getVersion().equals("");
        boolean isCompleted = isArtifactIdCompleted && isGroupIdCompleted && isVersionFieldCompleted;

        view.showArtifactIdMissingIndicator(!isArtifactIdCompleted);
        view.showGroupIdMissingIndicator(!isGroupIdCompleted);
        view.showVersionMissingIndicator(!isVersionFieldCompleted);
        return isCompleted;
    }

    @Override
    public void focusComponent() {
    }

    @Override
    public void removeOptions() {
    }

    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
        view.reset();
        ProjectDescriptor project = wizardContext.getData(ProjectWizard.PROJECT);
        if (project != null) {
            Map<String, List<String>> attributes = project.getAttributes();
            List<String> artifactIdAttr = attributes.get(MavenAttributes.ARTIFACT_ID);
            if (artifactIdAttr != null) {
                view.setArtifactId(artifactIdAttr.get(0));
                view.setGroupId(attributes.get(MavenAttributes.GROUP_ID).get(0));
                view.setVersion(attributes.get(MavenAttributes.VERSION).get(0));
                scheduleTextChanges();
            } else {
                pomReaderClient.readPomAttributes(project.getPath(), new AsyncRequestCallback<String>(new StringUnmarshaller()) {
                    @Override
                    protected void onSuccess(String result) {
                        Jso jso = Jso.deserialize(result);
                        view.setArtifactId(jso.getStringField(MavenAttributes.ARTIFACT_ID));
                        view.setGroupId(jso.getStringField(MavenAttributes.GROUP_ID));
                        view.setVersion(jso.getStringField(MavenAttributes.VERSION));
                        scheduleTextChanges();
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        Log.error(ExtensionPagePresenter.class, exception);
                    }
                });
            }
        }
    }

    private void scheduleTextChanges() {
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                onTextsChange();
            }
        });
    }

    @Override
    public void commit(@NotNull final CommitCallback callback) {
        Map<String, List<String>> options = new HashMap<>();
        options.put(MavenAttributes.ARTIFACT_ID, Arrays.asList(view.getArtifactId()));
        options.put(MavenAttributes.GROUP_ID, Arrays.asList(view.getGroupId()));
        options.put(MavenAttributes.VERSION, Arrays.asList(view.getVersion()));
        options.put(MavenAttributes.PACKAGING, Arrays.asList("jar"));

        final ProjectDescriptor projectDescriptorToUpdate = dtoFactory.createDto(ProjectDescriptor.class);
        projectDescriptorToUpdate.withType(wizardContext.getData(ProjectWizard.PROJECT_TYPE).getType());
        projectDescriptorToUpdate.setAttributes(options);
        BuildersDescriptor builders = projectDescriptorToUpdate.getBuilders();
        if (builders == null) {
            projectDescriptorToUpdate.setBuilders(builders = dtoFactory.createDto(BuildersDescriptor.class));
        }
        builders.setDefault("maven");
        RunnersDescriptor runners = projectDescriptorToUpdate.getRunners();
        if (runners == null) {
            projectDescriptorToUpdate.setRunners(runners = dtoFactory.createDto(RunnersDescriptor.class));
        }
        runners.setDefault("sdk");
        boolean visibility = wizardContext.getData(ProjectWizard.PROJECT_VISIBILITY);
        projectDescriptorToUpdate.setVisibility(visibility ? "public" : "private");
        projectDescriptorToUpdate.setDescription(wizardContext.getData(ProjectWizard.PROJECT_DESCRIPTION));
        final String name = wizardContext.getData(ProjectWizard.PROJECT_NAME);
        final ProjectDescriptor project = wizardContext.getData(ProjectWizard.PROJECT);
        if (project != null) {
            if (project.getName().equals(name)) {
                updateProject(project, projectDescriptorToUpdate, callback);
            } else {
                projectServiceClient.rename(project.getPath(), name, null, new AsyncRequestCallback<Void>() {
                    @Override
                    protected void onSuccess(Void result) {
                        project.setName(name);

                        updateProject(project, projectDescriptorToUpdate, callback);
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        callback.onFailure(exception);
                    }
                });
            }

        } else {
            createProject(callback, projectDescriptorToUpdate, name);
        }
    }

    private void updateProject(final ProjectDescriptor project, ProjectDescriptor projectDescriptorToUpdate,
                               final CommitCallback callback) {
        Unmarshallable<ProjectDescriptor> unmarshaller = dtoUnmarshallerFactory.newUnmarshaller(ProjectDescriptor.class);
        projectServiceClient.updateProject(project.getPath(), projectDescriptorToUpdate, new AsyncRequestCallback<ProjectDescriptor>(unmarshaller) {
            @Override
            protected void onSuccess(ProjectDescriptor result) {
                eventBus.fireEvent(new OpenProjectEvent(result.getName()));
                callback.onSuccess();
            }

            @Override
            protected void onFailure(Throwable exception) {
                callback.onFailure(exception);
            }
        });
    }

    private void createProject(final CommitCallback callback, ProjectDescriptor projectDescriptor, final String name) {
        Unmarshallable<ProjectDescriptor> unmarshaller = dtoUnmarshallerFactory.newUnmarshaller(ProjectDescriptor.class);
        projectServiceClient
                .createProject(name, projectDescriptor,
                               new AsyncRequestCallback<ProjectDescriptor>(unmarshaller) {
                                   @Override
                                   protected void onSuccess(ProjectDescriptor result) {
                                       eventBus.fireEvent(new OpenProjectEvent(result.getName()));
                                       callback.onSuccess();
                                   }

                                   @Override
                                   protected void onFailure(Throwable exception) {
                                       callback.onFailure(exception);
                                   }
                               }
                              );
    }

    @Override
    public void onTextsChange() {
        delegate.updateControls();
    }
}
