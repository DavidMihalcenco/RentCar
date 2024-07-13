package bookMyCar.controllers.api;

import bookMyCar.dtos.GetUserDto;
import bookMyCar.dtos.LogoutDto;
import bookMyCar.services.SecurityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class SecurityController {


    private final SecurityService securityService;

    public SecurityController(SecurityService securityService) {
        this.securityService = securityService;
    }

    @GetMapping("/user")
    public GetUserDto getUserInfo() {
        return securityService.getUserInfo();
    }

    @PostMapping("/logout")
    public ResponseEntity<String> userLogout(@RequestBody LogoutDto token) {
        securityService.userLogout(token);
        return ResponseEntity.ok().body("success");
    }
}
