/*
 * (c) Copyright 2017 sothawo.com
 */
package com.sothawo.taboo3.mvc

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ModelAttribute

import java.security.Principal

/**
 * @author P.J. Meisch (pj.meisch@sothawo.com)
 */
@ControllerAdvice
class AllControllers {

    @ModelAttribute("userName")
    fun addUserName(@AuthenticationPrincipal principal: Principal?): String? {
        return principal?.name
    }
}
