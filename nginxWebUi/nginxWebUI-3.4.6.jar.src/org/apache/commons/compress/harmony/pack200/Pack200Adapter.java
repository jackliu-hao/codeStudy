/*    */ package org.apache.commons.compress.harmony.pack200;
/*    */ 
/*    */ import java.beans.PropertyChangeListener;
/*    */ import java.beans.PropertyChangeSupport;
/*    */ import java.util.SortedMap;
/*    */ import java.util.TreeMap;
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
/*    */ public abstract class Pack200Adapter
/*    */ {
/*    */   protected static final int DEFAULT_BUFFER_SIZE = 8192;
/* 31 */   private final PropertyChangeSupport support = new PropertyChangeSupport(this);
/*    */   
/* 33 */   private final SortedMap<String, String> properties = new TreeMap<>();
/*    */   
/*    */   public SortedMap<String, String> properties() {
/* 36 */     return this.properties;
/*    */   }
/*    */   
/*    */   public void addPropertyChangeListener(PropertyChangeListener listener) {
/* 40 */     this.support.addPropertyChangeListener(listener);
/*    */   }
/*    */   
/*    */   protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
/* 44 */     this.support.firePropertyChange(propertyName, oldValue, newValue);
/*    */   }
/*    */   
/*    */   public void removePropertyChangeListener(PropertyChangeListener listener) {
/* 48 */     this.support.removePropertyChangeListener(listener);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected void completed(double value) {
/* 57 */     firePropertyChange("pack.progress", null, String.valueOf((int)(100.0D * value)));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\harmony\pack200\Pack200Adapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */