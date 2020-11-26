package com.demo.es.controller;

import com.demo.es.model.ElasticEntity;
import com.demo.es.service.ElasticEntityService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

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

    @Resource
    private ElasticEntityService elasticEntityService;

    @RequestMapping(value = "/create-index", method = RequestMethod.POST)
    public Object createEsIndex() {
        return elasticEntityService.createIndex();
    }

    @RequestMapping(value = "/delete-index", method = RequestMethod.POST)
    public Object deleteEsIndex() {
        return elasticEntityService.deleteEsIndex();
    }

    @RequestMapping(value = "/exist-index", method = RequestMethod.GET)
    public Object existEsIndex() {
        return elasticEntityService.existEsIndex();
    }

    @RequestMapping(value = "/save-doc", method = RequestMethod.POST)
    public ElasticEntity saveEsDoc(@RequestBody ElasticEntity elasticEntity) {
        return elasticEntityService.saveEsDoc(elasticEntity);
    }

    @RequestMapping(value = "/query-doc", method = RequestMethod.GET)
    public List<ElasticEntity> queryByName(String name) {
        return elasticEntityService.queryByName(name);
    }

    @RequestMapping(value = "/exist-doc", method = RequestMethod.GET)
    public Object existDoc(Long id) {
        return elasticEntityService.existDoc(id);
    }

    @RequestMapping(value = "/query-doc/complex", method = RequestMethod.POST)
    public Object queryByName(@RequestBody ElasticEntity elasticEntity) {
        return elasticEntityService.queryByName(elasticEntity);
    }

}
