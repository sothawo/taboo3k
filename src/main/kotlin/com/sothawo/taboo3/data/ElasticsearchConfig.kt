package com.sothawo.taboo3.data

import com.fasterxml.jackson.databind.ObjectMapper
import org.elasticsearch.client.Client
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.elasticsearch.core.DefaultResultMapper
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate
import org.springframework.data.elasticsearch.core.EntityMapper
import org.springframework.stereotype.Component

/**
 * code from http://stackoverflow.com/questions/40959722/why-doesnt-spring-data-elasticsearch-use-the-objectmapper-from-the-spring-conte
 *
 * to make spring-data-elasticsearch use ObjectMapper from Spring which uses jackson-module-kotlin.
 *
 * @author P.J. Meisch (pj.meisch@sothawo.com)
 */
@Configuration
open class ElasticsearchConfig(val elasticsearchEntityMapper: ElasticsearchEntityMapper) {

    @Bean
    open fun elasticsearchTemplate(client: Client) = ElasticsearchTemplate(client, DefaultResultMapper(elasticsearchEntityMapper))

}

@Component
class ElasticsearchEntityMapper(val objectMapper: ObjectMapper) : EntityMapper {
    override fun mapToString(`object`: Any?) = objectMapper.writeValueAsString(`object`)

    override fun <T : Any?> mapToObject(source: String?, clazz: Class<T>?) = objectMapper.readValue(source, clazz)

}
