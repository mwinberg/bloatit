package com.bloatit.data;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import com.bloatit.framework.exceptions.NonOptionalParameterException;

/**
 * This represent an invitation to join a group.
 */
@Entity
public final class DaoJoinGroupInvitation extends DaoIdentifiable {

    /**
     * The state of an invitation track its time line. (First PENDING, then REFUSED or
     * ACCEPTED)
     */
    public enum State {
        ACCEPTED, REFUSED, PENDING
    }

    @ManyToOne(optional = false)
    private DaoMember sender;
    @ManyToOne(optional = false)
    private DaoMember receiver;
    @ManyToOne(optional = false)
    private DaoGroup group;
    @Basic(optional = false)
    @Enumerated
    private State state;

    // ======================================================================
    // Static HQL requests
    // ======================================================================

    public static DaoJoinGroupInvitation getInvitation(final DaoGroup group, final DaoMember member) {
        return (DaoJoinGroupInvitation) SessionManager.createQuery("from DaoJoinGroupInvitation where group = :group and receiver = :member")
                .setEntity("group", group).setEntity("member", member).uniqueResult();
    }

    // ======================================================================
    // Construction.
    // ======================================================================

    public static DaoJoinGroupInvitation createAndPersist(final DaoMember sender, final DaoMember reciever, final DaoGroup group) {
        final Session session = SessionManager.getSessionFactory().getCurrentSession();
        final DaoJoinGroupInvitation joinDemand = new DaoJoinGroupInvitation(sender, reciever, group);
        try {
            session.save(joinDemand);
        } catch (final HibernateException e) {
            session.getTransaction().rollback();
            throw e;
        }
        return joinDemand;
    }

    /**
     * Create a new invitation. Set the state to PENDING.
     *
     * @throws NonOptionalParameterException if any of the parameters are null.
     */
    private DaoJoinGroupInvitation(final DaoMember sender, final DaoMember reciever, final DaoGroup group) {
        super();
        if (sender == null || reciever == null || group == null) {
            throw new NonOptionalParameterException();
        }
        this.sender = sender;
        this.receiver = reciever;
        this.group = group;
        this.state = State.PENDING;
    }

    /**
     * Set the state to accepted and add the receiver into the list of members of
     * this.group. If the state is not PENDING then do nothing.
     */
    public void accept() {
        if (state == State.PENDING) {
            receiver.addToGroup(group, false);
            this.state = State.ACCEPTED;
        }
    }

    /**
     * Set the state to refused. If the state is not PENDING then do nothing.
     */
    public void refuse() {
        if (state == State.PENDING) {
            this.state = State.REFUSED;
        }
    }

    // ======================================================================
    // Getters
    // ======================================================================

    public DaoMember getSender() {
        return sender;
    }

    public DaoMember getReceiver() {
        return receiver;
    }

    public State getState() {
        return state;
    }

    public DaoGroup getGroup() {
        return group;
    }

    // ======================================================================
    // For hibernate mapping
    // ======================================================================

    protected DaoJoinGroupInvitation() {
        super();
    }

    // ======================================================================
    // equals and hashcode
    // ======================================================================

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((group == null) ? 0 : group.hashCode());
        result = prime * result + ((receiver == null) ? 0 : receiver.hashCode());
        result = prime * result + ((sender == null) ? 0 : sender.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof DaoJoinGroupInvitation)) {
            return false;
        }
        final DaoJoinGroupInvitation other = (DaoJoinGroupInvitation) obj;
        if (group == null) {
            if (other.group != null) {
                return false;
            }
        } else if (!group.equals(other.group)) {
            return false;
        }
        if (receiver == null) {
            if (other.receiver != null) {
                return false;
            }
        } else if (!receiver.equals(other.receiver)) {
            return false;
        }
        if (sender == null) {
            if (other.sender != null) {
                return false;
            }
        } else if (!sender.equals(other.sender)) {
            return false;
        }
        return true;
    }

}
