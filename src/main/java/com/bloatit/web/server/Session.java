/*
 * Copyright (C) 2010 BloatIt.
 * 
 * This file is part of BloatIt.
 * 
 * BloatIt is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * BloatIt is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with BloatIt. If not, see <http://www.gnu.org/licenses/>.
 */

package com.bloatit.web.server;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Locale;

import com.bloatit.framework.AuthToken;

public class Session {
    private final String key;
    private boolean logged;
    private final Deque<Action> actionList;
    private final Deque<Notification> notificationList;
    private Language language;
    private Request lastStablePage = null;
    private Request targetPage = null;
    private AuthToken authToken;

    private final List<Language> preferredLocales;

    Session(String key) {
        this.key = key;
        this.authToken = null;
        this.logged = false;
        this.actionList = new ArrayDeque<Action>();
        this.notificationList = new ArrayDeque<Notification>();

        // TODO : Following lines are for testing purposes only
        preferredLocales = new ArrayList<Language>();
        preferredLocales.add(new Language(Locale.ENGLISH)); // TODO : ONLY FOR TEST
    }

    public String tr(String s) {
        return this.language.tr(s);
    }

    public Language getLanguage() {
        return this.language;
    }

    public void setLanguage(Language newLang) {
        this.language = newLang;
    }

    public void setAuthToken(AuthToken token) {
        this.authToken = token;
    }

    public AuthToken getAuthToken() {
        return this.authToken;
    }

    public void setLogged(boolean logged) {
        this.logged = logged;
    }

    public boolean isLogged() {
        return this.logged;
    }

    public String getKey() {
        return key;
    }

    public Deque<Action> getActionList() {
        return actionList;
    }

    public void setLastStablePage(Request p) {
        this.lastStablePage = p;
    }

    public Request getLastStablePage() {
        return lastStablePage;
    }

    public Request getTargetPage() {
        return targetPage;
    }

    public void setTargetPage(Request targetPage) {
        this.targetPage = targetPage;
    }

    public void notifyGood(String message) {
        this.notificationList.add(new Notification(message, Notification.Type.GOOD));
    }

    public void notifyBad(String message) {
        this.notificationList.add(new Notification(message, Notification.Type.BAD));
    }

    public void notifyError(String message) {
        this.notificationList.add(new Notification(message, Notification.Type.ERROR));
    }

    void flushNotifications() {
        this.notificationList.clear();
    }

    Deque<Notification> getNotifications() {
        return notificationList;
    }

    public List<Language> getPreferredLangs() {
        return this.preferredLocales;
    }
}