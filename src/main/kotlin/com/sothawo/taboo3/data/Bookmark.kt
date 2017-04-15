package com.sothawo.taboo3.data

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.PersistenceConstructor
import org.springframework.data.elasticsearch.annotations.Document
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * The bookmark POJO. Tags when added are converted to lowercase and duplicate tags are removed. The Id is built by
 * concatenating the owner and the url.
 *
 * @param owner the owener of the bookmark.
 * @param url the url of the bookmark.
 * @param title the title of the bookmark.
 *
 * @author P.J. Meisch (pj.meisch@sothawo.com).
 */
@Document(indexName = "bookmarks")
class Bookmark @PersistenceConstructor constructor(owner: String, val url: String, var title: String = "",
                                                   val tags: MutableSet<String> = mutableSetOf<String>()) {

    /** the id of the bookmark. */
    @Id
    var id: String = ""

    val owner = owner.toLowerCase()

    /**
     * initialize the id
     */
    init {
        buildId()
    }

    /**
     * adds the given tag in lowercase to the internal collection, if it is not already present.
     *
     * @param tag
     *         new tag
     */
    infix fun addTag(tag: String): Bookmark {
        val trimmed = tag.trim()
        if (!trimmed.isEmpty()) {
            tags.add(trimmed.toLowerCase())
        }
        return this
    }

    /**
     * build the bookmarks id from the owner and url properties.
     */
    private fun buildId() {
        val s = owner.toLowerCase() + '-' + url
        md5!!.update(StandardCharsets.UTF_8.encode(s))
        id = String.format("%032x", BigInteger(1, md5!!.digest()))
    }

    override fun toString() = "Bookmark(owner='$owner', url='$url', title='$title', id='$id', tags=$tags)"

    /**
     * equality is based on the id.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as Bookmark
        return id == other.id
    }

    /**
     * hashcode based on id.
     */
    override fun hashCode() = id.hashCode()


    /**
     * clear the tag set.
     */
    fun clearTags() = tags.clear()

    /**
     * companion object containing the message digest for creating the bookmark id.
     */
    companion object {
        private var md5: MessageDigest?

        init {
            try {
                md5 = MessageDigest.getInstance("MD5")
            } catch (e: NoSuchAlgorithmException) {
                throw ExceptionInInitializerError(e)
            }
        }
    }

    /**
     * join all strings together in ascendig order, separated by comma and space.
     *
     * @return all Strings joined
     */
    fun joinedTags() = tags.sorted().joinToString(", ")
}
