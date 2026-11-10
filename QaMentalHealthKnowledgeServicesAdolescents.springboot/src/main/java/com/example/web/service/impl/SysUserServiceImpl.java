package com.example.web.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.web.SysConst;
import com.example.web.common.IdInput;
import com.example.web.common.IdsInput;
import com.example.web.common.PagedResult;
import com.example.web.dto.SysUserDto;
import com.example.web.dto.query.SysUserPagedInput;
import com.example.web.entity.SysUser;
import com.example.web.exception.CustomException;
import com.example.web.mapper.SysUserMapper;
import com.example.web.service.SysUserService;
import com.example.web.utils.Extension;
import com.example.web.utils.JWTUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;

/**
 * 账号功能实现类
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    /**
     * 操作数据库的账号表mapper对象
     */
    @Autowired
    private SysUserMapper sysUserMpper;

    @Override
    @SneakyThrows
    public PagedResult<SysUserDto> list(SysUserPagedInput input) {
        // 声明一个支持账号查询的(拉姆达)表达式
        LambdaQueryWrapper<SysUser> queryWrapper = Wrappers.<SysUser>lambdaQuery()
                .eq(input.getId() != null && input.getId() != 0, SysUser::getId, input.getId());

        // 如果前端搜索传入NickName不为空,则进行精确查询
        if (Extension.isNotNullOrEmpty(input.getNickName())) {
            queryWrapper = queryWrapper.like(SysUser::getNickName, input.getNickName());
        }

        // 如果前端搜索传入Email不为空,则进行精确查询
        if (Extension.isNotNullOrEmpty(input.getEmail())) {
            queryWrapper = queryWrapper.like(SysUser::getEmail, input.getEmail());
        }

        // 如果前端搜索传入getPhoneNumber不为空,则进行精确查询
        if (Extension.isNotNullOrEmpty(input.getPhoneNumber())) {
            queryWrapper = queryWrapper.like(SysUser::getPhoneNumber, input.getPhoneNumber());
        }

        if (Extension.isNotNullOrEmpty(input.getUserName())) {
            queryWrapper = queryWrapper.eq(SysUser::getUserName, input.getUserName());
        }
        // 如果前端搜索传入getRoleType不为空,则进行精确查询
        if (input.getRoleType() != null) {
            queryWrapper = queryWrapper.eq(SysUser::getRoleType, input.getRoleType());
        }

        // 按创建时间从大到小排序 最新的显示在最前面
        queryWrapper = queryWrapper.orderByDesc(SysUser::getCreationTime);
        // 构建一个分页查询的model
        Page<SysUser> page = new Page<>(input.getPage(), input.getLimit());

        // 从数据库进行分页查询获取账号数据
        IPage<SysUser> pageRecords = sysUserMpper.selectPage(page, queryWrapper);

        // 把账号实体转换成账号传输模型
        List<SysUserDto> items = Extension.copyBeanList(pageRecords.getRecords(), SysUserDto.class);
        for (SysUserDto item : items) {
            // 不需要把密码暴露出去
            item.setPassWord(null);
        }
        // 返回一个分页结构给前端
        return PagedResult.getInstance(items, pageRecords.getTotal());
    }

    /**
     * 账号创建或者修改
     */
    @SneakyThrows
    @Override
    public SysUserDto addOrEdit(SysUserDto input) {

        if (Extension.isNullOrEmpty(input.getUserName())) {
            throw new CustomException("账号名不能为空");
        }
        if (input.isAdd() && Extension.isNullOrEmpty(input.getPassWord())) {
            throw new CustomException("密码不能为空");
        }

        // 如果前端传入新的密码且不是MD5加密的，则进行MD5加密
        if (Extension.isNotNullOrEmpty(input.getPassWord()) && !Extension.isMD5Encrypt(input.getPassWord())) {
            input.setPassWord(Extension.md5Encrypt(input.getPassWord()));
        }
        // 如果前端传入的密码为空且
        if (Extension.isNullOrEmpty(input.getPassWord())) {
            // 查询账号
            SysUser appUser = sysUserMpper.selectById(input.getId());
            input.setPassWord(appUser.getPassWord());
        }

        SysUser appUser = input.mapToEntity();
        // 调用数据库的增加或者修改方法
        saveOrUpdate(appUser);
        // 把传输模型返回给前端
        return appUser.mapToDto();
    }

    /**
     * 账号删除
     */
    @Override
    public void deleteById(IdInput input) {
        SysUser entity = sysUserMpper.selectById(input.getId());
        sysUserMpper.deleteById(entity);
    }

    /**
     * 账号批量删除
     */
    @Override
    public void batchDeleteByIds(IdsInput input) {
        for (Integer id : input.getIds()) {
            IdInput idInput = new IdInput();
            idInput.setId(id);
            deleteById(idInput);
        }
    }

    /**
     * 账号单个查询
     */
    @Override
    public SysUserDto findById(SysUserPagedInput input) {
        if (input.getId() == null) {
            return new SysUserDto();
        }
        return list(input).getItems().stream().findFirst().orElse(new SysUserDto());
    }

    /**
     * 登录
     */
    @Override
    public String signIn(SysUserDto input) {
        LambdaQueryWrapper<SysUser> queryWrapper = Wrappers.<SysUser>lambdaQuery();
        if (Extension.isNotNullOrEmpty(input.getUserName())) {
            queryWrapper.eq(SysUser::getUserName, input.getUserName());
        }
        if (Extension.isNotNullOrEmpty(input.getPassWord())) {
            queryWrapper.eq(SysUser::getPassWord, Extension.md5Encrypt(input.getPassWord()));
        }
        if (input.getRoleType() != null) {
            queryWrapper.eq(SysUser::getRoleType, input.getRoleType());
        }

        List<SysUser> items = sysUserMpper.selectList(queryWrapper);
        if (items.stream().count() == 0) {
            throw new CustomException("请检查登录的账号或者密码,角色是否都正确!");
        }
        Map<String, String> map = new HashMap<>();
        map.put(SysConst.UserIdClaim, items.get(0).getId().toString());
        map.put(SysConst.RoleTypeClaim, items.get(0).getRoleType().toString());
        String token = JWTUtils.getToken(map);
        return token;
    }

    /**
     * 注册
     */
    @Override
    public SysUserDto register(SysUserDto input) {

        // 对密码进行MD5加密
        input.setPassWord(Extension.md5Encrypt(input.getPassWord()));
        // 检查账号名是否存在
        Long userCount = sysUserMpper.selectCount(Wrappers.<SysUser>lambdaQuery()
                .eq(Extension.isNotNullOrEmpty(input.getUserName()), SysUser::getUserName, input.getUserName()));
        if (userCount > 0) {
            throw new CustomException("该账号名已经存在!");
        }
        if (Extension.isNotNullOrEmpty(input.getEmail())) {
            // 检查邮箱是否存在
            Long emailCount = sysUserMpper.selectCount(Wrappers.<SysUser>lambdaQuery()
                    .eq(Extension.isNotNullOrEmpty(input.getEmail()), SysUser::getEmail, input.getEmail()));
            if (emailCount > 0) {
                throw new CustomException("该邮箱已经存在!");
            }
        }
        if (Extension.isNotNullOrEmpty(input.getPhoneNumber())) {
            // 检查手机号是否存在
            Long phoneCount = sysUserMpper.selectCount(Wrappers.<SysUser>lambdaQuery()
                    .eq(Extension.isNotNullOrEmpty(input.getPhoneNumber()), SysUser::getPhoneNumber,
                            input.getPhoneNumber()));
            if (phoneCount > 0) {
                throw new CustomException("该手机号已经存在!");
            }
        }
        return addOrEdit(input);
    }

    /**
     * 找回密码
     */
    @Override
    public void forgetPassword(SysUserDto input) {

        // 检查账号名是否存在
        SysUser appUser = sysUserMpper.selectList(Wrappers.<SysUser>lambdaQuery()
                .eq(Extension.isNotNullOrEmpty(input.getUserName()), SysUser::getUserName, input.getUserName()))
                .stream().findFirst().orElse(null);

        if (appUser == null) {
            throw new CustomException("该账号名不存在!");
        }
        if (appUser.getPhoneNumber() == null) {
            throw new CustomException("该账号未绑定手机号!");
        }
        if (Extension.isNotNullOrEmpty(input.getEmail()) && appUser.getEmail() == null) {
            throw new CustomException("该账号未绑定邮箱!");
        }
        if (!appUser.getPhoneNumber().equals(input.getPhoneNumber())) {
            throw new CustomException("请输入你绑定的手机号!");
        }
        if (input.getEmail() != null && !input.getEmail().equals(appUser.getEmail())) {
            throw new CustomException("请输入你绑定的邮箱!");
        }
        appUser.setPassWord(Extension.md5Encrypt(input.getPassWord()));
        saveOrUpdate(appUser);
    }

    /** 修改密码 */
    @Override
    public void changePassword(SysUserDto input) {
        SysUser appUser = sysUserMpper.selectById(input.getId());
        if (!Extension.md5Encrypt(input.getOrginPassword()).equals(appUser.getPassWord())) {
            throw new CustomException("旧密码错误!");
        }
        // 对当前输入的密码进行加密
        if (Extension.md5Encrypt(input.getPassWord()).equals(appUser.getPassWord())) {
            throw new CustomException("新密码不能与旧密码相同!");
        }

        appUser.setPassWord(Extension.md5Encrypt(input.getPassWord()));
        saveOrUpdate(appUser);
    }

    /**
     * 重置密码
     */
    @Override
    public void resetPassword(SysUserDto input) {
        SysUser appUser = sysUserMpper.selectById(input.getId());
        // 重置密码必须明确提供新密码，不使用共享默认密码
        if (!Extension.isNotNullOrEmpty(input.getPassWord())) {
            throw new CustomException("请提供新密码");
        }
        String password = input.getPassWord();
        appUser.setPassWord(Extension.md5Encrypt(password));
        saveOrUpdate(appUser);
    }

    /**
     * 账号导出
     */
    @Override
    public void export(@RequestParam String query, HttpServletResponse response) throws IOException {

        ObjectMapper mapper = new ObjectMapper();

        SysUserPagedInput input = mapper.readValue(query, SysUserPagedInput.class);

        List<SysUserDto> items = list(input).getItems();

        // 声明一个工作簿
        HSSFWorkbook workbook = new HSSFWorkbook();

        // 生成一个表格，设置表格名称为"账号表"
        HSSFSheet sheet = workbook.createSheet("账号表");

        // 设置表格列宽度为10个字节
        sheet.setDefaultColumnWidth(10);
        // 创建标题的显示样式
        HSSFCellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.YELLOW.index);
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        // 创建第一行表头
        HSSFRow headrow = sheet.createRow(0);

        // 表头数据
        String[] header = { "账户", "姓名", "邮箱", "手机号码", "账号角色" };
        // 遍历添加表头(下面模拟遍历账号，也是同样的操作过程)
        for (int i = 0; i < header.length; i++) {
            // 创建一个单元格
            HSSFCell cell = headrow.createCell(i);

            // 创建一个内容对象
            HSSFRichTextString text = new HSSFRichTextString(header[i]);

            // 将内容对象的文字内容写入到单元格中
            cell.setCellValue(text);
            cell.setCellStyle(headerStyle);
        }

        for (int i = 0; i < items.size(); i++) {

            SysUserDto appUser = items.get(i);

            // 创建一行
            HSSFRow row = sheet.createRow(i + 1);

            if (appUser.getUserName() != null) {
                row.createCell(0).setCellValue(new HSSFRichTextString(appUser.getUserName()));
            }
            if (appUser.getNickName() != null) {
                row.createCell(1).setCellValue(new HSSFRichTextString(appUser.getNickName()));
            }
            if (appUser.getEmail() != null) {
                row.createCell(2).setCellValue(new HSSFRichTextString(appUser.getEmail()));
            }
            if (appUser.getPhoneNumber() != null) {
                row.createCell(3).setCellValue(new HSSFRichTextString(appUser.getPhoneNumber()));
            }
            if (appUser.getRoleType() != null) {
                row.createCell(4).setCellValue(new HSSFRichTextString(appUser.roleTypeFormat()));
            }

        }

        // 准备将Excel的输出流通过response输出到页面下载
        // 八进制输出流
        response.setContentType("application/octet-stream");

        // 这后面可以设置导出Excel的名称
        response.setHeader("Content-disposition", "attachment;filename=" + System.currentTimeMillis() + ".xls");

        // 刷新缓冲
        response.flushBuffer();

        // workbook将Excel写入到response的输出流中，供页面下载
        workbook.write(response.getOutputStream());
    }
}
