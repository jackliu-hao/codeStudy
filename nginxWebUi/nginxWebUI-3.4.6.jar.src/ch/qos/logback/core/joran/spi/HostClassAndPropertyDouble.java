/*    */ package ch.qos.logback.core.joran.spi;
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
/*    */ public class HostClassAndPropertyDouble
/*    */ {
/*    */   final Class<?> hostClass;
/*    */   final String propertyName;
/*    */   
/*    */   public HostClassAndPropertyDouble(Class<?> hostClass, String propertyName) {
/* 32 */     this.hostClass = hostClass;
/* 33 */     this.propertyName = propertyName;
/*    */   }
/*    */   
/*    */   public Class<?> getHostClass() {
/* 37 */     return this.hostClass;
/*    */   }
/*    */   
/*    */   public String getPropertyName() {
/* 41 */     return this.propertyName;
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 46 */     int prime = 31;
/* 47 */     int result = 1;
/* 48 */     result = 31 * result + ((this.hostClass == null) ? 0 : this.hostClass.hashCode());
/* 49 */     result = 31 * result + ((this.propertyName == null) ? 0 : this.propertyName.hashCode());
/* 50 */     return result;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object obj) {
/* 55 */     if (this == obj)
/* 56 */       return true; 
/* 57 */     if (obj == null)
/* 58 */       return false; 
/* 59 */     if (getClass() != obj.getClass())
/* 60 */       return false; 
/* 61 */     HostClassAndPropertyDouble other = (HostClassAndPropertyDouble)obj;
/* 62 */     if (this.hostClass == null) {
/* 63 */       if (other.hostClass != null)
/* 64 */         return false; 
/* 65 */     } else if (!this.hostClass.equals(other.hostClass)) {
/* 66 */       return false;
/* 67 */     }  if (this.propertyName == null) {
/* 68 */       if (other.propertyName != null)
/* 69 */         return false; 
/* 70 */     } else if (!this.propertyName.equals(other.propertyName)) {
/* 71 */       return false;
/* 72 */     }  return true;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\joran\spi\HostClassAndPropertyDouble.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */