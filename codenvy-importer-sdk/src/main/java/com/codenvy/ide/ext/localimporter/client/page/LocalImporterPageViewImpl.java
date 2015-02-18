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

import com.codenvy.ide.ui.Styles;
import com.codenvy.ide.ext.localimporter.client.LocalImporterResources;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.inject.Inject;

import javax.annotation.Nonnull;

/**
 * @author Roman Nikitenko
 */
public class LocalImporterPageViewImpl extends Composite implements LocalImporterPageView {
    @UiField(provided = true)
    Style       style;
    @UiField
    TextBox     projectName;
    @UiField
    TextArea    projectDescription;
    @UiField
    RadioButton projectPrivate;
    @UiField
    RadioButton projectPublic;
    @UiField
    TextBox     projectPath;
    @UiField
    Label       labelPathError;
    private LocalImporterPageView.ActionDelegate delegate;

    @Inject
    public LocalImporterPageViewImpl(LocalImporterResources resource,
                                     LocalImporterPageViewImplUiBinder uiBinder) {
        style = resource.localImporterPageStyle();
        style.ensureInjected();
        initWidget(uiBinder.createAndBindUi(this));
        projectName.getElement().setAttribute("maxlength", "32");
        projectDescription.getElement().setAttribute("maxlength", "256");
    }

    @UiHandler("projectName")
    void onProjectNameChanged(KeyUpEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
            return;
        }
        String name = projectName.getValue() != null ? projectName.getValue() : "";
        delegate.projectNameChanged(name);
    }

    @UiHandler("projectPath")
    void onProjectUrlChanged(KeyUpEvent event) {
        delegate.projectPathChanged(projectPath.getValue());
    }

    @UiHandler("projectDescription")
    void onProjectDescriptionChanged(KeyUpEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
            return;
        }
        delegate.projectDescriptionChanged(projectDescription.getValue());
    }

    @UiHandler({"projectPublic", "projectPrivate"})
    void visibilityHandler(ValueChangeEvent<Boolean> event) {
        delegate.projectVisibilityChanged(projectPublic.getValue());
    }

    @Override
    public void setProjectPath(@Nonnull String url) {
        projectPath.setText(url);
        delegate.projectPathChanged(url);
    }

    @Override
    public void showPathError(@Nonnull String message) {
        projectPath.addStyleName(style.inputError());
        labelPathError.setText(message);
    }

    @Override
    public void hidePathError() {
        projectPath.removeStyleName(style.inputError());
        labelPathError.setText("");
    }

    @Override
    public void showNameError() {
        projectName.addStyleName(style.inputError());
    }

    @Override
    public void hideNameError() {
        projectName.removeStyleName(style.inputError());
    }

    @Nonnull
    @Override
    public String getProjectName() {
        return projectName.getValue();
    }

    @Override
    public void setProjectName(@Nonnull String projectName) {
        this.projectName.setValue(projectName);
    }

    @Override
    public void setProjectDescription(@Nonnull String projectDescription) {
        this.projectDescription.setValue(projectDescription);
    }

    @Override
    public void focusInPathInput() {
        projectPath.setFocus(true);
    }

    @Override
    public void setInputsEnableState(boolean isEnabled) {
        projectName.setEnabled(isEnabled);
        projectDescription.setEnabled(isEnabled);
        projectPath.setEnabled(isEnabled);

        if (isEnabled) {
            focusInPathInput();
        }
    }

    @Override
    public void setVisibility(boolean visible) {
        projectPublic.setValue(visible, false);
        projectPrivate.setValue(!visible, false);
    }

    public void setDelegate(@Nonnull LocalImporterPageView.ActionDelegate delegate) {
        this.delegate = delegate;
    }

    interface LocalImporterPageViewImplUiBinder extends UiBinder<DockLayoutPanel, LocalImporterPageViewImpl> {
    }

    public interface Style extends Styles {
        String mainPanel();

        String namePanel();

        String labelPosition();

        String marginTop();

        String alignRight();

        String alignLeft();

        String labelErrorPosition();

        String radioButtonPosition();

        String description();

        String label();

        String horizontalLine();
    }
}
