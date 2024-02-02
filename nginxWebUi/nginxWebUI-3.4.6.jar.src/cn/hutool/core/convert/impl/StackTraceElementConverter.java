/*    */ package cn.hutool.core.convert.impl;
/*    */ 
/*    */ import cn.hutool.core.convert.AbstractConverter;
/*    */ import cn.hutool.core.map.MapUtil;
/*    */ import cn.hutool.core.util.ObjectUtil;
/*    */ import java.util.Map;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class StackTraceElementConverter
/*    */   extends AbstractConverter<StackTraceElement>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   protected StackTraceElement convertInternal(Object value) {
/* 21 */     if (value instanceof Map) {
/* 22 */       Map<?, ?> map = (Map<?, ?>)value;
/*    */       
/* 24 */       String declaringClass = MapUtil.getStr(map, "className");
/* 25 */       String methodName = MapUtil.getStr(map, "methodName");
/* 26 */       String fileName = MapUtil.getStr(map, "fileName");
/* 27 */       Integer lineNumber = MapUtil.getInt(map, "lineNumber");
/*    */       
/* 29 */       return new StackTraceElement(declaringClass, methodName, fileName, ((Integer)ObjectUtil.defaultIfNull(lineNumber, Integer.valueOf(0))).intValue());
/*    */     } 
/* 31 */     return null;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\convert\impl\StackTraceElementConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */