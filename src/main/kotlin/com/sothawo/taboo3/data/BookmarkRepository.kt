/*
 * (c) Copyright 2017 sothawo
 */
package com.sothawo.taboo3.data;

import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;


/**
 * @author P.J. Meisch (pj.meisch@sothawo.com)
 */
interface BookmarkRepository : ElasticsearchCrudRepository<Bookmark, String> {

    fun findByOwner(owner: String): Collection<Bookmark>
    fun findByTitleContaining(text: String): Collection<Bookmark>
    fun findByOwnerAndTitleContaining(owner: String, text: String): Collection<Bookmark>
    fun findByTagsIn(tags: Collection<String>): Collection<Bookmark>
    fun findByOwnerAndTagsIn(owner: String, tags: Collection<String>): Collection<Bookmark>
    fun findByTitleContainingAndTagsIn(text: String, tags: Collection<String>): Collection<Bookmark>
    fun findByOwnerAndTitleContainingAndTagsIn(owner: String, text: String, tags: Collection<String>):
            Collection<Bookmark>
}
