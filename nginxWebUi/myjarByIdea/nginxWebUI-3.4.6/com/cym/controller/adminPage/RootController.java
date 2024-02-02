package com.cym.controller.adminPage;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.cym.ext.TreeNode;
import com.cym.utils.BaseController;
import com.cym.utils.JsonResult;
import com.cym.utils.SystemTool;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;

@Controller
@Mapping("/adminPage/root")
public class RootController extends BaseController {
   @Mapping("getList")
   public List<TreeNode> getList(String id) {
      if (StrUtil.isEmpty(id)) {
         id = "/";
      }

      List<TreeNode> list = new ArrayList();
      File[] fileList = null;
      if (SystemTool.isWindows() && id.equals("/")) {
         fileList = File.listRoots();
      } else {
         fileList = (new File(id)).listFiles();
      }

      File[] var4 = fileList;
      int var5 = fileList.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         File temp = var4[var6];
         TreeNode treeNode = new TreeNode();
         treeNode.setId(temp.getPath());
         if (FileUtil.isDirectory(treeNode.getId())) {
            treeNode.setId(treeNode.getId() + "/");
         }

         if (StrUtil.isNotEmpty(temp.getName())) {
            treeNode.setName(temp.getName());
         } else {
            treeNode.setName(temp.getPath());
         }

         if (temp.isDirectory()) {
            treeNode.setIsParent("true");
         } else {
            treeNode.setIsParent("false");
         }

         list.add(treeNode);
      }

      list.sort(new Comparator<TreeNode>() {
         public int compare(TreeNode o1, TreeNode o2) {
            if (o1.getIsParent().equals("true") && o2.getIsParent().equals("false")) {
               return -1;
            } else {
               return o1.getIsParent().equals("false") && o2.getIsParent().equals("true") ? 1 : o1.getName().compareToIgnoreCase(o2.getName());
            }
         }
      });
      return list;
   }

   @Mapping("mkdir")
   public JsonResult mkdir(String dir, String name) {
      FileUtil.mkdir(dir + name);
      return this.renderSuccess();
   }
}
