package com.amazon.ata.kindlepublishingservice.publishing;

import com.amazon.ata.kindlepublishingservice.dao.CatalogDao;
import com.amazon.ata.kindlepublishingservice.dao.PublishingStatusDao;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.enums.PublishingRecordStatus;
import com.amazon.ata.kindlepublishingservice.utils.KindlePublishingUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;

public class BookPublishTask implements Runnable {
    private static final Logger LOGGER = LogManager.getLogger(BookPublisher.class);
    private final BookPublishRequestManager bookPublishRequestManager;
    private final PublishingStatusDao publishingStatusDao;
    private final CatalogDao catalogDao;

    @Inject
    public BookPublishTask(PublishingStatusDao publishingStatusDao,
                           CatalogDao catalogDao) {
        this.publishingStatusDao = publishingStatusDao;
        this.catalogDao = catalogDao;
        this.bookPublishRequestManager = new BookPublishRequestManager(publishingStatusDao, catalogDao);
    }

    public void publishTask() {
        System.out.println("PUBLISH TASK VOID METHOD.");
    }

    @Override
    public void run() {
        LOGGER.info("BookPublish Task executed.");
        this.publishTask();
    }
}
