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

import com.google.gwt.i18n.client.Messages;

/** @author Roman Nikitenko */
public interface LocalImporterLocalizationConstant extends Messages {

    //LocalImporterPage
    @Key("localImporterPage.projectPath")
    String localImporterPageProjectPath();

    @Key("localImporterPage.projectInfo")
    String localImporterPageProjectInfo();

    @Key("localImporterPage.projectName")
    String localImporterPageProjectName();

    @Key("localImporterPageProjectNamePrompt")
    String localImporterPageProjectNamePrompt();

    @Key("localImporterPage.projectDescription")
    String localImporterPageProjectDescription();

    @Key("localImporterPage.projectDescriptionPrompt")
    String localImporterPageProjectDescriptionPrompt();

    @Key("localImporterPage.projectPrivacy")
    String localImporterPageProjectPrivacy();

    @Key("localImporterPage.projectVisibilityPublic")
    String localImporterPageProjectVisibilityPublic();

    @Key("localImporterPage.projectVisibilityPrivate")
    String localImporterPageProjectVisibilityPrivate();

    //MESSAGES
    @Key("message.startWithWhiteSpace")
    String messageStartWithWhiteSpace();

}
