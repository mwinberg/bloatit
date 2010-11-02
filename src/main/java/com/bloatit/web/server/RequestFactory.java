/*
 * Copyright (C) 2010 BloatIt.
 * 
 * This file is part of BloatIt.
 * 
 * BloatIt is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * BloatIt is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with BloatIt. If not, see <http://www.gnu.org/licenses/>.
 */
package com.bloatit.web.server;

import java.lang.reflect.Constructor;
import java.util.Map;

import com.bloatit.common.FatalErrorException;

public class RequestFactory {

    static public Request build(Class<? extends Request> requestClass, Session session, Map<String, String> parameters) {

        Request request = null;

        try {
            final Constructor<? extends Request> constructor = requestClass.getConstructor(Session.class, Map.class);

            request = constructor.newInstance(session, parameters);

        } catch (final Exception ex) {
            throw new FatalErrorException("Request factory failed to build " + requestClass.getName(), ex);
        }

        return request;
    }
}