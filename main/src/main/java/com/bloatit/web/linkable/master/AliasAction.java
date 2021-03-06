/*
 * Copyright (C) 2010 BloatIt. This file is part of BloatIt. BloatIt is free
 * software: you can redistribute it and/or modify it under the terms of the GNU
 * Affero General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * BloatIt is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details. You should have received a copy of the GNU Affero General Public
 * License along with BloatIt. If not, see <http://www.gnu.org/licenses/>.
 */
package com.bloatit.web.linkable.master;

import com.bloatit.framework.webprocessor.url.PageNotFoundUrl;
import com.bloatit.framework.webprocessor.url.Url;
import com.bloatit.framework.xcgiserver.HttpReponseField.StatusCode;

public abstract class AliasAction extends ElveosAction {

    private final Url redirectUrl;

    public AliasAction(final Url url, final Url redirectUrl) {
        super(url);
        this.redirectUrl = redirectUrl;
    }

    @Override
    protected Url checkRightsAndEverything() {
        return NO_ERROR;
    }

    @Override
    protected Url doProcess() {
        return redirectUrl;
    }

    @Override
    protected Url doProcessErrors() {
        return new PageNotFoundUrl();
    }

    @Override
    protected void transmitParameters() {
    }

    @Override
    protected StatusCode getRedirectionType() {
        return StatusCode.REDIRECTION_302_FOUND;
    }
}
