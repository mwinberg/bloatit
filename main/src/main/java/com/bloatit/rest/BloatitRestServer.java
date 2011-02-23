package com.bloatit.rest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.bloatit.framework.rest.RestServer;

public class BloatitRestServer extends RestServer {
    private Map<String, Class<?>> locations = new HashMap<String, Class<?>>() {
        private static final long serialVersionUID = -5012179845511358309L;

        {
            put("members", Member.class);
        }
    };

    private Class<?>[] classes = new Class<?>[] { Member.class, Members.class };

    @Override
    protected Set<String> getResourcesDirectories() {
        HashSet<String> directories = new HashSet<String>();
        directories.add("rest");
        return directories;
    }

    @Override
    protected Class<?> getClass(String forResource) {
        return locations.get(forResource);
    }

    @Override
    protected boolean isValidResource(String forResource) {
        return locations.containsKey(forResource);
    }

    @Override
    protected Class<?>[] getJAXClasses() {
        return classes;
    }

    @Override
    public boolean initialize() {
        return true;
    }
}
