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
package com.bloatit.web.actions;

import static com.bloatit.framework.webprocessor.context.Context.tr;

import com.bloatit.framework.exceptions.lowlevel.RedirectException;
import com.bloatit.framework.webprocessor.PageNotFoundException;
import com.bloatit.framework.webprocessor.annotations.ParamContainer;
import com.bloatit.framework.webprocessor.annotations.RequestParam;
import com.bloatit.framework.webprocessor.components.HtmlDiv;
import com.bloatit.framework.webprocessor.components.HtmlTitleBlock;
import com.bloatit.framework.webprocessor.components.form.FieldData;
import com.bloatit.framework.webprocessor.components.form.HtmlFileInput;
import com.bloatit.framework.webprocessor.components.form.HtmlForm;
import com.bloatit.framework.webprocessor.components.form.HtmlSubmit;
import com.bloatit.framework.webprocessor.components.form.HtmlTextField;
import com.bloatit.framework.webprocessor.components.meta.HtmlElement;
import com.bloatit.framework.webprocessor.context.Context;
import com.bloatit.model.Member;
import com.bloatit.model.UserContentInterface;
import com.bloatit.web.components.SideBarUserContentBlock;
import com.bloatit.web.pages.LoggedPage;
import com.bloatit.web.pages.master.Breadcrumb;
import com.bloatit.web.pages.master.sidebar.TwoColumnLayout;
import com.bloatit.web.pages.tools.BreadcrumbTools;
import com.bloatit.web.url.AddAttachementActionUrl;
import com.bloatit.web.url.AddAttachementPageUrl;

/**
 * Page that hosts the form to create a new feature
 */
@ParamContainer("usercontent/attachfile")
public final class AddAttachementPage extends LoggedPage {

    @SuppressWarnings("rawtypes")
    @RequestParam(name = "user_content")
    UserContentInterface userContent;

    private final AddAttachementPageUrl url;

    public AddAttachementPage(final AddAttachementPageUrl url) {
        super(url);
        this.url = url;
        userContent = url.getUserContent();
    }

    @Override
    protected String createPageTitle() {
        return tr("Add an attachment to the release");
    }

    @Override
    public boolean isStable() {
        return false;
    }

    @Override
    public void processErrors() throws RedirectException {
        // TODO we should process the errors here.
    }

    @Override
    public HtmlElement createRestrictedContent(final Member loggedUser) throws PageNotFoundException {
        addNotifications(url.getMessages());
        if (!url.getMessages().isEmpty()) {
            throw new PageNotFoundException();
        }
        final TwoColumnLayout layout = new TwoColumnLayout(true, url);
        layout.addRight(new SideBarUserContentBlock(userContent));

        layout.addLeft(generateReleaseCreationForm());

        return layout;
    }

    private HtmlElement generateReleaseCreationForm() {
        final HtmlTitleBlock title = new HtmlTitleBlock(tr("Add a new attachment"), 1);

        final AddAttachementActionUrl formUrl = new AddAttachementActionUrl(userContent);

        // Create the form stub
        final HtmlForm form = new HtmlForm(formUrl.urlString());
        form.enableFileUpload();

        title.add(form);

        // attachment
        final FieldData attachmentDescriptiondData = formUrl.getAttachementDescriptionParameter().pickFieldData();
        final HtmlTextField attachmentDescriptionInput = new HtmlTextField(attachmentDescriptiondData.getName(),
                                                                            Context.tr("Attachment description"));
        attachmentDescriptionInput.setDefaultValue(attachmentDescriptiondData.getSuggestedValue());
        attachmentDescriptionInput.addErrorMessages(attachmentDescriptiondData.getErrorMessages());
        attachmentDescriptionInput.setComment(tr("Mandatory"));
        form.add(attachmentDescriptionInput);

        final FieldData attachedFileData = formUrl.getAttachementParameter().pickFieldData();
        final HtmlFileInput attachedFileInput = new HtmlFileInput(attachedFileData.getName(), tr("Attached file"));
        attachedFileInput.setDefaultValue(attachedFileData.getSuggestedValue());
        attachedFileInput.addErrorMessages(attachedFileData.getErrorMessages());
        attachedFileInput.setComment("You must attach a file. Maximum size is 3MB");
        form.add(attachedFileInput);

        form.add(new HtmlSubmit(tr("Submit")));

        final HtmlDiv group = new HtmlDiv();
        group.add(title);
        return group;
    }

    @Override
    protected Breadcrumb createBreadcrumb() {
        return AddAttachementPage.generateBreadcrumb(userContent);
    }

    public static Breadcrumb generateBreadcrumb(final UserContentInterface<?> userContent) {
        final Breadcrumb breadcrumb = BreadcrumbTools.generateBreadcrumb(userContent);
        breadcrumb.pushLink(new AddAttachementPageUrl(userContent).getHtmlLink(tr("Add an attachment")));
        return breadcrumb;
    }

    @Override
    public String getRefusalReason() {
        return tr("You must be logged to add a new attachment.");
    }
}
