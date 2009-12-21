package com.tikal.dbsynch.common;

import java.sql.Date;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: shalom
 * Date: Nov 22, 2009
 * Time: 3:29:12 PM
 */
public class SourceRow implements Serializable{
    private long id;
    private String description;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
