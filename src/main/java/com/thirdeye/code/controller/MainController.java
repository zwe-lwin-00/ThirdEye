package com.thirdeye.code.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.thirdeye.code.entity.Break;
import com.thirdeye.code.service.BreakService;

@Controller
@RequestMapping("/")
public class MainController {

    @Autowired
    private BreakService breakService;

    @GetMapping
    public String listBreaks(Model model) {
        List<Break> breaks = breakService.findAll();
        if (breaks != null && !breaks.isEmpty()) {
            model.addAttribute("breakstatus", true);
        } else {
            model.addAttribute("breakstatus", false);
        }
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy, EEEE, hh:mm a", Locale.ENGLISH);
        String formattedDateTime = now.format(formatter);
        model.addAttribute("formattedDateTime", formattedDateTime);
        return "index";
    }

}
