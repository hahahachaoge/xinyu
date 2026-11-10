package com.example.web.enums;

import java.util.HashMap;

 /**
   *题目类型枚举
   */
public enum QuestionTypeEnum 
  {
    /**
     * 单选
     */    
    单选(1), 
     
    /**
     * 多选
     */    
    多选(2); 
     
            
    private final int index;
    
    QuestionTypeEnum(int index) 
    {
      this.index = index;
    }

    public int index() {
      return index;
    }
    public static QuestionTypeEnum getEnum(Integer v) {
        if (v == null) {
            return values()[0];
        }
        for (QuestionTypeEnum myEnum : values()) {
            if (myEnum.index() == v) {
                return myEnum;
            }
        }
        return values()[0];
    }
      
     
 }
