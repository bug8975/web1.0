package com.monitor.core.constant;

import java.util.List;
import java.util.ArrayList;

public class IdNameEnum {
    private int id;
    private String name;

    public static List<IdNameEnum> AlarmLevels = new ArrayList<IdNameEnum>();
    static {
    	AlarmLevels.add(new IdNameEnum(1, "一级预警"));
    	AlarmLevels.add(new IdNameEnum(2, "二级预警"));
    	AlarmLevels.add(new IdNameEnum(3, "三级预警"));
    	AlarmLevels.add(new IdNameEnum(0, "正常"));
    }

    public IdNameEnum(int id, String name){
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
