package com.bloatit.framework;

import java.util.Date;

import com.bloatit.common.PageIterable;
import com.bloatit.framework.lists.FileMetadataList;
import com.bloatit.model.data.DaoUserContent;

public abstract class UserContent extends Identifiable {

    protected abstract DaoUserContent getDaoUserContent();

    public final Member getAuthor() {
        return Member.create(getDaoUserContent().getAuthor());
    }

    public final Date getCreationDate() {
        return getDaoUserContent().getCreationDate();
    }

    public final void setAsGroup(final Group asGroup) {
        getDaoUserContent().setAsGroup(asGroup.getDao());
    }

    public final Group getAsGroup() {
        return Group.create(getDaoUserContent().getAsGroup());
    }

    @Override
    public final int getId() {
        return getDaoUserContent().getId();
    }

    public PageIterable<FileMetadata> getFiles() {
        return new FileMetadataList(getDaoUserContent().getFiles());
    }

}
