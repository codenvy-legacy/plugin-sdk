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
package com.codenvy.ide.env.client.inject;

import com.codenvy.ide.api.action.permits.BuildActionDenyAccessDialog;
import com.codenvy.ide.api.action.permits.BuildActionPermit;
import com.codenvy.ide.api.action.permits.RunActionDenyAccessDialog;
import com.codenvy.ide.api.action.permits.RunActionPermit;
import com.codenvy.ide.api.extension.ExtensionGinModule;
import com.codenvy.ide.env.client.BuildActionDenyAccessDialogImpl;
import com.codenvy.ide.env.client.BuildActionPermitImpl;
import com.codenvy.ide.env.client.RunActionDenyAccessDialogImpl;
import com.codenvy.ide.env.client.RunActionPermitImpl;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

/**
 * @author Oleksii Orel
 */
@ExtensionGinModule
public class LocaleGinModule extends AbstractGinModule {

    @Override
    protected void configure() {
        bind(BuildActionPermit.class).to(BuildActionPermitImpl.class).in(Singleton.class);
        bind(BuildActionDenyAccessDialog.class).to(BuildActionDenyAccessDialogImpl.class).in(Singleton.class);
        bind(RunActionPermit.class).to(RunActionPermitImpl.class).in(Singleton.class);
        bind(RunActionDenyAccessDialog.class).to(RunActionDenyAccessDialogImpl.class).in(Singleton.class);
    }
}
