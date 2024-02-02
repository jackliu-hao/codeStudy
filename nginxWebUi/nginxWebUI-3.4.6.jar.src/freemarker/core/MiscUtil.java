/*    */ package freemarker.core;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collections;
/*    */ import java.util.Comparator;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class MiscUtil
/*    */ {
/*    */   static final String C_FALSE = "false";
/*    */   static final String C_TRUE = "true";
/*    */   
/*    */   static List sortMapOfExpressions(Map map) {
/* 43 */     ArrayList<?> res = new ArrayList(map.entrySet());
/* 44 */     Collections.sort(res, new Comparator()
/*    */         {
/*    */           public int compare(Object o1, Object o2)
/*    */           {
/* 48 */             Map.Entry ent1 = (Map.Entry)o1;
/* 49 */             Expression exp1 = (Expression)ent1.getValue();
/*    */             
/* 51 */             Map.Entry ent2 = (Map.Entry)o2;
/* 52 */             Expression exp2 = (Expression)ent2.getValue();
/*    */             
/* 54 */             int res = exp1.beginLine - exp2.beginLine;
/* 55 */             if (res != 0) return res; 
/* 56 */             res = exp1.beginColumn - exp2.beginColumn;
/* 57 */             if (res != 0) return res;
/*    */             
/* 59 */             if (ent1 == ent2) return 0;
/*    */ 
/*    */             
/* 62 */             return ((String)ent1.getKey()).compareTo((String)ent1.getKey());
/*    */           }
/*    */         });
/*    */     
/* 66 */     return res;
/*    */   }
/*    */   
/*    */   static Expression peelParentheses(Expression exp) {
/* 70 */     while (exp instanceof ParentheticalExpression) {
/* 71 */       exp = ((ParentheticalExpression)exp).getNestedExpression();
/*    */     }
/* 73 */     return exp;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\core\MiscUtil.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */