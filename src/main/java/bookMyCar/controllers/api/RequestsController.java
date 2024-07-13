package bookMyCar.controllers.api;

import bookMyCar.dtos.ViewRequest;
import bookMyCar.entities.Status;
import bookMyCar.services.RequestService;
import bookMyCar.services.CarService;
import bookMyCar.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/requests")
public class RequestsController {

    private final RequestService requestService;
    private final UserService userService;
    private final CarService carService;

    @PostMapping
    public String saveNewRequest(@RequestBody ViewRequest roomDto,
                                 @RequestParam Long roomId) {
        requestService.save(roomDto,
                carService.getRoomById(roomId),
                userService.getCurrentUser());
        return "success";
    }

    @GetMapping
    public List<ViewRequest> getRequests(@RequestParam Status status,
                                         @RequestParam(required = false, defaultValue = "false") Boolean isModerator) {
        switch (status) {
            case PENDING -> {
                if (isModerator)
                    return userService.getPendingRequestsByModerator();
                return userService.getPendingRequestsByGuest();
            }
            case ACCEPTED, DECLINED-> {
                if (isModerator)
                    return userService.getProcessedRequestByModerator(status);
                return userService.getProcessedRequestByGuest(status);
            }
        }

        return null;
    }

    @PostMapping("/{id}")
    public String processRequest(@RequestParam Status status, @PathVariable Long id) {
        if (!status.equals(Status.CANCEL))
            requestService.processRequest(id, status);
        else
            requestService.cancelRequest(id);
        return "success";
    }
}
