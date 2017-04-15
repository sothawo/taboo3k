/*
 * (c) Copyright 2017 sothawo
 */
package com.sothawo.taboo3.data

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Service to manage Bookmarks using an ElasticSearch repository.

 * @author P.J. Meisch (pj.meisch@sothawo.com)
 */
@Component
class BookmarkService @Autowired constructor(private val bookmarkRepository: BookmarkRepository) {

    /**
     * delete all entries from the repository.
     */
    fun deleteAll() = bookmarkRepository.deleteAll()

    /**
     * delete all entries for an owner.
     *
     * @param owner
     * *         the owner
     */
    fun deleteByOwner(owner: String) = bookmarkRepository.delete(bookmarkRepository.findByOwner(owner))

    /**
     * save a bookmark.
     *
     * @param bookmark
     * *         the bookmark to save
     */
    fun save(bookmark: Bookmark) {
        bookmarkRepository.save(bookmark)
    }

    /**
     * save bookmarks.
     *
     * @param bookmarks
     * *         the bookmarks to save
     */
    fun save(bookmarks: Iterable<Bookmark>) {
        bookmarkRepository.save(bookmarks)
    }


    /**
     * returns all bookmarks.
     *
     * @return collection of bookmarks
     */
    fun findAll() = bookmarkRepository.findAll().toList()

    /**
     * gets all the distinct tags from the repository.
     *
     * @return collection of tags
     *
     */
    fun findAllTags() = bookmarkRepository.findAll().flatMap { it.tags }.distinct()

    /**
     * gets all bookmarks for a given owner.
     *
     * @param owner
     * *         the owner
     * *
     * @return the bookmarks for the owner
     */
    fun findByOwner(owner: String) = bookmarkRepository.findByOwner(owner)

    /**
     * deletes a bookmark.
     *
     * @param bookmark
     * *         the bookmark to delete
     */
    fun deleteBookmark(bookmark: Bookmark) = bookmarkRepository.delete(bookmark)

    /**
     * returns all distinct tags from the bookmarks belnging to an owner.
     *
     * @param owner
     * *         the owner
     * *
     * @return the tags
     */
    fun findAllTagsByOwner(owner: String) = bookmarkRepository.findByOwner(owner).flatMap { it.tags }.distinct()

    /**
     * retrieves a Bookmark by its id
     *
     * @param id
     * *         the id
     * *
     * @return the bookmark
     */
    fun findById(id: String): Bookmark? = bookmarkRepository.findOne(id)

    /**
     * returns all bookmarks that contain a given text in their title.
     *
     * @param text
     * *         the text to search
     * *
     * @return the found bookmarks
     */
    fun findByTitle(text: String) = bookmarkRepository.findByTitleContaining(text)

    /**
     * returns all bookmarks for a owner that contain a given text in their title.
     *
     * @param owner
     * *         the ownwe
     * *
     * @param text
     * *         the text to search
     * *
     * @return the found bookmarks
     */
    fun findByOwnerAndTitle(owner: String, text: String) = bookmarkRepository.findByOwnerAndTitleContaining(owner, text)

    /**
     * finds all bookmarks which match the given tags
     *
     * @param tags
     * *         the tags to match
     * *
     * @return Collection of boookmarks
     */
    fun findByTags(tags: Collection<String>) =
            bookmarkRepository.findByTagsIn(tags).filter { it.tags.containsAll(tags) }

    /**
     * finds all bookmarks which match the given tags
     *
     * @param tags
     * *         the tags to match
     * *
     * @return Collection of boookmarks
     */
    fun findByOwnerAndTags(owner: String, tags: Collection<String>) =
            bookmarkRepository.findByOwnerAndTagsIn(owner, tags).filter { it.tags.containsAll(tags) }

    /**
     * finds all bookmarks which have a text in the title and match the given tags.
     *
     * @param text
     * *         the text to search
     * *
     * @param tags
     * *         the tags to match
     * *
     * @return Collection of boookmarks
     */
    fun findByTitleAndTags(text: String, tags: Collection<String>) =
            bookmarkRepository.findByTitleContainingAndTagsIn(text, tags).filter { it.tags.containsAll(tags) }

    /**
     * finds all bookmarks for an owner which have a text in the title and match the given tags.
     *
     * @param owner
     * *         the owner
     * *
     * @param text
     * *         the text to search
     * *
     * @param tags
     * *         the tags to match
     * *
     * @return Collection of boookmarks
     */
    fun findByOwnerAndTitleAndTags(owner: String, text: String, tags: Collection<String>) =
            bookmarkRepository.findByOwnerAndTitleContainingAndTagsIn(owner, text, tags)
                    .filter { it.tags.containsAll(tags) }

    /**
     * deletes a bookmark identified by its id
     *
     * @param id
     * *         the id
     */
    fun deleteBookmark(id: String) = bookmarkRepository.delete(id)
}
