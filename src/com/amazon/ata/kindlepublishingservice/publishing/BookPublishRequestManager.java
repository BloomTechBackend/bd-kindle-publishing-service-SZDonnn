package com.amazon.ata.kindlepublishingservice.publishing;

import javax.inject.Inject;
import java.util.List;
import java.util.Stack;

public class BookPublishRequestManager {
    Stack<BookPublishRequest> bookPublishRequests = new Stack<>();

    @Inject
    public BookPublishRequestManager() {}

    public void addBookPublishRequest(BookPublishRequest request) {
        bookPublishRequests.push(request);
    }

    public List<BookPublishRequest> getBookPublishRequestToProcess() {
        if (bookPublishRequests.empty()) {
            return null;
        }
        return bookPublishRequests;
    }
}
