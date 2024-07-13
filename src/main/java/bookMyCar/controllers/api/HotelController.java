package bookMyCar.controllers.api;

import bookMyCar.dtos.HotelDto;
import bookMyCar.dtos.RoomDto;
import bookMyCar.dtos.ViewHotelDto;
import bookMyCar.services.AgencyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping({"/api/agencies", "/api/agencies/"})
public class HotelController {

    private final AgencyService agencyService;

    public HotelController(AgencyService agencyService) {
        this.agencyService = agencyService;
    }

    @GetMapping
    public List<HotelDto> getHotels() {
        return agencyService.getUserHotels();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void addHotel(@RequestPart("hotelDto") HotelDto hotel, @RequestPart("img") MultipartFile img) {
        agencyService.save(hotel, img);
    }

    @PostMapping(value = "/{id}/rooms", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void addRoom(@RequestPart("roomDto") RoomDto room,
                        @RequestPart("img")MultipartFile image,
                        @PathVariable Long id) {
        agencyService.save(room, image, id);
    }

    @GetMapping("/{id}")
    public ViewHotelDto getHotel(@PathVariable Long id) {
        return agencyService.getViewHotel(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}")
    public String deleteHotel(@PathVariable Long id) {
        agencyService.deleteHotelById(id);
        return "success";
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{id}/rooms/{roomId}")
    public String deleteRoom(@PathVariable Long roomId, @PathVariable Long id) {
        agencyService.deleteRoomById(roomId, id);
        return "success";
    }
}
