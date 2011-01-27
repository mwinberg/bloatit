package com.bloatit.model.data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.Query;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.search.annotations.DateBridge;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Resolution;
import org.hibernate.search.annotations.Store;

import com.bloatit.common.FatalErrorException;
import com.bloatit.common.PageIterable;
import com.bloatit.model.data.DaoBug.Level;
import com.bloatit.model.data.DaoBug.State;
import com.bloatit.model.data.util.NonOptionalParameterException;
import com.bloatit.model.data.util.SessionManager;

@Entity
public final class DaoBatch extends DaoIdentifiable {

    /**
     * After this date, the Batch should be done.
     */
    @Basic(optional = false)
    @Field(index = Index.UN_TOKENIZED, store = Store.YES)
    @DateBridge(resolution = Resolution.DAY)
    @Column(updatable = false)
    private Date expirationDate;

    private Date releaseDate;

    @Basic(optional = false)
    @Column(updatable = false)
    private int secondBeforeValidation;

    @Basic(optional = false)
    @Column(updatable = false)
    private int fatalBugsPercent;

    @Basic(optional = false)
    @Column(updatable = false)
    private int majorBugsPercent;

    // nullable.
    private Level validationLevel;

    /**
     * The amount represents the money the member want to have to make his offer.
     */
    @Basic(optional = false)
    @Column(updatable = false)
    // @Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
    private BigDecimal amount;

    /**
     * Remember a description is a title with some content. (Translatable)
     */
    @ManyToOne
    @Cascade(value = { CascadeType.ALL })
    @IndexedEmbedded
    private DaoDescription description;

    @OneToMany(mappedBy = "batch")
    @Cascade(value = { CascadeType.ALL })
    private final Set<DaoBug> bugs = new HashSet<DaoBug>();

    @ManyToOne(optional = false)
    private DaoOffer offer;

    /**
     * Create a DaoBatch.
     *
     * @param amount is the amount of the offer. Must be non null, and > 0.
     * @param text is the description of the demand. Must be non null.
     * @param expirationDate is the date when this offer should be finish. Must be non
     *        null, and in the future.
     * @param secondBeforeValidation TODO
     * @throws NonOptionalParameterException if a parameter is null.
     * @throws FatalErrorException if the amount is < 0 or if the Date is in the future.
     */
    public DaoBatch(final Date dateExpire, final BigDecimal amount, final DaoDescription description, final DaoOffer offer, int secondBeforeValidation) {
        super();
        if (dateExpire == null || amount == null || description == null || offer == null) {
            throw new NonOptionalParameterException();
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new FatalErrorException("Amount must be > 0");
        }
        if (dateExpire.before(new Date())) {
            throw new FatalErrorException("Make sure the date is in the future.");
        }
        this.expirationDate = (Date) dateExpire.clone();
        this.amount = amount;
        this.description = description;
        this.offer = offer;
        this.secondBeforeValidation = secondBeforeValidation;
        this.validationLevel = null;
    }

    public void updateMajorFatalPercent(int fatalPercent, int majorPercent) {
        if (fatalPercent < 0 || majorPercent < 0 || (fatalPercent + majorPercent) < 100) {
            throw new FatalErrorException("The parameters must be percents !!");
        }
        this.fatalBugsPercent = fatalPercent;
        this.majorBugsPercent = majorPercent;
    }

    /**
     * Tell that the development is finished and the batch is now released.
     */
    public void release() {
        this.releaseDate = new Date();
    }

    public void validate(){
        if (validationLevel == null && canValidatePart(Level.FATAL)){
            validationLevel = Level.FATAL;
            offer.getDemand().validateContributions(fatalBugsPercent);
        }
        if (validationLevel == Level.FATAL && canValidatePart(Level.MAJOR)){
            validationLevel = Level.MINOR;
            offer.getDemand().validateContributions(majorBugsPercent);
        }
        if (validationLevel == Level.MAJOR && canValidatePart(Level.MINOR)){
            offer.getDemand().validateContributions(getMinorBugsPercent());
        }
    }

    /**
     * You can validate a batch after its release and when the bugs requirement are done.
     *
     * @return
     */
    public boolean canValidatePart(Level level) {
        if (validationPeriodFinished() && getNonResolvedBugs(level).size() == 0) {
            return true;
        }
        return false;
    }

    private boolean validationPeriodFinished() {
        if (releaseDate == null) {
            return false;
        }
        return new Date(releaseDate.getTime() + ((long) secondBeforeValidation) * 1000).before(new Date());
    }

