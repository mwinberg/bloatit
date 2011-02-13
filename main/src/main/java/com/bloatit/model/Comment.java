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

import com.bloatit.data.DaoComment;
import com.bloatit.framework.exceptions.UnauthorizedOperationException;
import com.bloatit.framework.utils.PageIterable;
import com.bloatit.model.lists.CommentList;

/**
 * @see DaoComment
 */
public final class Comment extends Kudosable<DaoComment> {

    /**
     * Create a new comment and return it. It return null if the <code>dao</code> is null.
     */
    public static Comment create(final DaoComment dao) {
        if (dao != null) {
            @SuppressWarnings("unchecked")
            final Identifiable<DaoComment> created = CacheManager.get(dao);
            if (created == null) {
                return new Comment(dao);
            }
            return (Comment) created;
        }
        return null;
    }

    private Comment(final DaoComment dao) {
        super(dao);
    }

    /**
     * Return all the children comment of this comment.
     *
     * @see DaoComment#getChildren()
     */
    public PageIterable<Comment> getChildren() {
        return new CommentList(getDao().getChildren());
    }

    /**
     * @param text is the comment text.
     * @throws UnauthorizedOperationException if the user does not have the WRITE right on
     *         the Comment property
     * @see #addChildComment(Comment)
     *  TODO: Make the authentication system.
     */
    public void addChildComment(final String text) throws UnauthorizedOperationException {
        getDao().addChildComment(DaoComment.createAndPersist(getAuthToken().getMember().getDao(), text));
    }

    /**
     * @return the text of this comment.
     */
    public String getText() {
        return getDao().getText();
    }

    /**
     * Add a comment to the list of children of this comment.
     *
     * @see #addChildComment(String)
     */
    public void addChildComment(final Comment comment) {
        getDao().addChildComment(comment.getDao());
    }

    /**
     * @see com.bloatit.model.Kudosable#turnPending()
     */
    @Override
    protected int turnPending() {
        return KudosableConfiguration.getCommentTurnPending();
    }

    /**
     * @see com.bloatit.model.Kudosable#turnValid()
     */
    @Override
    protected int turnValid() {
        return KudosableConfiguration.getCommentTurnValid();
    }

    /**
     * @see com.bloatit.model.Kudosable#turnRejected()
     */
    @Override
    protected int turnRejected() {
        return KudosableConfiguration.getCommentTurnRejected();
    }

    /**
     * @see com.bloatit.model.Kudosable#turnHidden()
     */
    @Override
    protected int turnHidden() {
        return KudosableConfiguration.getCommentTurnHidden();
    }

}
