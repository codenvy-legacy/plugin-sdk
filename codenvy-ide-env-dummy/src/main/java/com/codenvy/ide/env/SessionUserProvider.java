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

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.Boolean.parseBoolean;

import com.codenvy.api.account.server.dao.Account;
import com.codenvy.api.account.server.dao.AccountDao;
import com.codenvy.api.account.server.dao.Member;
import com.codenvy.api.auth.SessionStore;
import com.codenvy.api.auth.TokenManager;
import com.codenvy.api.auth.UserProvider;
import com.codenvy.api.core.NotFoundException;
import com.codenvy.api.core.ServerException;
import com.codenvy.api.user.server.dao.PreferenceDao;
import com.codenvy.api.user.server.dao.UserDao;
import com.codenvy.api.workspace.server.dao.MemberDao;
import com.codenvy.commons.env.EnvironmentContext;
import com.codenvy.commons.user.User;
import com.codenvy.commons.user.UserImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Provider user by token.
 * User and roles constructed only once and stored in session for future use.
 *
 * To be able to correctly set up user roles workspaceId and accountId have to be set up
 * in EnvironmentContext. see SingleEnvironmentFilter.
 *
 * @author Sergii Kabashniuk
 */
public class SessionUserProvider implements UserProvider {

    private static final Logger LOG = LoggerFactory.getLogger(SessionUserProvider.class);

    @Inject
    SessionStore  sessionStore;
    @Inject
    TokenManager  tokenManager;
    @Inject
    UserDao       userDao;
    @Inject
    PreferenceDao preferenceDao;
    @Inject
    AccountDao    accountDao;
    @Inject
    MemberDao     memberDao;


    @Override
    public User getUser(String token) {
        checkNotNull(token, "Token can't be null");
        HttpSession session = sessionStore.getSession(token);
        if (session != null) {
            User user = (User)session.getAttribute("codenvy_user");
            if (user == null) {
                String userId = tokenManager.getUserId(token);
                if (userId != null) {
                    try {
                        com.codenvy.api.user.server.dao.User daoUser = userDao.getById(userId);
                        EnvironmentContext context = EnvironmentContext.getCurrent();
                        Set<String> roles = extractRoles(userId, context.getWorkspaceId(), context.getAccountId());
                        user = new UserImpl(daoUser.getEmail(), daoUser.getId(), token, roles, roles.contains("temp_user"));
                        session.setAttribute("codenvy_user", user);
                        return user;
                    } catch (NotFoundException | ServerException e) {
                        LOG.error(e.getLocalizedMessage(), e);
                    }
                }
            }
            return user;

        }
        return null;
    }

    private Set<String> extractRoles(String userId, String workspaceId, String accountId) {

        try {
            final Set<String> userRoles = new HashSet<>();

            final Map<String, String> preferences = preferenceDao.getPreferences(userId);
            if (parseBoolean(preferences.get("temporary"))) {
                userRoles.add("temp_user");
            } else {
                userRoles.add("user");
            }

            com.codenvy.api.user.server.dao.User user = userDao.getById(userId);

            if (accountId != null) {
                Account account = accountDao.getById(accountId);
                if (account != null) {
                    for (Member accountMember : accountDao.getMembers(accountId)) {
                        if (accountMember.getUserId().equals(user.getId()))
                            userRoles.addAll(accountMember.getRoles());
                    }
                }
            }

            if (workspaceId != null) {
                for (com.codenvy.api.workspace.server.dao.Member workspaceMember : memberDao.getUserRelationships(user.getId())) {
                    if (workspaceMember.getWorkspaceId().equals(workspaceId))
                        userRoles.addAll(workspaceMember.getRoles());
                }
            }
            return userRoles;
        } catch (NotFoundException e) {
            return Collections.emptySet();
        } catch (ServerException e) {
            LOG.error(e.getLocalizedMessage(), e);
            throw new RuntimeException(e.getLocalizedMessage());
        }

    }

}