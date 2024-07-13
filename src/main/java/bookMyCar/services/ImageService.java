package bookMyCar.services;

import bookMyCar.entities.Agency;
import bookMyCar.entities.Car;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Getter
@AllArgsConstructor
@Service
@Slf4j
public class ImageService {

//    private final String DIR_PATH = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\img\\";
    private final ResourceLoader resourceLoader;
//    private final String DIR_PATH = new Path(this.getClass().getClassLoader().getResource("/static/img/"));


    private Path getDirPath() {
        try {
            Resource resource = resourceLoader.getResource("classpath:static/img/");
            return resource.getFile().toPath();
        } catch (IOException e) {
            // Handle the exception as needed
            throw new RuntimeException("Unable to get directory path", e);
        }
    }

    @Transactional
    public void saveHotelImage(Agency agency, MultipartFile image) {
        final Path DIR_PATH = getDirPath();
        String str = agency.getName() + " " + agency.getLocation();
        agency.setImage(str.replaceAll(" ", "_") + ".jpg");
        String filePath = DIR_PATH.resolve(agency.getImage()).toString();

        try {
            image.transferTo(new File(filePath));
        } catch (IOException e) {
            throw new RuntimeException("Can't save image " + agency.getImage(), e);
        }
    }

    public byte[] getHotelImage(Agency agency) {
        final Path DIR_PATH = getDirPath();
        String filePath = DIR_PATH.resolve(agency.getImage()).toString();
        log.info("File Path {}", filePath);
        try {
            return Files.readAllBytes(new File(filePath).toPath());
        } catch (IOException e) {
            throw new RuntimeException("Can't load image " + agency.getImage(), e);
        }
    }

    public byte[] getRoomImage(Car car) {
        final Path DIR_PATH = getDirPath();
        String filePath = DIR_PATH.resolve(car.getImage()).toString();

        try {
            return Files.readAllBytes(new File(filePath).toPath());
        } catch (IOException e) {
            throw new RuntimeException("Can't load image " + car.getImage(), e);
        }
    }

    public void saveRoomImage(Car car, MultipartFile image) {
        final Path DIR_PATH = getDirPath();
        String str = car.getAgency().getName() + " " + car.getAgency().getLocation() + " " + car.getId();
        car.setImage(str.replaceAll(" ", "_") + ".jpg");
        String filePath = DIR_PATH.resolve(car.getImage()).toString();

        try {
            image.transferTo(new File(filePath));
        } catch (IOException e) {
            throw new RuntimeException("Can't save image " + car.getImage(), e);
        }
    }

    public void deleteImage(String image) {
        final Path DIR_PATH = getDirPath();
        String filePath = DIR_PATH.resolve(image).toString();

        try {
            Files.deleteIfExists(new File(filePath).toPath());
        } catch (IOException e) {
            throw new RuntimeException("No image was found" ,e);
        }
    }
}
