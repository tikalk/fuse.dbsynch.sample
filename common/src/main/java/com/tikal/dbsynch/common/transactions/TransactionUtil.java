package com.tikal.dbsynch.common.transactions;

import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;

/**
 * Created by IntelliJ IDEA.
 * User: shalom
 * Date: Dec 9, 2009
 * Time: 6:45:15 PM
 */
public class TransactionUtil
{
    /**
     * this method checks if there is a transaction active and registers
     * an afterCommit invokation of the InvokationCallback.
     * if there is no transaction active it invokes the InvokationCallback immediately.
     *
     * @param invokationCallback
     */
    public static void doAfterCommit(final InvokationCallback invokationCallback){
       //currentlly supports spring synchronization
        if(TransactionSynchronizationManager.isActualTransactionActive()){
           TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter(){
               @Override
               public void afterCommit()
               {
                   invokationCallback.invoke();
               }
           });
        }else{
            //invoke now
            invokationCallback.invoke();
        }
    }

    public static void doBeforeCommit(final InvokationCallback invokationCallback) {
        if(TransactionSynchronizationManager.isActualTransactionActive()){
           TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter(){
               public void beforeCommit(boolean readOnly) {
                    invokationCallback.invoke();                   
               }
           });
        }else{
            //invoke now
            invokationCallback.invoke();
        }
    }
}
