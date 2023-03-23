package com.amazon.ata.kindlepublishingservice.publishing;

import javax.inject.Inject;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BookPublishRequestManager {
    private Queue<BookPublishRequest> bookPublishRequests;

    @Inject
    public BookPublishRequestManager() {
        this.bookPublishRequests = new ConcurrentLinkedQueue<>();
    }

    public void addBookPublishRequest(BookPublishRequest bookPublishRequest) {
        this.bookPublishRequests.add(bookPublishRequest);
    }

    public BookPublishRequest getBookPublishRequestToProcess() {
        if (bookPublishRequests.isEmpty()) return null;
        System.out.println("Size === " + bookPublishRequests.size());
        return bookPublishRequests.remove();
    }
}
