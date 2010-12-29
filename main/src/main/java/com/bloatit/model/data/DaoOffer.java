package com.bloatit.model.data;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.search.annotations.DateBridge;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Resolution;
import org.hibernate.search.annotations.Store;

import com.bloatit.model.data.util.NonOptionalParameterException;

/**
 * An offer is a developer offer to a demand.
 */
@Entity
public final class DaoOffer extends DaoKudosable {

    /**
     * This is demand on which this offer is done.
     */
    @ManyToOne(optional = false)
    private DaoDemand demand;

    /**
     * Remember a description is a title with some content. (Translatable)
     */
    @OneToOne
    @Cascade(value = { CascadeType.ALL })
    @IndexedEmbedded
    private DaoDescription description;

    /**
     * Because the 'lot' is not already implemented I added a dateExpire field. It is
     * really not very useful...
     */
    @Basic(optional = false)
    @Field(index = Index.UN_TOKENIZED, store = Store.YES)
    @DateBridge(resolution = Resolution.DAY)
    private Date dateExpire;

    /**
     * The amount represents the money the member want to have to make his offer. (this
     * will also probably will be modified by the 'lot' implementation)
     */
    @Basic(optional = false)
    private BigDecimal amount;

    // TODO comment
    public DaoOffer(final DaoMember member, final DaoDemand demand, final BigDecimal amount, final DaoDescription text, final Date dateExpire) {
        super(member);
        if (demand == null || text == null || dateExpire == null) {
            throw new NonOptionalParameterException();
        }
        setDemand(demand);
        setDescription(text);
        setDateExpire(dateExpire);
        setAmount(amount);
    }

    public final Date getDateExpire() {
        return dateExpire;
    }

    public final void setDateExpire(final Date dateExpire) {
        this.dateExpire = dateExpire;
    }

    public final DaoDemand getDemand() {
        return demand;
    }

    public final DaoDescription getDescription() {
        return description;
    }

    public final BigDecimal getAmount() {
        return amount;
    }

    // ======================================================================
    // For hibernate mapping
    // ======================================================================

    protected DaoOffer() {
        super();
    }

    private void setDemand(final DaoDemand Demand) {
        demand = Demand;
    }

    private void setDescription(final DaoDescription text) {
        description = text;
    }

    private void setAmount(final BigDecimal amount) {
        this.amount = amount;
    }
}
