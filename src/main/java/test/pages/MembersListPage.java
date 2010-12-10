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
package test.pages;

import com.bloatit.common.PageIterable;
import com.bloatit.framework.Member;
import com.bloatit.framework.managers.MemberManager;
import com.bloatit.web.utils.PageComponent;
import test.RedirectException;

import test.Request;
import test.UrlBuilder;
import test.html.HtmlNode;
import test.html.HtmlText;
import test.html.HtmlTools;
import test.html.components.advanced.HtmlPagedList;
import test.html.components.standard.HtmlLink;
import test.html.components.standard.HtmlListItem;
import test.html.components.standard.HtmlRenderer;
import test.html.components.standard.HtmlTitleBlock;
import test.pages.master.Page;

public class MembersListPage extends Page {

    @PageComponent
    private HtmlPagedList<Member> pagedMemberList;

    public MembersListPage(Request request) throws RedirectException {
        super(request);
        generateContent();
    }

    private void generateContent() {

        final HtmlTitleBlock pageTitle = new HtmlTitleBlock("Members list");

        final PageIterable<Member> memberList = MemberManager.getMembers();

        HtmlRenderer<Member> memberItemRenderer = new HtmlRenderer<Member>() {

            private UrlBuilder urlBuilder = new UrlBuilder(MemberPage.class);

            @Override
            public HtmlNode generate(Member member) {
                urlBuilder.addParameter("member", member);
                final HtmlLink htmlLink = urlBuilder.getHtmlLink(member.getFullname());
                final HtmlText htmlKarma = new HtmlText("<span class=\"karma\">" + HtmlTools.compressKarma(member.getKarma()) + "</span>");
                return new HtmlListItem(htmlLink).add(htmlKarma);
            }
        };

        //TODO: avoid conflict
        pagedMemberList = new HtmlPagedList<Member>(memberItemRenderer, memberList, request, session);

        pageTitle.add(pagedMemberList);

        add(pageTitle);

    }


    @Override
    protected String getTitle() {
        return "Members list";
    }

    @Override
    public boolean isStable() {
        return true;
    }
}
