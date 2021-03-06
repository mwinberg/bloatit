//
// Copyright (c) 2011 Linkeos.
//
// This file is part of Elveos.org.
// Elveos.org is free software: you can redistribute it and/or modify it
// under the terms of the GNU General Public License as published by the
// Free Software Foundation, either version 3 of the License, or (at your
// option) any later version.
//
// Elveos.org is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
// more details.
// You should have received a copy of the GNU General Public License along
// with Elveos.org. If not, see http://www.gnu.org/licenses/.
//
package com.bloatit.web.linkable.usercontent;

import com.bloatit.framework.webprocessor.annotations.ParamContainer;
import com.bloatit.web.linkable.master.LoggedElveosPage;
import com.bloatit.web.url.CreateUserContentPageUrl;

/**
 * @author thomas
 */
@ParamContainer("usercontent/create")
public abstract class CreateUserContentPage extends LoggedElveosPage {

    public CreateUserContentPage(final CreateUserContentPageUrl url) {
        super(url);
    }

    @Override
    public boolean isStable() {
        return false;
    }
}
