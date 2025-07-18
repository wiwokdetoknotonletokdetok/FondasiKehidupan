package org.gaung.wiwokdetok.fondasikehidupan.service;

import lombok.RequiredArgsConstructor;
import org.gaung.wiwokdetok.fondasikehidupan.dto.AmqpUserPointsMessage;
import org.gaung.wiwokdetok.fondasikehidupan.dto.BookLocationRequest;
import org.gaung.wiwokdetok.fondasikehidupan.dto.BookLocationResponse;
import org.gaung.wiwokdetok.fondasikehidupan.dto.UpdateBookLocationRequest;
import org.gaung.wiwokdetok.fondasikehidupan.model.Book;
import org.gaung.wiwokdetok.fondasikehidupan.model.BookLocation;
import org.gaung.wiwokdetok.fondasikehidupan.publisher.UserPointsPublisher;
import org.gaung.wiwokdetok.fondasikehidupan.repository.BookLocationRepository;
import org.gaung.wiwokdetok.fondasikehidupan.repository.BookRepository;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookLocationServiceImpl implements BookLocationService {

    private final BookRepository bookRepository;

    private final BookLocationRepository bookLocationRepository;

    private final UserPointsPublisher userPointsPublisher;

    @Override
    public List<BookLocationResponse> getBookLocations(UUID bookId, double latitude, double longitude) {
        bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Buku tidak ditemukan"));

        List<Object[]> raw = bookLocationRepository.findAllNearestBookLocations(bookId, latitude, longitude);

        return raw.stream()
                .map(row -> new BookLocationResponse(
                        (Integer) row[0],
                        (String) row[1],
                        ((Number) row[2]).doubleValue(),
                        ((Number) row[3]).doubleValue(),
                        ((Number) row[4]).doubleValue()
                ))
                .toList();
    }

    @Override
    public void addBookLocation(UUID userId, UUID bookId, BookLocationRequest request) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Buku tidak ditemukan"));

        BookLocation bookLocation = new BookLocation();
        bookLocation.setLocationName(request.getLocationName());
        bookLocation.setLocation(createPoint(request.getLongitude(), request.getLatitude()));
        bookLocation.setBook(book);
        bookLocationRepository.save(bookLocation);

        sendNewBookLocationMessage(userId);
    }

    private void sendNewBookLocationMessage(UUID userId) {
        AmqpUserPointsMessage message = new AmqpUserPointsMessage();
        message.setUserId(userId);
        message.setPoints(1);

        userPointsPublisher.sendUserPointsForLocation(message);
    }

    private Point createPoint(double longitude, double latitude) {
        GeometryFactory gf = new GeometryFactory();
        return gf.createPoint(new Coordinate(longitude, latitude));
    }

    @Override
    public void updateBookLocation(UUID bookId, int locationId, UpdateBookLocationRequest request) {
        BookLocation bookLocation = bookLocationRepository.findByIdAndBookId(locationId, bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lokasi tidak ditemukan"));

        Optional.ofNullable(request.getLocationName())
                .ifPresent(bookLocation::setLocationName);

        Point existingLocation = bookLocation.getLocation();
        double existingLongitude = existingLocation.getX();
        double existingLatitude = existingLocation.getY();

        Double requestedLongitude = request.getLongitude();
        Double requestedLatitude = request.getLatitude();

        if (requestedLongitude != null || requestedLatitude != null) {
            double updatedLongitude = requestedLongitude != null ? requestedLongitude : existingLongitude;
            double updatedLatitude = requestedLatitude != null ? requestedLatitude : existingLatitude;

            Point updatedLocation = createPoint(updatedLongitude, updatedLatitude);
            bookLocation.setLocation(updatedLocation);
        }

        bookLocationRepository.save(bookLocation);
    }

    @Override
    public void deleteBookLocation(UUID bookId, int locationId) {
        bookLocationRepository.findByIdAndBookId(locationId, bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lokasi tidak ditemukan"));

        bookLocationRepository.deleteById(locationId);
    }
}
