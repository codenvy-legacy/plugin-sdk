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
package com.codenvy.ide.tutorial.gin.factory.assited;

import com.codenvy.ide.util.loging.Log;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

/**
 * The class that uses {@link Assisted} annotation for defining string value. It for using in {@link
 * com.codenvy.ide.tutorial.gin.factory.MyFactory}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class SomeImplementationWithAssistedParam implements SomeInterface {
    private String      text;

    @Inject
    public SomeImplementationWithAssistedParam(@Assisted String text) {
        this.text = text;
    }

    /** {@inheritDoc} */
    @Override
    public void doSomething() {
        Log.info(SomeImplementationWithAssistedParam.class, text);
    }
}