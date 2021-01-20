package com.xz.spider.bilibili.pojo;

import java.io.Serializable;
import java.util.Date;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * news
 * @author 
 */
@Data
@TableName("news")
public class News implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.UUID)
    private String id;

    /**
     * 标题
     */
    private String title;

    /**
     * 链接地址
     */
//    @JSONField(name = "open_url")
    private String url;

    /**
     * 创建时间
     */
//    @Excel(name = "时间",exportFormat = "yyyy-MM-dd HH:mm:ss")
    @TableField( fill = FieldFill.INSERT)
    private Date creatime;

    private static final long serialVersionUID = 1L;
}