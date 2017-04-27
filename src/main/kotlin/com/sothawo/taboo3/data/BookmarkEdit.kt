/*
 * (c) Copyright 2017 sothawo.com
 */
package com.sothawo.taboo3.data

/**
 * A Bookmark class that has the tags as joined string, and that has editable properties.
 * @param  bookmark with wich the data is initialized
 * @author P.J. Meisch (pj.meisch@sothawo.com)
 */
class BookmarkEdit(bookmark: Bookmark?) {
    constructor() : this(null)

    /** the id of the original bookmark.  */
    var originalId = bookmark?.id ?: ""
    var owner = bookmark?.owner ?: ""
    var url = bookmark?.url ?: ""
    var title = bookmark?.title ?: ""
    val tags = bookmark?.tags ?: mutableSetOf<String>()

    var tagsAsString = bookmark?.joinedTags() ?: ""
        set(value) {
            tags.clear()
            val list = value.split("[,;\\s]+".toRegex())
            list.forEach { tags.add(it) }
        }

    override fun toString(): String {
        return "BookmarkEdit(originalId='$originalId', owner='$owner', tags=$tags, url='$url', title='$title', tagsAsString='$tagsAsString')"
    }

    /**
     * create a new Bookmark object from the current data
     */
    fun newBookmark() = Bookmark(owner, url, title, tags)

}
