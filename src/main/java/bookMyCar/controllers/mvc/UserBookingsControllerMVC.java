package bookMyCar.controllers.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/myRents")
public class UserBookingsControllerMVC {

    @GetMapping
    public String getUserBookings(Model model) {
        model.addAttribute("myRents", true);
        model.addAttribute("scriptSrc", "userRents.js");
        return "generic";
    }
}
