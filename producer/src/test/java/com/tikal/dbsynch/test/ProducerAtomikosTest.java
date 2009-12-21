package com.tikal.dbsynch.test;

import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.Ignore;
import com.tikal.dbsynch.producer.Producer;

/**
 * Created by IntelliJ IDEA.
 * User: shalom
 * Date: Dec 9, 2009
 * Time: 12:32:23 AM
 */
//this test will not pass
@Ignore
@ContextConfiguration(locations =
        {"classpath:/spring/atomikos-xa-producer.xml"})
public class ProducerAtomikosTest extends AbstractJUnit4SpringContextTests {

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