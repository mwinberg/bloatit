package com.bloatit.framework.webprocessor.url;

import com.bloatit.framework.utils.parameters.Parameters;

public final class PageNotFoundUrl extends Url implements Cloneable {
    public static String getName() {
        return "pagenotfound";
    }

    public PageNotFoundUrl() {
        super();
    }

    @Override
    public PageNotFoundUrl clone() {
        // this is imutable so ...
        return this;
    }

    @Override
    protected void doConstructUrl(final StringBuilder sb) {
        // nothing to do here. All the work is done in Url.
    }

    @Override
    public void addParameter(final String key, final String value) {
        // nothing to do here. There is no parameters in PageNotFound
    }

    @Override
    public Messages getMessages() {
        return new Messages();
    }

    @Override
    public boolean isAction() {
        return false;
    }

    @Override
    public String getCode() {
        return getName();
    }

    @Override
    protected void doGetParametersAsStrings(final Parameters parameters) {
        // Do nothing. There is no parameter.
    }
}
