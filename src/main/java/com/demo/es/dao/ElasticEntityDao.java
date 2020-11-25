package com.demo.es.dao;

import com.demo.es.model.ElasticEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * @author tanhuatang
 * @version 1.0
 * @Description: 〈一句话功能简述〉
 * @Copyright: 尚德机构 All rights reserved.
 * @date 2020-11-25 15:43
 * @see
 * @since JDK1.8
 */
public interface ElasticEntityDao extends ElasticsearchRepository<ElasticEntity,Long> {

    long deleteESUserByName(String name);

    List<ElasticEntity> queryESUserByName(String name);
}
