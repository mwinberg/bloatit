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

package com.bloatit.rest.resources;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.bloatit.data.DaoContribution.ContributionState;
import com.bloatit.framework.restprocessor.RestElement;
import com.bloatit.framework.restprocessor.RestServer.RequestMethod;
import com.bloatit.framework.restprocessor.annotations.REST;
import com.bloatit.framework.restprocessor.exception.RestException;
import com.bloatit.framework.xcgiserver.HttpReponseField.StatusCode;
import com.bloatit.model.Contribution;
import com.bloatit.model.managers.ContributionManager;
import com.bloatit.model.right.UnauthorizedOperationException;
import com.bloatit.rest.adapters.DateAdapter;
import com.bloatit.rest.list.RestContributionList;

/**
 * <p>
 * Representation of a Contribution for the ReST RPC calls
 * </p>
 * <p>
 * This class should implement any methods from Contribution that needs to be
 * called through the ReST RPC. Every such method needs to be mapped with the
 * {@code @REST} interface.
 * <p>
 * ReST uses the four HTTP request methods <code>GET</code>, <code>POST</code>,
 * <code>PUT</code>, <code>DELETE</code> each with their own meaning. Please
 * only bind the according to the following:
 * <li>GET list: List the URIs and perhaps other details of the collection's
 * members.</li>
 * <li>GET list/id: Retrieve a representation of the addressed member of the
 * collection, expressed in an appropriate Internet media type.</li>
 * <li>POST list: Create a new entry in the collection. The new entry's URL is
 * assigned automatically and is usually returned by the operation.</li>
 * <li>POST list/id: Treat the addressed member as a collection in its own right
 * and create a new entry in it.</li>
 * <li>PUT list: Replace the entire collection with another collection.</li>
 * <li>PUT list/id: Replace the addressed member of the collection, or if it
 * doesn't exist, create it.</li>
 * <li>DELETE list: Delete the entire collection.</li>
 * <li>DELETE list/id: Delete the addressed member of the collection.</li>
 * </p>
 * </p>
 * <p>
 * This class will be serialized as XML (or maybe JSON who knows) to be sent
 * over to the client RPC. Hence this class needs to be annotated to indicate
 * which methods (and/or fields) are to be matched in the XML data. For this
 * use:
 * <li>@XmlRootElement at the root of the class</li>
 * <li>@XmlElement on each method/attribute that will yield <i>complex</i> data</li>
 * <li>@XmlAttribute on each method/attribute that will yield <i>simple</i> data
 * </li>
 * <li>Methods that return a list need to be annotated with @XmlElement and to
 * return a RestContributionList</li>
 * </p>
 */
@XmlRootElement(name = "contribution")
@XmlAccessorType(XmlAccessType.NONE)
public class RestContribution extends RestElement<Contribution> {
    private Contribution model;

    // ---------------------------------------------------------------------------------------
    // -- Constructors
    // ---------------------------------------------------------------------------------------

    /**
     * Provided for JAXB
     */
    @SuppressWarnings("unused")
    private RestContribution() {
        super();
    }

    protected RestContribution(final Contribution model) {
        this.model = model;
    }

    // ---------------------------------------------------------------------------------------
    // -- Static methods
    // ---------------------------------------------------------------------------------------

    /**
     * <p>
     * Finds the RestContribution matching the <code>id</code>
     * </p>
     * 
     * @param id the id of the RestContribution
     */
    @REST(name = "contributions", method = RequestMethod.GET)
    public static RestContribution getById(final int id) {
        final RestContribution restContribution = new RestContribution(ContributionManager.getById(id));
        if (restContribution.isNull()) {
            return null;
        }
        return restContribution;
    }

    /**
     * <p>
     * Finds the list of all (valid) RestContribution
     * </p>
     */
    @REST(name = "contributions", method = RequestMethod.GET)
    public static RestContributionList getAll() {
        return new RestContributionList(ContributionManager.getAll());
    }

    // ---------------------------------------------------------------------------------------
    // -- XML Getters
    // ---------------------------------------------------------------------------------------

    @XmlAttribute
    @XmlID
    public String getId() {
        return model.getId().toString();
    }

    /**
     * @see com.bloatit.model.Contribution#getComment()
     */
    @XmlElement
    public String getComment() throws RestException {
        try {
            return model.getComment();
        } catch (final UnauthorizedOperationException e) {
            throw new RestException(StatusCode.ERROR_CLI_405_METHOD_NOT_ALLOWED, "Not allowed to use getComment on Contribution", e);
        }
    }

    /**
     * @see com.bloatit.model.Contribution#getAmount()
     */
    @XmlAttribute
    public BigDecimal getAmount() throws RestException {
        return model.getAmount();
    }

    @XmlAttribute
    public ContributionState getState() {
        return model.getState();
    }

    /**
     * @see com.bloatit.model.UserContent#getCreationDate()
     */
    @XmlAttribute
    @XmlJavaTypeAdapter(DateAdapter.class)
    public Date getCreationDate() {
        return model.getCreationDate();
    }

    /**
     * @see com.bloatit.model.UserContent#getMember()
     */
    @XmlAttribute
    @XmlIDREF
    public RestMember getAuthor() {
        return new RestMember(model.getMember());
    }

    /**
     * @see com.bloatit.model.UserContent#getAsTeam()
     */
    @XmlElement
    @XmlIDREF
    public RestTeam getAsTeam() {
        final RestTeam restTeam = new RestTeam(model.getAsTeam());
        if (restTeam.isNull()) {
            return null;
        }
        return restTeam;
    }

    // ---------------------------------------------------------------------------------------
    // -- Utils
    // ---------------------------------------------------------------------------------------

    /**
     * Provided for JAXB
     */
    void setModel(final Contribution model) {
        this.model = model;
    }

    /**
     * Package method to find the model
     */
    Contribution getModel() {
        return model;
    }

    @Override
    public boolean isNull() {
        return (model == null);
    }

}
