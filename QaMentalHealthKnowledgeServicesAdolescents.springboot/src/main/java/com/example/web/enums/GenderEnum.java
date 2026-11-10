package com.example.web.enums;

import java.util.HashMap;

 /**
   *性别枚举
   */
public enum GenderEnum 
  {
    /**
     * 男
     */    
    男(1), 
     
    /**
     * 女
     */    
    女(2); 
     
            
    private final int index;
    
    GenderEnum(int index) 
    {
      this.index = index;
    }

    public int index() {
      return index;
    }
    public static GenderEnum getEnum(Integer v) {
        if (v == null) {
            return values()[0];
        }
        for (GenderEnum myEnum : values()) {
            if (myEnum.index() == v) {
                return myEnum;
            }
        }
        return values()[0];
    }
      
     
 }
