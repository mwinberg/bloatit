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
package test.pages.demand;

import test.html.HtmlElement;
import test.html.components.standard.HtmlDiv;

import com.bloatit.framework.Demand;

import test.Request;

public class DemandHeadComponent extends HtmlElement {

    public DemandHeadComponent(Request request, Demand demand) {
        super();
        HtmlDiv demandHead = new HtmlDiv("demand_head");
        {
            // Add progress bar
            HtmlDiv demandHeadProgress = new HtmlDiv("demand_head_progress");
            {
                demandHeadProgress.add(new DemandProgressBarComponent(request, demand));
            }
            demandHead.add(demandHeadProgress);

            // Add kudo box
            HtmlDiv demandHeadKudo = new HtmlDiv("demand_head_kudo");
            {
                demandHeadKudo.add(new DemandKudoComponent(request, demand));
            }
            demandHead.add(demandHeadKudo);

        }
        add(demandHead);
    }
}
