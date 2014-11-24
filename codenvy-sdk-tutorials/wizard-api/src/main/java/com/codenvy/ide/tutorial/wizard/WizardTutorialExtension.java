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
package com.codenvy.ide.tutorial.wizard;

import com.codenvy.ide.api.action.ActionManager;
import com.codenvy.ide.api.action.DefaultActionGroup;
import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.parts.PartStackType;
import com.codenvy.ide.api.parts.WorkspaceAgent;
import com.codenvy.ide.api.wizard.DefaultWizard;
import com.codenvy.ide.api.wizard.WizardContext;
import com.codenvy.ide.tutorial.wizard.action.OpenSimpleWizardAction;
import com.codenvy.ide.tutorial.wizard.inject.SimpleWizard;
import com.codenvy.ide.tutorial.wizard.pages.page1.Page1Presenter;
import com.codenvy.ide.tutorial.wizard.pages.page2.Page2Presenter;
import com.codenvy.ide.tutorial.wizard.pages.page3.Page3Presenter;
import com.codenvy.ide.tutorial.wizard.pages.page4.Page4Presenter;
import com.codenvy.ide.tutorial.wizard.part.TutorialHowToPresenter;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import static com.codenvy.ide.api.action.IdeActions.GROUP_MAIN_MENU;

/** Extension used to demonstrate the Wizard feature. */
@Singleton
@Extension(title = "Wizard tutorial", version = "1.0.0")
public class WizardTutorialExtension {
    public static final WizardContext.Key<Boolean> PAGE2_NEXT = new WizardContext.Key<>("Page 2 next");
    public static final WizardContext.Key<Boolean> PAGE4_SKIP = new WizardContext.Key<>("Page 4 skip");

    @Inject
    public WizardTutorialExtension(@SimpleWizard DefaultWizard simpleWizard,
                                   ActionManager actionManager,
                                   Provider<Page1Presenter> page1,
                                   Provider<Page2Presenter> page2,
                                   Provider<Page3Presenter> page3,
                                   Provider<Page4Presenter> page4,
                                   OpenSimpleWizardAction openSimpleWizardAction,
                                   WorkspaceAgent workspaceAgent,
                                   TutorialHowToPresenter howToPresenter) {
        workspaceAgent.openPart(howToPresenter, PartStackType.EDITING);

        DefaultActionGroup mainMenu = (DefaultActionGroup)actionManager.getAction(GROUP_MAIN_MENU);

        DefaultActionGroup group = new DefaultActionGroup("Wizard", true, actionManager);
        actionManager.registerAction("wizard", group);

        group.add(openSimpleWizardAction);
        mainMenu.add(group);

        simpleWizard.addPage(page1);
        simpleWizard.addPage(page2);
        simpleWizard.addPage(page3);
        simpleWizard.addPage(page4);
    }
}