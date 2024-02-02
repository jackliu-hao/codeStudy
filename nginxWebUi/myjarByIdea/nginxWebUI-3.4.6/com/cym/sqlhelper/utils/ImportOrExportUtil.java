package com.cym.sqlhelper.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.json.JSONUtil;
import com.cym.sqlhelper.bean.Page;
import com.cym.sqlhelper.config.Table;
import java.io.BufferedReader;
import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

@Component
public class ImportOrExportUtil {
   @Inject
   private SqlHelper sqlHelper;
   @Inject("${project.beanPackage}")
   private String packageName;

   public void exportDb(String path) {
      path = path.replace(".zip", "");
      FileUtil.del(path);
      FileUtil.del(path + ".zip");

      try {
         Set<Class<?>> set = ClassUtil.scanPackage(this.packageName);
         Page page = new Page();
         page.setLimit(1000);
         Iterator var4 = set.iterator();

         while(var4.hasNext()) {
            Class<?> clazz = (Class)var4.next();

            try {
               Table table = (Table)clazz.getAnnotation(Table.class);
               if (table != null) {
                  page.setCurr(1);

                  while(true) {
                     page = this.sqlHelper.findPage(page, clazz);
                     if (page.getRecords().size() == 0) {
                        break;
                     }

                     List<String> lines = new ArrayList();
                     Iterator var8 = page.getRecords().iterator();

                     while(var8.hasNext()) {
                        Object object = var8.next();
                        lines.add(JSONUtil.toJsonStr(object));
                     }

                     FileUtil.appendLines(lines, (String)(path + File.separator + clazz.getSimpleName() + ".json"), (String)"UTF-8");
                     System.out.println(clazz.getSimpleName() + "表导出了" + page.getRecords().size() + "条数据");
                     page.setCurr(page.getCurr() + 1);
                  }
               }
            } catch (Exception var10) {
               System.err.println(var10.getMessage());
            }
         }

         ZipUtil.zip(path);
      } catch (Exception var11) {
         var11.printStackTrace();
         FileUtil.del(path + ".zip");
      }

      FileUtil.del(path);
   }

   public void importDb(String path) {
      if (!FileUtil.exist(path)) {
         System.out.println(path + "文件不存在");
      } else {
         BufferedReader reader = null;
         path = path.replace(".zip", "");
         FileUtil.del(path);
         ZipUtil.unzip(path + ".zip");

         try {
            Set<Class<?>> set = ClassUtil.scanPackage(this.packageName);
            Iterator var4 = set.iterator();

            label42:
            while(true) {
               Class clazz;
               File file;
               do {
                  Table table;
                  do {
                     if (!var4.hasNext()) {
                        break label42;
                     }

                     clazz = (Class)var4.next();
                     table = (Table)clazz.getAnnotation(Table.class);
                  } while(table == null);

                  file = new File(path + File.separator + clazz.getSimpleName() + ".json");
               } while(!file.exists());

               this.sqlHelper.deleteByQuery(new ConditionAndWrapper(), clazz);
               reader = FileUtil.getReader(file, Charset.forName("UTF-8"));
               List<Object> list = new ArrayList();

               while(true) {
                  String json = reader.readLine();
                  if (StrUtil.isEmpty(json)) {
                     this.sqlHelper.insertAll(list);
                     System.out.println(clazz.getSimpleName() + "表导入了" + list.size() + "条数据");
                     list.clear();
                     IoUtil.close(reader);
                     break;
                  }

                  list.add(JSONUtil.toBean(json, clazz));
                  if (list.size() == 1000) {
                     this.sqlHelper.insertAll(list);
                     System.out.println(clazz.getSimpleName() + "表导入了" + list.size() + "条数据");
                     list.clear();
                  }
               }
            }
         } catch (Exception var10) {
            var10.printStackTrace();
         }

         FileUtil.del(path);
      }
   }
}
