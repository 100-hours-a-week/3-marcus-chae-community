package kr.adapterz.springboot.legal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LegalController {

    @GetMapping("/terms")
    public String terms(Model model) {
        model.addAttribute("siteName", "Community");
        model.addAttribute("contactEmail", "marcus@example.com");
        model.addAttribute("lastUpdated", java.time.LocalDate.now());
        return "terms";
    }

    @GetMapping("/privacy")
    public String privacy(Model model) {
        model.addAttribute("siteName", "Community");
        model.addAttribute("contactEmail", "marcus@example.com");
        model.addAttribute("lastUpdated", java.time.LocalDate.now());
        return "privacy";
    }
}
