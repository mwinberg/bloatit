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
package com.bloatit.model;

import com.bloatit.data.DaoBankTransaction;
import com.bloatit.data.DaoBatch;
import com.bloatit.data.DaoBug;
import com.bloatit.data.DaoComment;
import com.bloatit.data.DaoContribution;
import com.bloatit.data.DaoDemand;
import com.bloatit.data.DaoDescription;
import com.bloatit.data.DaoExternalAccount;
import com.bloatit.data.DaoFileMetadata;
import com.bloatit.data.DaoGroup;
import com.bloatit.data.DaoHighlightDemand;
import com.bloatit.data.DaoImage;
import com.bloatit.data.DaoInternalAccount;
import com.bloatit.data.DaoJoinGroupInvitation;
import com.bloatit.data.DaoKudos;
import com.bloatit.data.DaoMember;
import com.bloatit.data.DaoOffer;
import com.bloatit.data.DaoProject;
import com.bloatit.data.DaoTransaction;
import com.bloatit.data.DaoTranslation;
import com.bloatit.data.DataClassVisitor;
import com.bloatit.model.demand.DemandImplementation;

/**
 * The Class ConstructorVisitor is a visitor that visit the Dao layer and
 * construct the right Model layer object.
 */
public class ConstructorVisitor implements DataClassVisitor<Identifiable<?>> {

    /*
     * (non-Javadoc)
     * @see
     * com.bloatit.data.DataClassVisitor#visit(com.bloatit.data.DaoExternalAccount
     * )
     */
    @Override
    public Identifiable<?> visit(final DaoExternalAccount dao) {
        return ExternalAccount.create(dao);
    }

    /*
     * (non-Javadoc)
     * @see
     * com.bloatit.data.DataClassVisitor#visit(com.bloatit.data.DaoInternalAccount
     * )
     */
    @Override
    public Identifiable<?> visit(final DaoInternalAccount dao) {
        return InternalAccount.create(dao);
    }

    /*
     * (non-Javadoc)
     * @see com.bloatit.data.DataClassVisitor#visit(com.bloatit.data.DaoMember)
     */
    @Override
    public Identifiable<?> visit(final DaoMember dao) {
        return Member.create(dao);
    }

    /*
     * (non-Javadoc)
     * @see
     * com.bloatit.data.DataClassVisitor#visit(com.bloatit.data.DaoBankTransaction
     * )
     */
    @Override
    public Identifiable<?> visit(final DaoBankTransaction dao) {
        return BankTransaction.create(dao);
    }

    /*
     * (non-Javadoc)
     * @see com.bloatit.data.DataClassVisitor#visit(com.bloatit.data.DaoBatch)
     */
    @Override
    public Identifiable<?> visit(final DaoBatch dao) {
        return Batch.create(dao);
    }

    /*
     * (non-Javadoc)
     * @see
     * com.bloatit.data.DataClassVisitor#visit(com.bloatit.data.DaoDescription)
     */
    @Override
    public Identifiable<?> visit(final DaoDescription dao) {
        return Description.create(dao);
    }

    /*
     * (non-Javadoc)
     * @see com.bloatit.data.DataClassVisitor#visit(com.bloatit.data.DaoGroup)
     */
    @Override
    public Identifiable<?> visit(final DaoGroup dao) {
        return Group.create(dao);
    }

    /*
     * (non-Javadoc)
     * @see
     * com.bloatit.data.DataClassVisitor#visit(com.bloatit.data.DaoHighlightDemand
     * )
     */
    @Override
    public Identifiable<?> visit(final DaoHighlightDemand dao) {
        return HighlightDemand.create(dao);
    }

    /*
     * (non-Javadoc)
     * @see com.bloatit.data.DataClassVisitor#visit(com.bloatit.data.DaoImage)
     */
    @Override
    public Identifiable<?> visit(final DaoImage dao) {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see com.bloatit.data.DataClassVisitor#visit(com.bloatit.data.
     * DaoJoinGroupInvitation)
     */
    @Override
    public Identifiable<?> visit(final DaoJoinGroupInvitation dao) {
        return JoinGroupInvitation.create(dao);
    }

    /*
     * (non-Javadoc)
     * @see com.bloatit.data.DataClassVisitor#visit(com.bloatit.data.DaoProject)
     */
    @Override
    public Identifiable<?> visit(final DaoProject dao) {
        return Project.create(dao);
    }

    /*
     * (non-Javadoc)
     * @see
     * com.bloatit.data.DataClassVisitor#visit(com.bloatit.data.DaoTransaction)
     */
    @Override
    public Identifiable<?> visit(final DaoTransaction dao) {
        return Transaction.create(dao);
    }

    /*
     * (non-Javadoc)
     * @see com.bloatit.data.DataClassVisitor#visit(com.bloatit.data.DaoBug)
     */
    @Override
    public Identifiable<?> visit(final DaoBug dao) {
        return Bug.create(dao);
    }

    /*
     * (non-Javadoc)
     * @see
     * com.bloatit.data.DataClassVisitor#visit(com.bloatit.data.DaoContribution)
     */
    @Override
    public Identifiable<?> visit(final DaoContribution dao) {
        return Contribution.create(dao);
    }

    /*
     * (non-Javadoc)
     * @see
     * com.bloatit.data.DataClassVisitor#visit(com.bloatit.data.DaoFileMetadata)
     */
    @Override
    public Identifiable<?> visit(final DaoFileMetadata dao) {
        return FileMetadata.create(dao);
    }

    /*
     * (non-Javadoc)
     * @see com.bloatit.data.DataClassVisitor#visit(com.bloatit.data.DaoKudos)
     */
    @Override
    public Identifiable<?> visit(final DaoKudos dao) {
        return Kudos.create(dao);
    }

    /*
     * (non-Javadoc)
     * @see com.bloatit.data.DataClassVisitor#visit(com.bloatit.data.DaoComment)
     */
    @Override
    public Identifiable<?> visit(final DaoComment dao) {
        return Comment.create(dao);
    }

    /*
     * (non-Javadoc)
     * @see com.bloatit.data.DataClassVisitor#visit(com.bloatit.data.DaoDemand)
     */
    @Override
    public Identifiable<?> visit(final DaoDemand dao) {
        return DemandImplementation.create(dao);
    }

    /*
     * (non-Javadoc)
     * @see com.bloatit.data.DataClassVisitor#visit(com.bloatit.data.DaoOffer)
     */
    @Override
    public Identifiable<?> visit(final DaoOffer dao) {
        return Offer.create(dao);
    }

    /*
     * (non-Javadoc)
     * @see
     * com.bloatit.data.DataClassVisitor#visit(com.bloatit.data.DaoTranslation)
     */
    @Override
    public Identifiable<?> visit(final DaoTranslation dao) {
        return Translation.create(dao);
    }

}
