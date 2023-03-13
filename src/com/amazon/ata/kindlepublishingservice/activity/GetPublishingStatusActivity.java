package com.amazon.ata.kindlepublishingservice.activity;

import com.amazon.ata.kindlepublishingservice.dao.PublishingStatusDao;
import com.amazon.ata.kindlepublishingservice.dynamodb.models.PublishingStatusItem;
import com.amazon.ata.kindlepublishingservice.exceptions.PublishingStatusNotFoundException;
import com.amazon.ata.kindlepublishingservice.models.PublishingStatusRecord;
import com.amazon.ata.kindlepublishingservice.models.requests.GetPublishingStatusRequest;
import com.amazon.ata.kindlepublishingservice.models.response.GetPublishingStatusResponse;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class GetPublishingStatusActivity {
    private PublishingStatusDao publishingStatusDao;
    @Inject
    public GetPublishingStatusActivity(PublishingStatusDao publishingStatusDao) {
        this.publishingStatusDao = publishingStatusDao;
    }

    public GetPublishingStatusResponse execute(GetPublishingStatusRequest publishingStatusRequest) {
        List<PublishingStatusItem> publishingStatusItemList =
                publishingStatusDao.getPublishingStatuses(publishingStatusRequest.getPublishingRecordId());

        if (publishingStatusItemList.isEmpty()) {
            throw new PublishingStatusNotFoundException("No publishing status not found.");
        }

        List<PublishingStatusRecord> publishingStatusRecordList = new ArrayList<>();

        for (PublishingStatusItem item : publishingStatusItemList) {
            PublishingStatusRecord publishingStatusRecord =
                    new PublishingStatusRecord(
                            item.getStatus().toString(),
                            item.getStatusMessage(),
                            item.getBookId());
            publishingStatusRecordList.add(publishingStatusRecord);
        }

        return GetPublishingStatusResponse.builder()
                .withPublishingStatusHistory(publishingStatusRecordList)
                .build();
    }
}
