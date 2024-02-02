/*    */ package com.github.odiszapc.nginxparser;
/*    */ 
/*    */ import com.github.odiszapc.nginxparser.antlr.NginxLexer;
/*    */ import com.github.odiszapc.nginxparser.antlr.NginxListenerImpl;
/*    */ import com.github.odiszapc.nginxparser.antlr.NginxParser;
/*    */ import com.github.odiszapc.nginxparser.javacc.NginxConfigParser;
/*    */ import com.github.odiszapc.nginxparser.javacc.ParseException;
/*    */ import java.io.FileInputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.util.Collection;
/*    */ import org.antlr.v4.runtime.ANTLRInputStream;
/*    */ import org.antlr.v4.runtime.CommonTokenStream;
/*    */ import org.antlr.v4.runtime.TokenSource;
/*    */ import org.antlr.v4.runtime.tree.ParseTree;
/*    */ import org.antlr.v4.runtime.tree.ParseTreeListener;
/*    */ import org.antlr.v4.runtime.tree.ParseTreeWalker;
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
/*    */ public class NgxConfig
/*    */   extends NgxBlock
/*    */ {
/* 39 */   public static final Class<? extends NgxEntry> PARAM = (Class)NgxParam.class;
/* 40 */   public static final Class<? extends NgxEntry> COMMENT = (Class)NgxComment.class;
/* 41 */   public static final Class<? extends NgxEntry> BLOCK = (Class)NgxBlock.class;
/* 42 */   public static final Class<? extends NgxEntry> IF = (Class)NgxIfBlock.class;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static NgxConfig read(String path) throws IOException {
/* 52 */     FileInputStream input = new FileInputStream(path);
/* 53 */     return read(input);
/*    */   }
/*    */   
/*    */   public static NgxConfig read(InputStream in) throws IOException {
/* 57 */     return readAntlr(in);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static NgxConfig readJavaCC(InputStream input) throws IOException, ParseException {
/* 68 */     NginxConfigParser parser = new NginxConfigParser(input);
/* 69 */     return parser.parse();
/*    */   }
/*    */ 
/*    */   
/*    */   public static NgxConfig readAntlr(InputStream in) throws IOException {
/* 74 */     ANTLRInputStream input = new ANTLRInputStream(in);
/* 75 */     NginxLexer lexer = new NginxLexer(input);
/* 76 */     CommonTokenStream tokens = new CommonTokenStream((TokenSource)lexer);
/* 77 */     NginxParser parser = new NginxParser(tokens);
/* 78 */     ParseTreeWalker walker = new ParseTreeWalker();
/*    */     
/* 80 */     NginxParser.ConfigContext configContext = parser.config();
/* 81 */     NginxListenerImpl listener = new NginxListenerImpl();
/* 82 */     walker.walk((ParseTreeListener)listener, (ParseTree)configContext);
/*    */     
/* 84 */     return listener.getResult();
/*    */   }
/*    */ 
/*    */   
/*    */   public Collection<NgxToken> getTokens() {
/* 89 */     throw new IllegalStateException("Not implemented");
/*    */   }
/*    */ 
/*    */   
/*    */   public void addValue(NgxToken token) {
/* 94 */     throw new IllegalStateException("Not implemented");
/*    */   }
/*    */   
/*    */   public String toString() {
/* 98 */     return "Nginx Config (" + getEntries().size() + " entries)";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\odiszapc\nginxparser\NgxConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */