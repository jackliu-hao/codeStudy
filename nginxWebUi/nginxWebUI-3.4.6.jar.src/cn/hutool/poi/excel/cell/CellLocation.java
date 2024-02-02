/*    */ package cn.hutool.poi.excel.cell;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import java.util.Objects;
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
/*    */ public class CellLocation
/*    */   implements Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private int x;
/*    */   private int y;
/*    */   
/*    */   public CellLocation(int x, int y) {
/* 25 */     this.x = x;
/* 26 */     this.y = y;
/*    */   }
/*    */   
/*    */   public int getX() {
/* 30 */     return this.x;
/*    */   }
/*    */   
/*    */   public void setX(int x) {
/* 34 */     this.x = x;
/*    */   }
/*    */   
/*    */   public int getY() {
/* 38 */     return this.y;
/*    */   }
/*    */   
/*    */   public void setY(int y) {
/* 42 */     this.y = y;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object o) {
/* 47 */     if (this == o) {
/* 48 */       return true;
/*    */     }
/* 50 */     if (o == null || getClass() != o.getClass()) {
/* 51 */       return false;
/*    */     }
/* 53 */     CellLocation that = (CellLocation)o;
/* 54 */     return (this.x == that.x && this.y == that.y);
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 59 */     return Objects.hash(new Object[] { Integer.valueOf(this.x), Integer.valueOf(this.y) });
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 64 */     return "CellLocation{x=" + this.x + ", y=" + this.y + '}';
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\poi\excel\cell\CellLocation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */