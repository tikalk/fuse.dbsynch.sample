package com.tikal.dbsynch.producer;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.connection.JmsTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.TransactionStatus;

import java.util.List;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.tikal.dbsynch.common.SourceRow;
import com.tikal.dbsynch.common.transactions.TransactionUtil;
import com.tikal.dbsynch.common.transactions.InvokationCallbackWithoutResult;

import javax.jms.Message;
import javax.jms.Session;
import javax.jms.JMSException;
import javax.annotation.PostConstruct;


/**
 * Created by IntelliJ IDEA.
 * User: shalom
 * Date: Nov 22, 2009
 * Time: 3:11:41 PM
 */
public class Producer {

    private JdbcTemplate jdbcTemplate;
    private JmsTemplate jmsTemplate;
    private int rowsLimit = 1;
    private TransactionTemplate jmsTransactionTemplate;

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void setJmsTransactionTemplate(TransactionTemplate jmsTransactionTemplate) {
        this.jmsTransactionTemplate = jmsTransactionTemplate;
    }

    public void setRowsLimit(int rowsLimit) {
        this.rowsLimit = rowsLimit;
    }


    @PostConstruct
    public void init(){
        //this is not really necessary if the context configuration is
        //not changing those properties
//        jmsTemplate.setSessionTransacted(false);
//        jmsTemplate.setSessionAcknowledgeMode(Session.AUTO_ACKNOWLEDGE);
    }


    //NOTE that this method does not handle the case of multiple
    //processes running against the same table.


    //two ways to implement this method.
    //the first one makes sure that the jms message is sent
    //just before the transaction manager commits.
    //the second sends the jms message immediately,but actually
    //we may have an exception before commit and then we sent the message
    //but didn't delete the row.
    //the first option minimizes the chance of sending the jms message
    //without deleting the row.

    //@Transactional(propagation = Propagation.REQUIRES_NEW,isolation = Isolation.SERIALIZABLE)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void process() {
        String sql = "select * from source_table LIMIT " + rowsLimit;

        List<SourceRow> sources = getRows(sql);
        if (sources == null) {
            return;
        }
        for (final SourceRow sourceRow : sources) {
            TransactionUtil.doBeforeCommit(new InvokationCallbackWithoutResult() {
                public void invokeWithoutResult() {
                    jmsTemplate.send(new MessageCreator() {
                        public Message createMessage(Session session) throws JMSException {
                            return session.createObjectMessage(sourceRow);
                        }
                    });
                }
            });

            jdbcTemplate.update("delete from source_table where id = ?", new Object[]{sourceRow.getId()});
        }

    }

    //@Transactional(propagation = Propagation.REQUIRES_NEW,isolation = Isolation.SERIALIZABLE)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void process_2() {
        String sql = "select * from source_table LIMIT " + rowsLimit;

        List<SourceRow> sources = getRows(sql);
        if (sources == null) {
            return;
        }
        for (final SourceRow sourceRow : sources) {
            //the message is sent immediatly
            jmsTemplate.send(new MessageCreator() {
                public Message createMessage(Session session) throws JMSException {
                    return session.createObjectMessage(sourceRow);
                }
            });

            jdbcTemplate.update("delete from source_table where id = ?", new Object[]{sourceRow.getId()});
        }

    }





//    public void process() {
//        String sql = "select * from source_table LIMIT " + rowsLimit;
//
//        List<SourceRow> sources = getRows(sql);
//        if (sources == null) {
//            return;
//        }
//        for (final SourceRow sourceRow : sources) {
//            jmsTransactionTemplate.execute(new TransactionCallback() {
//                public Object doInTransaction(TransactionStatus status) {
//                    jmsTemplate.send(new MessageCreator() {
//                        public Message createMessage(Session session) throws JMSException {
//                            return session.createObjectMessage(sourceRow);
//                        }
//                    });
//                    return null;
//                }
//            });
//
//            jdbcTemplate.update("delete from source_table where id = ?", new Object[]{sourceRow.getId()});
//        }
//
//    }

    private List<SourceRow> getRows(String sql) {
        List<SourceRow> source = jdbcTemplate.query(sql, new RowMapper() {
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                SourceRow sourceRow = new SourceRow();
                sourceRow.setId(rs.getLong("ID"));
                sourceRow.setDescription(rs.getString("DESC"));
                return sourceRow;
            }
        });
        return source;
    }
}
