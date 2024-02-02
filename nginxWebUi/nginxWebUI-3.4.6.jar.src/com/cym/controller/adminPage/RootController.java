/*    */ package com.cym.controller.adminPage;
/*    */ 
/*    */ import cn.hutool.core.io.FileUtil;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import com.cym.ext.TreeNode;
/*    */ import com.cym.utils.BaseController;
/*    */ import com.cym.utils.JsonResult;
/*    */ import com.cym.utils.SystemTool;
/*    */ import java.io.File;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Comparator;
/*    */ import java.util.List;
/*    */ import org.noear.solon.annotation.Controller;
/*    */ import org.noear.solon.annotation.Mapping;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Controller
/*    */ @Mapping("/adminPage/root")
/*    */ public class RootController
/*    */   extends BaseController
/*    */ {
/*    */   @Mapping("getList")
/*    */   public List<TreeNode> getList(String id) {
/* 26 */     if (StrUtil.isEmpty(id)) {
/* 27 */       id = "/";
/*    */     }
/*    */     
/* 30 */     List<TreeNode> list = new ArrayList<>();
/*    */     
/* 32 */     File[] fileList = null;
/* 33 */     if (SystemTool.isWindows().booleanValue() && id.equals("/")) {
/* 34 */       fileList = File.listRoots();
/*    */     } else {
/* 36 */       fileList = (new File(id)).listFiles();
/*    */     } 
/*    */     
/* 39 */     for (File temp : fileList) {
/*    */       
/* 41 */       TreeNode treeNode = new TreeNode();
/* 42 */       treeNode.setId(temp.getPath());
/* 43 */       if (FileUtil.isDirectory(treeNode.getId()))
/*    */       {
/* 45 */         treeNode.setId(treeNode.getId() + "/");
/*    */       }
/* 47 */       if (StrUtil.isNotEmpty(temp.getName())) {
/* 48 */         treeNode.setName(temp.getName());
/*    */       } else {
/* 50 */         treeNode.setName(temp.getPath());
/*    */       } 
/*    */       
/* 53 */       if (temp.isDirectory()) {
/* 54 */         treeNode.setIsParent("true");
/*    */       } else {
/* 56 */         treeNode.setIsParent("false");
/*    */       } 
/*    */       
/* 59 */       list.add(treeNode);
/*    */     } 
/*    */ 
/*    */ 
/*    */     
/* 64 */     list.sort(new Comparator<TreeNode>()
/*    */         {
/*    */           
/*    */           public int compare(TreeNode o1, TreeNode o2)
/*    */           {
/* 69 */             if (o1.getIsParent().equals("true") && o2.getIsParent().equals("false")) {
/* 70 */               return -1;
/*    */             }
/* 72 */             if (o1.getIsParent().equals("false") && o2.getIsParent().equals("true")) {
/* 73 */               return 1;
/*    */             }
/*    */             
/* 76 */             return o1.getName().compareToIgnoreCase(o2.getName());
/*    */           }
/*    */         });
/*    */     
/* 80 */     return list;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   @Mapping("mkdir")
/*    */   public JsonResult mkdir(String dir, String name) {
/* 87 */     FileUtil.mkdir(dir + name);
/*    */     
/* 89 */     return renderSuccess();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\controller\adminPage\RootController.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */