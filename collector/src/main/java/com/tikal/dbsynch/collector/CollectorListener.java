package com.tikal.dbsynch.collector;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.jms.listener.SessionAwareMessageListener;

import javax.jms.*;

import com.tikal.dbsynch.common.SourceRow;
import com.tikal.dbsynch.common.transactions.TransactionUtil;
import com.tikal.dbsynch.common.transactions.InvokationCallbackWithoutResult;

/**
 * Created by IntelliJ IDEA.
 * User: shalom
 * Date: Nov 22, 2009
 * Time: 4:23:47 PM
 */
public class CollectorListener implements MessageListener {

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    //this method should be set with sessionTransacted=true on the listener container
    //the jms session will be committed by the transaction manager after the DB commit.
    //the drawback is that it is not jms standard but is Spring behavior 
    //@Transactional(propagation = Propagation.REQUIRES_NEW,isolation = Isolation.SERIALIZABLE)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onMessage(Message message) {

        try {
            ObjectMessage objectMessage = (ObjectMessage) message;
            SourceRow sourceRow = (SourceRow) objectMessage.getObject();

            String sql = "insert into dest_table values(?,?)";
            jdbcTemplate.update(sql,new Object[]{sourceRow.getId(),sourceRow.getDescription()});

        } catch (JMSException e) {
            throw new RuntimeException(e);
        }

    }


    //this method should be set with sessionTransacted=false and
    //sessionAcknowledgeMode=CLIENT_ACKNOWLEDGE.
    //the advantage is that it don't relay on Spring's transaction manager to commit the jms session
//    @Transactional(propagation = Propagation.REQUIRES_NEW)
//    public void onMessage(final Message message){
//        try {
//            ObjectMessage objectMessage = (ObjectMessage) message;
//            SourceRow sourceRow = (SourceRow) objectMessage.getObject();
//
//            String sql = "insert into dest_table values(?,?)";
//            jdbcTemplate.update(sql,new Object[]{sourceRow.getId(),sourceRow.getDescription()});
//
//            TransactionUtil.doAfterCommit(new InvokationCallbackWithoutResult(){
//                public void invokeWithoutResult() {
//                    try {
//                        message.acknowledge();
//                    } catch (JMSException e) {
//                        //todo:recover
//                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//                    }
//                }
//            });
//
//        } catch (JMSException e) {
//            throw new RuntimeException(e);
//        }
//    }

    //to use this method implement SessionAwareMessageListener
    //this method should be set with sessionTransacted=false on the
    //listener container, the session is committed localy
//    @Transactional(propagation = Propagation.REQUIRES_NEW)
//    public void onMessage(Message message, final Session session) throws JMSException {
//        try {
//            ObjectMessage objectMessage = (ObjectMessage) message;
//            SourceRow sourceRow = (SourceRow) objectMessage.getObject();
//
//            String sql = "insert into dest_table values(?,?)";
//            jdbcTemplate.update(sql,new Object[]{sourceRow.getId(),sourceRow.getDescription()});
//
//            TransactionUtil.doAfterCommit(new InvokationCallbackWithoutResult(){
//                public void invokeWithoutResult() {
//                    try {
//                        session.commit();
//                    } catch (JMSException e) {
//                        //todo:recover
//                        e.printStackTrace();
//                    }
//                }
//            });
//
//        } catch (JMSException e) {
//            throw new RuntimeException(e);
//        }
//
//    }






}
