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

package com.bloatit.web.pages;

import com.bloatit.web.htmlrenderer.HtmlTools;
import com.bloatit.web.server.Page;
import com.bloatit.web.server.Session;
import java.util.HashMap;
import java.util.Map;


public class IndexPage extends Page {

    public IndexPage(Session session, Map<String, String> parameters) {
        super(session, parameters);        
    }

    public IndexPage(Session session) {
        this(session, new HashMap<String, String>());
    }

    @Override
    protected void generateContent() {
        this.htmlResult.write("<h2>Welcome in "+HtmlTools.generateLogo()+" website</h2>");
        this.htmlResult.write(HtmlTools.generateLogo()+" is a wonderful website !");
        this.htmlResult.write(" is a wonderful website !");
    }

    @Override
    public String getCode() {
        return "index";
    }

    @Override
    protected String getTitle() {
        return "Finance free software";
    }
}