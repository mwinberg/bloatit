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

import java.util.Locale;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.RandomStringUtils;

import com.bloatit.data.DaoExternalAccount;
import com.bloatit.data.DaoFileMetadata;
import com.bloatit.data.DaoInternalAccount;
import com.bloatit.data.DaoJoinTeamInvitation;
import com.bloatit.data.DaoJoinTeamInvitation.State;
import com.bloatit.data.DaoMember;
import com.bloatit.data.DaoMember.Role;
import com.bloatit.data.DaoTeam.Right;
import com.bloatit.data.DaoTeamRight.UserTeamRight;
import com.bloatit.data.DaoUserContent;
import com.bloatit.framework.exceptions.lowlevel.MalformedArgumentException;
import com.bloatit.framework.exceptions.lowlevel.NonOptionalParameterException;
import com.bloatit.framework.exceptions.lowlevel.UnauthorizedOperationException;
import com.bloatit.framework.exceptions.lowlevel.UnauthorizedOperationException.SpecialCode;
import com.bloatit.framework.utils.Image;
import com.bloatit.framework.utils.PageIterable;
import com.bloatit.framework.utils.SecuredHash;
import com.bloatit.framework.webprocessor.context.User;
import com.bloatit.model.feature.FeatureList;
import com.bloatit.model.lists.CommentList;
import com.bloatit.model.lists.ContributionList;
import com.bloatit.model.lists.JoinTeamInvitationList;
import com.bloatit.model.lists.KudosList;
import com.bloatit.model.lists.OfferList;
import com.bloatit.model.lists.TeamList;
import com.bloatit.model.lists.TranslationList;
import com.bloatit.model.lists.UserContentList;
import com.bloatit.model.right.Action;
import com.bloatit.model.right.AuthToken;
import com.bloatit.model.right.MemberRight;

public final class Member extends Actor<DaoMember> implements User {

    // /////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTION
    // /////////////////////////////////////////////////////////////////////////////////////////

    private static final int PASSWORD_SALT_LENGTH = 50;
    private static final String RESET_SALT = "GSQISUDHOI1232193IOSDJHOQIOOISQJD";
    private static final String ACTIVATE_SALT = "1Z9UI901IE09II90I31JD091J09DJ01KPO";

    private static final class MyCreator extends Creator<DaoMember, Member> {
        @SuppressWarnings("synthetic-access")
        @Override
        public Member doCreate(final DaoMember dao) {
            return new Member(dao);
        }
    }

    /**
     * Create a new member using its Dao version.
     * 
     * @param dao a DaoMember
     * @return the new member or null if dao is null.
     */
    @SuppressWarnings("synthetic-access")
    public static Member create(final DaoMember dao) {
        return new MyCreator().create(dao);
    }

    /**
     * Create a new DaoActor. Initialize the creation date to now. Create a new
     * {@link DaoInternalAccount} and a new {@link DaoExternalAccount}.
     * 
     * @param login is the login or name of this actor. It must be non null,
     *            unique, longer than 2 chars and do not contains space chars
     *            ("[^\\p{Space}]+").
     * @throws NonOptionalParameterException if login or mail is null.
     * @throws MalformedArgumentException if the login is to small or contain
     *             space chars.
     */
    private static DaoMember createDaoMember(final String login, final String password, final String email, final Locale locale) {
        final String salt = RandomStringUtils.randomAscii(PASSWORD_SALT_LENGTH);
        final String passwd = SecuredHash.calculateHash(password, salt);
        return DaoMember.createAndPersist(login, passwd, salt, email, locale);
    }

    public Member(final String login, final String password, final String email, final Locale locale) {
        super(createDaoMember(login, password, email, locale));

    }

    public Member(final String login, final String password, final String email, final String fullname, final Locale locale) {
        this(login, password, email, locale);
        getDao().setFullname(fullname);
    }

    private Member(final DaoMember dao) {
        super(dao);
    }

