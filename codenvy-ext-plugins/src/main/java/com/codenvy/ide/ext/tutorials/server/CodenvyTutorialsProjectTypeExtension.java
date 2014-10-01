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

import com.codenvy.api.project.server.ProjectTypeDescriptionRegistry;
import com.codenvy.api.project.server.ProjectTypeExtension;
import com.codenvy.api.project.shared.Attribute;
import com.codenvy.api.project.shared.ProjectTemplateDescription;
import com.codenvy.api.project.shared.ProjectType;
import com.codenvy.ide.ext.tutorials.shared.Constants;
import com.codenvy.api.project.server.ProjectTemplateDescriptionLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** @author Artem Zatsarynnyy */
@Singleton
public class CodenvyTutorialsProjectTypeExtension implements ProjectTypeExtension {
    private static final Logger LOG = LoggerFactory.getLogger(CodenvyTutorialsProjectTypeExtension.class);
    private final ProjectTemplateDescriptionLoader projectTemplateDescriptionLoader;
    private final ProjectType                      projectType;

    @Inject
    public CodenvyTutorialsProjectTypeExtension(ProjectTemplateDescriptionLoader projectTemplateDescriptionLoader,
                                                ProjectTypeDescriptionRegistry registry) {
        this.projectTemplateDescriptionLoader = projectTemplateDescriptionLoader;
        projectType = new ProjectType(Constants.TUTORIAL_ID,
                                      Constants.TUTORIAL_NAME,
                                      Constants.CODENVY_TUTORIAL_CATEGORY,
                                      "maven",
                                      "sdk");
        registry.registerProjectType(this);
    }

    @Override
    public ProjectType getProjectType() {
        return projectType;
    }

    @Override
    public List<Attribute> getPredefinedAttributes() {
        final List<Attribute> list = new ArrayList<>(2);
        list.add(new Attribute(Constants.LANGUAGE, "java"));
        list.add(new Attribute(Constants.FRAMEWORK, "codenvy_sdk"));
        return list;
    }

    @Override
    public List<ProjectTemplateDescription> getTemplates() {
        final List<ProjectTemplateDescription> list = new ArrayList<>();
        try {
            projectTemplateDescriptionLoader.load(getProjectType().getId(), list);
        } catch (IOException e) {
            LOG.error("Unable to load external templates for project type: {}", getProjectType().getId());
        }
        return list;
    }

    @Override
    public Map<String, String> getIconRegistry() {
        return null;
    }
}
