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
package com.bloatit.web.linkable.team.tabs;

import com.bloatit.framework.exceptions.highlevel.ShallNotPassException;
import com.bloatit.framework.webprocessor.components.HtmlDiv;
import com.bloatit.framework.webprocessor.components.advanced.HtmlTabBlock.HtmlTab;
import com.bloatit.framework.webprocessor.components.meta.HtmlNode;
import com.bloatit.model.Team;
import com.bloatit.model.right.UnauthorizedOperationException;
import com.bloatit.web.components.AccountComponent;

public class AccountTab extends HtmlTab {
    private final Team team;

    public AccountTab(final Team team, final String title, final String tabKey) {
        super(title, tabKey);
        this.team = team;
    }

    @Override
    public HtmlNode generateBody() {
        final HtmlDiv master = new HtmlDiv("tab_pane");
        try {
            master.add(new AccountComponent(team));
        } catch (final UnauthorizedOperationException e) {
            throw new ShallNotPassException(e);
        }
        return master;
    }
}
