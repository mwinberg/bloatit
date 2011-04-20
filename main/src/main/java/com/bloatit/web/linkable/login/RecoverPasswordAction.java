package com.bloatit.web.linkable.login;

import com.bloatit.framework.exceptions.highlevel.MeanUserException;
import com.bloatit.framework.exceptions.highlevel.ShallNotPassException;
import com.bloatit.framework.exceptions.lowlevel.UnauthorizedOperationException;
import com.bloatit.framework.utils.StringUtils;
import com.bloatit.framework.webprocessor.annotations.ParamConstraint;
import com.bloatit.framework.webprocessor.annotations.ParamContainer;
import com.bloatit.framework.webprocessor.annotations.RequestParam;
import com.bloatit.framework.webprocessor.annotations.RequestParam.Role;
import com.bloatit.framework.webprocessor.annotations.tr;
import com.bloatit.framework.webprocessor.context.Context;
import com.bloatit.framework.webprocessor.masters.Action;
import com.bloatit.framework.webprocessor.url.Url;
import com.bloatit.model.Member;
import com.bloatit.model.managers.MemberManager;
import com.bloatit.model.right.AuthToken;
import com.bloatit.web.url.RecoverPasswordActionUrl;
import com.bloatit.web.url.RecoverPasswordPageUrl;

@ParamContainer("password/doreset")
public class RecoverPasswordAction extends Action {
    private final RecoverPasswordActionUrl url;

    private Member member;

    @RequestParam(role = Role.GET)
    private final String resetKey;

    @RequestParam(role = Role.GET)
    private final String login;

    @RequestParam(role = Role.POST)
    @ParamConstraint(optionalErrorMsg = @tr("Password cannot be blank."),//
    min = "7", minErrorMsg = @tr("Number of characters for password has to be superior to %constraint%."),//
    max = "15", maxErrorMsg = @tr("Number of characters for password has to be inferior to %constraint%."))
    private final String newPassword;

    @RequestParam(role = Role.POST)
    @ParamConstraint(optionalErrorMsg = @tr("Password check cannot be blank."),//
    min = "7", minErrorMsg = @tr("Number of characters for password check has to be superior to %constraint%."),//
    max = "15", maxErrorMsg = @tr("Number of characters for password check has to be inferior to %constraint%."))
    private final String checkNewPassword;

    public RecoverPasswordAction(RecoverPasswordActionUrl url) {
        super(url);
        this.url = url;
        this.newPassword = url.getNewPassword();
        this.checkNewPassword = url.getCheckNewPassword();
        this.login = url.getLogin();
        this.resetKey = url.getResetKey();
    }

    @Override
    protected Url doProcess() {
        session.setAuthToken(new AuthToken(member));
        try {
            member.setPassword(newPassword);
        } catch (UnauthorizedOperationException e) {
            throw new ShallNotPassException("Error setting user password.", e);
        }
        session.notifyGood(Context.tr("Password reset successful, you are now logged."));
        return session.pickPreferredPage();
    }

    @Override
    protected Url doProcessErrors() {
        if (StringUtils.isEmpty(login) || StringUtils.isEmpty(resetKey)) {
            throw new MeanUserException("Stop trying to modify poor user password.");
        }
        return new RecoverPasswordPageUrl(resetKey, login);
    }

    @Override
    protected Url checkRightsAndEverything() {
        if (!newPassword.equals(checkNewPassword)) {
            session.notifyBad("Password doesn't match confirmation.");
            return new RecoverPasswordPageUrl(resetKey, login);
        }
        member = MemberManager.getMemberByLogin(login);
        if (member == null || !member.getResetKey().equals(resetKey)) {
            throw new MeanUserException("Stop trying to modify poor user password.");
        }
        return NO_ERROR;
    }

    @Override
    protected void transmitParameters() {
        session.addParameter(url.getNewPasswordParameter());
        session.addParameter(url.getCheckNewPasswordParameter());
    }
}
