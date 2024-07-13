package bookMyCar.controllers.api;

import bookMyCar.services.AgencyService;
import bookMyCar.services.CarService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@RestController
@RequestMapping("/api/img")
public class ImageController {

    private final AgencyService agencyService;
    private final CarService carService;

    @PostMapping(value = "/agencies/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void saveHotelImage(@PathVariable Long id, @RequestParam("imagefile") MultipartFile image) {
        agencyService.saveHotelImage(id, image);
    }

    @GetMapping(value = "/agencies/{id}",
            produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] renderHotelImageFromDB(@PathVariable Long id) {
        return agencyService.getHotelImage(id);
    }

    @PostMapping(value = "/rooms/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void saveRoomImage(@PathVariable Long id, @RequestParam("imagefile") MultipartFile image) {
        carService.saveRoomImage(id, image);
    }

    @GetMapping(value = "/rooms/{id}",
            produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] renderRoomImageFromDB(@PathVariable Long id) {
        return carService.getRoomImage(id);
    }
}
