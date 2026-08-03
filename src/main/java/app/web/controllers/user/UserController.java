package app.web.controllers.user;

import app.web.dto.user.*;
import app.services.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/users")
    public ModelAndView getUsers(){

        return new ModelAndView("users")
                .addObject("users", userService.getAllUsers());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/users/{id}/delete")
    public ModelAndView deleteUser(@PathVariable UUID id){

            userService.deleteUser(id);
            return new ModelAndView("redirect:/admin/users?delete");

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


        if(bindingResult.hasErrors()){
            UserDto user = userService.getById(principal.getId());
            return new ModelAndView("profile")
                    .addObject("userProfileDto", userProfileDto)
                    .addObject("user", user);
        }

        UserDto user = userService.updateProfileInformation(principal.getId(), userProfileDto);

        return new ModelAndView("redirect:/profile?success")
                .addObject("user", user)
                .addObject("userProfileDto", userProfileDto);

    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/users/{id}/update-role")
    public ModelAndView menageRole(@PathVariable UUID id){

        UserDto user = userService.getById(id);

        UpdateUserRoleDto updateUserRoleDto = UpdateUserRoleDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getUserRole())
                .build();

        return new ModelAndView("update-role")
                .addObject("updateUserRoleDto", updateUserRoleDto)
                .addObject("user", user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/users/{id}/update-role")
    public ModelAndView updateUserRole(@Valid @ModelAttribute UpdateUserRoleDto updateUserRoleDto,
                                       BindingResult bindingResult){


        if(bindingResult.hasErrors()){

            return new ModelAndView("update-role")
                    .addObject("updateUserRoleDto", updateUserRoleDto);
        }

        UserDto user = userService.updateRole(updateUserRoleDto.getId(), updateUserRoleDto);

        return new ModelAndView("redirect:/admin/users/{id}/update-role?success")
                .addObject("updateUserRoleDto", updateUserRoleDto)
                .addObject("user", user)
                .addObject("id", updateUserRoleDto.getId());
    }

}
