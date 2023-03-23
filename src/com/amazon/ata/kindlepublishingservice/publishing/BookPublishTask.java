package com.amazon.ata.kindlepublishingservice.publishing;

import com.amazon.ata.kindlepublishingservice.dao.CatalogDao;
import com.amazon.ata.kindlepublishingservice.dao.PublishingStatusDao;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.PublishingStatusItem;
import com.amazon.ata.kindlepublishingservice.enums.PublishingRecordStatus;
import com.amazon.ata.kindlepublishingservice.exceptions.BookNotFoundException;
import com.amazon.ata.kindlepublishingservice.utils.KindlePublishingUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;

/**
 * A Runnable that performs no action in its run method. It is currently being scheduled to run repeatedly. This class
 * comes with the service, but should be removed once your publishing task that implements Runnable is created and the
 * providePublishingTask() method is updated to return it in the PublishingModiule.
 */
public class BookPublishTask implements Runnable {
    private static final Logger LOGGER = LogManager.getLogger(BookPublisher.class);
    private final BookPublishRequestManager bookPublishRequestManager;
    private final PublishingStatusDao publishingStatusDao;
    private final CatalogDao catalogDao;

    /**
     * Constructs a BookPublishTask object.
     */
    @Inject
    public BookPublishTask(BookPublishRequestManager bookPublishRequestManager,
                           PublishingStatusDao publishingStatusDao,
                           CatalogDao catalogDao) {
        this.bookPublishRequestManager = bookPublishRequestManager;
        this.publishingStatusDao = publishingStatusDao;
        this.catalogDao = catalogDao;
    }

    @Override
    public void run() {
        LOGGER.info("BookPublish Task executed.");
        BookPublishRequest bookPublishRequest = bookPublishRequestManager.getBookPublishRequestToProcess();
        if (bookPublishRequest != null) {
            LOGGER.info("BookRequest is not null.");
            PublishingStatusItem inProgress = publishingStatusDao.setPublishingStatus(
                                            bookPublishRequest.getPublishingRecordId(),
                                            PublishingRecordStatus.IN_PROGRESS,
                                            bookPublishRequest.getBookId());
            KindleFormattedBook formattedBook = KindleFormatConverter.format(bookPublishRequest);
            CatalogItemVersion book = catalogDao.createOrUpdateBook(formattedBook);
            PublishingStatusItem successful =  publishingStatusDao.setPublishingStatus(
                    bookPublishRequest.getPublishingRecordId(),
                    PublishingRecordStatus.SUCCESSFUL,
                    book.getBookId());
        }
    }
}
