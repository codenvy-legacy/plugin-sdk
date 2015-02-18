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

import com.codenvy.ide.ext.localimporter.client.page.LocalImporterPageViewImpl;
import com.google.gwt.resources.client.ClientBundle;

/** @author Roman Nikitenko */
public interface LocalImporterResources extends ClientBundle {
    @Source({"LocalImporterPage.css", "com/codenvy/ide/ui/Styles.css"})
    LocalImporterPageViewImpl.Style localImporterPageStyle();
}
