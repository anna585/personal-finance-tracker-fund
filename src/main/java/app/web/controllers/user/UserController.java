package app.web.controllers.user;

import app.web.dto.user.*;
import app.services.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class  UserController {

    private final UserService userService;


    @GetMapping("/login")
    public ModelAndView getLogin(@RequestParam(required = false) String error) {

        UserLoginRequest userLoginRequest = UserLoginRequest.builder().build();

        ModelAndView modelAndView = new ModelAndView("login");
        modelAndView.addObject("userLoginRequest", userLoginRequest);

        if(error != null){
         modelAndView.addObject("error", "Invalid username or password.");
        }
        return modelAndView;
    }


    @GetMapping("/register")
    public ModelAndView getRegister() {

        UserRegisterRequest userRegisterRequest = UserRegisterRequest.builder().build();

        return new ModelAndView("register")
                .addObject("userRegisterRequest", userRegisterRequest);
    }

    @PostMapping("/register")
    public ModelAndView postRegister(@Valid @ModelAttribute UserRegisterRequest userRegisterRequest,
                                     BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return new ModelAndView("register")
                    .addObject("userRegisterRequest", userRegisterRequest);
        }

            userService.register(userRegisterRequest);
            return new ModelAndView("redirect:/login");
    }

    @GetMapping("/admin/users")
    public ModelAndView getUsers(){

        return new ModelAndView("users")
                .addObject("users", userService.getAllUsers());
    }


    @PostMapping("/users/{id}/delete")
    public ModelAndView deleteUser(@PathVariable UUID id){

            userService.deleteUser(id);
            return new ModelAndView("redirect:/admin/users");

    }

    @GetMapping("/profile")
    public ModelAndView getProfile(@AuthenticationPrincipal AuthenticationUserDetails principal){

        UserDto user = userService.getById(principal.getId());
        UserProfileDto userProfileDto = UserProfileDto.builder()
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .userRole(user.getUserRole())
                .password(user.getPassword())
                .build();

        return new ModelAndView("profile")
                .addObject("user", user)
                .addObject("userProfileDto", userProfileDto);
    }

    @PostMapping("/profile")
    public ModelAndView updateProfile(@Valid @ModelAttribute UserProfileDto userProfileDto,
                                      BindingResult bindingResult,
                                      @AuthenticationPrincipal AuthenticationUserDetails principal){

        UserDto user = userService.updateProfileInformation(principal.getId(), userProfileDto);

        if(bindingResult.hasErrors()){
            return new ModelAndView("profile")
                    .addObject("userProfileDto", userProfileDto)
                    .addObject("user", user);
        }

        return new ModelAndView("redirect:/profile?success")
                .addObject("user", user)
                .addObject("userProfileDto", userProfileDto);

    }
}
