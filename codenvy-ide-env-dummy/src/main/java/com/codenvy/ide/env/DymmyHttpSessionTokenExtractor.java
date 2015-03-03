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
package com.codenvy.ide.env;

import com.codenvy.api.auth.TokenExtractor;
import com.codenvy.api.auth.TokenManager;
import com.codenvy.api.auth.shared.dto.Credentials;
import com.codenvy.api.auth.shared.dto.Token;
import com.codenvy.api.core.ApiException;
import com.codenvy.api.core.NotFoundException;
import com.codenvy.api.core.ServerException;
import com.codenvy.api.core.rest.HttpJsonHelper;
import com.codenvy.api.user.server.dao.UserDao;
import com.codenvy.commons.user.User;
import com.codenvy.dto.server.DtoFactory;
import com.google.inject.name.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @author Sergii Kabashniuk
 */
public class DymmyHttpSessionTokenExtractor implements TokenExtractor {

    private static final Logger LOG = LoggerFactory.getLogger(DymmyHttpSessionTokenExtractor.class);

    @Inject
    protected TokenManager tokenManager;

    @Inject
    protected UserDao userDao;

    @Inject
    @Named("api.endpoint")
    String apiEndpoint;

    @Override
    public String getToken(HttpServletRequest request) {

        HttpSession session = request.getSession();
        User user = (User)session.getAttribute("codenvy_user");
        if (user != null) {
            return user.getToken();
        } else {
            if (user == null) {
                final String query = request.getQueryString();
                String username = null;
                String password = null;
                if (query != null) {
                    username = request.getParameter("username");
                    password = request.getParameter("password");
                }
                if (username == null) {
                    username = "codenvy@codenvy.com";
                }

                if (password != null) {
                    try {

                        return HttpJsonHelper.post(Token.class, apiEndpoint + "/auth/login",
                                                   DtoFactory.getInstance().createDto(Credentials.class)
                                                             .withUsername(username)
                                                             .withPassword(password)).getValue();

                    } catch (IOException | ApiException e) {
                        LOG.warn(e.getLocalizedMessage(), e);

                    }
                } else {
                    try {
                        return tokenManager.createToken(userDao.getByAlias(username).getId());
                    } catch (NotFoundException | ServerException e) {
                        LOG.warn(e.getLocalizedMessage(), e);
                    }
                }

            }
            return null;
        }
    }
}
