/*
 * (c) Copyright 2017 sothawo.com
 */
package com.sothawo.taboo3.mvc

import com.sothawo.taboo3.data.Bookmark
import com.sothawo.taboo3.data.BookmarkEdit
import com.sothawo.taboo3.data.BookmarkService
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import java.io.IOException
import java.security.Principal
import java.util.*


/**
 * Controller to handle single bookmarks (add, edit, delete).
 * @author P.J. Meisch (pj.meisch@sothawo.com)
 */
@Controller
@RequestMapping("/bookmark")
class BookmarkController
@Autowired constructor(private val bookmarkService: BookmarkService) {

    /**
     * display the given Bookmark and request deletion confirmation. If no bookmarks is found, the user is redirected
     * to the bookmark list size.
     * @return ModelAndView for the confirm page.
     */
    @GetMapping("/delete/{id}")
    fun showForDelete(@PathVariable id: String): ModelAndView {
        logger.info("delete view requested for id {}", id)
        val bookmark = bookmarkService.findById(id)
        return if (null != bookmark) ModelAndView("delete").addObject("bookmark", bookmark)
        else ModelAndView("redirect:/")
    }

    /**
     * called to do a delete.
     *
     * @param id
     * *         the id of the bookmark to delete
     * *
     * @return redirect to the list view
     */
    @PostMapping("/delete/{id}")
    fun doDelete(@PathVariable id: String): ModelAndView {
        logger.info("deleting bookmark with id {}", id)
        try {
            bookmarkService.deleteBookmark(id)
        } catch (e: Exception) {
            logger.warn(e.message)
        }

        return ModelAndView("redirect:/")
    }

    /**
     * displays a bookmark for editing.
     *
     * @param id
     * *         the id of the bookmark.
     * *
     * @return MOdelAndView for editing or redirect to home page if bookmark is not found
     */
    @GetMapping("/edit/{id}")
    fun showForEdit(@PathVariable id: String): ModelAndView {
        logger.info("edit view requested for id {}", id)
        val bookmark = bookmarkService.findById(id)
        return if (null != bookmark)
            ModelAndView("edit")
                    .addObject("bookmark", BookmarkEdit(bookmark))
                    .addObject("config",
                            AddEditConfig(caption = "edit bookmark", buttonLabel = "update", mode = "edit"))
        else ModelAndView("redirect:/")

    }

    /**
     * does the editing/adding
     *
     * @param principal
     * *         the user, needed to insert owner in add
     * *
     * @param bookmarkEdit
     * *         the edited/new  bookmark.
     * *
     * @param mode
     * *         the mode, update or add
     * *
     * @return ModelAndView with redirect to the main page.
     */
    @PostMapping("/edit")
    fun doUpdate(@AuthenticationPrincipal principal: Principal, bookmarkEdit: BookmarkEdit,
                 @RequestParam("mode") mode: String): ModelAndView {
        val url = bookmarkEdit.url
        if (url.isEmpty()) {
            return ModelAndView("redirect:/bookmark/edit/" + bookmarkEdit.originalId)
        }
        if (!url.startsWith("http")) {
            bookmarkEdit.url = "http://" + url
        }
        bookmarkEdit.owner = principal.name
        val newBookmark = bookmarkEdit.newBookmark()
        if ("edit" == mode) {
            logger.info("updating {}", newBookmark)
            bookmarkService.deleteBookmark(bookmarkEdit.originalId)
            bookmarkService.save(newBookmark)
        } else if ("add" == mode) {
            logger.info("inserting {}", newBookmark)
            bookmarkService.save(newBookmark)
        }
        return ModelAndView("redirect:/")
    }

    /**
     * shows the view for adding a bookmark.

     * @param url
     * *         optional argument to preload a url, used for bookmarklet
     * *
     * @return ModelAndView for editiing with a new empty BookmarkEdit object.
     */
    @GetMapping("/add")
    fun showForAdd(@RequestParam(required = false) url: String?): ModelAndView {
        val bookmarkEdit = BookmarkEdit()
        if (null != url) {
            bookmarkEdit.url = url
            val responseEntity = loadTitle(LoadTitleRequest(url))
            if (HttpStatus.OK == responseEntity.statusCode) {
                bookmarkEdit.title = responseEntity.body
            }
        }

        return ModelAndView("edit").addObject("bookmark", bookmarkEdit)
                .addObject("config", AddEditConfig(caption = "add bookmark", buttonLabel = "add", mode = "add"))
    }

    /**
     * extracts the title for a webpage.
     *
     * @param loadTitleRequest
     * *         the request object conatining the url.
     * *
     * @return the title or empty string if it cannot be loaded
     */
    @PostMapping("/loadtitle")
    @ResponseBody
    fun loadTitle(loadTitleRequest: LoadTitleRequest): ResponseEntity<String> {
        var url = loadTitleRequest.url
        if (null != url && !url.isEmpty()) {
            if (!url.startsWith("http")) {
                url = "http://" + url
            }
            logger.info("loading title for url {}", url)
            try {
                val htmlTitle = Jsoup
                        .connect(url)
                        .timeout(5000)
                        .userAgent(JSOUP_USER_AGENT)
                        .get()
                        .title()
                logger.info("got title: {}", htmlTitle)
                return ResponseEntity(htmlTitle, HttpStatus.OK)
            } catch (e: HttpStatusException) {
                logger.info("loading url http error", e)
                return ResponseEntity(HttpStatus.valueOf(e.statusCode))
            } catch (e: IOException) {
                logger.info("loading url error", e)
            }

        }
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    /**
     * inserts a whole array of bookmarks into the service. id values contained in the repository are recalculated by
     * setting the owner to the principal.
     *
     * @param principal
     * *         the user calling the service
     * *
     * @param bookmarks
     * *         the bookmarks to insert
     * *
     * @return status code with message
     */
    @PostMapping("/upload")
    @ResponseBody
    fun upload(@AuthenticationPrincipal principal: Principal,
               @RequestBody bookmarks: Array<Bookmark>): ResponseEntity<String> {
        logger.info("should upload {} bookmarks", bookmarks.size)
        bookmarks.forEach { it.owner = principal.name }
        bookmarkService.save(bookmarks.asList())
        return ResponseEntity("OK", HttpStatus.OK)
    }

    /**
     * dumps all bookmarks for a given principal.

     * @param principal
     * *         the principal whose bookmarks are to be dumped.
     * *
     * @return possibliy empty list of bookmarks
     */
    @GetMapping("/dump")
    @ResponseBody
    fun dumpBookmarks(@AuthenticationPrincipal principal: Principal): ResponseEntity<Collection<Bookmark>> {
        val bookmarks = bookmarkService.findByOwner(principal.name)
        return ResponseEntity(bookmarks, HttpStatus.OK)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(BookmarkController::class.java)
        /**
         * user agent that jsoup sends when fetching the page title. Some sites send 403, when no known user agent is
         * sent).
         */
        private val JSOUP_USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36"
    }
}
