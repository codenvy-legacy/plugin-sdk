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
package com.codenvy.ide.ext.tutorials.server;

import com.codenvy.api.project.server.type.ProjectType;
import com.codenvy.ide.extension.maven.server.projecttype.MavenProjectType;
import com.google.inject.Inject;

import static com.codenvy.ide.Constants.CODENVY_PLUGIN_ID;
import static com.codenvy.ide.Constants.CODENVY_PLUGIN_NAME;

/** @author Artem Zatsarynnyy */
public class ExtensionProjectType extends ProjectType {

    @Inject
    public ExtensionProjectType(MavenProjectType mavenProjectType) {
        super(CODENVY_PLUGIN_ID, CODENVY_PLUGIN_NAME, true, false);

        addParent(mavenProjectType);
        setDefaultBuilder("maven");
        setDefaultRunner("system:/sdk/tomcat7");
    }
}