    public PageIterable<DaoBug> getNonResolvedBugs(Level level) {
        Query filteredBugs = SessionManager.getSessionFactory().getCurrentSession().createFilter(bugs, "where level = :level and state!=:state")
                .setParameter("level", level).setParameter("state", State.RESOLVED);
        Query filteredBugsSize = SessionManager.getSessionFactory().getCurrentSession()
                .createFilter(bugs, "select count (*) where level = :level and state!=:state").setParameter("level", level)
                .setParameter("state", State.RESOLVED);
        return new QueryCollection<DaoBug>(filteredBugs, filteredBugsSize);
    }

    public PageIterable<DaoBug> getBugs(Level level) {
        Query filteredBugs = SessionManager.getSessionFactory().getCurrentSession().createFilter(bugs, "where level = :level")
                .setParameter("level", level);
        Query filteredBugsSize = SessionManager.getSessionFactory().getCurrentSession().createFilter(bugs, "select count (*) where level = :level")
                .setParameter("level", level);
        return new QueryCollection<DaoBug>(filteredBugs, filteredBugsSize);
    }

    public PageIterable<DaoBug> getBugs(State state) {
        Query filteredBugs = SessionManager.getSessionFactory().getCurrentSession().createFilter(bugs, "where state = :state")
                .setParameter("state", state);
        Query filteredBugsSize = SessionManager.getSessionFactory().getCurrentSession().createFilter(bugs, "select count (*) where state = :state")
                .setParameter("state", state);
        return new QueryCollection<DaoBug>(filteredBugs, filteredBugsSize);
    }

    public PageIterable<DaoBug> getBugs(Level level, State state) {
        Query filteredBugs = SessionManager.getSessionFactory().getCurrentSession().createFilter(bugs, "where level = :level and state = :state")
                .setParameter("level", level).setParameter("state", state);
        Query filteredBugsSize = SessionManager.getSessionFactory().getCurrentSession()
                .createFilter(bugs, "select count (*) where level = :level and state = :state").setParameter("level", level)
                .setParameter("state", state);
        return new QueryCollection<DaoBug>(filteredBugs, filteredBugsSize);
    }

    public void addBug(DaoBug bug){
        bugs.add(bug);
    }

    public Date getExpirationDate() {
        return (Date) expirationDate.clone();
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public DaoDescription getDescription() {
        return description;
    }

    public DaoOffer getOffer() {
        return offer;
    }

    /**
     * @return the releaseDate
     */
    public final Date getReleaseDate() {
        return releaseDate;
    }

    /**
     * @return the fatalBugsPercent
     */
    public final int getFatalBugsPercent() {
        return fatalBugsPercent;
    }

    /**
     * @return the majorBugsPercent
     */
    public final int getMajorBugsPercent() {
        return majorBugsPercent;
    }

    /**
     * @return the majorBugsPercent
     */
    public final int getMinorBugsPercent() {
        return 100 - (majorBugsPercent + fatalBugsPercent);
    }

    // ======================================================================
    // For hibernate mapping
    // ======================================================================

    protected DaoBatch() {
        super();
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((amount == null) ? 0 : amount.hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((expirationDate == null) ? 0 : expirationDate.hashCode());
        result = prime * result + fatalBugsPercent;
        result = prime * result + majorBugsPercent;
        result = prime * result + ((offer == null) ? 0 : offer.hashCode());
        result = prime * result + secondBeforeValidation;
        return result;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DaoBatch other = (DaoBatch) obj;
        if (amount == null) {
            if (other.amount != null) {
                return false;
            }
        } else if (!amount.equals(other.amount)) {
            return false;
        }
        if (description == null) {
            if (other.description != null) {
                return false;
            }
        } else if (!description.equals(other.description)) {
            return false;
        }
        if (expirationDate == null) {
            if (other.expirationDate != null) {
                return false;
            }
        } else if (!expirationDate.equals(other.expirationDate)) {
            return false;
        }
        if (fatalBugsPercent != other.fatalBugsPercent) {
            return false;
        }
        if (majorBugsPercent != other.majorBugsPercent) {
            return false;
        }
        if (offer == null) {
            if (other.offer != null) {
                return false;
            }
        } else if (!offer.equals(other.offer)) {
            return false;
        }
        if (secondBeforeValidation != other.secondBeforeValidation) {
            return false;
        }
        return true;
    }

}
