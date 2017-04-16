/*
 * (c) Copyright 2017 sothawo
 */
package com.sothawo.taboo3.data

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

/**
 * Component to setup a couple of Bookmarks for users peter and work if the repository is empty.

 * @author P.J. Meisch (pj.meisch@sothawo.com)
 */
@Component
class TestInitializer @Autowired constructor(private val bookmarkService: BookmarkService) {

    @PostConstruct
    fun createBookmarks() {
        createBookmarksPeterIfNecessary()
        createBookmarksWorkfNecessary()
    }

    private fun createBookmarksPeterIfNecessary() {
        val owner = "peter"
        val numBookmarks = bookmarkService.findByOwner(owner).size
        logger.info("repository has {} entries for user {}", numBookmarks, owner)
        if (0 == numBookmarks) {
            logger.info("setting up repository")
            val bookmark1 = Bookmark(
                    owner = owner,
                    url = "https://www.sothawo.com",
                    title = "P.J.'s own website")
                    .addTag("sothawo").addTag("blog")
            val bookmark2 = Bookmark(
                    owner = owner,
                    url = "http://stackoverflow.com/users/4393565/p-j-meisch?tab=topactivity",
                    title = "P.J. at StackOverflow")
                    .addTag("sothawo").addTag("programming")
            val bookmark3 = Bookmark(owner = owner,
                    url = "https://github.com/sothawo",
                    title = "P.J. at GitHUb")
                    .addTag("sothawo").addTag("programming")
            bookmarkService.save(listOf(bookmark1, bookmark2, bookmark3))

            logger.info("repository now has {} entries for user {}", bookmarkService.findByOwner(owner).size, owner)
        }
        bookmarkService.findByOwner(owner).forEach { logger.debug(it.toString()) }
    }

    private fun createBookmarksWorkfNecessary() {
        val owner = "work"
        val numBookmarks = bookmarkService.findByOwner(owner).size
        logger.info("repository has {} entries for user {}", numBookmarks, owner)
        if (0 == numBookmarks) {
            logger.info("setting up repository")
            val bookmark1 = Bookmark(
                    owner = owner,
                    url = "http://www.postdirekt.de",
                    title = "Deutsche Post Direkt")
                    .addTag("work")
            val bookmark2 = Bookmark(
                    owner = owner,
                    url = "http://www.jaroso.de",
                    title = "Jaroso")
                    .addTag("work")
            val bookmark3 = Bookmark(
                    owner = owner,
                    url = "http://www.hlx.com",
                    title = "HLX")
                    .addTag("work")
            bookmarkService.save(listOf(bookmark1, bookmark2, bookmark3))

            logger.info("repository now has {} entries for user {}", bookmarkService.findByOwner(owner).size, owner)
        }
        bookmarkService.findByOwner(owner).forEach { logger.debug(it.toString()) }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(TestInitializer::class.qualifiedName)
    }
}
