/*    */ package ch.qos.logback.core.spi;
/*    */ 
/*    */ import ch.qos.logback.core.helpers.CyclicBuffer;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
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
/*    */ public class CyclicBufferTracker<E>
/*    */   extends AbstractComponentTracker<CyclicBuffer<E>>
/*    */ {
/*    */   static final int DEFAULT_NUMBER_OF_BUFFERS = 64;
/*    */   static final int DEFAULT_BUFFER_SIZE = 256;
/* 30 */   int bufferSize = 256;
/*    */ 
/*    */   
/*    */   public CyclicBufferTracker() {
/* 34 */     setMaxComponents(64);
/*    */   }
/*    */   
/*    */   public int getBufferSize() {
/* 38 */     return this.bufferSize;
/*    */   }
/*    */   
/*    */   public void setBufferSize(int bufferSize) {
/* 42 */     this.bufferSize = bufferSize;
/*    */   }
/*    */ 
/*    */   
/*    */   protected void processPriorToRemoval(CyclicBuffer<E> component) {
/* 47 */     component.clear();
/*    */   }
/*    */ 
/*    */   
/*    */   protected CyclicBuffer<E> buildComponent(String key) {
/* 52 */     return new CyclicBuffer(this.bufferSize);
/*    */   }
/*    */ 
/*    */   
/*    */   protected boolean isComponentStale(CyclicBuffer<E> eCyclicBuffer) {
/* 57 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   List<String> liveKeysAsOrderedList() {
/* 62 */     return new ArrayList<String>(this.liveMap.keySet());
/*    */   }
/*    */   
/*    */   List<String> lingererKeysAsOrderedList() {
/* 66 */     return new ArrayList<String>(this.lingerersMap.keySet());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\spi\CyclicBufferTracker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */