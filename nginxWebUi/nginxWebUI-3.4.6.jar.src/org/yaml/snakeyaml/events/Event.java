/*     */ package org.yaml.snakeyaml.events;
/*     */ 
/*     */ import org.yaml.snakeyaml.error.Mark;
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
/*     */ public abstract class Event
/*     */ {
/*     */   private final Mark startMark;
/*     */   private final Mark endMark;
/*     */   
/*     */   public enum ID
/*     */   {
/*  26 */     Alias,
/*  27 */     Comment,
/*  28 */     DocumentEnd,
/*  29 */     DocumentStart,
/*  30 */     MappingEnd,
/*  31 */     MappingStart,
/*  32 */     Scalar,
/*  33 */     SequenceEnd,
/*  34 */     SequenceStart,
/*  35 */     StreamEnd,
/*  36 */     StreamStart;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Event(Mark startMark, Mark endMark) {
/*  43 */     this.startMark = startMark;
/*  44 */     this.endMark = endMark;
/*     */   }
/*     */   
/*     */   public String toString() {
/*  48 */     return "<" + getClass().getName() + "(" + getArguments() + ")>";
/*     */   }
/*     */   
/*     */   public Mark getStartMark() {
/*  52 */     return this.startMark;
/*     */   }
/*     */   
/*     */   public Mark getEndMark() {
/*  56 */     return this.endMark;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String getArguments() {
/*  65 */     return "";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean is(ID id) {
/*  74 */     return (getEventId() == id);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract ID getEventId();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/*  88 */     if (obj instanceof Event) {
/*  89 */       return toString().equals(obj.toString());
/*     */     }
/*  91 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 100 */     return toString().hashCode();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\yaml\snakeyaml\events\Event.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */