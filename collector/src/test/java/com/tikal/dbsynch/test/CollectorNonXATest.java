package com.tikal.dbsynch.test;

import com.tikal.dbsynch.common.SourceRow;
import org.junit.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

/**
 * Created by IntelliJ IDEA.
 * User: shalom
 * Date: Dec 9, 2009
 * Time: 1:16:16 PM
 */
@Ignore
@ContextConfiguration(locations = {"classpath:/spring/nonxa-collector.xml"})
public class CollectorNonXATest extends AbstractJUnit4SpringContextTests {

    private int rowNum = 10;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private DefaultMessageListenerContainer listenerContainer;


    //there is nothing to do here except fill some messages in the queue
    //for the collector to process them
    //its hard to unit test a listener, this unit test is used manually to debug the program

    @Before
    public void setUp() {
        jdbcTemplate.update("delete from dest_table");

        //empty the queue
//        jmsTemplate.setReceiveTimeout(JmsTemplate.RECEIVE_TIMEOUT_NO_WAIT);
//        Message message = jmsTemplate.receive();
//        while (message != null) {
//            message = jmsTemplate.receive();
//        }

        //fill the queue with some messages
        for (int i = 0; i < rowNum; i++) {
            SourceRow sourceRow = new SourceRow();
            //use current time to not have duplicate keys
            sourceRow.setId(System.currentTimeMillis());
            sourceRow.setDescription("desc " + i);
            jmsTemplate.convertAndSend(sourceRow);
        }

    }

    @Test
    public void testCollector() {
        if (!listenerContainer.isRunning()) {
            listenerContainer.start();
        }

        //do nothing, the collector listens for messages
        //we only need to wait until the collector processes all messages
        //so the proccess do not end before that

//        Object message = jmsTemplate.browse(new BrowserCallback() {
//            public Object doInJms(Session session, QueueBrowser browser) throws JMSException {
//                return browser.getEnumeration().nextElement();
//            }
//        });
//
//        while (message != null) {
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//            }
//            message = jmsTemplate.browse(new BrowserCallback() {
//                public Object doInJms(Session session, QueueBrowser browser) throws JMSException {
//                    return browser.getEnumeration().nextElement();
//                }
//            });
//        }


        //just wait some time for the collector to finish
        //if the test exits before the collector finishes then
        //not all messages are processed
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

}
