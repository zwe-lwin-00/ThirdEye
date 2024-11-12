package com.thirdeye.code.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.thirdeye.code.entity.Break;
import com.thirdeye.code.service.BreakService;

@Controller
@RequestMapping("/break")
public class BreakController {

    @Autowired
    private BreakService breakService;

    // List all breaks
    @GetMapping
    public String listBreaks(Model model) {
        List<Break> breaks = breakService.findAll();
        if (breaks != null && !breaks.isEmpty()) {
            model.addAttribute("breakstatus", true);
        }else{
            model.addAttribute("breakstatus", false);
        }
        model.addAttribute("breaks", breaks);
        return "break/break-list"; 
    }

    // Show form to create a new Break
    @GetMapping("/new")
    public String createBreakForm(Model model) {
        List<Break> breaks = breakService.findAll();
        if (breaks != null && !breaks.isEmpty()) {
            model.addAttribute("breakstatus", true);
        }else{
            model.addAttribute("breakstatus", false);
        }
        model.addAttribute("break", new Break());
        return "break/break-form";
    }

    // Show form to edit an existing Break
    @GetMapping("/{id}")
    public String editBreakForm(@PathVariable Long id, Model model) {
        Break breakEntity = breakService.findById(id);
        model.addAttribute("break", breakEntity);
        return "break/break-form";
    }

    // Handle creation of a new Break
    @PostMapping
    public String createBreak(@ModelAttribute Break breakEntity) {
        breakService.createBreak(breakEntity);
        return "redirect:/break"; 
    }

    // Handle update of an existing Break
    @PostMapping("/{id}")
    public String updateBreak(@PathVariable Long id, @ModelAttribute Break breakEntity) {
        breakService.updateBreak(id, breakEntity);
        return "redirect:/break"; 
    }

    // Optionally, handle deletion
    @GetMapping("/delete/{id}")
    public String deleteBreak(@PathVariable Long id) {
        breakService.deleteBreak(id); 
        return "redirect:/break"; 
    }
}
