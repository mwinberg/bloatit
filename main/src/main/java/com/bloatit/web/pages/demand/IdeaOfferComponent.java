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

import com.bloatit.framework.exceptions.UnauthorizedOperationException;
import com.bloatit.framework.utils.i18n.DateLocale.FormatStyle;
import com.bloatit.framework.webserver.Context;
import com.bloatit.framework.webserver.components.HtmlDiv;
import com.bloatit.framework.webserver.components.HtmlImage;
import com.bloatit.framework.webserver.components.HtmlParagraph;
import com.bloatit.framework.webserver.components.HtmlSpan;
import com.bloatit.framework.webserver.components.meta.HtmlElement;
import com.bloatit.model.Batch;
import com.bloatit.model.Offer;
import com.bloatit.web.pages.master.HtmlPageComponent;

public final class IdeaOfferComponent extends HtmlPageComponent {

    private final Offer offer;
    private final boolean currentOffer;

    public IdeaOfferComponent(final Offer offer, final boolean b) {
        super();
        this.offer = offer;
        this.currentOffer = b;

        if (offer != null) {
            try {
                add(produce());
            } catch (final UnauthorizedOperationException e) {
                // No right, no display
            }
        }
    }

    protected HtmlElement produce() throws UnauthorizedOperationException {

        HtmlParagraph author = null;

        author = new HtmlParagraph(Context.tr("Author : ") + offer.getAuthor().getDisplayName(), "offer_author");

        final HtmlParagraph price = new HtmlParagraph(Context.tr("Price : ")
                + Context.getLocalizator().getCurrency(offer.getAmount()).getLocaleString(), "offer_price");
        final HtmlParagraph expirationDate = new HtmlParagraph(Context.tr("Expiration date : ")
                + Context.getLocalizator().getDate(offer.getExpirationDate()).toDateTimeString(FormatStyle.LONG, FormatStyle.MEDIUM),
                "offer_expiry_date");
        final HtmlImage authorAvatar = new HtmlImage(offer.getAuthor().getAvatar(), "offer_avatar");
        final HtmlParagraph creationDate = new HtmlParagraph(Context.tr("Creation Date : ")
                + Context.getLocalizator().getDate(offer.getCreationDate()).toDateTimeString(FormatStyle.LONG, FormatStyle.MEDIUM),
                "offer_creation_date");

        final HtmlDiv offerBlock = new HtmlDiv("offer_block");
        {

            if (this.currentOffer) {
                offerBlock.add(new HtmlSpan().addText(Context.tr("Currently favored offer")).setCssClass("offer_validated_info"));
                offerBlock.setCssClass("offer_block_validated");
            }

            final HtmlDiv offerMainBlock = new HtmlDiv("offer_main_block");
            offerBlock.add(offerMainBlock);

            offerMainBlock.add(authorAvatar);

            final HtmlDiv offerInfoBlock = new HtmlDiv("offer_info_block");
            {
                offerInfoBlock.add(author);
                offerInfoBlock.add(price);
                offerInfoBlock.add(expirationDate);
                offerInfoBlock.add(creationDate);
            }

            offerMainBlock.add(offerInfoBlock);
            for (final Batch batch : offer.getBatches()) {
                final HtmlParagraph title = new HtmlParagraph(batch.getTitle(), "offer_title");
                final HtmlParagraph description = new HtmlParagraph(batch.getDescription(), "offer_description");
                offerMainBlock.add(title);
                offerMainBlock.add(description);
            }

        }
        return offerBlock;
    }
}