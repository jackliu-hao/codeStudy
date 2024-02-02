/*     */ package com.cym.sqlhelper.utils;
/*     */ 
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.util.ClassUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import cn.hutool.core.util.ZipUtil;
/*     */ import cn.hutool.json.JSONUtil;
/*     */ import com.cym.sqlhelper.bean.Page;
/*     */ import com.cym.sqlhelper.config.Table;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import org.noear.solon.annotation.Component;
/*     */ import org.noear.solon.annotation.Inject;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Component
/*     */ public class ImportOrExportUtil
/*     */ {
/*     */   @Inject
/*     */   private SqlHelper sqlHelper;
/*     */   @Inject("${project.beanPackage}")
/*     */   private String packageName;
/*     */   
/*     */   public void exportDb(String path) {
/*  40 */     path = path.replace(".zip", "");
/*  41 */     FileUtil.del(path);
/*  42 */     FileUtil.del(path + ".zip");
/*     */     try {
/*  44 */       Set<Class<?>> set = ClassUtil.scanPackage(this.packageName);
/*  45 */       Page page = new Page();
/*  46 */       page.setLimit(Integer.valueOf(1000));
/*     */       
/*  48 */       label30: for (Class<?> clazz : set) {
/*     */         try {
/*  50 */           Table table = clazz.<Table>getAnnotation(Table.class);
/*  51 */           if (table != null) {
/*  52 */             page.setCurr(Integer.valueOf(1));
/*     */             while (true) {
/*  54 */               page = this.sqlHelper.findPage(page, clazz);
/*  55 */               if (page.getRecords().size() == 0) {
/*     */                 continue label30;
/*     */               }
/*     */               
/*  59 */               List<String> lines = new ArrayList<>();
/*  60 */               for (Object object : page.getRecords()) {
/*  61 */                 lines.add(JSONUtil.toJsonStr(object));
/*     */               }
/*  63 */               FileUtil.appendLines(lines, path + File.separator + clazz.getSimpleName() + ".json", "UTF-8");
/*  64 */               System.out.println(clazz.getSimpleName() + "表导出了" + page.getRecords().size() + "条数据");
/*  65 */               page.setCurr(Integer.valueOf(page.getCurr().intValue() + 1));
/*     */             } 
/*     */           } 
/*  68 */         } catch (Exception e) {
/*  69 */           System.err.println(e.getMessage());
/*     */         } 
/*     */       } 
/*  72 */       ZipUtil.zip(path);
/*     */     }
/*  74 */     catch (Exception e) {
/*  75 */       e.printStackTrace();
/*  76 */       FileUtil.del(path + ".zip");
/*     */     } 
/*     */     
/*  79 */     FileUtil.del(path);
/*     */   }
/*     */   
/*     */   public void importDb(String path) {
/*  83 */     if (!FileUtil.exist(path)) {
/*  84 */       System.out.println(path + "文件不存在");
/*     */       return;
/*     */     } 
/*  87 */     BufferedReader reader = null;
/*     */     
/*  89 */     path = path.replace(".zip", "");
/*  90 */     FileUtil.del(path);
/*  91 */     ZipUtil.unzip(path + ".zip");
/*     */     try {
/*  93 */       Set<Class<?>> set = ClassUtil.scanPackage(this.packageName);
/*  94 */       for (Class<?> clazz : set) {
/*  95 */         Table table = clazz.<Table>getAnnotation(Table.class);
/*  96 */         if (table != null) {
/*  97 */           File file = new File(path + File.separator + clazz.getSimpleName() + ".json");
/*  98 */           if (file.exists()) {
/*  99 */             this.sqlHelper.deleteByQuery(new ConditionAndWrapper(), clazz);
/*     */             
/* 101 */             reader = FileUtil.getReader(file, Charset.forName("UTF-8"));
/* 102 */             List<Object> list = new ArrayList();
/*     */             while (true) {
/* 104 */               String json = reader.readLine();
/* 105 */               if (StrUtil.isEmpty(json)) {
/* 106 */                 this.sqlHelper.insertAll(list);
/* 107 */                 System.out.println(clazz.getSimpleName() + "表导入了" + list.size() + "条数据");
/* 108 */                 list.clear();
/*     */                 break;
/*     */               } 
/* 111 */               list.add(JSONUtil.toBean(json, clazz));
/* 112 */               if (list.size() == 1000) {
/* 113 */                 this.sqlHelper.insertAll(list);
/* 114 */                 System.out.println(clazz.getSimpleName() + "表导入了" + list.size() + "条数据");
/* 115 */                 list.clear();
/*     */               } 
/*     */             } 
/*     */             
/* 119 */             IoUtil.close(reader);
/*     */           } 
/*     */         } 
/*     */       } 
/* 123 */     } catch (Exception e) {
/* 124 */       e.printStackTrace();
/*     */     } 
/*     */     
/* 127 */     FileUtil.del(path);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\sqlhelpe\\utils\ImportOrExportUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */