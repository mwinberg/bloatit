package com.bloatit.mail;

import static com.bloatit.framework.webprocessor.context.Context.tr;

import java.text.NumberFormat;

import com.bloatit.data.DaoFeature.FeatureState;
import com.bloatit.framework.utils.i18n.Localizator;
import com.bloatit.framework.webprocessor.components.HtmlDiv;
import com.bloatit.framework.webprocessor.components.HtmlImage;
import com.bloatit.framework.webprocessor.components.HtmlLink;
import com.bloatit.framework.webprocessor.components.HtmlTitle;
import com.bloatit.framework.webprocessor.components.meta.HtmlBranch;
import com.bloatit.framework.webprocessor.components.meta.HtmlNode;
import com.bloatit.model.Feature;
import com.bloatit.model.Image;
import com.bloatit.web.WebConfiguration;
import com.bloatit.web.components.MoneyDisplayComponent;
import com.bloatit.web.linkable.features.FeatureTabPane.FeatureTabKey;
import com.bloatit.web.url.FeaturePageUrl;
import com.bloatit.web.url.FileResourceUrl;
import com.bloatit.web.url.SoftwarePageUrl;

public class EventFeatureComponent extends HtmlDiv {

    private final HtmlDiv entriesDiv;

    public EventFeatureComponent(final Feature f, final Localizator l) {
        this(f, l, false);
    }

    public EventFeatureComponent(final Feature f, final Localizator l, final boolean hook) {
        super("event-feature");

        final HtmlDiv progress = new HtmlDiv("event-progress");
        super.add(progress);
        final HtmlDiv topBorder = new HtmlDiv("event-top-border");
        progress.add(topBorder);
        float progression = f.getProgression();
        if (progression == Float.POSITIVE_INFINITY) {
            progression = 0;
        }
        if (progression > 100) {
            progression = 100;
        }

        if (progression < 5 && progression > 0) {
            progression = 5;
        }
        final HtmlDiv progressBar = new HtmlDiv("event-progress-bar");
        progress.add(progressBar);

        progressBar.add(new HtmlDiv("event-progress-background"));
        progressBar.add(new HtmlDiv("event-progress-filled").addAttribute("style", "width: " + String.valueOf(progression) + "%;"));
        final HtmlDiv progressBarText = new HtmlDiv("event-progress-text");
        progressBar.add(progressBarText);
        if (f.getFeatureState() == FeatureState.PENDING) {
            progressBarText.add(new HtmlDiv("event-progress-money").add(new MoneyDisplayComponent(f.getContribution(), l)).addText(l.tr("financed")));
            progressBarText.add(new HtmlDiv("event-progress-no-offer").addText(l.tr("no offer")));
        } else if (f.getFeatureState() == FeatureState.PREPARING) {
            progressBarText.add(new HtmlDiv("event-progress-money").add(new MoneyDisplayComponent(f.getContribution(), l)).addText(l.tr("financed")));
            final NumberFormat format = l.getNumberFormat();
            format.setMinimumFractionDigits(0);
            progressBarText.add(new HtmlDiv("event-progress-percent").addText(format.format(f.getProgression()) + " %"));
            progressBarText.add(new HtmlDiv("event-progress-pledged").add(new MoneyDisplayComponent(f.getSelectedOffer().getAmount(), l))
                                                                     .addText(l.tr("requested")));
        } else if (f.getFeatureState() == FeatureState.DEVELOPPING) {
            progressBarText.add(new HtmlDiv("event-progress-money").add(new MoneyDisplayComponent(f.getContribution(), l)).addText(l.tr("financed")));
            progressBarText.add(new HtmlDiv("event-progress-developing").addText(l.tr("In development")));
        } else if (f.getFeatureState() == FeatureState.FINISHED) {
            progressBarText.add(new HtmlDiv("event-progress-money").add(new MoneyDisplayComponent(f.getContribution(), l)).addText(l.tr("financed")));
            progressBarText.add(new HtmlDiv("event-progress-success").addText(l.tr("success")));
            progressBar.add(new HtmlDiv("event-progress-filled-success"));
        } else if (f.getFeatureState() == FeatureState.DISCARDED) {
        	progressBarText.add(new HtmlDiv("event-progress-no-offer").addText(l.tr("discarded")));
        }

        final HtmlDiv logodiv = new HtmlDiv("software_logo_block");
        if (f.getSoftware() == null || f.getSoftware().getImage() == null) {
            logodiv.add(new HtmlImage(new Image(WebConfiguration.getImgSoftwareNoLogo()), tr("Software logo"), "software_logo"));
        } else {
            final FileResourceUrl imageUrl = new FileResourceUrl(f.getSoftware().getImage());
            final HtmlLink softwareLink = new SoftwarePageUrl(f.getSoftware()).getHtmlLink();
            logodiv.add(softwareLink);
            softwareLink.add(new HtmlImage(imageUrl, l.tr("Software logo"), "software_logo"));
        }

        super.add(new HtmlDiv("event-feature-logo").add(logodiv));

        final HtmlDiv description = new HtmlDiv("event-feature-description");
        super.add(description);

        description.add(new HtmlTitle(2).add(new FeaturePageUrl(f, FeatureTabKey.description).getHtmlLink(f.getTitle(l.getLocale()))));
        entriesDiv = new HtmlDiv("event-feature-entries");
        description.add(entriesDiv);

        if (hook) {
            super.add(new HtmlDiv("event-feature-hook-point"));
            super.add(new HtmlDiv("event-feature-hook-line"));
            super.add(new HtmlDiv("event-feature-hook-curve"));
        }

    }

    @Override
    public HtmlBranch add(final HtmlNode html) {
        return entriesDiv.add(html);
    }

}
