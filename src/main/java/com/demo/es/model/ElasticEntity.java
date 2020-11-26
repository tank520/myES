package com.demo.es.model;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

/**
 * @author tanhuatang
 * @version 1.0
 * @Description: 〈一句话功能简述〉
 * @Copyright: 尚德机构 All rights reserved.
 * @date 2020-11-25 15:35
 * @see
 * @since JDK1.8
 */
@Data
@Accessors(chain = true)
@Document(indexName = "elastic_entity")
public class ElasticEntity {

    @Id
    private Long id;

    @Field(type = FieldType.Text)
    private String name;

    @Field(type = FieldType.Integer)
    private Integer age;

    @Field(type = FieldType.Keyword)
    private List<String> tags;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String desc;
}
