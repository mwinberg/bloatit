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
package com.bloatit.web.linkable.money;

import com.bloatit.framework.webprocessor.annotations.ParamContainer;
import com.bloatit.framework.webprocessor.annotations.ParamContainer.Protocol;
import com.bloatit.framework.webprocessor.url.Url;
import com.bloatit.model.ElveosUserToken;
import com.bloatit.web.actions.PaymentProcess;
import com.bloatit.web.actions.WebProcess;
import com.bloatit.web.url.AccountChargingPageUrl;
import com.bloatit.web.url.AccountChargingProcessUrl;
import com.bloatit.web.url.AccountPageUrl;

@ParamContainer(value="account/charging/process", protocol=Protocol.HTTPS)
public class AccountChargingProcess extends PaymentProcess {

    @SuppressWarnings("unused")
    private final AccountChargingProcessUrl url;

    public AccountChargingProcess(final AccountChargingProcessUrl url) {
        super(url);
        this.url = url;
    }

    @Override
    protected Url doProcess(ElveosUserToken userToken) {
        return new AccountChargingPageUrl(this);
    }

    @Override
    public Url notifyChildClosed(final WebProcess subProcess) {
        if (subProcess.getClass().equals(PaylineProcess.class)) {
            final PaylineProcess subPro = (PaylineProcess) subProcess;
            if (subPro.isSuccessful()) {
                // Redirects to the contribution action which will perform the
                // actual contribution

                AccountPageUrl accountPageUrl = new AccountPageUrl();
                if(getTeam() != null) {
                    accountPageUrl.setTeam(getTeam());
                }
                return accountPageUrl;
            }
            unlock();
            return new AccountChargingPageUrl(this);
        }
        return null;
    }

    @Override
    public void doDoLoad() {
        // Nothing to do.
    }

}
