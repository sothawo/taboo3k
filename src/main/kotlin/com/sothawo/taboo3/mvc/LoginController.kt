/*
 * (c) Copyright 2017 sothawo.com
 */
package com.sothawo.taboo3.mvc

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView

/**
 * @author P.J. Meisch (pj.meisch@sothawo.com)
 */
@Controller
@RequestMapping("/login")
class LoginController {

    /**
     * returns the model and view for the login page.
     *
     * @return view and model with messages.
     */
    @GetMapping
    fun login(@RequestParam(value = "error", required = false) error: String?,
              @RequestParam(value = "logout", required = false) logout: String?): ModelAndView {
        val mav = ModelAndView("login")
        if (null != error) {
            mav.addObject("errorText", "invalid login data");
        }
        if (null != logout) {
            mav.addObject("msg", "successfully logged out.");
        }
        return mav
    }
}
