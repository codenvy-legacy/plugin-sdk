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
package com.codenvy.ide.ext.tutorials.server;

import com.codenvy.api.project.server.ProjectTypeDescriptionExtension;
import com.codenvy.api.project.server.ProjectTypeDescriptionRegistry;
import com.codenvy.api.project.server.AttributeDescription;
import com.codenvy.api.project.server.ProjectType;
import com.codenvy.ide.ext.tutorials.shared.Constants;
import com.codenvy.ide.extension.maven.shared.MavenAttributes;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

/** @author Artem Zatsarynnyy */
@Singleton
public class CodenvyPluginsProjectTypeDescriptionExtension implements ProjectTypeDescriptionExtension {
    @Inject
    public CodenvyPluginsProjectTypeDescriptionExtension(ProjectTypeDescriptionRegistry registry) {
        registry.registerDescription(this);
    }

    @Override
    public List<ProjectType> getProjectTypes() {
        final List<ProjectType> list = new ArrayList<>(2);
        list.add(new ProjectType(com.codenvy.ide.Constants.CODENVY_PLUGIN_ID,
                                 com.codenvy.ide.Constants.CODENVY_PLUGIN_NAME,
                                 com.codenvy.ide.Constants.CODENVY_CATEGORY));

        list.add(new ProjectType(Constants.TUTORIAL_ID, Constants.TUTORIAL_NAME, Constants.CODENVY_TUTORIAL_CATEGORY));
        return list;
    }

    @Override
    public List<AttributeDescription> getAttributeDescriptions() {
        final List<AttributeDescription> list = new ArrayList<>(4);
        list.add(new AttributeDescription(Constants.LANGUAGE));
        list.add(new AttributeDescription(Constants.LANGUAGE_VERSION));
        list.add(new AttributeDescription(Constants.FRAMEWORK));
        list.add(new AttributeDescription(Constants.BUILDER_MAVEN_SOURCE_FOLDERS));

        list.add(new AttributeDescription(MavenAttributes.GROUP_ID));
        list.add(new AttributeDescription(MavenAttributes.ARTIFACT_ID));
        list.add(new AttributeDescription(MavenAttributes.VERSION));
        list.add(new AttributeDescription(MavenAttributes.PARENT_VERSION));
        list.add(new AttributeDescription(MavenAttributes.PARENT_ARTIFACT_ID));
        list.add(new AttributeDescription(MavenAttributes.PARENT_GROUP_ID));
        list.add(new AttributeDescription(MavenAttributes.PACKAGING));

        list.add(new AttributeDescription(MavenAttributes.SOURCE_FOLDER));
        list.add(new AttributeDescription(MavenAttributes.TEST_SOURCE_FOLDER));



        return list;
    }
}
