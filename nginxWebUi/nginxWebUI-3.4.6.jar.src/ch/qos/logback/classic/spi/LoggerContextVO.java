/*    */ package ch.qos.logback.classic.spi;
/*    */ 
/*    */ import ch.qos.logback.classic.LoggerContext;
/*    */ import java.io.Serializable;
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
/*    */ 
/*    */ public class LoggerContextVO
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 5488023392483144387L;
/*    */   final String name;
/*    */   final Map<String, String> propertyMap;
/*    */   final long birthTime;
/*    */   
/*    */   public LoggerContextVO(LoggerContext lc) {
/* 45 */     this.name = lc.getName();
/* 46 */     this.propertyMap = lc.getCopyOfPropertyMap();
/* 47 */     this.birthTime = lc.getBirthTime();
/*    */   }
/*    */   
/*    */   public LoggerContextVO(String name, Map<String, String> propertyMap, long birthTime) {
/* 51 */     this.name = name;
/* 52 */     this.propertyMap = propertyMap;
/* 53 */     this.birthTime = birthTime;
/*    */   }
/*    */   
/*    */   public String getName() {
/* 57 */     return this.name;
/*    */   }
/*    */   
/*    */   public Map<String, String> getPropertyMap() {
/* 61 */     return this.propertyMap;
/*    */   }
/*    */   
/*    */   public long getBirthTime() {
/* 65 */     return this.birthTime;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 70 */     return "LoggerContextVO{name='" + this.name + '\'' + ", propertyMap=" + this.propertyMap + ", birthTime=" + this.birthTime + '}';
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object o) {
/* 75 */     if (this == o)
/* 76 */       return true; 
/* 77 */     if (!(o instanceof LoggerContextVO)) {
/* 78 */       return false;
/*    */     }
/* 80 */     LoggerContextVO that = (LoggerContextVO)o;
/*    */     
/* 82 */     if (this.birthTime != that.birthTime)
/* 83 */       return false; 
/* 84 */     if ((this.name != null) ? !this.name.equals(that.name) : (that.name != null))
/* 85 */       return false; 
/* 86 */     if ((this.propertyMap != null) ? !this.propertyMap.equals(that.propertyMap) : (that.propertyMap != null)) {
/* 87 */       return false;
/*    */     }
/* 89 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 94 */     int result = (this.name != null) ? this.name.hashCode() : 0;
/* 95 */     result = 31 * result + ((this.propertyMap != null) ? this.propertyMap.hashCode() : 0);
/* 96 */     result = 31 * result + (int)(this.birthTime ^ this.birthTime >>> 32L);
/*    */     
/* 98 */     return result;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\spi\LoggerContextVO.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */