package com.xz.spider.bilibili.util;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelWriter;
import com.xz.spider.bilibili.pojo.News;
import com.xz.spider.bilibili.service.NewsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Author:   lyc
 * Date:     2020/6/15 16:27
 */
@Slf4j
public class ExcelUtils {
    @Autowired
    NewsService newsService;

    /**
     * 导出
     *
     * @param response
     * @param fileName
     * @param rows
     */
    public static void exportExcel(HttpServletResponse response, String fileName, List<News> rows) {
        ExcelWriter writer = cn.hutool.poi.excel.ExcelUtil.getWriter();
        writer.write(rows, true);
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        try {
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes(), "ISO-8859-1") + ".xlsx");
        } catch (UnsupportedEncodingException e) {
            log.error("编码格式不支持");
        }
        ServletOutputStream out = null;
        try {
            out = response.getOutputStream();
            writer.flush(out);
            writer.close();
        } catch (IOException e) {
            log.error("导出文件错误", e);
        } finally {
            IoUtil.close(out);
        }
    }

    /**
     * 导出
     *
     * @param file
     * @return
     */
    public static List<Map<String, Object>> importExcel(MultipartFile file) {
        ExcelReader reader = null;
        try {
            reader = cn.hutool.poi.excel.ExcelUtil.getReader(file.getInputStream());
            List<Map<String, Object>> readAll = reader.readAll();
            ;
            return readAll;
        } catch (IOException e) {
            log.error("导入文件错误", e);
        }
        return null;
    }

    /**
     * 导出模板
     *
     * @param response
     * @param fileName
     * @param header
     */
    public static void exportExcelTpl(HttpServletResponse response, String fileName, List<String> header) {
        ExcelWriter writer = cn.hutool.poi.excel.ExcelUtil.getWriter();
        writer.writeHeadRow(header);
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        try {
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes(), "ISO-8859-1") + ".xlsx");
        } catch (UnsupportedEncodingException e) {
            log.error("编码格式不支持");
        }
        ServletOutputStream out = null;
        try {
            out = response.getOutputStream();
            writer.flush(out);
            writer.close();
        } catch (IOException e) {
            log.error("导出文件错误", e);
        } finally {
            IoUtil.close(out);
        }

    }

    /**
     * 根据模板导出
     *
     * @param list
     */
    public static void exportExcelByTmp(List list) {
        //1.获取废品的excel模板
        TemplateExportParams params = new TemplateExportParams("d:/舆情监测日报/newsTemPlate.xlsx", true);
        //2.获取所有过磅数据
        //3.执行excel导出
        // 存放数据map

        Map<String, Object> map1 = new HashMap<String, Object>();
        map1.put("rows", list);
        Workbook workbook = ExcelExportUtil.exportExcel(params, map1);

        //4.创建文件存储路径
//        File saveFile = new File("D:\\testword");
        File saveFile = new File("D:\\舆情监测日报");
        if (!saveFile.exists()) {
            saveFile.mkdirs();
        }
        FileOutputStream fos = null;
        String filePath = "";
        try {
            //4.写入文件
            String FileName = new SimpleDateFormat("yyyyMMdd").format(new Date()).toString() + ".xlsx";
//            filePath = saveFile + "\\news.xlsx";
            filePath = saveFile + "\\" + FileName;
            fos = new FileOutputStream(filePath);
            workbook.write(fos);
            fos.close();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            log.error("IOException={}", e.getMessage());
        }
    }

}
