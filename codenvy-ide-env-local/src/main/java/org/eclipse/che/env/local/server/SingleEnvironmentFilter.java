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
package org.eclipse.che.env.local.server;

import org.eclipse.che.api.core.NotFoundException;
import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.user.server.dao.UserDao;
import org.eclipse.che.commons.env.EnvironmentContext;
import org.eclipse.che.commons.user.User;
import org.eclipse.che.commons.user.UserImpl;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.Principal;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set up environment variable. Only for local packaging with single workspace. Don't use it in production packaging.
 *
 * @author andrew00x
 */
@Singleton
public class SingleEnvironmentFilter implements Filter {
    @Inject
    private UserDao userDao;

    private String wsName;
    private String wsId;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        wsName = filterConfig.getInitParameter("ws-name");
        wsId = filterConfig.getInitParameter("ws-id");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest httpRequest = (HttpServletRequest)request;
        HttpSession session = httpRequest.getSession(false);
        User user = null;
        if (session != null) {
            user = (User)session.getAttribute("codenvy_user");
        }
        if (user == null) {
            final String query = httpRequest.getQueryString();
            String username = null;
            String password = null;
            if (query != null) {
                username = getParameter("username", query);
                password = getParameter("password", query);
            }
            if (username == null) {
                username = "codenvy@codenvy.com";
            }
            org.eclipse.che.api.user.server.dao.User daoUser;
            try {
                daoUser = userDao.getByAlias(username);
                if (password != null) {
                    userDao.authenticate(username, password);
                }
            } catch (NotFoundException | ServerException e) {
                throw new ServletException(e);
            }
            final List<String> roles = new LinkedList<>();
            Collections.addAll(roles, "workspace/admin", "workspace/developer", "system/admin", "system/manager", "user");
            user = new UserImpl(daoUser.getAliases().get(0), daoUser.getId(), "dummy_token", roles, false);
            session = httpRequest.getSession();
            session.setAttribute("codenvy_user", user);
        }
        final EnvironmentContext env = EnvironmentContext.getCurrent();
        try {
            env.setWorkspaceName(wsName);
            env.setWorkspaceId(wsId);
            env.setUser(user);
            chain.doFilter(addUserInRequest(httpRequest, user), response);
        } finally {
            EnvironmentContext.reset();
        }
    }

    private HttpServletRequest addUserInRequest(final HttpServletRequest httpRequest, final User user) {
        return new HttpServletRequestWrapper(httpRequest) {
            @Override
            public String getRemoteUser() {
                return user.getName();
            }

            @Override
            public boolean isUserInRole(String role) {
                return user.isMemberOf(role);
            }

            @Override
            public Principal getUserPrincipal() {
                return new Principal() {
                    @Override
                    public String getName() {
                        return user.getName();
                    }
                };
            }
        };
    }

    @Override
    public void destroy() {
    }

    private String getParameter(String name, String query) {
        int start;
        int length;
        if (query.startsWith(name + "=")) {
            start = name.length() + 1;
        } else {
            start = query.indexOf("&" + name + "=");
            if (start > 0) {
                start += (name.length() + 2);
            }
        }
        if (start >= 0) {
            length = query.indexOf('&', start);
            if (length == -1) {
                length = query.length();
            }
            return query.substring(start, length);
        }
        return null;
    }
}
