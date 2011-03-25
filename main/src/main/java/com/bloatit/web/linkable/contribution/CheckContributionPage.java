/*
 * Copyright (C) 2010 BloatIt. This file is part of BloatIt. BloatIt is free
 * software: you can redistribute it and/or modify it under the terms of the GNU
 * Affero General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * BloatIt is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details. You should have received a copy of the GNU Affero General Public
 * License along with BloatIt. If not, see <http://www.gnu.org/licenses/>.
 */
package com.bloatit.web.linkable.contribution;

import static com.bloatit.framework.webserver.Context.tr;

import java.math.BigDecimal;

import com.bloatit.common.Log;
import com.bloatit.framework.exceptions.RedirectException;
import com.bloatit.framework.exceptions.UnauthorizedOperationException;
import com.bloatit.framework.webserver.Context;
import com.bloatit.framework.webserver.annotations.ParamConstraint;
import com.bloatit.framework.webserver.annotations.ParamContainer;
import com.bloatit.framework.webserver.annotations.RequestParam;
import com.bloatit.framework.webserver.annotations.tr;
import com.bloatit.framework.webserver.components.HtmlLink;
import com.bloatit.framework.webserver.components.HtmlParagraph;
import com.bloatit.framework.webserver.components.HtmlTitleBlock;
import com.bloatit.framework.webserver.components.meta.HtmlElement;
import com.bloatit.model.Feature;
import com.bloatit.model.Member;
import com.bloatit.model.Payline;
import com.bloatit.web.components.SideBarFeatureBlock;
import com.bloatit.web.linkable.features.FeaturePage;
import com.bloatit.web.linkable.features.FeaturesTools;
import com.bloatit.web.linkable.money.HtmlQuotation;
import com.bloatit.web.linkable.money.Quotation;
import com.bloatit.web.linkable.money.Quotation.QuotationAmountEntry;
import com.bloatit.web.linkable.money.Quotation.QuotationDifferenceEntry;
import com.bloatit.web.linkable.money.Quotation.QuotationPercentEntry;
import com.bloatit.web.linkable.money.Quotation.QuotationProxyEntry;
import com.bloatit.web.linkable.money.Quotation.QuotationTotalEntry;
import com.bloatit.web.pages.LoggedPage;
import com.bloatit.web.pages.master.Breadcrumb;
import com.bloatit.web.pages.master.DefineParagraph;
import com.bloatit.web.pages.master.TwoColumnLayout;
import com.bloatit.web.url.AccountChargingProcessUrl;
import com.bloatit.web.url.CheckContributionPageUrl;
import com.bloatit.web.url.ContributePageUrl;
import com.bloatit.web.url.ContributionActionUrl;
import com.bloatit.web.url.FeaturePageUrl;
import com.bloatit.web.url.PaylineActionUrl;

/**
 * A page that hosts the form used to check the contribution on a Feature
 */
@ParamContainer("contribute/check")
public final class CheckContributionPage extends LoggedPage {

    @RequestParam
    @ParamConstraint(optionalErrorMsg = @tr("The process is closed, expired, missing or invalid."))
    private final ContributionProcess process;

    private final CheckContributionPageUrl url;

    public CheckContributionPage(final CheckContributionPageUrl url) {
        super(url);
        this.url = url;
        process = url.getProcess();

    }

    @Override
    public void processErrors() throws RedirectException {
        addNotifications(url.getMessages());
        if (url.getMessages().hasMessage()) {
            session.notifyList(url.getMessages());
            throw new RedirectException(Context.getSession().getLastStablePage());
        }

    }

    @Override
    public HtmlElement createRestrictedContent() throws RedirectException {

        final TwoColumnLayout layout = new TwoColumnLayout(true);
        layout.addLeft(generateCheckContributeForm());

        layout.addRight(new SideBarFeatureBlock(process.getFeature()));

        return layout;
    }

    public HtmlElement generateCheckContributeForm() throws RedirectException {
        final HtmlTitleBlock group = new HtmlTitleBlock("Check contribution", 1);

        try {

            Member member = Context.getSession().getAuthToken().getMember();
            BigDecimal account = member.getInternalAccount().getAmount();

            if (process.getAmount().compareTo(account) <= 0) {

                group.add(new DefineParagraph(tr("Target feature: "), FeaturesTools.getTitle(process.getFeature())));

                group.add(new DefineParagraph(tr("Contribution amount: "), Context.getLocalizator()
                                                                                  .getCurrency(process.getAmount())
                                                                                  .getDefaultString()));

                if (process.getComment() != null) {
                    group.add(new DefineParagraph(tr("Comment: "), process.getComment()));
                } else {
                    group.add(new DefineParagraph(tr("Comment: "), "No comment"));
                }

                group.add(new DefineParagraph(tr("Author: "), member.getDisplayName()));
                group.add(new DefineParagraph(tr("Available money: "), Context.getLocalizator().getCurrency(account).getDecimalDefaultString()));

                // enought money

                BigDecimal accountAfter = account.subtract(process.getAmount());

                group.add(new DefineParagraph(tr("Money after contribution: "), Context.getLocalizator()
                                                                                       .getCurrency(accountAfter)
                                                                                       .getDecimalDefaultString()));

                ContributionActionUrl contributionActionUrl = new ContributionActionUrl(process);
                HtmlLink confirmContributionLink = contributionActionUrl.getHtmlLink(tr("Confirm contribution"));
                confirmContributionLink.setCssClass("button");
                group.add(confirmContributionLink);

            } else {

                generateNoMoneyContent(group, account);

            }

        } catch (UnauthorizedOperationException e) {
            Log.web().error("Fail to check contribution", e);
            throw new RedirectException(new FeaturePageUrl(process.getFeature()));
        }

        // Modify contribution button
        ContributePageUrl contributePageUrl = new ContributePageUrl(process);
        HtmlLink modifyContributionLink = contributePageUrl.getHtmlLink(tr("Modify contribution"));
        modifyContributionLink.setCssClass("button");

        group.add(modifyContributionLink);

        return group;
    }

