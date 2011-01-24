package com.bloatit.framework.demand;

import java.math.BigDecimal;
import java.util.Date;

import com.bloatit.model.data.DaoOffer;

public abstract class CanContributeMetaState extends AbstractDemandState {

    public CanContributeMetaState(final Demand demand) {
        super(demand);
    }

    protected abstract AbstractDemandState notifyAddContribution();

    @Override
    public final AbstractDemandState eventAddContribution() {
        return notifyAddContribution();
    }

    protected final AbstractDemandState handleEvent() {
        final BigDecimal contribution = demand.getDao().getContribution();
        final DaoOffer selectedOffer = demand.getDao().getSelectedOffer();
        Date validationDate = demand.getDao().getValidationDate();
        if (selectedOffer != null && validationDate != null && contribution.compareTo(selectedOffer.getAmount()) >= 0 && new Date().after(validationDate)) {
            return new DeveloppingState(demand);
        }
        return this;
    }

}
