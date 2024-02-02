/*    */ package cn.hutool.core.convert.impl;
/*    */ 
/*    */ import cn.hutool.core.convert.AbstractConverter;
/*    */ import cn.hutool.core.convert.ConvertException;
/*    */ import cn.hutool.core.io.IoUtil;
/*    */ import cn.hutool.core.util.CharsetUtil;
/*    */ import cn.hutool.core.util.XmlUtil;
/*    */ import java.io.InputStream;
/*    */ import java.io.Reader;
/*    */ import java.lang.reflect.Type;
/*    */ import java.sql.Blob;
/*    */ import java.sql.Clob;
/*    */ import java.sql.SQLException;
/*    */ import java.util.TimeZone;
/*    */ import org.w3c.dom.Node;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class StringConverter
/*    */   extends AbstractConverter<String>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   protected String convertInternal(Object value) {
/* 27 */     if (value instanceof TimeZone)
/* 28 */       return ((TimeZone)value).getID(); 
/* 29 */     if (value instanceof Node)
/* 30 */       return XmlUtil.toStr((Node)value); 
/* 31 */     if (value instanceof Clob)
/* 32 */       return clobToStr((Clob)value); 
/* 33 */     if (value instanceof Blob)
/* 34 */       return blobToStr((Blob)value); 
/* 35 */     if (value instanceof Type) {
/* 36 */       return ((Type)value).getTypeName();
/*    */     }
/*    */ 
/*    */     
/* 40 */     return convertToStr(value);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private static String clobToStr(Clob clob) {
/* 51 */     Reader reader = null;
/*    */     try {
/* 53 */       reader = clob.getCharacterStream();
/* 54 */       return IoUtil.read(reader);
/* 55 */     } catch (SQLException e) {
/* 56 */       throw new ConvertException(e);
/*    */     } finally {
/* 58 */       IoUtil.close(reader);
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private static String blobToStr(Blob blob) {
/* 70 */     InputStream in = null;
/*    */     try {
/* 72 */       in = blob.getBinaryStream();
/* 73 */       return IoUtil.read(in, CharsetUtil.CHARSET_UTF_8);
/* 74 */     } catch (SQLException e) {
/* 75 */       throw new ConvertException(e);
/*    */     } finally {
/* 77 */       IoUtil.close(in);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\convert\impl\StringConverter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */