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
package com.bloatit.web.pages.demand;

import com.bloatit.data.NullCollection;
import com.bloatit.framework.exceptions.UnauthorizedOperationException;
import com.bloatit.framework.utils.PageIterable;
import com.bloatit.framework.webserver.components.HtmlDiv;
import com.bloatit.model.Offer;
import com.bloatit.model.demand.DemandInterface;

public class IdeaOfferListComponent extends HtmlDiv {

    public IdeaOfferListComponent(final DemandInterface demand) {
        super();
        PageIterable<Offer> offers = new NullCollection<Offer>();
        try {
            offers = demand.getOffers();
        } catch (final UnauthorizedOperationException e) {
            // No right, no offers
        }
        final HtmlDiv offersBlock = new HtmlDiv("offers_block");

        try {
            final Offer currentOffer = demand.getSelectedOffer();
            offersBlock.add(new IdeaOfferComponent(currentOffer, true));
            for (final Offer offer : offers) {
                if (!offer.equals(currentOffer)) {
                    offersBlock.add(new IdeaOfferComponent(offer, false));
                }
            }
        } catch (final UnauthorizedOperationException e) {
            // No right no current offer.
        }

        add(offersBlock);
    }

}
