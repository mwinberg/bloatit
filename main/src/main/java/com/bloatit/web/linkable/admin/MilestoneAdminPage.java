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
package com.bloatit.web.linkable.admin;

import static com.bloatit.framework.webprocessor.context.Context.tr;

import java.util.EnumSet;

import com.bloatit.data.DaoBug.Level;
import com.bloatit.data.DaoMilestone;
import com.bloatit.framework.exceptions.highlevel.ShallNotPassException;
import com.bloatit.framework.utils.i18n.DateLocale.FormatStyle;
import com.bloatit.framework.webprocessor.annotations.Optional;
import com.bloatit.framework.webprocessor.annotations.ParamContainer;
import com.bloatit.framework.webprocessor.annotations.RequestParam;
import com.bloatit.framework.webprocessor.components.HtmlParagraph;
import com.bloatit.framework.webprocessor.components.PlaceHolderElement;
import com.bloatit.framework.webprocessor.components.advanced.HtmlGenericTableModel;
import com.bloatit.framework.webprocessor.components.advanced.HtmlGenericTableModel.ColumnGenerator;
import com.bloatit.framework.webprocessor.components.advanced.HtmlGenericTableModel.StringColumnGenerator;
import com.bloatit.framework.webprocessor.components.form.FieldData;
import com.bloatit.framework.webprocessor.components.form.HtmlDropDown;
import com.bloatit.framework.webprocessor.components.meta.HtmlBranch;
import com.bloatit.framework.webprocessor.components.meta.HtmlNode;
import com.bloatit.framework.webprocessor.context.Context;
import com.bloatit.model.Member;
import com.bloatit.model.Milestone;
import com.bloatit.model.Release;
import com.bloatit.model.admin.MilestoneAdminListFactory;
import com.bloatit.model.right.UnauthorizedOperationException;
import com.bloatit.model.right.UnauthorizedPublicAccessException;
import com.bloatit.web.linkable.IndexPage;
import com.bloatit.web.linkable.master.Breadcrumb;
import com.bloatit.web.url.MilestoneAdminPageUrl;

@ParamContainer("admin/milestones")
public final class MilestoneAdminPage extends IdentifiablesAdminPage<DaoMilestone, Milestone, MilestoneAdminListFactory> {

    @RequestParam(role = RequestParam.Role.POST)
    @Optional("NOT_SELECTED")
    private final DisplayableMilestoneState milestoneState;

    private final MilestoneAdminPageUrl url;

    public MilestoneAdminPage(final MilestoneAdminPageUrl url) {
        super(url, new MilestoneAdminListFactory());
        this.url = url;
        milestoneState = url.getMilestoneState();

        if (milestoneState != null && milestoneState != DisplayableMilestoneState.NOT_SELECTED) {
            getFactory().stateEquals(DisplayableMilestoneState.getState(milestoneState));
            Context.getSession().addParameter(url.getMilestoneStateParameter());
        }
    }

    @Override
    protected String createPageTitle() {
        return tr("Administration Kudosable");
    }

    @Override
    public boolean isStable() {
        return true;
    }

    @Override
    protected void addActions(final HtmlDropDown dropDown, final HtmlBranch block) {
        // Add actions into the drop down
        dropDown.addDropDownElements(new AdminActionManager().milestoneActions());
    }

    @Override
    protected void addFormFilters(final HtmlBranch form) {

        final FieldData stateData = url.getMilestoneStateParameter().pickFieldData();
        final HtmlDropDown stateInput = new HtmlDropDown(stateData.getName());
        stateInput.addErrorMessages(stateData.getErrorMessages());
        stateInput.addDropDownElements(EnumSet.allOf(DisplayableMilestoneState.class));
        stateInput.setLabel(tr("Filter by milestone state"));
        form.add(stateInput);
    }

    @Override
    protected void addColumns(final HtmlGenericTableModel<Milestone> tableModel) {
        final MilestoneAdminPageUrl clonedUrl = url.clone();
        clonedUrl.setOrderByStr("milestoneState");
        tableModel.addColumn(clonedUrl.getHtmlLink(tr("milestoneState")), new StringColumnGenerator<Milestone>() {
            @Override
            public String getStringBody(final Milestone element) {
                return String.valueOf(element.getMilestoneState());
            }
        });
        tableModel.addColumn(tr("description"), new StringColumnGenerator<Milestone>() {
            @Override
            public String getStringBody(final Milestone element) {
                return element.getDescription();
            }
        });
        tableModel.addColumn(tr("Release"), new ColumnGenerator<Milestone>() {
            @Override
            public HtmlNode getBody(final Milestone element) {

                final PlaceHolderElement place = new PlaceHolderElement();
                for (final Release release : element.getReleases()) {
                    place.add(new HtmlParagraph(release.getVersion() + " "
                            + Context.getLocalizator().getDate(release.getCreationDate()).toString(FormatStyle.MEDIUM)));
                }
                return place;
            }
        });
        tableModel.addColumn(tr("Should validated"), new ColumnGenerator<Milestone>() {
            @Override
            public HtmlNode getBody(final Milestone element) {
                final PlaceHolderElement place = new PlaceHolderElement();
                for (final Level level : EnumSet.allOf(Level.class)) {
                    try {
                        if (element.partIsValidated(level)) {
                            place.add(new HtmlParagraph(level.toString() + " -> VALIDATED"));
                        } else {
                            if (element.shouldValidatePart(level)) {
                                place.add(new HtmlParagraph(level.toString() + " -> SHOULD"));
                            } else {
                                place.add(new HtmlParagraph(level.toString() + " -> SHOULDN'T"));
                            }
                        }
                    } catch (final UnauthorizedPublicAccessException e) {
                        throw new ShallNotPassException(e);
                    } catch (final UnauthorizedOperationException e) {
                        throw new ShallNotPassException(e);
                    }
                }
                return place;
            }
        });
    }

    @Override
    protected Breadcrumb createBreadcrumb(final Member member) {
        return MilestoneAdminPage.generateBreadcrumb();
    }

    private static Breadcrumb generateBreadcrumb() {
        final Breadcrumb breadcrumb = IndexPage.generateBreadcrumb();

        breadcrumb.pushLink(new MilestoneAdminPageUrl().getHtmlLink(tr("Milestone administration")));

        return breadcrumb;
    }
}
