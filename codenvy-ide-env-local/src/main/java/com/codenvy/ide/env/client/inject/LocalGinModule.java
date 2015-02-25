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

import com.codenvy.ide.api.action.permits.ActionDenyAccessDialog;
import com.codenvy.ide.api.action.permits.ActionPermit;
import com.codenvy.ide.api.action.permits.Build;
import com.codenvy.ide.api.action.permits.Run;
import com.codenvy.ide.api.extension.ExtensionGinModule;
import com.codenvy.ide.env.client.ActionDenyAccessDialogImpl;
import com.codenvy.ide.env.client.ActionPermitImpl;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

/**
 * @author Oleksii Orel
 */
@ExtensionGinModule
public class LocalGinModule extends AbstractGinModule {

    @Override
    protected void configure() {
        bind(ActionPermit.class).annotatedWith(Build.class).to(ActionPermitImpl.class).in(Singleton.class);
        bind(ActionDenyAccessDialog.class).annotatedWith(Build.class).to(ActionDenyAccessDialogImpl.class).in(Singleton.class);
        bind(ActionPermit.class).annotatedWith(Run.class).to(ActionPermitImpl.class).in(Singleton.class);
        bind(ActionDenyAccessDialog.class).annotatedWith(Run.class).to(ActionDenyAccessDialogImpl.class).in(Singleton.class);
    }
}
