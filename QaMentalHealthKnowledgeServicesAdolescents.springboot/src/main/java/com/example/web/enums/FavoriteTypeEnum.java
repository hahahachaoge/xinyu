package com.example.web.enums;

import java.util.HashMap;

 /**
   *收藏类型枚举
   */
public enum FavoriteTypeEnum 
  {
    /**
     * 科普文章
     */    
    科普文章(1), 
     
    /**
     * 公告
     */    
    公告(2); 
     
            
    private final int index;
    
    FavoriteTypeEnum(int index) 
    {
      this.index = index;
    }

    public int index() {
      return index;
    }
    public static FavoriteTypeEnum getEnum(Integer v) {
        if (v == null) {
            return values()[0];
        }
        for (FavoriteTypeEnum myEnum : values()) {
            if (myEnum.index() == v) {
                return myEnum;
            }
        }
        return values()[0];
    }
      
     
 }
