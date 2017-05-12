package com.myndyk.repository.search;

import com.myndyk.domain.WishList;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the WishList entity.
 */
public interface WishListSearchRepository extends ElasticsearchRepository<WishList, Long> {
}
