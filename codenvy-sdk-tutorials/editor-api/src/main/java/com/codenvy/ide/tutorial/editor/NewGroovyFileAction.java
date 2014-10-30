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
package com.codenvy.ide.tutorial.editor;

import com.codenvy.ide.newresource.AbstractNewResourceAction;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Action to create new Groovy file.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class NewGroovyFileAction extends AbstractNewResourceAction {

    private EditorTutorialResource resource;

    @Inject
    public NewGroovyFileAction(EditorTutorialResource resource) {
        super("Groovy file",
              "Creates new Groovy file",
              resource.groovyFile(),
              null);
        this.resource = resource;
    }

    @Override
    protected String getExtension() {
        return "groovy";
    }

    @Override
    protected String getDefaultContent() {
        return resource.contentFile().getText();
    }

    @Override
    protected String getMimeType() {
        return "text/x-groovy";
    }
}
