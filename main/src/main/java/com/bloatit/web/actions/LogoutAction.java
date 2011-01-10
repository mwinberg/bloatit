/*
 * Copyright (C) 2010 BloatIt. This file is part of BloatIt. BloatIt is free software: you
 * can redistribute it and/or modify it under the terms of the GNU Affero General Public
 * License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version. BloatIt is distributed in the hope that it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details. You should have received a copy of the GNU Affero General
 * Public License along with BloatIt. If not, see <http://www.gnu.org/licenses/>.
 */

package com.bloatit.web.actions;

import com.bloatit.web.annotations.ParamContainer;
import com.bloatit.web.exceptions.RedirectException;
import com.bloatit.web.server.Context;
import com.bloatit.web.utils.url.LoginPageUrl;
import com.bloatit.web.utils.url.LogoutActionUrl;
import com.bloatit.web.utils.url.Url;

@ParamContainer("action/logout")
public class LogoutAction extends Action {

    public LogoutAction(final LogoutActionUrl url) {
        super(url);
    }

    @Override
    public final Url doProcess() {
        session.setAuthToken(null);
        session.notifyGood(Context.tr("Logout success."));
        return session.pickPreferredPage();
    }
    
    @Override
	protected final Url doProcessErrors() throws RedirectException {
    	//TODO
		return new LoginPageUrl();
	}
}
