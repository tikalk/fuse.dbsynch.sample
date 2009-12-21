package com.tikal.dbsynch.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA.
 * User: shalom
 * Date: Nov 22, 2009
 * Time: 3:07:53 PM
 */
public class Main {

    private static Logger logger = LoggerFactory.getLogger(Main.class);

    private Producer producer;

    public void setProducer(Producer producer) {
        this.producer = producer;
    }

    public void start() {

        //this loop should never stop...
        while (true){
            try{
                producer.process();
            }catch(Throwable e){
                logger.error("Error in main loop...",e);
            }
        }

    }
}
