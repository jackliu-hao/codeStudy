/*     */ package org.codehaus.plexus.util.xml.pull;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.Reader;
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
/*     */ public interface XmlPullParser
/*     */ {
/*     */   public static final String NO_NAMESPACE = "";
/*     */   public static final int START_DOCUMENT = 0;
/*     */   public static final int END_DOCUMENT = 1;
/*     */   public static final int START_TAG = 2;
/*     */   public static final int END_TAG = 3;
/*     */   public static final int TEXT = 4;
/*     */   public static final int CDSECT = 5;
/*     */   public static final int ENTITY_REF = 6;
/*     */   public static final int IGNORABLE_WHITESPACE = 7;
/*     */   public static final int PROCESSING_INSTRUCTION = 8;
/*     */   public static final int COMMENT = 9;
/*     */   public static final int DOCDECL = 10;
/* 329 */   public static final String[] TYPES = new String[] { "START_DOCUMENT", "END_DOCUMENT", "START_TAG", "END_TAG", "TEXT", "CDSECT", "ENTITY_REF", "IGNORABLE_WHITESPACE", "PROCESSING_INSTRUCTION", "COMMENT", "DOCDECL" };
/*     */   public static final String FEATURE_PROCESS_NAMESPACES = "http://xmlpull.org/v1/doc/features.html#process-namespaces";
/*     */   public static final String FEATURE_REPORT_NAMESPACE_ATTRIBUTES = "http://xmlpull.org/v1/doc/features.html#report-namespace-prefixes";
/*     */   public static final String FEATURE_PROCESS_DOCDECL = "http://xmlpull.org/v1/doc/features.html#process-docdecl";
/*     */   public static final String FEATURE_VALIDATION = "http://xmlpull.org/v1/doc/features.html#validation";
/*     */   
/*     */   void setFeature(String paramString, boolean paramBoolean) throws XmlPullParserException;
/*     */   
/*     */   boolean getFeature(String paramString);
/*     */   
/*     */   void setProperty(String paramString, Object paramObject) throws XmlPullParserException;
/*     */   
/*     */   Object getProperty(String paramString);
/*     */   
/*     */   void setInput(Reader paramReader) throws XmlPullParserException;
/*     */   
/*     */   void setInput(InputStream paramInputStream, String paramString) throws XmlPullParserException;
/*     */   
/*     */   String getInputEncoding();
/*     */   
/*     */   void defineEntityReplacementText(String paramString1, String paramString2) throws XmlPullParserException;
/*     */   
/*     */   int getNamespaceCount(int paramInt) throws XmlPullParserException;
/*     */   
/*     */   String getNamespacePrefix(int paramInt) throws XmlPullParserException;
/*     */   
/*     */   String getNamespaceUri(int paramInt) throws XmlPullParserException;
/*     */   
/*     */   String getNamespace(String paramString);
/*     */   
/*     */   int getDepth();
/*     */   
/*     */   String getPositionDescription();
/*     */   
/*     */   int getLineNumber();
/*     */   
/*     */   int getColumnNumber();
/*     */   
/*     */   boolean isWhitespace() throws XmlPullParserException;
/*     */   
/*     */   String getText();
/*     */   
/*     */   char[] getTextCharacters(int[] paramArrayOfint);
/*     */   
/*     */   String getNamespace();
/*     */   
/*     */   String getName();
/*     */   
/*     */   String getPrefix();
/*     */   
/*     */   boolean isEmptyElementTag() throws XmlPullParserException;
/*     */   
/*     */   int getAttributeCount();
/*     */   
/*     */   String getAttributeNamespace(int paramInt);
/*     */   
/*     */   String getAttributeName(int paramInt);
/*     */   
/*     */   String getAttributePrefix(int paramInt);
/*     */   
/*     */   String getAttributeType(int paramInt);
/*     */   
/*     */   boolean isAttributeDefault(int paramInt);
/*     */   
/*     */   String getAttributeValue(int paramInt);
/*     */   
/*     */   String getAttributeValue(String paramString1, String paramString2);
/*     */   
/*     */   int getEventType() throws XmlPullParserException;
/*     */   
/*     */   int next() throws XmlPullParserException, IOException;
/*     */   
/*     */   int nextToken() throws XmlPullParserException, IOException;
/*     */   
/*     */   void require(int paramInt, String paramString1, String paramString2) throws XmlPullParserException, IOException;
/*     */   
/*     */   String nextText() throws XmlPullParserException, IOException;
/*     */   
/*     */   int nextTag() throws XmlPullParserException, IOException;
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\xml\pull\XmlPullParser.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */