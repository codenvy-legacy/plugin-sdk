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
package com.codenvy.ide.ext.tutorials.client.wizard;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ImportProject;
import com.codenvy.ide.api.projecttype.wizard.ProjectWizardMode;
import com.codenvy.ide.api.wizard.AbstractWizardPage;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.StringMapListUnmarshaller;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.codenvy.ide.api.projecttype.wizard.ProjectWizardMode.CREATE;
import static com.codenvy.ide.api.projecttype.wizard.ProjectWizardMode.UPDATE;
import static com.codenvy.ide.api.projecttype.wizard.ProjectWizardRegistrar.PROJECT_PATH_KEY;
import static com.codenvy.ide.api.projecttype.wizard.ProjectWizardRegistrar.WIZARD_MODE_KEY;
import static com.codenvy.ide.extension.maven.shared.MavenAttributes.ARTIFACT_ID;
import static com.codenvy.ide.extension.maven.shared.MavenAttributes.DEFAULT_SOURCE_FOLDER;
import static com.codenvy.ide.extension.maven.shared.MavenAttributes.DEFAULT_TEST_SOURCE_FOLDER;
import static com.codenvy.ide.extension.maven.shared.MavenAttributes.DEFAULT_VERSION;
import static com.codenvy.ide.extension.maven.shared.MavenAttributes.GROUP_ID;
import static com.codenvy.ide.extension.maven.shared.MavenAttributes.MAVEN_ID;
import static com.codenvy.ide.extension.maven.shared.MavenAttributes.PACKAGING;
import static com.codenvy.ide.extension.maven.shared.MavenAttributes.PARENT_GROUP_ID;
import static com.codenvy.ide.extension.maven.shared.MavenAttributes.PARENT_VERSION;
import static com.codenvy.ide.extension.maven.shared.MavenAttributes.SOURCE_FOLDER;
import static com.codenvy.ide.extension.maven.shared.MavenAttributes.TEST_SOURCE_FOLDER;
import static com.codenvy.ide.extension.maven.shared.MavenAttributes.VERSION;

/**
 * @author Evgen Vidolob
 */
@Singleton
public class ExtensionPagePresenter extends AbstractWizardPage<ImportProject> implements ExtensionPageView.ActionDelegate {

    private final ProjectServiceClient projectServiceClient;
    private       ExtensionPageView    view;

    @Inject
    public ExtensionPagePresenter(ExtensionPageView view, ProjectServiceClient projectServiceClient) {
        super();
        this.view = view;
        this.projectServiceClient = projectServiceClient;
        view.setDelegate(this);
    }

    @Override
    public void init(ImportProject dataObject) {
        super.init(dataObject);

        final ProjectWizardMode wizardMode = ProjectWizardMode.parse(context.get(WIZARD_MODE_KEY));
        if (CREATE == wizardMode) {
            // set default values
            setAttribute(VERSION, DEFAULT_VERSION);
            setAttribute(PACKAGING, "jar");
            setAttribute(SOURCE_FOLDER, DEFAULT_SOURCE_FOLDER);
            setAttribute(TEST_SOURCE_FOLDER, DEFAULT_TEST_SOURCE_FOLDER);
        } else if (UPDATE == wizardMode && getAttribute(ARTIFACT_ID).isEmpty()) {
            estimateAndSetAttributes();
        }
    }

