package gr.di.uoa.m151;

import gr.di.uoa.m151.entity.AppUser;
import gr.di.uoa.m151.service.RestClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class AppUserController {

    private final RestClientService restClientService;

    private static final String SIGN_UP = "signup";

    private static final String NEW_APP_USER = "newAppUser";


    @Autowired
    public AppUserController(RestClientService restClientService) {
        this.restClientService = restClientService;
    }

    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public String registerPage(Model model) {
        AppUser newAppUser = new AppUser();
        model.addAttribute(NEW_APP_USER, newAppUser);
        return SIGN_UP;
    }


    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public String signUpPage(@ModelAttribute AppUser newAppUser) {
        return restClientService.signUp(newAppUser).getFullName();
    }

}

