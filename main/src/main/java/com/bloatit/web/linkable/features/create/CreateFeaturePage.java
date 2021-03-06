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
package com.bloatit.web.linkable.features.create;

import static com.bloatit.framework.webprocessor.context.Context.tr;

import com.bloatit.data.DaoTeamRight.UserTeamRight;
import com.bloatit.framework.webprocessor.annotations.ParamContainer;
import com.bloatit.framework.webprocessor.components.HtmlTitleBlock;
import com.bloatit.framework.webprocessor.components.advanced.showdown.MarkdownEditor;
import com.bloatit.framework.webprocessor.components.form.FieldData;
import com.bloatit.framework.webprocessor.components.form.FormBuilder;
import com.bloatit.framework.webprocessor.components.form.HtmlFormField;
import com.bloatit.framework.webprocessor.components.form.HtmlSubmit;
import com.bloatit.framework.webprocessor.components.form.HtmlTextField;
import com.bloatit.framework.webprocessor.components.meta.HtmlElement;
import com.bloatit.framework.webprocessor.context.Context;
import com.bloatit.model.Member;
import com.bloatit.web.components.HtmlElveosForm;
import com.bloatit.web.components.SidebarMarkdownHelp;
import com.bloatit.web.linkable.documentation.SideBarDocumentationBlock;
import com.bloatit.web.linkable.features.FeatureListPage;
import com.bloatit.web.linkable.master.Breadcrumb;
import com.bloatit.web.linkable.master.sidebar.TwoColumnLayout;
import com.bloatit.web.linkable.softwares.SoftwaresTools;
import com.bloatit.web.linkable.usercontent.AsTeamField;
import com.bloatit.web.linkable.usercontent.CreateUserContentPage;
import com.bloatit.web.url.CreateFeatureActionUrl;
import com.bloatit.web.url.CreateFeaturePageUrl;

/**
 * Page that hosts the form to create a new Feature
 */
@ParamContainer("feature/create")
public final class CreateFeaturePage extends CreateUserContentPage {

    public static final int SPECIF_INPUT_NB_LINES = 20;
    public static final int SPECIF_INPUT_NB_COLUMNS = 100;
    public static final int FILE_MAX_SIZE_MIO = 2;

    private final CreateFeaturePageUrl url;

    public CreateFeaturePage(final CreateFeaturePageUrl url) {
        super(url);
        this.url = url;
    }

    @Override
    protected String createPageTitle() {
        return Context.tr("Create new feature");
    }

    @Override
    public boolean isStable() {
        return false;
    }

    @Override
    public HtmlElement createRestrictedContent(final Member loggedUser) {
        return generateFeatureCreationForm(loggedUser);
    }

    private HtmlElement generateFeatureCreationForm(final Member loggedUser) {
        final TwoColumnLayout layout = new TwoColumnLayout(true, url);

        final HtmlTitleBlock createFeatureTitle = new HtmlTitleBlock(tr("Create a new feature"), 1);
        final CreateFeatureActionUrl targetUrl = new CreateFeatureActionUrl(getSession().getShortKey());

        // Create the form stub
        final HtmlElveosForm form = new HtmlElveosForm(targetUrl.urlString());
        form.enableFileUpload();
        createFeatureTitle.add(form);

        form.addLanguageChooser(targetUrl.getLocaleParameter().getName(), Context.getLocalizator().getLanguageCode());
        form.addAsTeamField(new AsTeamField(targetUrl,
                                            loggedUser,
                                            UserTeamRight.TALK,
                                            tr("In the name of"),
                                            tr("You can create this feature in the name of a team.")));

        FormBuilder formBuilder = new FormBuilder(CreateFeatureAction.class, targetUrl);

        formBuilder.add(form, new HtmlTextField(targetUrl.getDescriptionParameter().getName()));

        // Linked software
        final FieldData newSoftwareNameFD = targetUrl.getNewSoftwareNameParameter().pickFieldData();
        final FieldData newSoftwareFD = targetUrl.getNewSoftwareParameter().pickFieldData();
        final SoftwaresTools.SoftwareChooserElement software = new SoftwaresTools.SoftwareChooserElement(targetUrl.getSoftwareParameter().getName(),
                                                                                                         newSoftwareNameFD.getName(),
                                                                                                         newSoftwareFD.getName());
        formBuilder.add(form, software);
        if (newSoftwareNameFD.getSuggestedValue() != null) {
            software.setNewSoftwareDefaultValue(newSoftwareNameFD.getSuggestedValue());
        }
        if (newSoftwareFD.getSuggestedValue() != null) {
            software.setNewSoftwareCheckboxDefaultValue(newSoftwareFD.getSuggestedValue());
        }

        //@formatter:off
        final String suggestedValue = tr(
                "Be precise, don't forget to specify :\n" +
                " - The expected result\n" +
                " - On which system it has to work (Windows/Mac/Linux ...)\n" +
                " - When do you want to have the result\n" +
                " - In which free license the result must be.\n" +
                "\n" +
                "You can also join a diagram, or a design/mockup of the expected user interface.\n" +
                "\n" +
                "Do not forget to specify if you want the result to be integrated upstream (in the official version of the software)"
                );
        //@formatter:on
        HtmlFormField specifInput = formBuilder.add(form, new MarkdownEditor(targetUrl.getSpecificationParameter().getName(), 10, 80));
        formBuilder.setDefaultValueIfNeeded(specifInput, suggestedValue);

        // Submit button
        form.addSubmit(new HtmlSubmit(tr("submit")));

        layout.addLeft(createFeatureTitle);

        // RightColunm
        layout.addRight(new SideBarDocumentationBlock("create_feature"));
        layout.addRight(new SideBarDocumentationBlock("cc_by"));
        layout.addRight(new SidebarMarkdownHelp());

        return layout;
    }

    @Override
    public String getRefusalReason() {
        return tr("You must be logged to create a new feature.");
    }

    @Override
    protected Breadcrumb createBreadcrumb(final Member member) {
        return CreateFeaturePage.generateBreadcrumb();
    }

    private static Breadcrumb generateBreadcrumb() {
        final Breadcrumb breadcrumb = FeatureListPage.generateBreadcrumb();
        breadcrumb.pushLink(new CreateFeaturePageUrl().getHtmlLink(tr("Create a feature")));
        return breadcrumb;
    }
}
