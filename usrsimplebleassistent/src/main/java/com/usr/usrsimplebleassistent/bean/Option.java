package com.usr.usrsimplebleassistent.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liu on 15/11/23.
 */
public class Option {
    public static final String NOTIFY = "Notify";
    public static final String STOP_NOTIFY = "Stop Notify";
    public static final String WRITE = "Write";
    public static final String READ = "Read";
    public static final String INDICATE = "Indicate";
    public static final String STOP_INDICATE = "Stop Indicate";

    public static Map<String,OPTION_PROPERTY> OPTIONS_MAP = new HashMap<String,OPTION_PROPERTY>();

    static {
        OPTIONS_MAP.put(NOTIFY,OPTION_PROPERTY.PROPERTY_NOTIFY);
        OPTIONS_MAP.put(WRITE,OPTION_PROPERTY.PROPERTY_WRITE);
        OPTIONS_MAP.put(READ,OPTION_PROPERTY.PROPERTY_READ);
        OPTIONS_MAP.put(INDICATE,OPTION_PROPERTY.PROPERTY_INDICATE);
    }

    private String name;
    private OPTION_PROPERTY propertyType;

    public Option() {
    }

    public Option(String name, OPTION_PROPERTY propertyType) {
        this.name = name;
        this.propertyType = propertyType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OPTION_PROPERTY getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(OPTION_PROPERTY propertyType) {
        this.propertyType = propertyType;
    }

    public static enum  OPTION_PROPERTY{
        PROPERTY_READ,PROPERTY_WRITE,PROPERTY_NOTIFY,PROPERTY_INDICATE;
    }
}
