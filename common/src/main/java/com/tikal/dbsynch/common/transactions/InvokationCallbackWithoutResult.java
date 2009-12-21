package com.tikal.dbsynch.common.transactions;

/**
 * Created by IntelliJ IDEA.
 * User: shalom
 * Date: Dec 9, 2009
 * Time: 6:47:37 PM
 */
public abstract class InvokationCallbackWithoutResult implements InvokationCallback
{
    public final Object invoke(){
        invokeWithoutResult();
        return null;
    }

    public abstract void invokeWithoutResult();
}