    private void estimateAndSetAttributes() {
        projectServiceClient.estimateProject(
                context.get(PROJECT_PATH_KEY), MAVEN_ID,
                new AsyncRequestCallback<Map<String, List<String>>>(new StringMapListUnmarshaller()) {
                    @Override
                    protected void onSuccess(Map<String, List<String>> result) {
                        List<String> artifactIdValues = result.get(ARTIFACT_ID);
                        if (artifactIdValues != null && !artifactIdValues.isEmpty()) {
                            setAttribute(ARTIFACT_ID, artifactIdValues.get(0));
                        }

                        List<String> groupIdValues = result.get(GROUP_ID);
                        List<String> parentGroupIdValues = result.get(PARENT_GROUP_ID);
                        if (groupIdValues != null && !groupIdValues.isEmpty()) {
                            setAttribute(GROUP_ID, groupIdValues.get(0));
                        } else if (parentGroupIdValues != null && !parentGroupIdValues.isEmpty()) {
                            setAttribute(GROUP_ID, parentGroupIdValues.get(0));
                        }

                        List<String> versionValues = result.get(VERSION);
                        List<String> parentVersionValues = result.get(PARENT_VERSION);
                        if (versionValues != null && !versionValues.isEmpty()) {
                            setAttribute(VERSION, versionValues.get(0));
                        } else if (parentVersionValues != null && !parentVersionValues.isEmpty()) {
                            setAttribute(VERSION, parentVersionValues.get(0));
                        }

                        List<String> packagingValues = result.get(PACKAGING);
                        if (packagingValues != null && !packagingValues.isEmpty()) {
                            setAttribute(PACKAGING, packagingValues.get(0));
                        }

                        updateDelegate.updateControls();
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        Log.error(ExtensionPagePresenter.class, exception);
                    }
                });
    }

    @Override
    public boolean isCompleted() {
        return isCoordinatesCompleted();
    }

    private boolean isCoordinatesCompleted() {
        final String artifactId = getAttribute(ARTIFACT_ID);
        final String groupId = getAttribute(GROUP_ID);
        final String version = getAttribute(VERSION);

        return !(artifactId.isEmpty() || groupId.isEmpty() || version.isEmpty());
    }

    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);

        final ProjectWizardMode wizardMode = ProjectWizardMode.parse(context.get(WIZARD_MODE_KEY));
        final String projectName = dataObject.getProject().getName();

        // use project name for artifactId and groupId for new project
        if (CREATE == wizardMode && projectName != null) {
            if (getAttribute(ARTIFACT_ID).isEmpty()) {
                setAttribute(ARTIFACT_ID, projectName);
            }
            if (getAttribute(GROUP_ID).isEmpty()) {
                setAttribute(GROUP_ID, projectName);
            }
            updateDelegate.updateControls();
        }

        updateView();
        validateCoordinates();
    }

    /** Updates view from data-object. */
    private void updateView() {
        Map<String, List<String>> attributes = dataObject.getProject().getAttributes();

        final String artifactId = getAttribute(ARTIFACT_ID);
        if (!artifactId.isEmpty()) {
            view.setArtifactId(artifactId);
        }

        if (attributes.get(GROUP_ID) != null) {
            view.setGroupId(getAttribute(GROUP_ID));
        } else {
            view.setGroupId(getAttribute(PARENT_GROUP_ID));
        }

        if (attributes.get(VERSION) != null) {
            view.setVersion(getAttribute(VERSION));
        } else {
            view.setVersion(getAttribute(PARENT_VERSION));
        }
    }

    @Override
    public void onTextsChange() {
        setAttribute(ARTIFACT_ID, view.getArtifactId());
        setAttribute(GROUP_ID, view.getGroupId());
        setAttribute(VERSION, view.getVersion());

        validateCoordinates();
        updateDelegate.updateControls();
    }

    private void validateCoordinates() {
        view.showArtifactIdMissingIndicator(view.getArtifactId().isEmpty());
        view.showGroupIdMissingIndicator(view.getGroupId().isEmpty());
        view.showVersionMissingIndicator(view.getVersion().isEmpty());
    }

    /** Reads single value of attribute from data-object. */
    @Nonnull
    private String getAttribute(String attrId) {
        Map<String, List<String>> attributes = dataObject.getProject().getAttributes();
        List<String> values = attributes.get(attrId);
        if (!(values == null || values.isEmpty())) {
            return values.get(0);
        }
        return "";
    }

    /** Sets single value of attribute of data-object. */
    private void setAttribute(String attrId, String value) {
        Map<String, List<String>> attributes = dataObject.getProject().getAttributes();
        attributes.put(attrId, Arrays.asList(value));
    }
}
