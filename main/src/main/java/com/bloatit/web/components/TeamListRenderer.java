package com.bloatit.web.components;

import com.bloatit.framework.webprocessor.components.HtmlDiv;
import com.bloatit.framework.webprocessor.components.HtmlLink;
import com.bloatit.framework.webprocessor.components.HtmlRenderer;
import com.bloatit.framework.webprocessor.components.advanced.HtmlClearer;
import com.bloatit.framework.webprocessor.components.meta.XmlNode;
import com.bloatit.model.Team;
import com.bloatit.web.linkable.team.TeamTools;
import com.bloatit.web.url.TeamPageUrl;

/**
 * A simple renderer for teams that display only their name on one line, plus a
 * link to their page
 */
public class TeamListRenderer implements HtmlRenderer<Team> {
    @Override
    public XmlNode generate(final Team team) {
        final TeamPageUrl teamUrl = new TeamPageUrl(team);
        final HtmlDiv box = new HtmlDiv("team_box");

        box.add(new HtmlDiv("float_right").add(TeamTools.getTeamAvatar(team)));

        final HtmlDiv textBox = new HtmlDiv("team_text");
        HtmlLink htmlLink;
        htmlLink = teamUrl.getHtmlLink(team.getDisplayName());

        textBox.add(htmlLink);
        box.add(textBox);
        box.add(new HtmlClearer());

        return box;
    }
}
