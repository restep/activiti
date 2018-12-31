package com.activiti.controller.ch06;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author restep
 * @date 2018/12/31
 */
@Controller
@RequestMapping("/main")
public class MainController {
    @RequestMapping(value = "/index")
    public String index() {
        return "/main/index";
    }

    @RequestMapping(value = "/welcome")
    public String welcome() {
        return "/main/welcome";
    }
}

