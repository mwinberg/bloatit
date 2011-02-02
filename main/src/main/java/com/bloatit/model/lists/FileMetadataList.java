package com.bloatit.model.lists;

import java.util.Iterator;

import com.bloatit.data.DaoFileMetadata;
import com.bloatit.framework.utils.PageIterable;
import com.bloatit.model.FileMetadata;

public final class FileMetadataList extends ListBinder<FileMetadata, DaoFileMetadata> {

    public FileMetadataList(final PageIterable<DaoFileMetadata> daoCollection) {
        super(daoCollection);
    }

    @Override
    protected Iterator<FileMetadata> createFromDaoIterator(final Iterator<DaoFileMetadata> dao) {
        return new FileMetadataIterator(dao);
    }

    static final class FileMetadataIterator extends com.bloatit.model.lists.IteratorBinder<FileMetadata, DaoFileMetadata> {

        public FileMetadataIterator(final Iterable<DaoFileMetadata> daoIterator) {
            super(daoIterator);
        }

        public FileMetadataIterator(final Iterator<DaoFileMetadata> daoIterator) {
            super(daoIterator);
        }

        @Override
        protected FileMetadata createFromDao(final DaoFileMetadata dao) {
            return FileMetadata.create(dao);
        }

    }

}