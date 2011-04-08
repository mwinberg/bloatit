package com.bloatit.model;

import java.util.Date;

import com.bloatit.data.DaoFeature;
import com.bloatit.data.DaoHighlightFeature;
import com.bloatit.data.queries.DBRequests;
import com.bloatit.model.feature.FeatureImplementation;

public class HighlightFeature extends Identifiable<DaoHighlightFeature> {

    // /////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTION
    // /////////////////////////////////////////////////////////////////////////////////////////

    private static final class MyCreator extends Creator<DaoHighlightFeature, HighlightFeature> {
        @SuppressWarnings("synthetic-access")
        @Override
        public HighlightFeature doCreate(final DaoHighlightFeature dao) {
            return new HighlightFeature(dao);
        }
    }

    @SuppressWarnings("synthetic-access")
    public static HighlightFeature create(final DaoHighlightFeature dao) {
        return new MyCreator().create(dao);
    }

    public HighlightFeature(final Feature feature, final int position, final String reason, final Date activationDate, final Date desactivationDate) {
        super(DaoHighlightFeature.createAndPersist(DBRequests.getById(DaoFeature.class, feature.getId()),
                                                  position,
                                                  reason,
                                                  activationDate,
                                                  desactivationDate));
    }

    private HighlightFeature(final DaoHighlightFeature dao) {
        super(dao);
    }

    public int getPosition() {
        return getDao().getPosition();
    }

    public Date getActivationDate() {
        return getDao().getActivationDate();
    }

    public Feature getFeature() {
        return FeatureImplementation.create(getDao().getFeature());
    }

    public String getReason() {
        return getDao().getReason();
    }

    @Override
    protected boolean isMine(final Member member) {
        // TODO Auto-generated method stub
        return false;
    }

    // /////////////////////////////////////////////////////////////////////////////////////////
    // Visitor
    // /////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public <ReturnType> ReturnType accept(final ModelClassVisitor<ReturnType> visitor) {
        return visitor.visit(this);
    }

}
