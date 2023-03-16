package com.amazon.ata.kindlepublishingservice.publishing;

import com.amazon.ata.kindlepublishingservice.dao.CatalogDao;
import com.amazon.ata.kindlepublishingservice.dao.PublishingStatusDao;

import javax.inject.Inject;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class BookPublishRequestManager {
    private final ConcurrentLinkedQueue<BookPublishRequest> bookPublishRequests = new ConcurrentLinkedQueue<>();
    private final PublishingStatusDao publishingStatusDao;
    private final CatalogDao catalogDao;
    @Inject
    public BookPublishRequestManager(PublishingStatusDao publishingStatusDao, CatalogDao catalogDao) {
        this.publishingStatusDao = publishingStatusDao;
        this.catalogDao = catalogDao;
    }

    public void addBookPublishRequest(BookPublishRequest request) {
        bookPublishRequests.add(request);
    }

    public BookPublishRequest getBookPublishRequestToProcess() {
        if (bookPublishRequests.isEmpty()) {
            return null;
        }
        return bookPublishRequests.remove();
    }
}
