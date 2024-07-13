package bookMyCar.bootstrap;

import bookMyCar.entities.Agency;
import bookMyCar.entities.Car;
import bookMyCar.services.AgencyService;
import bookMyCar.services.ImageService;
import bookMyCar.services.CarService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Transactional
@AllArgsConstructor
@Component
public class ImageBootstrap implements CommandLineRunner {

    private final ImageService imageService;
    private final AgencyService agencyService;
    private final CarService carService;

    @Override
    public void run(String... args) throws Exception {
        bootstrapHotelsImages();
        bootstrapRoomsImages();
    }

    private void bootstrapRoomsImages() {
        Car car = carService.findById(1L);

        if (car.getImage() != null || carService.count() > 5)
            return;

        carService.getAll().forEach(r -> {
            String str = r.getAgency().getName() + " " + r.getAgency().getLocation() + " " + r.getId();
            r.setImage(str.replaceAll(" ", "_") + ".jpg");
            carService.save(r);
        });
    }

    private void bootstrapHotelsImages() {
        Agency agency = agencyService.findById(1L);

        if (agency.getImage() != null || agencyService.count() > 5)
            return;

        agencyService.getAll().forEach(h -> {
            String str = h.getName() + " " + h.getLocation();
            h.setImage(str.replaceAll(" ", "_") + ".jpg");
            agencyService.save(h);
        });
    }


}
