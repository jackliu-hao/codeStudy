/*     */ package javax.mail;
/*     */ 
/*     */ import java.util.Vector;
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
/*     */ public class FetchProfile
/*     */ {
/*     */   private Vector specials;
/*     */   private Vector headers;
/*     */   
/*     */   public static class Item
/*     */   {
/* 116 */     public static final Item ENVELOPE = new Item("ENVELOPE");
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
/* 128 */     public static final Item CONTENT_INFO = new Item("CONTENT_INFO");
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 133 */     public static final Item FLAGS = new Item("FLAGS");
/*     */ 
/*     */     
/*     */     private String name;
/*     */ 
/*     */ 
/*     */     
/*     */     protected Item(String name) {
/* 141 */       this.name = name;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String toString() {
/* 148 */       return getClass().getName() + "[" + this.name + "]";
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FetchProfile() {
/* 156 */     this.specials = null;
/* 157 */     this.headers = null;
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
/*     */   public void add(Item item) {
/* 170 */     if (this.specials == null)
/* 171 */       this.specials = new Vector(); 
/* 172 */     this.specials.addElement(item);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void add(String headerName) {
/* 182 */     if (this.headers == null)
/* 183 */       this.headers = new Vector(); 
/* 184 */     this.headers.addElement(headerName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean contains(Item item) {
/* 191 */     return (this.specials != null && this.specials.contains(item));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean contains(String headerName) {
/* 198 */     return (this.headers != null && this.headers.contains(headerName));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Item[] getItems() {
/* 207 */     if (this.specials == null) {
/* 208 */       return new Item[0];
/*     */     }
/* 210 */     Item[] s = new Item[this.specials.size()];
/* 211 */     this.specials.copyInto((Object[])s);
/* 212 */     return s;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String[] getHeaderNames() {
/* 221 */     if (this.headers == null) {
/* 222 */       return new String[0];
/*     */     }
/* 224 */     String[] s = new String[this.headers.size()];
/* 225 */     this.headers.copyInto((Object[])s);
/* 226 */     return s;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\FetchProfile.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */