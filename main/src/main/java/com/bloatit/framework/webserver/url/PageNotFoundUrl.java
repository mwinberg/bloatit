package com.bloatit.framework.webserver.url;

import com.bloatit.framework.utils.Parameters;
import com.bloatit.framework.utils.SessionParameters;

@SuppressWarnings("unused")
public final class PageNotFoundUrl extends Url implements Cloneable {
    public static String getName() {
        return "pagenotfound";
    }

    public PageNotFoundUrl(final Parameters params, final SessionParameters session) {
        this();
    }

    public PageNotFoundUrl() {
        super(getName());
    }

    @Override
    public PageNotFoundUrl clone() {
        // this is imutable so ...
        return this;
    }

    @Override
    protected void doConstructUrl(StringBuilder sb) {
        // nothing to do here. All the work is done in Url.
    }

    @Override
    public void addParameter(String key, String value) {
        // nothing to do here. There is no parameters in PageNotFound
    }

    @Override
    public Messages getMessages() {
        return new Messages();
    }
}
