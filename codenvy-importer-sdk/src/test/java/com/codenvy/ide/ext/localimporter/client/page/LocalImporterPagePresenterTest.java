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
import com.codenvy.api.project.shared.dto.ImportSourceDescriptor;
import com.codenvy.api.project.shared.dto.NewProject;
import com.codenvy.api.project.shared.dto.Source;
import com.codenvy.ide.api.wizard.Wizard;
import com.codenvy.ide.ext.localimporter.client.LocalImporterLocalizationConstant;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Map;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testing {@link LocalImporterPagePresenter} functionality.
 *
 * @author Roman Nikitenko
 */
@RunWith(MockitoJUnitRunner.class)
public class LocalImporterPagePresenterTest {
    @Mock
    private Wizard.UpdateDelegate             updateDelegate;
    @Mock
    private LocalImporterPageView             view;
    @Mock
    private LocalImporterLocalizationConstant locale;
    @Mock
    private ImportProject                     dataObject;
    @Mock
    private ImportSourceDescriptor            importSourceDescriptor;
    @Mock
    private NewProject                        newProject;
    @Mock
    private Map<String, String>               parameters;
    @InjectMocks
    private LocalImporterPagePresenter        presenter;

    @Before
    public void setUp() {
        Source source = mock(Source.class);
        when(importSourceDescriptor.getParameters()).thenReturn(parameters);
        when(source.getProject()).thenReturn(importSourceDescriptor);
        when(dataObject.getSource()).thenReturn(source);
        when(dataObject.getProject()).thenReturn(newProject);

        presenter.setUpdateDelegate(updateDelegate);
        presenter.init(dataObject);
    }

    @Test
    public void testGo() {
        AcceptsOneWidget container = mock(AcceptsOneWidget.class);

        presenter.go(container);

        verify(container).setWidget(eq(view));
        verify(view).setProjectName(anyString());
        verify(view).setProjectDescription(anyString());
        verify(view).setVisibility(anyBoolean());
        verify(view).setProjectPath(anyString());
        verify(view).setInputsEnableState(eq(true));
        verify(view).focusInPathInput();
    }

    @Test
    public void projectPathContainWhiteSpaceEnteredTest() {
        String incorrectPath = " /home/user/projects/ide";

        presenter.projectPathChanged(incorrectPath);

        verify(importSourceDescriptor).setLocation(eq(incorrectPath));
        verify(view).showPathError(eq(locale.messageStartWithWhiteSpace()));
        verify(newProject, never()).setName(anyString());
        verify(view, never()).setProjectName(anyString());
        verify(updateDelegate).updateControls();
    }

    @Test
    public void correctProjectNameEnteredTest() {
        String correctName = "TestProject";
        when(view.getProjectName()).thenReturn(correctName);

        presenter.projectNameChanged(correctName);

        verify(newProject).setName(eq(correctName));
        verify(view).hideNameError();
        verify(view, never()).showNameError();
        verify(updateDelegate).updateControls();
    }

    @Test
    public void correctProjectNameWithPointEnteredTest() {
        String correctName = "Test.project..ForCodenvy";
        when(view.getProjectName()).thenReturn(correctName);

        presenter.projectNameChanged(correctName);

        verify(newProject).setName(eq(correctName));
        verify(view).hideNameError();
        verify(view, never()).showNameError();
        verify(updateDelegate).updateControls();
    }

    @Test
    public void emptyProjectNameEnteredTest() {
        String emptyName = "";
        when(view.getProjectName()).thenReturn(emptyName);

        presenter.projectNameChanged(emptyName);

        verify(newProject).setName(eq(emptyName));
        verify(view).showNameError();
        verify(updateDelegate).updateControls();
    }

    @Test
    public void incorrectProjectNameEnteredTest() {
        String incorrectName = "TestProject+";
        when(view.getProjectName()).thenReturn(incorrectName);

        presenter.projectNameChanged(incorrectName);

        verify(newProject).setName(eq(incorrectName));
        verify(view).showNameError();
        verify(updateDelegate).updateControls();
    }

    @Test
    public void projectNameWithWhiteSpaceEnteredTest() {
        String name = "Test project";
        String replaceName = "Test-project";
        when(view.getProjectName()).thenReturn(name);

        presenter.projectNameChanged(name);

        verify(newProject).setName(eq(replaceName));
        verify(view).showNameError();
        verify(updateDelegate).updateControls();
    }

    @Test
    public void projectDescriptionChangedTest() {
        String description = "description";
        presenter.projectDescriptionChanged(description);

        verify(newProject).setDescription(eq(description));
    }

    @Test
    public void projectVisibilityChangedTest() {
        presenter.projectVisibilityChanged(true);

        verify(newProject).setVisibility(eq("public"));
    }
}
