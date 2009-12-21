package com.tikal.dbsynch.test;

import com.tikal.dbsynch.common.SourceRow;
import org.junit.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.transaction.*;
import org.springframework.transaction.support.*;

/**
 * Created by IntelliJ IDEA.
 * User: shalom
 * Date: Dec 9, 2009
 * Time: 1:16:16 PM
 */
@Ignore
@ContextConfiguration(locations = {"classpath:/spring/atomikos-xa-collector.xml"})
public class CollectorAtomikosTest extends AbstractJUnit4SpringContextTests{

    private int rowNum = 10;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private DefaultMessageListenerContainer listenerContainer;


    private TransactionTemplate transactionTemplate;

    @Autowired
    private void setTransactionManager(PlatformTransactionManager transactionManager){
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    //there is nothing to do here except fill some messages in the queue
    //for the collector to process them
    //its hard to unit test a listener, this unit test is used manually to debug the program

    @Before
    //this must be transactional because jmsTemplate must be run in a transaction when using jta.
    public void setUp() {
        //fill the queue with some messages
        transactionTemplate.execute(new TransactionCallback(){
            public Object doInTransaction(TransactionStatus status) {

                jdbcTemplate.update("delete from dest_table");

                for (int i = 0; i < rowNum; i++) {
                    SourceRow sourceRow = new SourceRow();
                    //use current time to not have duplicate keys
                    sourceRow.setId(System.currentTimeMillis());
                    sourceRow.setDescription("desc " + i);
                    jmsTemplate.convertAndSend(sourceRow);
                }
                return null;
            }
        });


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