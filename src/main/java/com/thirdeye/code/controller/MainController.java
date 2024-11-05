package com.thirdeye.code.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.thirdeye.code.service.BreakService;

@Controller
@RequestMapping("/")
public class MainController {

    @Autowired
    private BreakService breakService;

    // List all breaks
    @GetMapping
    public String listBreaks(Model model) {
        // List<Break> breaks = breakService.findAll();
        // model.addAttribute("breaks", breaks);
        return "index"; // Return the Thymeleaf template name
    }

}
