package com.sothawo.taboo3.data

import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit4.SpringRunner

/**
 * @author P.J. Meisch (pj.meisch@sothawo.com)
 */
@RunWith(SpringRunner::class)
@SpringBootTest
@DirtiesContext
class BookmarkServiceTest {

    @Autowired
    private var bookmarkService: BookmarkService? = null

    @Before
    fun setup() {
        bookmarkService!!.deleteAll()
    }


    @Test
    fun insertAndRetrieve() {
        val bookmark = Bookmark(
                owner = "peter",
                url = "https://www.sothawo.com",
                tags = mutableSetOf("cool", "important"))

        bookmarkService!!.save(bookmark)

        val bookmarks = bookmarkService!!.findAll()
        assertThat(bookmarks).containsExactlyInAnyOrder(bookmark)
    }

    @Test
    fun sameUrlDifferentOwner() {
        val bookmark1 = Bookmark(
                owner = "peter",
                url = "https://www.sothawo.com",
                title = "this is the first entry",
                tags = mutableSetOf("cool", "important"))

        val bookmark2 = Bookmark(
                owner = "other",
                url = "https://www.sothawo.com",
                title = "this is the first entry",
                tags = mutableSetOf("cool", "important"))

        bookmarkService!!.save(listOf(bookmark1, bookmark2))

        assertThat(bookmarkService!!.findByOwner("peter")).containsOnly(bookmark1)
        assertThat(bookmarkService!!.findByOwner("other")).containsOnly(bookmark2)
    }


    @Test
    fun initiallyEmpty() {
        assertThat(bookmarkService!!.findAll()).isEmpty()
        assertThat(bookmarkService!!.findAllTags()).isEmpty()
    }

    @Test
    fun deleteExistingBookmark() {
        val bookmark1 = Bookmark(owner = "owner", url = "url1", title = "title1").addTag("tag1")
        val bookmark2 = Bookmark(owner = "owner", url = "url2", title = "title2").addTag("tag2")

        bookmarkService!!.save(listOf(bookmark1, bookmark2))
        bookmarkService!!.deleteBookmark(bookmark2)

        val bookmarks = bookmarkService!!.findAll()
        assertThat(bookmarks).containsExactlyInAnyOrder(bookmark1)
    }

    @Test
    fun deleteExistingBookmarkById() {
        val bookmark1 = Bookmark(owner = "owner", url = "url1", title = "title1").addTag("tag1")
        val bookmark2 = Bookmark(owner = "owner", url = "url2", title = "title2").addTag("tag2")

        bookmarkService!!.save(listOf(bookmark1, bookmark2))
        bookmarkService!!.deleteBookmark(bookmark2.id)

        val bookmarks = bookmarkService!!.findAll()
        assertThat(bookmarks).containsExactlyInAnyOrder(bookmark1)
    }

    @Test
    fun deleteAllBookmarkForOwner() {
        val bookmark1 = Bookmark(owner = "owner1", url = "url1", title = "title1").addTag("tag1")
        val bookmark2 = Bookmark(owner = "owner2", url = "url2", title = "title2").addTag("tag2")

        bookmarkService!!.save(listOf(bookmark1, bookmark2))
        bookmarkService!!.deleteByOwner("owner1")

        val bookmarks = bookmarkService!!.findAll()
        assertThat(bookmarks).containsExactlyInAnyOrder(bookmark2)
    }

    @Test
    fun findById() {
        val bookmark1 = Bookmark(owner = "owner1", url = "url1", title = "title1").addTag("tag1")

        bookmarkService!!.save(bookmark1)
        val bookmark = bookmarkService!!.findById(bookmark1.id)

        assertThat(bookmark).isNotNull()
        assertThat(bookmark).isEqualTo(bookmark1)
    }

    @Test
    fun findAllBookmarks() {
        val bookmark1 = Bookmark(owner = "owner1", url = "url1", title = "title1").addTag("tag1")
        val bookmark2 = Bookmark(owner = "owner2", url = "url2", title = "title2").addTag("tag2")

        bookmarkService!!.save(listOf(bookmark1, bookmark2))
        val bookmarks = bookmarkService!!.findAll()

        assertThat(bookmarks).containsExactlyInAnyOrder(bookmark1, bookmark2)
    }

