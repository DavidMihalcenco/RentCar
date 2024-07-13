package bookMyCar.controllers.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/agencies")
public class HotelControllerMVC {

    @GetMapping
    public String getHotels(Model model) {
        model.addAttribute("agencies", true);
        model.addAttribute("scriptSrc", "agencies.js");
        return "generic";
    }

    @GetMapping("/new")
    public String addHotel(Model model) {
        model.addAttribute("agencies", true);
        model.addAttribute("scriptSrc", "addAgency.js");
        return "generic";
    }

    @GetMapping("/{id}")
    public String viewHotel(Model model, @PathVariable Integer id) {
        model.addAttribute("agencies", true);
        model.addAttribute("scriptSrc", "viewAgency.js");
        return "generic";
    }
}