    // /////////////////////////////////////////////////////////////////////////////////////////
    // Accessors
    // /////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Tells if a user can access the team property. You have to unlock this
     * Member using the {@link Member#authenticate(AuthToken)} method.
     * 
     * @param action can be read/write/delete. for example use READ to know if
     *            you can use {@link Member#getTeams()}.
     * @return true if you can use the method.
     */
    public boolean canAccessTeams(final Action action) {
        return canAccess(new MemberRight.TeamList(), action);
    }

    public boolean canGetKarma() {
        return canAccess(new MemberRight.Karma(), Action.READ);
    }

    public boolean canAccessName(final Action action) {
        return canAccess(new MemberRight.Name(), action);
    }

    public boolean canSetPassword() {
        return canAccess(new MemberRight.Password(), Action.WRITE);
    }

    public boolean canAccessLocale(final Action action) {
        return canAccess(new MemberRight.Locale(), action);
    }

    // /////////////////////////////////////////////////////////////////////////////////////////
    // Setter / modification
    // /////////////////////////////////////////////////////////////////////////////////////////

    // / TEAM RIGHTS

    // /////////////////////////////////////////////////////////////////////////////////////////
    // Accessors
    // /////////////////////////////////////////////////////////////////////////////////////////

    public boolean hasTeamRight(final Team aTeam, final UserTeamRight aRight) {
        if (getTeamRights(aTeam) == null) {
            return false;
        }
        return getTeamRights(aTeam).contains(aRight);
    }

    public Set<UserTeamRight> getTeamRights(final Team g) {
        return getDao().getTeamRights(g.getDao());
    }

    public boolean hasConsultTeamRight(final Team aTeam) {
        return hasTeamRight(aTeam, UserTeamRight.CONSULT);
    }

    public boolean hasTalkTeamRight(final Team aTeam) {
        return hasTeamRight(aTeam, UserTeamRight.TALK);
    }

    public boolean hasInviteTeamRight(final Team aTeam) {
        return hasTeamRight(aTeam, UserTeamRight.INVITE);
    }

    public boolean hasModifyTeamRight(final Team aTeam) {
        return hasTeamRight(aTeam, UserTeamRight.MODIFY);
    }

    public boolean hasPromoteTeamRight(final Team aTeam) {
        return hasTeamRight(aTeam, UserTeamRight.PROMOTE);
    }

    public boolean hasBankTeamRight(final Team aTeam) {
        return hasTeamRight(aTeam, UserTeamRight.BANK);
    }

    public boolean canBeKickFromTeam(final Team aTeam, final Member actor) {
        if (actor == null) {
            return false;
        }

        if (this.equals(actor)) {
            if (hasPromoteTeamRight(aTeam)) {
                return false;
            }

            return true;
        }

        if (!actor.hasPromoteTeamRight(aTeam)) {
            return false;
        }

        return true;
    }

    // / END TEAM RIGHTS

    /**
     * Adds a user to a team without checking if the team is Public or not
     * 
     * @param team the team to which the user will be added
     */
    protected void addToTeamUnprotected(final Team team) {
        getDao().addToTeam(team.getDao());
    }

    /**
     * To invite a member into a team you have to have the WRITE right on the
     * "invite" property.
     * 
     * @param member The member you want to invite
     * @param team The team in which you invite a member.
     * @throws UnauthorizedOperationException
     */
    public void sendInvitation(final Member member, final Team team) throws UnauthorizedOperationException {
        if (!hasInviteTeamRight(team)) {
            throw new UnauthorizedOperationException(SpecialCode.INVITATION_SEND_NO_RIGHT);
        }
        DaoJoinTeamInvitation.createAndPersist(getDao(), member.getDao(), team.getDao());
    }

    /**
     * To accept an invitation you must have the DELETED right on the "invite"
     * property. If the invitation is not in PENDING state then nothing is done,
     * and <i>false</i> is returned.
     * 
     * @param invitation the authenticate member must be receiver of the
     *            invitation.
     * @return true if the invitation is accepted, false if there is an error.
     * @throws UnauthorizedOperationException
     */
    public boolean acceptInvitation(final JoinTeamInvitation invitation) throws UnauthorizedOperationException {
        if (!invitation.getReciever().getId().equals(getAuthToken().getMember().getId())) {
            throw new UnauthorizedOperationException(SpecialCode.INVITATION_RECIEVER_MISMATCH);
        }

        // Accept the invitation
        if (invitation.accept()) {
            // discard all other invitation to join the same team
            final Team team = invitation.getTeam();
            final PageIterable<JoinTeamInvitation> receivedInvitation = this.getReceivedInvitation(State.PENDING, team);
            for (final JoinTeamInvitation invite : receivedInvitation) {
                invite.discard();
            }
            return true;
        }
        return false;
    }

    /**
     * To refuse an invitation you must have the DELETED right on the "invite"
     * property. If the invitation is not in PENDING state then nothing is done.
     * 
     * @param invitation the authenticate member must be receiver of the
     *            invitation.
     * @throws UnauthorizedOperationException
     */
    public void refuseInvitation(final JoinTeamInvitation invitation) throws UnauthorizedOperationException {
        if (!invitation.getReciever().getId().equals(getAuthToken().getMember().getId())) {
            throw new UnauthorizedOperationException(SpecialCode.INVITATION_RECIEVER_MISMATCH);
        }
        invitation.refuse();
    }

    /**
     * To remove this member from a team you have to have the DELETED right on
     * the "team" property. If the member is not in the "team", nothing is done.
     * (Although it should be considered as an error and will be logged)
     * 
     * @param aTeam is the team from which the user will be removed.
     * @throws UnauthorizedOperationException
     */
    public void kickFromTeam(final Team aTeam, final Member actor) throws UnauthorizedOperationException {
        if (!canBeKickFromTeam(aTeam, actor)) {
            throw new UnauthorizedOperationException(SpecialCode.TEAM_PROMOTE_RIGHT_MISSING);
        }
        getDao().removeFromTeam(aTeam.getDao());
    }

    /**
     * Updates user password with right checking
     * 
     * @param password the new password
     * @throws UnauthorizedOperationException when the logged user cannot modify
     *             the password
     */
    public void setPassword(final String password) throws UnauthorizedOperationException {
        tryAccess(new MemberRight.Password(), Action.WRITE);
        setPasswordUnprotected(password);
    }

    /**
     * Updates user password without checking rights
     * 
     * @param password the new password
     */
    public void setPasswordUnprotected(final String password) {
        getDao().setPassword(SecuredHash.calculateHash(password, getDao().getSalt()));
    }

    public void setLocal(final Locale loacle) throws UnauthorizedOperationException {
        tryAccess(new MemberRight.Locale(), Action.WRITE);
        getDao().setLocale(loacle);
    }

    // TODO Right management
    public void setRole(final Role role) {
        getDao().setRole(role);
    }

    public void activate() {
        getDao().setActivationState(ActivationState.ACTIVE);
    }

    /**
     * To add a user into a public team, you have to make sure you can access
     * the teams with the {@link Action#WRITE} action.
     * 
     * @param team must be a public team.
     * @throws UnauthorizedOperationException if the authenticated member do not
     *             have the right to use this methods.
     * @see Member#canAccessTeams(Action)
     */
    public void addToPublicTeam(final Team team) throws UnauthorizedOperationException {
        if (team.getRight() != Right.PUBLIC) {
            throw new UnauthorizedOperationException(SpecialCode.TEAM_NOT_PUBLIC);
        }
        getDao().addToTeam(team.getDao());
    }

    // /////////////////////////////////////////////////////////////////////////////////////////
    // Getters
    // /////////////////////////////////////////////////////////////////////////////////////////

    /**
     * @param state can be PENDING, ACCEPTED or REFUSED
     * @return all the received invitation with the specified state.
     */
    public PageIterable<JoinTeamInvitation> getReceivedInvitation(final State state) {
        return new JoinTeamInvitationList(getDao().getReceivedInvitation(state));
    }

    /**
     * @param state can be PENDING, ACCEPTED or REFUSED
     * @param team the team invited to join
     * @return all the received invitation with the specified state and team
     */
    public PageIterable<JoinTeamInvitation> getReceivedInvitation(final State state, final Team team) {
        return new JoinTeamInvitationList(getDao().getReceivedInvitation(state, team.getDao()));
    }

    /**
     * @param state can be PENDING, ACCEPTED or REFUSED
     * @return all the sent invitation with the specified state.
     */
    public PageIterable<DaoJoinTeamInvitation> getSentInvitation(final State state) {
        return getDao().getSentInvitation(state);
    }

    /**
     * To get the teams you have the have the READ right on the "team" property.
     * 
     * @return all the team in which this member is.
     * @throws UnauthorizedOperationException
     */
    public PageIterable<Team> getTeams() throws UnauthorizedOperationException {
        tryAccess(new MemberRight.TeamList(), Action.READ);
        return new TeamList(getDao().getTeams());
    }

    public int getKarma() throws UnauthorizedOperationException {
        tryAccess(new MemberRight.Karma(), Action.READ);
        return getDao().getKarma();
    }

    public PageIterable<UserContent<? extends DaoUserContent>> getActivity() {
        return new UserContentList(getDao().getActivity());
    }

    private static final float INFLUENCE_MULTIPLICATOR = 2;
    private static final float INFLUENCE_DIVISER = 100;
    private static final float INFLUENCE_BASE = 1;

    protected int calculateInfluence() {
        final int karma = getDao().getKarma();
        if (karma > 0) {
            return (int) (Math.log10((INFLUENCE_DIVISER + karma) / INFLUENCE_DIVISER) * INFLUENCE_MULTIPLICATOR + INFLUENCE_BASE);
        } else if (karma == 0) {
            return 1;
        }
        return 0;
    }

    @Override
    public String getDisplayName() throws UnauthorizedOperationException {
        tryAccess(new MemberRight.Name(), Action.READ);
        if (getDao().getFullname() != null && !getDao().getFullname().isEmpty()) {
            return getFullname();
        }
        return getLogin();
    }

    public boolean canAccessEmail(final Action action) {
        return canAccess(new MemberRight.Email(), action);
    }

    public String getEmail() throws UnauthorizedOperationException {
        tryAccess(new MemberRight.Email(), Action.READ);
        return getEmailUnprotected();
    }

    public String getEmailUnprotected() {
        return getDao().getEmail();
    }

    public void setEmail(final String email) throws UnauthorizedOperationException {
        tryAccess(new MemberRight.Email(), Action.WRITE);
        getDao().setEmail(email);
    }

    @Override
    public String getUserLogin() {
        return getLoginUnprotected();
    }

    @Override
    public Locale getUserLocale() {
        return getLocaleUnprotected();
    }

    public Locale getLocaleUnprotected() {
        return getDao().getLocale();
    }

    public Locale getLocale() throws UnauthorizedOperationException {
        // TODO delete one of those methods
        tryAccess(new MemberRight.Locale(), Action.READ);
        return getDao().getLocale();
    }

    public String getFullname() throws UnauthorizedOperationException {
        tryAccess(new MemberRight.Name(), Action.READ);
        return getDao().getFullname();
    }

    public void setFullname(final String fullname) throws UnauthorizedOperationException {
        tryAccess(new MemberRight.Name(), Action.WRITE);
        getDao().setFullname(fullname);
    }

    public PageIterable<Feature> getFeatures(final boolean asMemberOnly) {
        return new FeatureList(getDao().getFeatures(asMemberOnly));
    }

    public PageIterable<Kudos> getKudos() {
        return new KudosList(getDao().getKudos());
    }

    @Override
    public PageIterable<Contribution> getContributions() throws UnauthorizedOperationException {
        return getContributions(true);
    }

    public PageIterable<Contribution> getContributions(final boolean asMemberOnly) throws UnauthorizedOperationException {
        tryAccess(new MemberRight.Contributions(), Action.READ);
        return new ContributionList(getDao().getContributions(asMemberOnly));
    }

    public PageIterable<Comment> getComments(final boolean asMemberOnly) {
        return new CommentList(getDao().getComments(asMemberOnly));
    }

    public PageIterable<Offer> getOffers(final boolean asMemberOnly) {
        return new OfferList(getDao().getOffers(asMemberOnly));
    }

    public PageIterable<Translation> getTranslations(final boolean asMemberOnly) {
        return new TranslationList(getDao().getTranslations(asMemberOnly));
    }

    public boolean isInTeam(final Team team) {
        return isInTeamUnprotected(team);
    }

    protected boolean isInTeamUnprotected(final Team team) {
        return getDao().isInTeam(team.getDao());
    }

    protected void addToKarma(final int value) {
        getDao().addToKarma(value);
    }

    public Role getRole() {
        return getDao().getRole();
    }

    /*
     * (non-Javadoc)
     * @see com.bloatit.model.User#getActivationState()
     */
    @Override
    public ActivationState getActivationState() {
        return getDao().getActivationState();
    }

    public String getActivationKey() {
        final DaoMember m = getDao();
        final String digest = "" + m.getId() + m.getEmail() + m.getFullname() + m.getPassword() + m.getSalt() + ACTIVATE_SALT;
        return DigestUtils.sha256Hex(digest);
    }

    public String getResetKey() {
        final DaoMember m = getDao();
        final String digest = "" + m.getId() + m.getEmail() + m.getFullname() + m.getPassword() + m.getSalt() + RESET_SALT;
        return DigestUtils.sha256Hex(digest);
    }

    @Override
    public Image getAvatar() {
        final DaoFileMetadata avatar = getDao().getAvatar();
        if (avatar != null) {
            return new Image(FileMetadata.create(avatar));
        }
        String libravatar = null;
        libravatar = libravatar(getDao().getEmail().toLowerCase().trim());
        if (libravatar == null) {
            return null;
        }
        return new Image(libravatar);
    }

    private String libravatar(final String email) {
        final String digest = DigestUtils.md5Hex(email.toLowerCase());
        // return "http://cdn.libravatar.org/avatar/" + digest +
        // "?d=http://elveos.org/resources/commons/img/none.png&s=64";
        return digest;
    }

    public void setAvatar(final FileMetadata fileImage) {
        // TODO: right management
        getDao().setAvatar(fileImage.getDao());
    }

    @Override
    public <ReturnType> ReturnType accept(final ModelClassVisitor<ReturnType> visitor) {
        return visitor.visit(this);
    }

    /**
     * Checks if an inputed password matches the user password
     * 
     * @param password the password to match
     * @return <i>true</i> if the inputed password matches the password in the
     *         database, <i>false</i> otherwise
     */
    public boolean checkPassword(final String password) {
        final String digestedPassword = SecuredHash.calculateHash(password, getDao().getSalt());
        return getDao().passwordEquals(digestedPassword);
    }
}
