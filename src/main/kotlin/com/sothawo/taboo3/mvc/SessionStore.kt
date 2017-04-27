/*
 * (c) Copyright 2017 sothawo.com
 */
package com.sothawo.taboo3.mvc


import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.context.annotation.SessionScope
import java.time.LocalDateTime

/**
 * session scoped object to store the currently selections and bookmarks.

 * @author P.J. Meisch (pj.meisch@sothawo.com)
 */
@Component
@SessionScope
class SessionStore {

    /** time when the store was created.  */
    val creationTime: LocalDateTime = LocalDateTime.now()

    /** the selected tags.  */
    val selectedTags = mutableSetOf<String>()

    /** the search text. */
    var searchText: String? = null

    /**
     * checks wether some criteria for selecting bookmarks are set.

     * @return true if criteria are set
     */
    fun hasSelectCriteria(): Boolean = selectedTags.size > 0 || !(searchText?.isEmpty() ?: true)

    /**
     * adds a tag to to the set of selected tags

     * @param tag
     * *         the tag to add, if null it is ignored
     */
    fun addSelectedTag(tag: String?) {
        if (null != tag) {
            selectedTags.add(tag)
            logger.info("added tag {} to selection {}", tag, toString())
        }
    }

    /**
     * removes a tag from the set of selected tags

     * @param tag
     */
    fun removeSelectedTag(tag: String?) {
        if (null != tag) {
            selectedTags.remove(tag)
            logger.info("removed tag {} from selection {}", tag, toString())
        }
    }

    /**
     * clears the selection data.
     */
    fun clearSelection() {
        searchText = null
        selectedTags.clear()
        logger.info("cleared selection")
    }

    override fun toString(): String {
        return "SessionStore(creationTime=$creationTime, selectedTags=$selectedTags, searchText=$searchText)"
    }

    companion object {
        private val logger = LoggerFactory.getLogger(SessionStore::class.java)
    }
}
