package com.cortex.backend.controllers;

import com.cortex.backend.entities.user.User;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

  @RequestMapping("/hello")
  public String hello(Authentication connectedUser) {
    User user = (User) connectedUser.getPrincipal();

    return "Hello, " + user.getUsername() + "!";
  }


  @RequestMapping("/bye")
  public String bye() {
    return "Goodbye, World!";
  }


}
