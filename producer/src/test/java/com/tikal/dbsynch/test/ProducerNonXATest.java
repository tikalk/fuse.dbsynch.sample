package com.tikal.dbsynch.test;

import com.tikal.dbsynch.producer.Producer;
import org.junit.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

/**
 * Created by IntelliJ IDEA.
 * User: shalom
 * Date: Dec 9, 2009
 * Time: 12:32:23 AM
 */
@Ignore
@ContextConfiguration(locations = {"classpath:/spring/nonxa-producer.xml"})
public class ProducerNonXATest extends AbstractJUnit4SpringContextTests {

    private static int rowNum = 10;

    @Autowired
    private Producer producer;


    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Before
    public void insertRows() {
        jdbcTemplate.update("delete from source_table");
        
        for (int i = 0; i < rowNum; i++) {
            long id = System.currentTimeMillis() + i;
            jdbcTemplate.update("insert into source_table values (" + id + ",'Description" + i + "')");
        }
    }

    @Test
    public void testProducer() {

        for (int i = 0; i < rowNum; i++) {
            producer.process();
        }
    }


    @After
    public void deleteTable() {
        jdbcTemplate.update("delete from source_table");
    }
}
