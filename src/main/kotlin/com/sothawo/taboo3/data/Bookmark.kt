package com.sothawo.taboo3.data

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
class Bookmark(owner: String, url: String, var title: String = "") {
    /** the id of the bookmark. */
    var id: String = ""

    /** the owner of the bookmark. */
    var owner: String = ""
        set(value) {
            field = value.toLowerCase()
            buildId()
        }

    /** the url of the bookmark. */
    var url = ""
        set(value) {
            field = value
            buildId()
        }

    /** the tags of the bookmark. internally mutable, unmutable to the world. */
    private val _tags = mutableSetOf<String>()
    val tags get() = _tags.toSet()

    /**
     * initialize the object by calling the setters
     */
    init {
        this.owner = owner
        this.url = url
    }

    /**
     * adds the given tag in lowercase to the internal collection, if it is not already present.
     *
     * @param tag
     *         new tag
     */
    infix fun addTag(tag: String): Bookmark {
        _tags.add(tag.toLowerCase())
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

        if (id != other.id) return false

        return true
    }

    /**
     * hashcode based on id.
     */
    override fun hashCode() = id.hashCode()


    /**
     * clear the tag set.
     */
    fun clearTags() = _tags.clear()

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
