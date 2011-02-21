package com.bloatit.framework.webserver.components.meta;

import java.util.Collections;
import java.util.Iterator;

import com.bloatit.framework.exceptions.NonOptionalParameterException;
import com.bloatit.framework.webserver.components.writers.QueryResponseStream;

public class XmlText extends XmlNode {
    protected String content;

    protected XmlText() {
        super();
    }

    /**
     * Creates a component to add raw Html to a page
     * 
     * @param content the Html string to add
     */
    public XmlText(final String content) {
        super();
        if (content == null) {
            throw new NonOptionalParameterException();
        }
        this.content = content;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final Iterator<XmlNode> iterator() {
        return Collections.EMPTY_LIST.iterator();
    }

    /**
     * Do not use Only present as a quick hack to write a tad cleaner html
     * content
     */
    public String _getContent() {
        return content;
    }

    @Override
    public final void write(final QueryResponseStream txt) {
        txt.writeRawText(content);
    }
}
