package com.sothawo.taboo3.data

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

/**
 * @author P.J. Meisch (pj.meisch@sothawo.com)
 */
class BookmarkTest {
    @Test
    fun ctorSetsAllFields() {
        val bookmark = Bookmark(owner = "owner", url = "url", title = "title")

        assertThat(bookmark.id).isNotNull().isNotEmpty()
        assertThat(bookmark.owner).isEqualTo("owner")
        assertThat(bookmark.title).isEqualTo("title")
        assertThat(bookmark.url).isEqualTo("url")
        assertThat(bookmark.tags).isEmpty()
    }

    @Test
    fun addTags() {
        val bookmark = Bookmark(owner = "owner", url = "url", title = "title")
        bookmark addTag "tag1" addTag "tag2"

        assertThat(bookmark.id).isNotNull().isNotEmpty()
        assertThat(bookmark.owner).isEqualTo("owner")
        assertThat(bookmark.title).isEqualTo("title")
        assertThat(bookmark.url).isEqualTo("url")
        assertThat(bookmark.tags).containsExactlyInAnyOrder("tag1", "tag2")
    }

    @Test
    fun twoBookmarksWithSameOwnerAndUrlAreEqual() {
        val bookmark1 = Bookmark(owner = "own", url = "uurrll")
        val bookmark2 = Bookmark(owner = "own", url = "uurrll")

        assertThat(bookmark1).isNotSameAs(bookmark2)
        assertThat(bookmark1).isEqualTo(bookmark2)
    }

    @Test
    fun tagsAreLowercase() {
        val tags = Bookmark(owner = "owner", url = "url").addTag("ABC").tags

        assertThat(tags).containsExactly("abc")
    }

    @Test
    fun ownerIsLowercase() {
        val bookmark = Bookmark(owner = "OWNER", url = "url")

        assertThat(bookmark.owner).isEqualTo("owner")
    }

    @Test
    fun tagsHaveNoDuplicates() {
        val tags = Bookmark(owner = "owner", url = "url").addTag("ABC").addTag("abc").tags

        assertThat<String>(tags).containsExactly("abc")
    }

    @Test
    fun clearTags() {
        val bookmark = Bookmark(owner = "owner", url = "url", title = "title").addTag("tag1").addTag("tag2")

        assertThat<String>(bookmark.tags).containsExactlyInAnyOrder("tag1", "tag2")

        bookmark.clearTags()
        assertThat<String>(bookmark.tags).isEmpty()
    }

    @Test
    fun newOwnerChangesId() {
        val bookmark = Bookmark(owner = "owner", url = "url")
        val id = bookmark.id
        bookmark.owner = "newowner"

        assertThat(bookmark.id).isNotEqualTo(id)
    }

    @Test
    fun newUrlChangesId() {
        val bookmark = Bookmark(owner = "owner", url = "url")
        val id = bookmark.id
        bookmark.url = "newurl"

        assertThat(bookmark.id).isNotEqualTo(id)
    }

    @Test
    fun joinTags() {
        val bookmark = Bookmark(owner = "owner", url = "url") addTag "world" addTag "hello"

        assertThat(bookmark.joinedTags()).isEqualTo("hello, world")
    }
}
