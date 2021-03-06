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
package com.bloatit.model.visitor;

import org.apache.commons.lang.NotImplementedException;

import com.bloatit.model.BankTransaction;
import com.bloatit.model.Bug;
import com.bloatit.model.Comment;
import com.bloatit.model.Contribution;
import com.bloatit.model.ContributionInvoice;
import com.bloatit.model.Description;
import com.bloatit.model.Event;
import com.bloatit.model.ExternalAccount;
import com.bloatit.model.ExternalService;
import com.bloatit.model.ExternalServiceMembership;
import com.bloatit.model.Feature;
import com.bloatit.model.FileMetadata;
import com.bloatit.model.Follow;
import com.bloatit.model.FollowActor;
import com.bloatit.model.FollowFeature;
import com.bloatit.model.FollowSoftware;
import com.bloatit.model.HighlightFeature;
import com.bloatit.model.InternalAccount;
import com.bloatit.model.Invoice;
import com.bloatit.model.JoinTeamInvitation;
import com.bloatit.model.Kudos;
import com.bloatit.model.Member;
import com.bloatit.model.Milestone;
import com.bloatit.model.MilestoneContributionAmount;
import com.bloatit.model.MoneyWithdrawal;
import com.bloatit.model.NewsFeed;
import com.bloatit.model.Offer;
import com.bloatit.model.Release;
import com.bloatit.model.Software;
import com.bloatit.model.Team;
import com.bloatit.model.Transaction;
import com.bloatit.model.Translation;

public class AbstractModelClassVisitor<T> implements ModelClassVisitor<T> {

    @Override
    public T visit(final ExternalAccount model) {
        throw new NotImplementedException();
    }

    @Override
    public T visit(final InternalAccount model) {
        throw new NotImplementedException();
    }

    @Override
    public T visit(final Member model) {
        throw new NotImplementedException();
    }

    @Override
    public T visit(final BankTransaction model) {
        throw new NotImplementedException();
    }

    @Override
    public T visit(final Milestone model) {
        throw new NotImplementedException();
    }

    @Override
    public T visit(final Description model) {
        throw new NotImplementedException();
    }

    @Override
    public T visit(final Team model) {
        throw new NotImplementedException();
    }

    @Override
    public T visit(final HighlightFeature model) {
        throw new NotImplementedException();
    }

    @Override
    public T visit(final JoinTeamInvitation model) {
        throw new NotImplementedException();
    }

    @Override
    public T visit(final Software model) {
        throw new NotImplementedException();
    }

    @Override
    public T visit(final Transaction model) {
        throw new NotImplementedException();
    }

    @Override
    public T visit(final Bug model) {
        throw new NotImplementedException();
    }

    @Override
    public T visit(final Contribution model) {
        throw new NotImplementedException();
    }

    @Override
    public T visit(final FileMetadata model) {
        throw new NotImplementedException();
    }

    @Override
    public T visit(final Kudos model) {
        throw new NotImplementedException();
    }

    @Override
    public T visit(final Comment model) {
        throw new NotImplementedException();
    }

    @Override
    public T visit(final Feature model) {
        throw new NotImplementedException();
    }

    @Override
    public T visit(final Offer model) {
        throw new NotImplementedException();
    }

    @Override
    public T visit(final Translation model) {
        throw new NotImplementedException();
    }

    @Override
    public T visit(final Release model) {
        throw new NotImplementedException();
    }

    @Override
    public T visit(final MoneyWithdrawal model) {
        throw new NotImplementedException();
    }

    @Override
    public T visit(final Invoice model) {
        throw new NotImplementedException();
    }

    @Override
    public T visit(final ContributionInvoice model) {
        throw new NotImplementedException();
    }

    @Override
    public T visit(final MilestoneContributionAmount model) {
        throw new NotImplementedException();
    }

    @Override
    public T visit(final NewsFeed newsFeed) {
        throw new NotImplementedException();
    }

    @Override
    public T visit(final ExternalService externalService) {
        throw new NotImplementedException();
    }

    @Override
    public T visit(ExternalServiceMembership externalService) {
        throw new NotImplementedException();
    }

    @Override
    public T visit(Follow model) {
        throw new NotImplementedException();
    }

    @Override
    public T visit(Event event) {
        throw new NotImplementedException();
    }

    @Override
    public T visit(FollowFeature model) {
        throw new NotImplementedException();
    }

    @Override
    public T visit(FollowSoftware model) {
        throw new NotImplementedException();
    }

    @Override
    public T visit(FollowActor model) {
        throw new NotImplementedException();
    }
}
