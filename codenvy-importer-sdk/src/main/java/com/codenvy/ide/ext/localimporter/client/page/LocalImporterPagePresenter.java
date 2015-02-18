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
package com.codenvy.ide.ext.localimporter.client.page;

import com.codenvy.api.project.shared.dto.ImportProject;
import com.codenvy.api.project.shared.dto.NewProject;
import com.codenvy.ide.api.wizard.AbstractWizardPage;
import com.codenvy.ide.ext.localimporter.client.LocalImporterLocalizationConstant;
import com.codenvy.ide.util.NameUtils;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

import javax.annotation.Nonnull;

/**
 * @author Roman Nikitenko
 */
public class LocalImporterPagePresenter extends AbstractWizardPage<ImportProject> implements LocalImporterPageView.ActionDelegate {

    private static final String PUBLIC_VISIBILITY  = "public";
    private static final String PRIVATE_VISIBILITY = "private";

    private LocalImporterLocalizationConstant locale;
    private LocalImporterPageView             view;

    @Inject
    public LocalImporterPagePresenter(LocalImporterPageView view, LocalImporterLocalizationConstant locale) {
        this.view = view;
        this.locale = locale;
        this.view.setDelegate(this);
    }

    @Override
    public void init(ImportProject dataObject) {
        super.init(dataObject);
    }

    @Override
    public boolean isCompleted() {
        return isProjectPathCorrect(dataObject.getSource().getProject().getLocation());
    }

    @Override
    public void projectNameChanged(@Nonnull String name) {
        if (name.contains(" ")) {
            name = name.replace(" ", "-");
            view.setProjectName(name);
        }
        validateProjectName();
        dataObject.getProject().setName(name);
        updateDelegate.updateControls();
    }

    private void validateProjectName() {
        if (NameUtils.checkProjectName(view.getProjectName())) {
            view.hideNameError();
        } else {
            view.showNameError();
        }
    }

    @Override
    public void projectPathChanged(@Nonnull String path) {
        dataObject.getSource().getProject().setLocation(path);
        if (isProjectPathCorrect(path)) {
            String projectName = extractProjectNameFromPath(path);
            view.setProjectName(projectName);
            validateProjectName();

            dataObject.getProject().setName(projectName);
        }
        updateDelegate.updateControls();
    }

    @Override
    public void projectDescriptionChanged(@Nonnull String projectDescription) {
        dataObject.getProject().setDescription(projectDescription);
        updateDelegate.updateControls();
    }

    @Override
    public void projectVisibilityChanged(boolean visible) {
        dataObject.getProject().setVisibility(visible ? PUBLIC_VISIBILITY : PRIVATE_VISIBILITY);
        updateDelegate.updateControls();
    }

    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
        updateView();

        view.setInputsEnableState(true);
        view.focusInPathInput();
    }

    /** Updates view from data-object. */
    private void updateView() {
        final NewProject project = dataObject.getProject();

        view.setProjectName(project.getName());
        view.setProjectDescription(project.getDescription());
        view.setVisibility(PUBLIC_VISIBILITY.equals(project.getVisibility()));
        view.setProjectPath(dataObject.getSource().getProject().getLocation());
    }

    private String extractProjectNameFromPath(@Nonnull String path) {
        int indexStartProjectName = path.lastIndexOf("/") + 1;
        if (indexStartProjectName == 0) {
            indexStartProjectName = path.lastIndexOf("\\") + 1;
        }
        return (indexStartProjectName != 0) ? path.substring(indexStartProjectName) : "";
    }

    /**
     * Validate path
     *
     * @param path
     *         path for validate
     * @return <code>true</code> if path is correct
     */
    private boolean isProjectPathCorrect(@Nonnull String path) {
        if (path.isEmpty()) {
            view.showPathError("");
            return false;
        }
        if (path.contains(" ")) {
            view.showPathError(locale.messageStartWithWhiteSpace());
            return false;
        }
        view.hidePathError();
        return true;
    }
}