    @Test
    fun findAllBookmarksForUser() {
        val bookmark1 = Bookmark(owner = "owner1", url = "url1", title = "title1").addTag("tag1")
        val bookmark2 = Bookmark(owner = "owner2", url = "url2", title = "title2").addTag("tag2")

        bookmarkService!!.save(listOf(bookmark1, bookmark2))
        val bookmarks = bookmarkService!!.findByOwner("owner1")

        assertThat(bookmarks).containsExactlyInAnyOrder(bookmark1)
    }

    @Test
    fun findAllTags() {
        val bookmark1 = Bookmark(owner = "owner", url = "url1", title = "title1").addTag("tag1").addTag("common")

        val bookmark2 = Bookmark(owner = "owner", url = "url2", title = "title2").addTag("tag2").addTag("common")

        val bookmark3 = Bookmark(owner = "owner", url = "url3", title = "title3").addTag("tag3")

        bookmarkService!!.save(listOf(bookmark1, bookmark2, bookmark3))
        val tags = bookmarkService!!.findAllTags()

        assertThat(tags).containsExactlyInAnyOrder("tag1", "tag2", "tag3", "common")
    }

    @Test
    fun findAllTagsbyOwner() {
        val bookmark1 = Bookmark(owner = "owner1", url = "url1", title = "title1").addTag("tag1").addTag("common")
        val bookmark2 = Bookmark(owner = "owner1", url = "url2", title = "title2").addTag("tag2").addTag("common")
        val bookmark3 = Bookmark(owner = "owner2", url = "url3", title = "title3").addTag("tag3")

        bookmarkService!!.save(listOf(bookmark1, bookmark2, bookmark3))
        val tags = bookmarkService!!.findAllTagsByOwner("owner1")

        assertThat(tags).containsExactlyInAnyOrder("tag1", "tag2", "common")
    }

    @Test
    fun updateBookmark() {
        val bookmarkIn = Bookmark(owner = "owner", url = "url0", title = "title0").addTag("tag0")
        bookmarkService!!.save(bookmarkIn)

        bookmarkIn.title = "title1"
        bookmarkIn.clearTags()
        bookmarkIn.addTag("tag1")
        val id = bookmarkIn.id

        bookmarkService!!.save(bookmarkIn)

        val bookmarkOut = bookmarkService!!.findById(id) ?: throw Exception("no bookmark returned")

        assertThat(bookmarkOut).isNotNull()
        assertThat(bookmarkOut.id).isEqualTo(id)
        assertThat(bookmarkOut.url).isEqualTo("url0")
        assertThat(bookmarkOut.title).isEqualTo("title1")
        assertThat<String>(bookmarkOut.tags).containsExactlyInAnyOrder("tag1")

        assertThat(bookmarkService!!.findAll()).hasSize(1)
        assertThat(bookmarkService!!.findAllTags()).hasSize(1)
    }

    @Test
    fun findBookmarkBySearchInTitle() {
        val bookmark1 = Bookmark(owner = "owner1", url = "url1", title = "Hello world")
        val bookmark2 = Bookmark(owner = "owner2", url = "url2", title = "world wide web")
        val bookmark3 = Bookmark(owner = "owner3", url = "url3", title = "say hello")
        bookmarkService!!.save(listOf(bookmark1, bookmark2, bookmark3))

        val bookmarks = bookmarkService!!.findByTitle("hello")

        assertThat(bookmarks).containsExactlyInAnyOrder(bookmark1, bookmark3)
    }

    @Test
    fun findBookmarkBySearchInTitleForOwner() {
        val bookmark1 = Bookmark(owner = "owner1", url = "url1", title = "Hello world")
        val bookmark2 = Bookmark(owner = "owner2", url = "url2", title = "world wide web")
        val bookmark3 = Bookmark(owner = "owner3", url = "url3", title = "say hello")
        bookmarkService!!.save(listOf(bookmark1, bookmark2, bookmark3))

        val bookmarks = bookmarkService!!.findByOwnerAndTitle("owner1", "hello")

        assertThat(bookmarks).containsExactlyInAnyOrder(bookmark1)
    }

