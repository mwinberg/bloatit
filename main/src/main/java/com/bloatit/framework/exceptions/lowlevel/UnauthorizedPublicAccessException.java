package com.bloatit.framework.exceptions.lowlevel;

import com.bloatit.model.right.Action;

public class UnauthorizedPublicAccessException extends UnauthorizedOperationException {

    private static final long serialVersionUID = -2718286565324742260L;

    public UnauthorizedPublicAccessException(Action action) {
        super(action);
    }

}
