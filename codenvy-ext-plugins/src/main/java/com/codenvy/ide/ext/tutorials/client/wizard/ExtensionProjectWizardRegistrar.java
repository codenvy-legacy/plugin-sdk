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

import com.codenvy.api.project.shared.dto.ImportProject;
import com.codenvy.ide.api.projecttype.wizard.ProjectWizardRegistrar;
import com.codenvy.ide.api.wizard.WizardPage;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.google.inject.Inject;
import com.google.inject.Provider;

import javax.annotation.Nonnull;

import static com.codenvy.ide.Constants.CODENVY_CATEGORY;
import static com.codenvy.ide.Constants.CODENVY_PLUGIN_ID;

/**
 * Provides information for registering AngularJS project type into project wizard.
 *
 * @author Artem Zatsarynnyy
 */
public class ExtensionProjectWizardRegistrar implements ProjectWizardRegistrar {
    private final Array<Provider<? extends WizardPage<ImportProject>>> wizardPages;

    @Inject
    public ExtensionProjectWizardRegistrar(Provider<ExtensionPagePresenter> provider) {
        wizardPages = Collections.createArray();
        wizardPages.add(provider);
    }

    @Nonnull
    public String getProjectTypeId() {
        return CODENVY_PLUGIN_ID;
    }

    @Nonnull
    public String getCategory() {
        return CODENVY_CATEGORY;
    }

    @Nonnull
    public Array<Provider<? extends WizardPage<ImportProject>>> getWizardPages() {
        return wizardPages;
    }
}
