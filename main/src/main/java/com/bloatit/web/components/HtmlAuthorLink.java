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
package com.bloatit.web.components;

import com.bloatit.framework.webprocessor.components.HtmlLink;
import com.bloatit.model.UserContentInterface;
import com.bloatit.model.right.AuthToken;
import com.bloatit.web.url.MemberPageUrl;
import com.bloatit.web.url.TeamPageUrl;

public class HtmlAuthorLink extends HtmlLink {
    public HtmlAuthorLink(final UserContentInterface content) {
        // @formatter:off
        super(
              content.getAsTeam() != null ?
                      new TeamPageUrl(content.getAsTeam()).urlString() :
                      new MemberPageUrl(content.getMember()).urlString()
                      ,
              "@" + content.getAuthor().getDisplayName());
        if(content.getAsTeam() != null) {
            setCssClass("team-link");
        } else {
            if(content.getMember().equals(AuthToken.getMember())) {
                setCssClass("me-link");
            } else {
                setCssClass("member-link");
            }
        }
    }
    // @formatter:on
}