    public void generateNoMoneyContent(final HtmlTitleBlock group, BigDecimal account) {
        BigDecimal missingAmount = process.getAmount().subtract(account);

        session.setTargetPage(url);


        Quotation quotation = generateQuotationModel(missingAmount);
        HtmlQuotation quotationBlock = new HtmlQuotation(quotation);
        group.add(quotationBlock);

        final PaylineActionUrl payActionUrl = new PaylineActionUrl();
        payActionUrl.setAmount(missingAmount);

        HtmlLink payContributionLink = payActionUrl.getHtmlLink(tr("Pay"));
        payContributionLink.setCssClass("button");

        group.add(payContributionLink);

        HtmlParagraph chargeAccountPara = new HtmlParagraph(tr("You can also pay now more money for future contributions."));
        group.add(chargeAccountPara);

        final AccountChargingProcessUrl accountChargingProcess = new AccountChargingProcessUrl();
        accountChargingProcess.setAmount(missingAmount);

        HtmlLink accountChargingProcessLink = accountChargingProcess.getHtmlLink(tr("Charge account"));
        accountChargingProcessLink.setCssClass("button");
        group.add(accountChargingProcessLink);

    }

    private Quotation generateQuotationModel(BigDecimal amount) {


        String fixBank = "0.30";
        String variableBank = "0.03";

        Quotation quotation = new Quotation();

        QuotationTotalEntry contributionTotal = new QuotationTotalEntry("Contributions", null, "Total before fees");
        QuotationAmountEntry missingAmount = new QuotationAmountEntry("Missing amount", null, amount);

        QuotationAmountEntry prepaid = new QuotationAmountEntry("Prepaid", null, new BigDecimal(0));
        contributionTotal.addEntry(missingAmount);
        contributionTotal.addEntry(prepaid);
        quotation.getRootEntry().addEntry(contributionTotal);

        QuotationTotalEntry feesTotal = new QuotationTotalEntry(null, null, null);

        QuotationPercentEntry percentFeesTotal = new QuotationPercentEntry("Fees", null, contributionTotal, Payline.COMMISSION_VARIABLE_RATE);

        QuotationAmountEntry fixfeesTotal = new QuotationAmountEntry("Fees", null, Payline.COMMISSION_FIX_RATE);
        feesTotal.addEntry(percentFeesTotal);
        feesTotal.addEntry(fixfeesTotal);

        QuotationProxyEntry feesProxy = new QuotationProxyEntry("Fees", ""+Payline.COMMISSION_VARIABLE_RATE.multiply(new BigDecimal(100))+"% + "+Payline.COMMISSION_FIX_RATE+"€", feesTotal);

        // Fees details
        // Bank fees
        QuotationTotalEntry bankFeesTotal = new QuotationTotalEntry("Bank fees", null, "Total bank fees");

        QuotationAmountEntry fixBankFee = new QuotationAmountEntry("Fix fee", null, new BigDecimal(fixBank));

        QuotationPercentEntry variableBankFee = new QuotationPercentEntry("Variable fee", ""+Float.valueOf(variableBank)*100+"%", quotation.getRootEntry(), new BigDecimal(variableBank));
        bankFeesTotal.addEntry(variableBankFee);
        bankFeesTotal.addEntry(fixBankFee);
        feesProxy.addEntry(bankFeesTotal);

        // Our fees
        QuotationDifferenceEntry commissionTTC = new QuotationDifferenceEntry("Elveos's commission TTC", null, feesTotal, bankFeesTotal);


        QuotationPercentEntry commissionHT = new QuotationPercentEntry("Commission HT", null, commissionTTC, new BigDecimal(1/1.196));
        QuotationDifferenceEntry ourFeesTVA = new QuotationDifferenceEntry("TVA for commission", "19.6%", commissionTTC, commissionHT);
        commissionTTC.addEntry(commissionHT);
        commissionTTC.addEntry(ourFeesTVA);
        feesProxy.addEntry(commissionTTC);

        quotation.getRootEntry().addEntry(feesProxy);

        quotation.getRootEntry().print(0);

        System.out.println("Rendement: "
                + commissionHT.getValue()
                              .divide(quotation.getRootEntry().getValue(), BigDecimal.ROUND_HALF_EVEN)
                              .multiply(new BigDecimal(100))
                              .setScale(2, BigDecimal.ROUND_HALF_EVEN));

        quotation.check(Payline.computateAmountToPay(amount));

        return quotation;
    }


    @Override
    protected String getPageTitle() {
        return tr("Contribute to a feature - check");
    }

    @Override
    public boolean isStable() {
        return false;
    }

    @Override
    public String getRefusalReason() {
        return tr("You must be logged to contribute");
    }

    @Override
    protected Breadcrumb getBreadcrumb() {
        return CheckContributionPage.generateBreadcrumb(process.getFeature(), process);
    }

    public static Breadcrumb generateBreadcrumb(Feature feature, ContributionProcess process) {
        Breadcrumb breadcrumb = FeaturePage.generateBreadcrumbContributions(feature);

        breadcrumb.pushLink(new CheckContributionPageUrl(process).getHtmlLink(tr("Contribute - Check")));

        return breadcrumb;
    }

}
