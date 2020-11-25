package com.demo.es.controller;

import com.demo.es.dao.ElasticEntityDao;
import com.demo.es.model.ElasticEntity;
import com.demo.es.vo.ElasticEntityVO;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author tanhuatang
 * @version 1.0
 * @Description: 〈一句话功能简述〉
 * @Copyright: 尚德机构 All rights reserved.
 * @date 2020-11-25 17:02
 * @see
 * @since JDK1.8
 */
@RestController
@RequestMapping(value = "/estest")
public class EsTestController {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Resource
    private ElasticEntityDao elasticEntityDao;

    @RequestMapping(value = "/create-index", method = RequestMethod.POST)
    public Object createEsIndex() {
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(ElasticEntity.class);
        Document document = indexOperations.createMapping(ElasticEntity.class);
        boolean index = indexOperations.create();
        System.out.println("创建索引结果是" + index);
        System.out.println("创建文档结果是" + document);
        return index;
    }

    @RequestMapping(value = "/delete-index", method = RequestMethod.POST)
    public Object deleteEsIndex() {
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(ElasticEntity.class);
        boolean deleteIndex = indexOperations.delete();
        System.out.println("删除索引结果是" + deleteIndex);
        return deleteIndex;
    }

    @RequestMapping(value = "/exist-index", method = RequestMethod.GET)
    public Object existEsIndex() {
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(ElasticEntity.class);
        boolean existsIndex = indexOperations.exists();
        System.out.println("是否存在的结果是" + existsIndex);
        return existsIndex;
    }

    @RequestMapping(value = "/save-doc", method = RequestMethod.POST)
    public ElasticEntity saveEsDoc(@RequestBody ElasticEntity ElasticEntity) {
        ElasticEntity result = elasticEntityDao.save(ElasticEntity);
        return result;
    }

    @RequestMapping(value = "/query-doc", method = RequestMethod.GET)
    public List<ElasticEntity> queryByName(String name) {
        List<ElasticEntity> result = elasticEntityDao.queryESUserByName(name);
        return result;
    }

    @RequestMapping(value = "/exist-doc", method = RequestMethod.GET)
    public Object existDoc(Long id) {
        return elasticEntityDao.existsById(id);
    }

    //---------------- 复杂查询 ------------------
    @RequestMapping(value = "/query-doc/complex", method = RequestMethod.POST)
    public Object queryByName(@RequestBody ElasticEntity ElasticEntity) {
        String desc = ElasticEntity.getDesc();
        List<String> tags = ElasticEntity.getTags();
        String name = ElasticEntity.getName();
        // 先构建查询条件
        BoolQueryBuilder defaultQueryBuilder = QueryBuilders.boolQuery();
        if (StringUtils.isNotBlank(desc)) {
            defaultQueryBuilder.should(QueryBuilders.termQuery("desc", desc));
        }
        if (StringUtils.isNotBlank(name)) {
            defaultQueryBuilder.should(QueryBuilders.termQuery("name", name));
        }
        if (!CollectionUtils.isEmpty(tags)) {
            for (String tag : tags) {
                defaultQueryBuilder.must(QueryBuilders.termQuery("tags", tag));
            }
        }

        // 分页条件
        PageRequest pageRequest = PageRequest.of(0, 10);
        // 高亮条件
        HighlightBuilder highlightBuilder = getHighlightBuilder("desc", "tags");
        // 排序条件
        FieldSortBuilder sortBuilder = SortBuilders.fieldSort("age").order(SortOrder.DESC);
        //组装条件
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(defaultQueryBuilder)
                .withHighlightBuilder(highlightBuilder)
                .withPageable(pageRequest)
                .withSort(sortBuilder).build();

        SearchHits<ElasticEntity> searchHits = elasticsearchRestTemplate.search(searchQuery, ElasticEntity.class);

        // 高亮字段映射
        List<ElasticEntityVO> userVoList = Lists.newArrayList();
        for (SearchHit<ElasticEntity> searchHit : searchHits) {
            ElasticEntity content = searchHit.getContent();
            ElasticEntityVO esUserVo = new ElasticEntityVO();
            BeanUtils.copyProperties(content, esUserVo);
            Map<String, List<String>> highlightFields = searchHit.getHighlightFields();
            for (String highlightField : highlightFields.keySet()) {
                if (StringUtils.equals(highlightField, "tags")) {
                    esUserVo.setTags(highlightFields.get(highlightField));
                } else if (StringUtils.equals(highlightField, "desc")) {
                    esUserVo.setDesc(highlightFields.get(highlightField).get(0));
                }

            }
            userVoList.add(esUserVo);
        }

        // 组装分页对象
        Page<ElasticEntityVO> userPage = new PageImpl<>(userVoList, pageRequest, searchHits.getTotalHits());

        return userPage;
    }

    // 设置高亮字段
    private HighlightBuilder getHighlightBuilder(String... fields) {
        // 高亮条件
        HighlightBuilder highlightBuilder = new HighlightBuilder(); //生成高亮查询器
        for (String field : fields) {
            highlightBuilder.field(field);//高亮查询字段
        }
        highlightBuilder.requireFieldMatch(false);     //如果要多个字段高亮,这项要为false
        highlightBuilder.preTags("<span style=\"color:red\">");   //高亮设置
        highlightBuilder.postTags("</span>");
        //下面这两项,如果你要高亮如文字内容等有很多字的字段,必须配置,不然会导致高亮不全,文章内容缺失等
        highlightBuilder.fragmentSize(800000); //最大高亮分片数
        highlightBuilder.numOfFragments(0); //从第一个分片获取高亮片段

        return highlightBuilder;
    }

}
