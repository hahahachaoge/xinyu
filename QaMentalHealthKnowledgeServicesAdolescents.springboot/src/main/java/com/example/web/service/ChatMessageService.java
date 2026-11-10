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
 * 对话消息功能的Service接口的定义清单
 */
public interface ChatMessageService extends IService<ChatMessage> {

    /**
     * 对话消息的分页查询方法接口定义
     */
    public PagedResult<ChatMessageDto> list(ChatMessagePagedInput input) ;
    /**
     * 对话消息的新增或者修改方法接口定义
     */
    public ChatMessageDto addOrEdit(ChatMessageDto input);

     /**
     * 获取对话消息信息
     */
    public ChatMessageDto findById(ChatMessagePagedInput input);
 	 /**
     * 对话消息删除
     */
    public void deleteById(IdInput input);

    /**
     * 对话消息批量删除
     */
    public void batchDeleteByIds(IdsInput input);
  

}