    @Test
    fun findBookmarksWithTagsAnd() {
        val bookmark1 = Bookmark(owner = "owner", url = "url1", title = "title1").addTag("tag1").addTag("common")
        val bookmark2 = Bookmark(owner = "owner", url = "url2", title = "title2").addTag("tag2").addTag("common")
        val bookmark3 = Bookmark(owner = "owner", url = "url3", title = "title3").addTag("tag3")
        bookmarkService!!.save(listOf(bookmark1, bookmark2, bookmark3))

        val bookmarks = bookmarkService!!.findByTags(listOf("tag2", "common"))

        assertThat(bookmarks).containsExactlyInAnyOrder(bookmark2)
    }

    @Test
    fun findBookmarksWithOwnerAndTagsAnd() {
        val bookmark1 = Bookmark(owner = "owner1", url = "url1", title = "title1").addTag("tag1").addTag("common")
        val bookmark2 = Bookmark(owner = "owner1", url = "url2", title = "title2").addTag("tag2").addTag("common")
        val bookmark3 = Bookmark(owner = "owner1", url = "url3", title = "title3").addTag("tag3")
        val bookmark4 = Bookmark(owner = "owner2", url = "url1", title = "title1").addTag("tag1").addTag("common")
        val bookmark5 = Bookmark(owner = "owner2", url = "url2", title = "title2").addTag("tag2").addTag("common")
        val bookmark6 = Bookmark(owner = "owner2", url = "url3", title = "title3").addTag("tag3")
        bookmarkService!!.save(listOf(bookmark1, bookmark2, bookmark3, bookmark4, bookmark5, bookmark6))

        var bookmarks = bookmarkService!!.findByOwnerAndTags("owner1", listOf("tag2", "common"))
        assertThat(bookmarks).containsExactlyInAnyOrder(bookmark2)

        bookmarks = bookmarkService!!.findByOwnerAndTags("owner2", listOf("tag2", "common"))
        assertThat(bookmarks).containsExactlyInAnyOrder(bookmark5)
    }

    @Test
    fun findBookmarksBySearchInTitleAndTags() {
        val bookmark1 = Bookmark(owner = "owner", url = "url1", title = "Hello world").addTag("tag1")
        val bookmark2 = Bookmark(owner = "owner", url = "url2", title = "world wide web").addTag("tag1").addTag("tag2")

        val bookmark3 = Bookmark(owner = "owner", url = "url3", title = "say hello").addTag("tag3")

        bookmarkService!!.save(listOf(bookmark1, bookmark2, bookmark3))

        val bookmarks = bookmarkService!!.findByTitleAndTags("hello", listOf("tag1"))

        assertThat(bookmarks).containsExactlyInAnyOrder(bookmark1)
    }

    @Test
    fun findBookmarksWithOwnerBySearchInTitleAndTags() {
        val bookmark1 = Bookmark(owner = "owner", url = "url1", title = "Hello world").addTag("tag1")
        val bookmark2 = Bookmark(owner = "owner", url = "url2", title = "world wide web").addTag("tag1").addTag("tag2")

        val bookmark3 = Bookmark(owner = "owner", url = "url3", title = "say hello").addTag("tag3")

        val bookmark4 = Bookmark(owner = "owner2", url = "url1", title = "Hello world").addTag("tag1")
        val bookmark5 = Bookmark(owner = "owner2", url = "url2", title = "world wide web").addTag("tag1").addTag("tag2")

        val bookmark6 = Bookmark(owner = "owner2", url = "url3", title = "say hello").addTag("tag3")

        bookmarkService!!.save(listOf(bookmark1, bookmark2, bookmark3, bookmark4, bookmark5, bookmark6))

        val bookmarks = bookmarkService!!.findByOwnerAndTitleAndTags("owner", "hello", listOf("tag1"))

        assertThat(bookmarks).containsExactlyInAnyOrder(bookmark1)
    }
}
