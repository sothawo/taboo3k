package com.sothawo.taboo3.data

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

/**
 * @author P.J. Meisch (pj.meisch@sothawo.com)
 */
class BookmarkEditTest {
    @Test
    fun getTagsAsString() {
        val bookmark = Bookmark(owner = "owner", url = "url")
                .addTag("tag1")
                .addTag("tag3")
                .addTag("tag2")
        val bookmarkEdit = BookmarkEdit(bookmark)

        assertThat(bookmarkEdit.tagsAsString).isEqualToIgnoringCase("tag1, tag2, tag3")
    }

    @Test
    @Throws(Exception::class)
    fun setTagsAsString() {
        val bookmarkEdit = BookmarkEdit()
        bookmarkEdit.tagsAsString = "tag1, tag2; tag3 tag4"

        assertThat(bookmarkEdit.tags).containsExactlyInAnyOrder("tag1", "tag2", "tag3", "tag4")
    }
}
