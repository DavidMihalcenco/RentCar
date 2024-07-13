package bookMyCar.services;

import bookMyCar.dtos.RoomDto;
import bookMyCar.dtos.ViewRoomDto;
import bookMyCar.entities.Agency;
import bookMyCar.entities.Car;
import bookMyCar.repositories.CarRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Transactional
@AllArgsConstructor
@Service
public class CarService {

    private final CarRepository carRepository;
    private final ImageService imageService;
    private final RequestService requestService;

    public List<ViewRoomDto> getListViewRoomsByHotelId(Long hotelId) {
        return carRepository.getAllByAgency_Id(hotelId)
                .stream()
                .map(room -> ViewRoomDto.builder()
                        .id(room.getId())
                        .price(room.getPrice())
                        .nrGuests(room.getNrGuests())
                        .hotelName(room.getAgency().getName())
                        .location(room.getAgency().getLocation())
                        .build())
                .collect(Collectors.toList());
    }

    public Car findById(Long id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No Car with id: " + id));
    }

    public Long count() {
        return carRepository.count();
    }

    public List<Car> getAll() {
        return carRepository.findAll();
    }

    public Car save(Car car) {
        return carRepository.save(car);
    }

    public byte[] getRoomImage(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No Car with id: " + id));

        return imageService.getRoomImage(car);
    }

    public void saveRoomImage(Long id, MultipartFile image) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No Car with id: " + id));

        imageService.saveRoomImage(car, image);
        carRepository.save(car);
    }

    public void save(RoomDto roomDto, MultipartFile image, Agency agency) {
        Car car = new Car(null, roomDto.price(), roomDto.nrOfGuests(), agency, null);

        car = carRepository.save(car);
        imageService.saveRoomImage(car, image);
        carRepository.save(car);
    }

    public void deleteRoom(Long id, Long hotelId) {
        Car car = carRepository.getRoomByIdAndAgency_Id(id, hotelId)
                .orElseThrow(() -> new RuntimeException("No Car with id: " + id + " in hotel with id: " + hotelId));

        imageService.deleteImage(car.getImage());
        requestService.deleteRoomsRequests(car);
        carRepository.delete(car);
    }

    public void deleteHotelRooms(Long hotelId) {
        carRepository.getAllByAgency_Id(hotelId)
                .forEach(r -> deleteRoom(r.getId(), hotelId));
    }

    public List<ViewRoomDto> getListViewRoomsWithFilters(BigDecimal min,
                                                         BigDecimal max,
                                                         LocalDate startDate,
                                                         LocalDate endDate,
                                                         String location,
                                                         Integer nrOfGuests) {

        Timestamp start = Timestamp.valueOf(startDate.atStartOfDay());
        Timestamp end = Timestamp.valueOf(endDate.atStartOfDay());
        Set<Long> ids = new HashSet<>(List.of(-1L));
        ids.addAll(requestService.getNotAvailableRoomIds(start, end));
        BigDecimal minPerDay = getPricePerDay(start, end, min);
        BigDecimal maxPerDay = getPricePerDay(start, end, max);

        return carRepository.getAllByFilters(minPerDay, maxPerDay, ids, location, nrOfGuests)
                .stream()
                .map(r -> ViewRoomDto.builder()
                        .id(r.getId())
                        .price(requestService.calculatePrice(start, end, r.getPrice()))
                        .nrGuests(r.getNrGuests())
                        .location(r.getAgency().getLocation())
                        .hotelName(r.getAgency().getName())
                        .build())
                .collect(Collectors.toList());
    }

    private BigDecimal getPricePerDay(Timestamp start, Timestamp end, BigDecimal price) {
        long millisecondsDiff = end.getTime() - start.getTime();
        long daysDiff = TimeUnit.MILLISECONDS.toDays(millisecondsDiff);
        return price.divide(new BigDecimal(daysDiff), 2, RoundingMode.DOWN);
    }

    public List<ViewRoomDto> getListAvailableRooms() {
        Set<Long> ids = requestService.getNotAvailableRoomIds();

        return carRepository.getAllByIdNotIn(ids)
                .stream()
                .map(r -> ViewRoomDto.builder()
                        .id(r.getId())
                        .price(r.getPrice())
                        .nrGuests(r.getNrGuests())
                        .location(r.getAgency().getLocation())
                        .hotelName(r.getAgency().getName())
                        .build())
                .collect(Collectors.toList());
    }

    public Car getRoomById(Long id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No car was found with id: " + id));
    }
}
