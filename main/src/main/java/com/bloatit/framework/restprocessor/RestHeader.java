package com.bloatit.framework.restprocessor;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.bloatit.common.Log;
import com.bloatit.framework.utils.parameters.Parameters;
import com.bloatit.framework.xcgiserver.HttpHeader;

/**
 * <p>
 * Encapsulates all information from the HttpHeader when used by ReST protocol
 * </p>
 */
public class RestHeader {
    private static final String UTF_8 = "UTF-8";

    private String resourceName = "";
    private final Parameters parameters = new Parameters();
    private final HttpHeader httpHeader;

    /**
     * Constructs a new RestHeader based on a given <code>HttpHeader</code>
     *
     * @param httpHeader the <code>HttpHeader</code> containing all informations
     *            of the Http request
     * @see HttpHeader
     */
    public RestHeader(HttpHeader httpHeader) {
        this.httpHeader = httpHeader;
        try {
            // Extract language
            String pathInfo = httpHeader.getPathInfo();
            if (pathInfo.startsWith("/") && pathInfo.length() > 1) {
                resourceName = URLDecoder.decode(pathInfo.substring(1), UTF_8);
            }

            // Extract get params
            for (final String param : httpHeader.getQueryString().split("&")) {
                final String[] pair = param.split("=");
                if (pair.length >= 2) {
                    parameters.add(URLDecoder.decode(pair[0], UTF_8), URLDecoder.decode(pair[1], UTF_8));
                }
            }

        } catch (final UnsupportedEncodingException ex) {
            Log.framework().error("Cannot parse url", ex);
        }

    }

    /**
     * <p>
     * Example : <br />
     * <code>
     * http://elveos.com/rest/directory/id/action?parameter=value <br />
     * ==>    directory/id/action
     * </code>
     * </p>
     *
     * @return the name of the resource
     */
    public final String getResourceName() {
        return resourceName;
    }

    /**
     * <p>
     * Finds the list of parameters (after the <code>?</code>)
     * </p>
     * <p>
     * Example : <br />
     * <code>
     * http://elveos.com/rest/directory/id/action?parameter1=value1&parameter2=value2 <br />
     * ==>    {[parameter1 : value1][parameter2 : value2]}
     * </code>
     * </p>
     *
     * @return the list of parameters
     */
    public final Parameters getParameters() {
        return parameters;
    }

    /**
     * @return the underlying <code>HttpHeader</code>
     */
    public HttpHeader getHttpHeader() {
        return httpHeader;
    }

}