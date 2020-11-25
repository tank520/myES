package com.demo.es;

import com.demo.es.dao.ElasticEntityDao;
import com.demo.es.model.ElasticEntity;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class EsApplicationTests {

    @Resource
    private ElasticEntityDao elasticEntityDao;

    @Test
    void elasticInsertTest() {
        for (int i = 1; i < 100; i ++) {
            ElasticEntity elasticEntity = new ElasticEntity()
                    .setId((long)i).setName("tank" + i).setAge(i);

            elasticEntityDao.save(elasticEntity);
        }
    }

    @Test
    public void elasticGetTest() {
        ElasticEntity result = elasticEntityDao.findById(0L).get();
        System.out.println(result);
    }

    @Test
    public void elasticUpdateTest() {
        ElasticEntity elasticEntity = new ElasticEntity()
                .setId(1L).setName("elasticTest").setAge(100);

        ElasticEntity result = elasticEntityDao.save(elasticEntity);
    }

    @Test
    public void elasticDeleteTest() {
        ElasticEntity elasticEntity = new ElasticEntity()
                .setId(1L);

        elasticEntityDao.deleteById(1L);

        elasticEntityDao.delete(elasticEntity);
    }

}
