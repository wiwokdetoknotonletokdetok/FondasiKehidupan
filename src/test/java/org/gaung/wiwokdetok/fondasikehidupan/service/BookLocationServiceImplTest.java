package org.gaung.wiwokdetok.fondasikehidupan.service;

import org.gaung.wiwokdetok.fondasikehidupan.dto.AmqpUserPointsMessage;
import org.gaung.wiwokdetok.fondasikehidupan.dto.BookLocationRequest;
import org.gaung.wiwokdetok.fondasikehidupan.dto.BookLocationResponse;
import org.gaung.wiwokdetok.fondasikehidupan.dto.UpdateBookLocationRequest;
import org.gaung.wiwokdetok.fondasikehidupan.model.Book;
import org.gaung.wiwokdetok.fondasikehidupan.model.BookLocation;
import org.gaung.wiwokdetok.fondasikehidupan.publisher.UserPointsPublisher;
import org.gaung.wiwokdetok.fondasikehidupan.repository.BookLocationRepository;
import org.gaung.wiwokdetok.fondasikehidupan.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookLocationServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookLocationRepository bookLocationRepository;

    @Mock
    private UserPointsPublisher userPointsPublisher;

    @InjectMocks
    private BookLocationServiceImpl bookLocationService;

    private UUID userId;

    private Book book;

    private BookLocation bookLocation;

    @BeforeEach
    public void setUp() {
        userId = UUID.randomUUID();

        book = new Book();
        book.setTitle("Book Title");
        book.setIsbn("978-0-306-40615-7");
        book.setSynopsis("Book Synopsis");
        book.setBookPicture("https://example.com");

        GeometryFactory gf = new GeometryFactory();
        Point point =  gf.createPoint(new Coordinate(106.82861863734638, -6.364475653741472));

        bookLocation = new BookLocation();
        bookLocation.setBook(book);
        bookLocation.setLocationName("Book Location");
        bookLocation.setLocation(point);
    }

    @Test
    void testGetBookLocationSuccess() {
        UUID bookId = book.getId();
        double latitude = -6.3702510913875;
        double longitude = 106.82742266305259;
        double distance = 0.0;

        List<Object[]> mockResult = new ArrayList<>();
        mockResult.add(new Object[]{bookLocation.getId(), bookLocation.getLocationName(),
                bookLocation.getLocation().getY(), bookLocation.getLocation().getX(), distance});

        when(bookRepository.findById(bookId))
                .thenReturn(Optional.of(book));

        when(bookLocationRepository.findAllNearestBookLocations(bookId, latitude, longitude))
                .thenReturn(mockResult);

        List<BookLocationResponse> response = bookLocationService.getBookLocations(bookId, latitude, longitude);

        BookLocationResponse result = response.getFirst();
        assertEquals(bookLocation.getId(), result.getId());
        assertEquals(bookLocation.getLocationName(), result.getLocationName());
        assertEquals(bookLocation.getLocation().getY(), result.getCoordinates().getFirst());
        assertEquals(bookLocation.getLocation().getX(), result.getCoordinates().getLast());
        assertEquals(distance, result.getDistanceMeters());
        verify(bookRepository, times(1)).findById(bookId);
        verify(bookLocationRepository, times(1)).findAllNearestBookLocations(bookId, latitude, longitude);
    }

    @Test
    void testGetBookLocationSuccessButEmpty() {
        UUID bookId = book.getId();
        double latitude = -6.3702510913875;
        double longitude = 106.82742266305259;

        when(bookRepository.findById(bookId))
                .thenReturn(Optional.of(book));

        when(bookLocationRepository.findAllNearestBookLocations(bookId, latitude, longitude))
                .thenReturn(List.of());

        List<BookLocationResponse> response = bookLocationService.getBookLocations(bookId, latitude, longitude);

        assertEquals(0, response.size());
        verify(bookRepository, times(1)).findById(bookId);
        verify(bookLocationRepository, times(1)).findAllNearestBookLocations(bookId, latitude, longitude);
    }

    @Test
    void testGetBookLocationFailedWhenBookIsNotExist() {
        UUID bookId = UUID.randomUUID();
        double latitude = -6.3702510913875;
        double longitude = 106.82742266305259;

        when(bookRepository.findById(bookId))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            bookLocationService.getBookLocations(bookId, latitude, longitude);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(bookRepository, times(1)).findById(bookId);
        verify(bookLocationRepository, times(0)).findAllNearestBookLocations(bookId, latitude, longitude);
    }

    @Test
    void testAddBookLocationSuccess() {
        UUID bookId = book.getId();
        BookLocationRequest bookLocationRequest = new BookLocationRequest();
        bookLocationRequest.setLocationName(bookLocation.getLocationName());
        bookLocationRequest.setLatitude(bookLocation.getLocation().getY());
        bookLocationRequest.setLongitude(bookLocation.getLocation().getX());

        when(bookRepository.findById(bookId))
                .thenReturn(Optional.of(book));

        when(bookLocationRepository.save(any(BookLocation.class)))
                .thenReturn(bookLocation);

        doNothing().when(userPointsPublisher).sendUserPointsForLocation(any(AmqpUserPointsMessage.class));

        bookLocationService.addBookLocation(userId, bookId, bookLocationRequest);

        verify(bookRepository, times(1)).findById(bookId);
        verify(bookLocationRepository, times(1)).save(any(BookLocation.class));
        verify(userPointsPublisher, times(1)).sendUserPointsForLocation(any(AmqpUserPointsMessage.class));
    }

    @Test
    void testAddBookLocationFailedWhenBookIsNotExist() {
        UUID bookId = book.getId();
        BookLocationRequest bookLocationRequest = new BookLocationRequest();
        bookLocationRequest.setLocationName(bookLocation.getLocationName());
        bookLocationRequest.setLatitude(bookLocation.getLocation().getY());
        bookLocationRequest.setLongitude(bookLocation.getLocation().getX());

        when(bookRepository.findById(bookId))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            bookLocationService.addBookLocation(userId, bookId, bookLocationRequest);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(bookRepository, times(1)).findById(bookId);
        verify(bookLocationRepository, times(0)).save(any(BookLocation.class));
    }

    @Test
    void testUpdateBookLocationSuccessWithAllFields() {
        UUID bookId = book.getId();
        int locationId = bookLocation.getId();
        UpdateBookLocationRequest updateBookLocationRequest = new UpdateBookLocationRequest();
        updateBookLocationRequest.setLocationName(bookLocation.getLocationName());
        updateBookLocationRequest.setLatitude(bookLocation.getLocation().getY());
        updateBookLocationRequest.setLongitude(bookLocation.getLocation().getX());

        when(bookLocationRepository.findByIdAndBookId(locationId, bookId))
                .thenReturn(Optional.of(bookLocation));

        when(bookLocationRepository.save(any(BookLocation.class)))
                .thenReturn(bookLocation);

        bookLocationService.updateBookLocation(bookId, locationId, updateBookLocationRequest);

        verify(bookLocationRepository, times(1)).findByIdAndBookId(locationId, bookId);
        verify(bookLocationRepository, times(1)).save(any(BookLocation.class));
    }

    @Test
    void testUpdateBookLocationSuccessOnlyLocationName() {
        UUID bookId = book.getId();
        int locationId = bookLocation.getId();
        UpdateBookLocationRequest updateBookLocationRequest = new UpdateBookLocationRequest();
        updateBookLocationRequest.setLocationName(bookLocation.getLocationName());

        when(bookLocationRepository.findByIdAndBookId(locationId, bookId))
                .thenReturn(Optional.of(bookLocation));

        when(bookLocationRepository.save(any(BookLocation.class)))
                .thenReturn(bookLocation);

        bookLocationService.updateBookLocation(bookId, locationId, updateBookLocationRequest);

        verify(bookLocationRepository, times(1)).findByIdAndBookId(locationId, bookId);
        verify(bookLocationRepository, times(1)).save(any(BookLocation.class));
    }

    @Test
    void testUpdateBookLocationSuccessOnlyLatitude() {
        UUID bookId = book.getId();
        int locationId = bookLocation.getId();
        UpdateBookLocationRequest updateBookLocationRequest = new UpdateBookLocationRequest();
        updateBookLocationRequest.setLongitude(bookLocation.getLocation().getX());

        when(bookLocationRepository.findByIdAndBookId(locationId, bookId))
                .thenReturn(Optional.of(bookLocation));

        when(bookLocationRepository.save(any(BookLocation.class)))
                .thenReturn(bookLocation);

        bookLocationService.updateBookLocation(bookId, locationId, updateBookLocationRequest);

        verify(bookLocationRepository, times(1)).findByIdAndBookId(locationId, bookId);
        verify(bookLocationRepository, times(1)).save(any(BookLocation.class));
    }

    @Test
    void testUpdateBookLocationSuccessOnlyLongitude() {
        UUID bookId = book.getId();
        int locationId = bookLocation.getId();
        UpdateBookLocationRequest updateBookLocationRequest = new UpdateBookLocationRequest();
        updateBookLocationRequest.setLatitude(bookLocation.getLocation().getY());

        when(bookLocationRepository.findByIdAndBookId(locationId, bookId))
                .thenReturn(Optional.of(bookLocation));

        when(bookLocationRepository.save(any(BookLocation.class)))
                .thenReturn(bookLocation);

        bookLocationService.updateBookLocation(bookId, locationId, updateBookLocationRequest);

        verify(bookLocationRepository, times(1)).findByIdAndBookId(locationId, bookId);
        verify(bookLocationRepository, times(1)).save(any(BookLocation.class));
    }

    @Test
    void testUpdateBookLocationFailedWhenLocationIsNotExist() {
        UUID bookId = book.getId();
        int locationId = bookLocation.getId();
        UpdateBookLocationRequest updateBookLocationRequest = new UpdateBookLocationRequest();
        updateBookLocationRequest.setLocationName(bookLocation.getLocationName());

        when(bookLocationRepository.findByIdAndBookId(locationId, bookId))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            bookLocationService.updateBookLocation(bookId, locationId, updateBookLocationRequest);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(bookLocationRepository, times(1)).findByIdAndBookId(locationId, bookId);
        verify(bookLocationRepository, times(0)).save(any(BookLocation.class));
    }

    @Test
    void testDeleteBookLocationSuccess() {
        UUID bookId = book.getId();
        int locationId = bookLocation.getId();

        when(bookLocationRepository.findByIdAndBookId(locationId, bookId))
                .thenReturn(Optional.of(bookLocation));

        doNothing().when(bookLocationRepository).deleteById(bookLocation.getId());

        bookLocationService.deleteBookLocation(bookId, locationId);

        verify(bookLocationRepository, times(1)).findByIdAndBookId(locationId, bookId);
        verify(bookLocationRepository, times(1)).deleteById(bookLocation.getId());
    }

    @Test
    void testDeleteBookLocationFailedWhenLocationIsNotExist() {
        UUID bookId = book.getId();
        int locationId = bookLocation.getId();

        when(bookLocationRepository.findByIdAndBookId(locationId, bookId))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            bookLocationService.deleteBookLocation(bookId, locationId);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(bookLocationRepository, times(1)).findByIdAndBookId(locationId, bookId);
        verify(bookLocationRepository, times(0)).deleteById(bookLocation.getId());
    }
}
