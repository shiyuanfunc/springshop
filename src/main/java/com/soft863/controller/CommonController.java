package com.soft863.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;

/**
 * Created by admin on 2017/4/22.
 */
@Controller
@RequestMapping("/c")
public class CommonController {

    @RequestMapping("{f}/{d}")
    public String rpage(@PathVariable("f")String f , @PathVariable("d")String d ,Model model){
        return f+ "/" + d ;
    }

    @RequestMapping("{f}")
    public String rpage(@PathVariable("f")String f ,Model model){
        return f;
    }
}
