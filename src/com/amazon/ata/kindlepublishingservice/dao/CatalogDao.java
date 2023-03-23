package com.amazon.ata.kindlepublishingservice.dao;

import com.amazon.ata.kindlepublishingservice.dynamodb.models.CatalogItemVersion;
import com.amazon.ata.kindlepublishingservice.exceptions.BookNotFoundException;

import com.amazon.ata.kindlepublishingservice.publishing.KindleFormattedBook;
import com.amazon.ata.kindlepublishingservice.utils.KindlePublishingUtils;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;

import java.util.List;
import javax.inject.Inject;

public class CatalogDao {
    private final DynamoDBMapper dynamoDbMapper;


    /**
     * Instantiates a new CatalogDao object.
     *
     * @param dynamoDbMapper The {@link DynamoDBMapper} used to interact with the catalog table.
     */
    @Inject
    public CatalogDao(DynamoDBMapper dynamoDbMapper) {
        this.dynamoDbMapper = dynamoDbMapper;
    }

    public void removeBookFromCatalog(CatalogItemVersion book) {
        if (book == null) {
            throw new BookNotFoundException("Book not found or empty.");
        }
        book.setInactive(true);
        dynamoDbMapper.save(book);
    }

    /**
     * Returns the latest version of the book from the catalog corresponding to the specified book id.
     * Throws a BookNotFoundException if the latest version is not active or no version is found.
     * @param bookId Id associated with the book.
     * @return The corresponding CatalogItem from the catalog table.
     */
    public CatalogItemVersion getBookFromCatalog(String bookId) {
        CatalogItemVersion book = getLatestVersionOfBook(bookId);
        if (book.isInactive()) {
            throw new BookNotFoundException(String.format("No book found for id: %s", bookId));
        }
        return book;
    }

    public void validateBookExists(String bookId) {
        CatalogItemVersion book = getLatestVersionOfBook(bookId);
        if (book == null) {
            throw new BookNotFoundException(String.format("No book found for id: %s", bookId));
        }
    }

    public CatalogItemVersion createOrUpdateBook(KindleFormattedBook formattedBook) {
        String bookId = formattedBook.getBookId();
        if (bookId == null || bookId.isEmpty()) {
            bookId = KindlePublishingUtils.generateBookId();
            CatalogItemVersion createBook = new CatalogItemVersion();
            createBook.setBookId(bookId);
            createBook.setTitle(formattedBook.getTitle());
            createBook.setAuthor(formattedBook.getAuthor());
            createBook.setText(formattedBook.getText());
            createBook.setGenre(formattedBook.getGenre());
            createBook.setVersion(1);
            createBook.setInactive(false);
            dynamoDbMapper.save(createBook);
            return createBook;
        } else {
            CatalogItemVersion existingBook = getLatestVersionOfBook(bookId);
            existingBook.setVersion(1);
            existingBook.setInactive(true);
            dynamoDbMapper.save(existingBook);

            CatalogItemVersion updatedBook = new CatalogItemVersion();
            updatedBook.setBookId(bookId);
            updatedBook.setTitle(formattedBook.getTitle());
            updatedBook.setAuthor(formattedBook.getAuthor());
            updatedBook.setText(formattedBook.getText());
            updatedBook.setGenre(formattedBook.getGenre());
            updatedBook.setVersion(existingBook.getVersion()+1);
            updatedBook.setInactive(false);
            dynamoDbMapper.save(updatedBook);
            return updatedBook;
        }
    }
    // Returns null if no version exists for the provided bookId
    private CatalogItemVersion getLatestVersionOfBook(String bookId) {
        CatalogItemVersion book = new CatalogItemVersion();
        book.setBookId(bookId);

        DynamoDBQueryExpression<CatalogItemVersion> queryExpression = new DynamoDBQueryExpression()
            .withHashKeyValues(book)
            .withScanIndexForward(false)
            .withLimit(1);

        List<CatalogItemVersion> results = dynamoDbMapper.query(CatalogItemVersion.class, queryExpression);
        if (results.isEmpty()) {
            throw new BookNotFoundException("Book not found.");
        }
        return results.get(0);
    }

}
