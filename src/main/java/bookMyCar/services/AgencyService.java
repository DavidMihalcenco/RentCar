package bookMyCar.services;

import bookMyCar.dtos.HotelDto;
import bookMyCar.dtos.RoomDto;
import bookMyCar.dtos.ViewHotelDto;
import bookMyCar.entities.Agency;
import bookMyCar.entities.User;
import bookMyCar.repositories.AgencyRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Transactional
@Service
public class AgencyService {

    private final AgencyRepository agencyRepository;
    private final SecurityService securityService;
    private final UserService userService;
    private final ImageService imageService;
    private final CarService carService;

    public List<HotelDto> getUserHotels() {
        String email = securityService.getClaim("email");
        return agencyRepository
                .findAllByOwner_Email(email)
                .stream()
                .map(h -> new HotelDto(h.getId(), h.getName(), h.getLocation(), h.getPhoneNumber(), h.getOwner().getEmail()))
                .collect(Collectors.toList());
    }

    public Agency findById(Long id) {
        return agencyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No hotel with id " + id));
    }

    public void save(Agency agency) {
        agencyRepository.save(agency);
    }

    public long count() {
        return agencyRepository.count();
    }

    public List<Agency> getAll() {
        return agencyRepository.findAll();
    }

    public void save(HotelDto hotelDto, MultipartFile img) {
        String email = securityService.getClaim("email");
        User user = userService.findUserByEmail(email);
        Agency agency = new Agency(null, hotelDto.name(), hotelDto.location(), hotelDto.phoneNumber(), user, null);

        agency = agencyRepository.save(agency);
        imageService.saveHotelImage(agency, img);
        agencyRepository.save(agency);
    }

    public void saveHotelImage(Long id, MultipartFile image) {
        Optional<Agency> optionalHotel = agencyRepository.findById(id);

        if (optionalHotel.isEmpty())
            throw new RuntimeException("No agency with id " + id);

        Agency agency = optionalHotel.get();
        imageService.saveHotelImage(agency, image);
        agencyRepository.save(agency);
    }

    public byte[] getHotelImage(Long id) {
        Optional<Agency> optionalHotel = agencyRepository.findById(id);

        if (optionalHotel.isEmpty())
            throw new RuntimeException("No agency with id " + id);

        Agency agency = optionalHotel.get();
        return imageService.getHotelImage(agency);
    }

    public ViewHotelDto getViewHotel(Long id) {
        Agency agency = agencyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No agency with id " + id));

        return ViewHotelDto.builder()
                .id(agency.getId())
                .ownerEmail(agency.getOwner().getEmail())
                .location(agency.getLocation())
                .name(agency.getName())
                .phoneNumber(agency.getPhoneNumber())
                .rooms(carService.getListViewRoomsByHotelId(agency.getId()))
                .build();
    }

    public void save(RoomDto room, MultipartFile image, Long id) {
        Agency agency = agencyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No agency with id " + id));

        carService.save(room, image, agency);
    }

    public void deleteHotelById(Long id) {
        Agency agency = agencyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No agency with id " + id));

        imageService.deleteImage(agency.getImage());
        carService.deleteHotelRooms(agency.getId());
        agencyRepository.delete(agency);
    }

    public void deleteRoomById(Long roomId, Long id) {
        Agency agency = agencyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No agency with id " + id));

        carService.deleteRoom(roomId, agency.getId());
    }
}
