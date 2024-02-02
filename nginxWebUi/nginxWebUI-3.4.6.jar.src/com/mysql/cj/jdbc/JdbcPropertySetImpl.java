/*    */ package com.mysql.cj.jdbc;
/*    */ 
/*    */ import com.mysql.cj.conf.DefaultPropertySet;
/*    */ import com.mysql.cj.conf.PropertyDefinition;
/*    */ import com.mysql.cj.conf.PropertyDefinitions;
/*    */ import com.mysql.cj.conf.PropertyKey;
/*    */ import com.mysql.cj.conf.RuntimeProperty;
/*    */ import com.mysql.cj.util.StringUtils;
/*    */ import java.sql.DriverPropertyInfo;
/*    */ import java.sql.SQLException;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import java.util.stream.Collectors;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class JdbcPropertySetImpl
/*    */   extends DefaultPropertySet
/*    */   implements JdbcPropertySet
/*    */ {
/*    */   private static final long serialVersionUID = -8223499903182568260L;
/*    */   
/*    */   public void postInitialization() {
/* 53 */     if (((Integer)getIntegerProperty(PropertyKey.maxRows).getValue()).intValue() == 0)
/*    */     {
/* 55 */       getProperty(PropertyKey.maxRows).setValue(Integer.valueOf(-1), null);
/*    */     }
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 61 */     String testEncoding = (String)getStringProperty(PropertyKey.characterEncoding).getValue();
/*    */     
/* 63 */     if (testEncoding != null) {
/*    */       
/* 65 */       String testString = "abc";
/* 66 */       StringUtils.getBytes(testString, testEncoding);
/*    */     } 
/*    */     
/* 69 */     if (((Boolean)getBooleanProperty(PropertyKey.useCursorFetch).getValue()).booleanValue())
/*    */     {
/* 71 */       getProperty(PropertyKey.useServerPrepStmts).setValue(Boolean.valueOf(true));
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public List<DriverPropertyInfo> exposeAsDriverPropertyInfo() throws SQLException {
/* 77 */     return (List<DriverPropertyInfo>)PropertyDefinitions.PROPERTY_KEY_TO_PROPERTY_DEFINITION.entrySet().stream()
/* 78 */       .filter(e -> !((PropertyDefinition)e.getValue()).getCategory().equals(PropertyDefinitions.CATEGORY_XDEVAPI)).map(Map.Entry::getKey).map(this::getProperty)
/* 79 */       .map(this::getAsDriverPropertyInfo).collect(Collectors.toList());
/*    */   }
/*    */   
/*    */   private DriverPropertyInfo getAsDriverPropertyInfo(RuntimeProperty<?> pr) {
/* 83 */     PropertyDefinition<?> pdef = pr.getPropertyDefinition();
/*    */     
/* 85 */     DriverPropertyInfo dpi = new DriverPropertyInfo(pdef.getName(), null);
/* 86 */     dpi.choices = pdef.getAllowableValues();
/* 87 */     dpi.value = (pr.getStringValue() != null) ? pr.getStringValue() : null;
/* 88 */     dpi.required = false;
/* 89 */     dpi.description = pdef.getDescription();
/*    */     
/* 91 */     return dpi;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\jdbc\JdbcPropertySetImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */