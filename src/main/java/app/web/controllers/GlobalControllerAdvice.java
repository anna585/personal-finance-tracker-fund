package app.web.controllers;

import app.exeption.ApplicationException;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;


@Slf4j
@ControllerAdvice
public class GlobalControllerAdvice {


    @ExceptionHandler(ApplicationException.class)
    public ModelAndView handleApplicationException(ApplicationException ex){
        log.error("ApplicationException occurred: {}", ex.getMessage(), ex);

        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("errorMessage", ex.getMessage());
        modelAndView.addObject("errorTitle", ex.getErrorTitle());
        modelAndView.addObject("errorCode", ex.getErrorCode());

        return modelAndView;
    }

    @ExceptionHandler(FeignException.class)
    public ModelAndView handleFeignException(FeignException ex){
        log.error("ApplicationException occurred: {}", ex.getMessage(), ex);

        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("errorMessage",  "Analytics service is currently unavailable.");
        modelAndView.addObject("errorTitle", "Feign Exception");
        modelAndView.addObject("errorCode", ex.status());


        return modelAndView;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleLeftoverException(Exception ex){
        log.error("ApplicationException occurred: {}", ex.getMessage(), ex);

        ModelAndView modelAndView = new ModelAndView("error");
        modelAndView.addObject("errorMessage","Unexpected error occurred!");
        modelAndView.addObject("errorTitle", "Internal Server Error");
        modelAndView.addObject("errorCode", "500");


        return modelAndView;
    }
}
