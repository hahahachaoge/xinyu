package com.example.web.service;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.web.dto.*;
import com.example.web.dto.query.*;
import com.example.web.entity.*;
import com.example.web.common.*;
import com.example.web.enums.*;
import java.lang.reflect.InvocationTargetException;
import org.springframework.web.bind.annotation.RequestParam;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import jakarta.servlet.http.HttpServletResponse;
/**
 * 公告信息功能的Service接口的定义清单
 */
public interface NoticeService extends IService<Notice> {

    /**
     * 公告信息的分页查询方法接口定义
     */
    public PagedResult<NoticeDto> list(NoticePagedInput input) ;
    /**
     * 公告信息的新增或者修改方法接口定义
     */
    public NoticeDto addOrEdit(NoticeDto input);

     /**
     * 获取公告信息信息
     */
    public NoticeDto findById(NoticePagedInput input);
 	 /**
     * 公告信息删除
     */
    public void deleteById(IdInput input);

    /**
     * 公告信息批量删除
     */
    public void batchDeleteByIds(IdsInput input);
  

}
