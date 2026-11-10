package com.example.web.enums;

import java.util.HashMap;

 /**
   *向量化状态枚举
   */
public enum VectorStatusTypeEnum 
  {
    /**
     * 待处理
     */    
    待处理(1), 
     
    /**
     * 处理中
     */    
    处理中(2), 
     
    /**
     * 已完成
     */    
    已完成(3), 
     
    /**
     * 失败
     */    
    失败(4); 
     
            
    private final int index;
    
    VectorStatusTypeEnum(int index) 
    {
      this.index = index;
    }

    public int index() {
      return index;
    }
    public static VectorStatusTypeEnum getEnum(Integer v) {
        if (v == null) {
            return values()[0];
        }
        for (VectorStatusTypeEnum myEnum : values()) {
            if (myEnum.index() == v) {
                return myEnum;
            }
        }
        return values()[0];
    }
      
     
 }
