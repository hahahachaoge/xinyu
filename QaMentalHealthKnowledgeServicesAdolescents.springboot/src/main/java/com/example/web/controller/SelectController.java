package com.example.web.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.web.common.PagedResult;
import com.example.web.common.SelectResult;
import com.example.web.enums.*;

@RestController
@RequestMapping("/select")
public class SelectController {

    @RequestMapping(value = "/roleType", method = RequestMethod.POST)
    public PagedResult<SelectResult> roleType() {

        List<SelectResult> rs = Arrays.stream(RoleTypeEnum.values())
                .map(n -> new SelectResult(n.toString(), n.name(), n.index() + "", "")).toList();
        return PagedResult.getInstance(rs, rs.stream().count());
    }
 
     /**
     *题目类型枚举接口
     */
    @RequestMapping(value = "/questionTypeEnum", method = RequestMethod.POST)
    public PagedResult<SelectResult> questionTypeEnum() {

       var rs=Arrays.stream(QuestionTypeEnum.values()).map(n->new SelectResult(n.toString(),n.name(),Integer.toString(n.index()),"")).toList();
       return PagedResult.getInstance(rs,rs.stream().count());
    }
  
     /**
     *收藏类型枚举接口
     */
    @RequestMapping(value = "/favoriteTypeEnum", method = RequestMethod.POST)
    public PagedResult<SelectResult> favoriteTypeEnum() {

       var rs=Arrays.stream(FavoriteTypeEnum.values()).map(n->new SelectResult(n.toString(),n.name(),Integer.toString(n.index()),"")).toList();
       return PagedResult.getInstance(rs,rs.stream().count());
    }
  
     /**
     *性别枚举接口
     */
    @RequestMapping(value = "/genderEnum", method = RequestMethod.POST)
    public PagedResult<SelectResult> genderEnum() {

       var rs=Arrays.stream(GenderEnum.values()).map(n->new SelectResult(n.toString(),n.name(),Integer.toString(n.index()),"")).toList();
       return PagedResult.getInstance(rs,rs.stream().count());
    }
  
     /**
     *量表类型枚举接口
     */
    @RequestMapping(value = "/scaleTypeEnum", method = RequestMethod.POST)
    public PagedResult<SelectResult> scaleTypeEnum() {

       var rs=Arrays.stream(ScaleTypeEnum.values()).map(n->new SelectResult(n.toString(),n.name(),Integer.toString(n.index()),"")).toList();
       return PagedResult.getInstance(rs,rs.stream().count());
    }
  
     /**
     *发送角色枚举接口
     */
    @RequestMapping(value = "/senderRoleEnum", method = RequestMethod.POST)
    public PagedResult<SelectResult> senderRoleEnum() {

       var rs=Arrays.stream(SenderRoleEnum.values()).map(n->new SelectResult(n.toString(),n.name(),Integer.toString(n.index()),"")).toList();
       return PagedResult.getInstance(rs,rs.stream().count());
    }
  
     /**
     *向量化状态枚举接口
     */
    @RequestMapping(value = "/vectorStatusTypeEnum", method = RequestMethod.POST)
    public PagedResult<SelectResult> vectorStatusTypeEnum() {

       var rs=Arrays.stream(VectorStatusTypeEnum.values()).map(n->new SelectResult(n.toString(),n.name(),Integer.toString(n.index()),"")).toList();
       return PagedResult.getInstance(rs,rs.stream().count());
    }
  
}
