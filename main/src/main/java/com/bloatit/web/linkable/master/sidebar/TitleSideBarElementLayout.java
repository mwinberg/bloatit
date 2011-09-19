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
package com.bloatit.web.linkable.master.sidebar;

import com.bloatit.framework.webprocessor.components.HtmlDiv;
import com.bloatit.framework.webprocessor.components.HtmlTitle;
import com.bloatit.framework.webprocessor.components.PlaceHolderElement;
import com.bloatit.framework.webprocessor.components.meta.HtmlBranch;
import com.bloatit.framework.webprocessor.components.meta.HtmlElement;
import com.bloatit.framework.webprocessor.components.meta.HtmlNode;

public class TitleSideBarElementLayout extends SideBarElementLayout {

    private final HtmlTitle title;
    private final HtmlDiv body;
    private final PlaceHolderElement floatRight;

    public TitleSideBarElementLayout() {
        super();

        title = new HtmlTitle(1);
        title.setCssClass("side_bar_element_title");
        body = new HtmlDiv("side_bar_element_body");
        floatRight = new PlaceHolderElement();
        super.add(floatRight);
        super.add(title);
        super.add(body);
    }

    public void setTitle(final String title) {
        this.title.addText(title);
    }

    @Override
    public HtmlBranch add(final HtmlNode element) {
        body.add(element);
        return this;
    }

    public HtmlBranch setFloatRight(final HtmlElement element) {
        floatRight.add(new HtmlDiv("float_right").add(element));
        return this;
    }

}