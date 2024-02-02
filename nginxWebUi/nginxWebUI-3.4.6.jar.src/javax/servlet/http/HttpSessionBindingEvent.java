/*     */ package javax.servlet.http;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HttpSessionBindingEvent
/*     */   extends HttpSessionEvent
/*     */ {
/*     */   private static final long serialVersionUID = 7308000419984825907L;
/*     */   private String name;
/*     */   private Object value;
/*     */   
/*     */   public HttpSessionBindingEvent(HttpSession session, String name) {
/*  63 */     super(session);
/*  64 */     this.name = name;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpSessionBindingEvent(HttpSession session, String name, Object value) {
/*  82 */     super(session);
/*  83 */     this.name = name;
/*  84 */     this.value = value;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public HttpSession getSession() {
/*  90 */     return super.getSession();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getName() {
/* 101 */     return this.name;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getValue() {
/* 117 */     return this.value;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\servlet\http\HttpSessionBindingEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */