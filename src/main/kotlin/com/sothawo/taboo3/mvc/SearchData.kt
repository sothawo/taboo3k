/*
 * (c) Copyright 2017 sothawo.com
 */
package com.sothawo.taboo3.mvc

/**
 * data for a search request.
 * @author P.J. Meisch (pj.meisch@sothawo.com)
 */
class SearchData(var text: String? = null) {
    override fun toString(): String {
        return "SearchData(text=$text)"
    }
}
