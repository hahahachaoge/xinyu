package com.example.web.enums;

import java.util.HashMap;

 /**
   *量表类型枚举
   */
public enum ScaleTypeEnum 
  {
    /**
     * 情绪评估
     */    
    情绪评估(1), 
     
    /**
     * 压力评估
     */    
    压力评估(2), 
     
    /**
     * 人际关系
     */    
    人际关系(3), 
     
    /**
     * 自我认知
     */    
    自我认知(4), 
     
    /**
     * 综合评估
     */    
    综合评估(5); 
     
            
    private final int index;
    
    ScaleTypeEnum(int index) 
    {
      this.index = index;
    }

    public int index() {
      return index;
    }
    public static ScaleTypeEnum getEnum(Integer v) {
        if (v == null) {
            return values()[0];
        }
        for (ScaleTypeEnum myEnum : values()) {
            if (myEnum.index() == v) {
                return myEnum;
            }
        }
        return values()[0];
    }
      
     
 }
