/*
 * Copyright (C) 2010 BloatIt. This file is part of BloatIt. BloatIt is free software: you
 * can redistribute it and/or modify it under the terms of the GNU Affero General Public
 * License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version. BloatIt is distributed in the hope that it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details. You should have received a copy of the GNU Affero General
 * Public License along with BloatIt. If not, see <http://www.gnu.org/licenses/>.
 */
package com.bloatit.model.demand;

import com.bloatit.data.DBRequests;
import com.bloatit.data.DaoDemand;
import com.bloatit.data.DaoDemand.DemandState;
import com.bloatit.framework.utils.PageIterable;
import com.bloatit.model.AuthToken;
import com.bloatit.model.Demand;

public final class DemandManager {

    private DemandManager() {
        // Desactivate default ctor
    }

    public static PageIterable<Demand> getDemands() {
        return new DemandList(DBRequests.getAllUserContentOrderByDate(DaoDemand.class));
    }

    public static PageIterable<Demand> getDemands(DemandState state, boolean hasSelectedOffer) {
        return new DemandList(DBRequests.getDemands(state, null, null, null, null, null, null, null, hasSelectedOffer, null, null, null, null, null,//
                                                    null,
                                                    null,
                                                    null));
    }

    public static Demand getDemandById(final Integer id) {
        return getDemandImplementationById(id);
    }

    static DemandImplementation getDemandImplementationById(final Integer id) {
        return DemandImplementation.create(DBRequests.getById(DaoDemand.class, id));
    }

    public static int getDemandsCount() {
        return DBRequests.count(DaoDemand.class);
    }

    // Can create if authenticated.
    public static boolean canCreate(final AuthToken authToken) {
        return authToken != null;
    }

}
