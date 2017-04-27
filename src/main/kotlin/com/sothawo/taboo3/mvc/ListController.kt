/*
 * (c) Copyright 2017 sothawo.com
 */
package com.sothawo.taboo3.mvc

import com.sothawo.taboo3.data.Bookmark
import com.sothawo.taboo3.data.BookmarkService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView
import java.security.Principal
import java.util.stream.Collectors

/**
 * Controller for displaying the bookmarks list.
 *
 * @author P.J. Meisch (pj.meisch@sothawo.com)
 */
@Controller
@RequestMapping("/")
class ListController
@Autowired constructor(private val sessionStore: SessionStore, private val bookmarkService: BookmarkService) {

    /**
     * returns all bookmarks to display. Takes the selection criteria from the injected SessionStorage.
     *
     * @return model data and view name
     */
    @GetMapping
    fun bookmarksList(@AuthenticationPrincipal principal: Principal?,
                      @RequestParam(value = "selectTag", required = false) selectTag: String?,
                      @RequestParam(value = "deselectTag", required = false) deselectTag: String?): ModelAndView {
        val mav = ModelAndView("list")

        val owner = principal?.name
        if (null != owner) {

            sessionStore.addSelectedTag(selectTag)
            sessionStore.removeSelectedTag(deselectTag)

            val bookmarks = mutableListOf<Bookmark>()
            val availableTags = mutableSetOf<String>()
            val selectedTags = sessionStore.selectedTags

            val searchText = sessionStore.searchText
            if (sessionStore.hasSelectCriteria()) {
                if (null != searchText && !searchText.isEmpty()) {
                    if (selectedTags.isEmpty()) {
                        bookmarks.addAll(bookmarkService.findByOwnerAndTitle(owner, searchText))
                    } else {
                        bookmarks.addAll(bookmarkService.findByOwnerAndTitleAndTags(owner, searchText, selectedTags))
                    }
                } else {
                    bookmarks.addAll(bookmarkService.findByOwnerAndTags(owner, selectedTags))
                }
                // available tags are the tags from the bookmarks which are not in the selected tags.
                availableTags.addAll(bookmarks.flatMap { it.tags }.filter { !selectedTags.contains(it) })
            } else {
                // leave bookmarks empty

                mav.addObject("bookmarksMessage", "no selection.")

                // get all available tags
                availableTags.addAll(bookmarkService.findAllTagsByOwner(owner).sorted())
            }

            // set all objects, eventually to empty lists
            mav.addObject("bookmarks", bookmarks)
            mav.addObject("availableTags", sortStrings(availableTags))
            mav.addObject("selectedTags", sortStrings(selectedTags))

            // need this to bind the form to
            mav.addObject("searchData", SearchData(searchText))
        }
        return mav
    }

    /**
     * sets the search text and calls the bookmarks method.
     *
     * @param searchData
     * *         search parameters
     * *
     * @return redirectting ModelAndView
     */
    @PostMapping("/searchText")
    fun searchText(searchData: SearchData?): ModelAndView {
        // replace blank with asterisk
        val searchText = searchData?.text?.trim()?.replace("\\W+", "*") ?: ""
        logger.info("setting search text to {}", searchText)
        sessionStore.searchText = searchText
        return ModelAndView("redirect:/")
    }

    /**
     * clears the selection data.
     *
     * @return redirecting ModelAndView
     */
    @PostMapping("/clearSelection")
    fun clearSelection(): ModelAndView {
        sessionStore.clearSelection()
        return ModelAndView("redirect:/")
    }

    /**
     * converts a String collection to a sorted list.
     *
     * @param c
     * *         the collection
     * *
     * @return the sorted list
     */
    private fun sortStrings(c: Collection<String>): List<String> {
        return c.sorted()
    }

    companion object {
        private val logger = LoggerFactory.getLogger(ListController::class.qualifiedName)
    }
}
