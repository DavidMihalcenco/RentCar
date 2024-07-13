package bookMyCar.services;

import bookMyCar.dtos.ViewRequest;
import bookMyCar.entities.*;
import bookMyCar.repositories.RentRepository;
import bookMyCar.repositories.RequestRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Transactional
@AllArgsConstructor
@Service
public class RequestService {

    private final RentRepository rentRepository;
    private final RequestRepository requestRepository;

    public List<ViewRequest> getPendingRequestsByModerator(User moderator) {
        return requestRepository.getAllByModerator_Id(moderator.getId(), Sort.by(Sort.Direction.ASC, "startDate"))
                .stream()
                .map(request -> ViewRequest.builder()
                        .id(request.getId())
                        .roomId(request.getCar().getId())
                        .price(calculatePrice(request))
                        .nrGuests(request.getCar().getNrGuests())
                        .startDate(request.getStartDate().toLocalDateTime().toLocalDate())
                        .endDate(request.getEndDate().toLocalDateTime().toLocalDate())
                        .guestEmail(request.getGuest().getEmail())
                        .build())
                .collect(Collectors.toList());
    }

    public List<ViewRequest> getProcessedRequestByModerator(User moderator, Status status) {
        return rentRepository.getAllByStatusAndModerator_Id(status, moderator.getId(), Sort.by(Sort.Direction.ASC, "startDate"))
                .stream()
                .map(request -> ViewRequest.builder()
                        .id(request.getId())
                        .roomId(request.getCar().getId())
                        .price(calculatePrice(request))
                        .nrGuests(request.getCar().getNrGuests())
                        .startDate(request.getStartDate().toLocalDateTime().toLocalDate())
                        .endDate(request.getEndDate().toLocalDateTime().toLocalDate())
                        .guestEmail(request.getGuest().getEmail())
                        .build())
                .collect(Collectors.toList());
    }

    public void deleteRoomsRequests(Car car) {
        requestRepository.deleteAllByCar(car);
        rentRepository.deleteAllByCar(car);
    }

    public void processRequest(Long id, Status status) {
        RentRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No requests with id: " + id));

        rentRepository.save(Rent.builder()
                .guest(request.getGuest())
                .moderator(request.getModerator())
                .car(request.getCar())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(status)
                .build());

        requestRepository.delete(request);
    }

    public List<ViewRequest> getProcessedRequestByGuest(User guest, Status status) {
        return rentRepository.getAllByStatusAndGuest_Id(status, guest.getId(), Sort.by(Sort.Direction.ASC, "startDate"))
                .stream()
                .map(request -> ViewRequest.builder()
                        .id(request.getId())
                        .roomId(request.getCar().getId())
                        .price(calculatePrice(request))
                        .nrGuests(request.getCar().getNrGuests())
                        .startDate(request.getStartDate().toLocalDateTime().toLocalDate())
                        .endDate(request.getEndDate().toLocalDateTime().toLocalDate())
                        .guestEmail(request.getGuest().getEmail())
                        .ownerEmail(request.getModerator().getEmail())
                        .build())
                .collect(Collectors.toList());
    }

    public List<ViewRequest> getPendingRequestsByGuest(User guest) {
        return requestRepository.getAllByGuest_Id(guest.getId(), Sort.by(Sort.Direction.ASC, "startDate"))
                .stream()
                .map(request -> ViewRequest.builder()
                        .id(request.getId())
                        .roomId(request.getCar().getId())
                        .price(calculatePrice(request))
                        .nrGuests(request.getCar().getNrGuests())
                        .startDate(request.getStartDate().toLocalDateTime().toLocalDate())
                        .endDate(request.getEndDate().toLocalDateTime().toLocalDate())
                        .guestEmail(request.getGuest().getEmail())
                        .ownerEmail(request.getModerator().getEmail())
                        .build())
                .collect(Collectors.toList());
    }

    public void cancelRequest(Long id) {
        RentRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No requests with id: " + id));

        requestRepository.delete(request);
    }

    public Set<Long> getNotAvailableRoomIds() {
        Set<Long> ids = rentRepository.getActualIds();
        ids.addAll(requestRepository.getActualIds());

        return ids;
    }

    public Set<Long> getNotAvailableRoomIds(Timestamp startDate, Timestamp endDate) {
        Set<Long> ids = rentRepository.getActualIds(startDate, endDate);
        ids.addAll(requestRepository.getActualIds(startDate, endDate));

        return ids;
    }

    private Set<Long> getNotAvailableRoomIdsByGuest(Timestamp start, Timestamp end, Long id) {
        Set<Long> ids = rentRepository.getActualIdsByGuest(start, end, id);
        ids.addAll(requestRepository.getActualIdsByGuest(start, end, id));

        return ids;
    }

    public void save(ViewRequest roomDto, Car car, User user) {
        Timestamp start = Timestamp.valueOf(roomDto.startDate().atStartOfDay());
        Timestamp end = Timestamp.valueOf(roomDto.endDate().atStartOfDay());
        Set<Long> ids = getNotAvailableRoomIds(start, end);
        if (ids.contains(car.getId()))
            throw new RuntimeException("Sorry, this car is already booked for this period");

        ids = getNotAvailableRoomIdsByGuest(start, end, user.getId());
        if (!ids.isEmpty())
            throw new RuntimeException("Sorry, you already have a reservation for this period");

        RentRequest newRequest = RentRequest.builder()
                .startDate(start)
                .endDate(end)
                .guest(user)
                .car(car)
                .moderator(car.getAgency().getOwner())
                .build();

        requestRepository.save(newRequest);
    }

    private BigDecimal calculatePrice(RentRequest request) {
        return calculatePrice(request.getStartDate(),
                request.getEndDate(),
                request.getCar().getPrice());
    }

    private BigDecimal calculatePrice(Rent request) {
        return calculatePrice(request.getStartDate(),
                request.getEndDate(),
                request.getCar().getPrice());
    }

    BigDecimal calculatePrice(Timestamp start, Timestamp end, BigDecimal price) {
        long millisecondsDiff = end.getTime() - start.getTime();
        long daysDiff = TimeUnit.MILLISECONDS.toDays(millisecondsDiff);

        return price.multiply(new BigDecimal(daysDiff));
    }

}
