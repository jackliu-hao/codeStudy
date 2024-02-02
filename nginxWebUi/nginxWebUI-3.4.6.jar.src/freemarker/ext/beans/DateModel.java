/*    */ package freemarker.ext.beans;
/*    */ 
/*    */ import freemarker.ext.util.ModelFactory;
/*    */ import freemarker.template.ObjectWrapper;
/*    */ import freemarker.template.TemplateDateModel;
/*    */ import freemarker.template.TemplateModel;
/*    */ import java.util.Date;
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
/*    */ public class DateModel
/*    */   extends BeanModel
/*    */   implements TemplateDateModel
/*    */ {
/* 39 */   static final ModelFactory FACTORY = new ModelFactory()
/*    */     {
/*    */       
/*    */       public TemplateModel create(Object object, ObjectWrapper wrapper)
/*    */       {
/* 44 */         return (TemplateModel)new DateModel((Date)object, (BeansWrapper)wrapper);
/*    */       }
/*    */     };
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private final int type;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public DateModel(Date date, BeansWrapper wrapper) {
/* 59 */     super(date, wrapper);
/* 60 */     if (date instanceof java.sql.Date) {
/* 61 */       this.type = 2;
/* 62 */     } else if (date instanceof java.sql.Time) {
/* 63 */       this.type = 1;
/* 64 */     } else if (date instanceof java.sql.Timestamp) {
/* 65 */       this.type = 3;
/*    */     } else {
/* 67 */       this.type = wrapper.getDefaultDateType();
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public Date getAsDate() {
/* 73 */     return (Date)this.object;
/*    */   }
/*    */ 
/*    */   
/*    */   public int getDateType() {
/* 78 */     return this.type;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\beans\DateModel.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */