package bookMyCar.controllers.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/map")
public class MapControllerMVC {

    @GetMapping("/admin")
    public String getAdminMap(Model model) {
        model.addAttribute("showMap", true);
        model.addAttribute("adminMap", true);
        model.addAttribute("scriptSrcMap", "scriptAdmin.js");

        return "generic";
    }

    @GetMapping("/user")
    public String getUserMap(Model model) {
        model.addAttribute("showMap", true);
        model.addAttribute("userMap", true);
        model.addAttribute("scriptSrcMap", "script.js");
        return "generic";
    }
}
