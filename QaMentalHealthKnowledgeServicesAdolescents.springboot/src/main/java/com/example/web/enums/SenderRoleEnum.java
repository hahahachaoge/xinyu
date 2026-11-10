package com.example.web.enums;

import java.util.HashMap;

 /**
   *发送角色枚举
   */
public enum SenderRoleEnum 
  {
    /**
     * 用户
     */    
    用户(1), 
     
    /**
     * 系统
     */    
    系统(2); 
     
            
    private final int index;
    
    SenderRoleEnum(int index) 
    {
      this.index = index;
    }

    public int index() {
      return index;
    }
    public static SenderRoleEnum getEnum(Integer v) {
        if (v == null) {
            return values()[0];
        }
        for (SenderRoleEnum myEnum : values()) {
            if (myEnum.index() == v) {
                return myEnum;
            }
        }
        return values()[0];
    }
      
     
 }
