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
package com.codenvy.ide.ext.localimporter.client;

import com.codenvy.api.project.shared.dto.ImportProject;
import com.codenvy.ide.api.projectimport.wizard.ImportWizardRegistrar;
import com.codenvy.ide.api.wizard.WizardPage;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.ext.localimporter.client.page.LocalImporterPagePresenter;
import com.google.inject.Inject;
import com.google.inject.Provider;

import javax.annotation.Nonnull;

/**
 * Provides information for registering Local importer into import wizard.
 *
 * @author Roman Nikitenko
 */
public class LocalImportWizardRegistrar implements ImportWizardRegistrar {
    private final Array<Provider<? extends WizardPage<ImportProject>>> wizardPages;

    @Inject
    public LocalImportWizardRegistrar(Provider<LocalImporterPagePresenter> provider) {
        wizardPages = Collections.createArray();
        wizardPages.add(provider);
    }

    @Nonnull
    public String getImporterId() {
        return "local";
    }

    @Nonnull
    public Array<Provider<? extends WizardPage<ImportProject>>> getWizardPages() {
        return wizardPages;
    }
}
