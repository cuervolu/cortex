package com.cortex.backend.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

  @RequestMapping("/hello")
  public String hello() {
    return "Hello, World!";
  }


  @RequestMapping("/bye")
  public String bye() {
    return "Goodbye, World!";
  }


}
