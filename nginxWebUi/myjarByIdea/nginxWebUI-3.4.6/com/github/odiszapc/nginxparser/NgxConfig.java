package com.github.odiszapc.nginxparser;

import com.github.odiszapc.nginxparser.antlr.NginxLexer;
import com.github.odiszapc.nginxparser.antlr.NginxListenerImpl;
import com.github.odiszapc.nginxparser.antlr.NginxParser;
import com.github.odiszapc.nginxparser.javacc.NginxConfigParser;
import com.github.odiszapc.nginxparser.javacc.ParseException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class NgxConfig extends NgxBlock {
   public static final Class<? extends NgxEntry> PARAM = NgxParam.class;
   public static final Class<? extends NgxEntry> COMMENT = NgxComment.class;
   public static final Class<? extends NgxEntry> BLOCK = NgxBlock.class;
   public static final Class<? extends NgxEntry> IF = NgxIfBlock.class;

   public static NgxConfig read(String path) throws IOException {
      FileInputStream input = new FileInputStream(path);
      return read((InputStream)input);
   }

   public static NgxConfig read(InputStream in) throws IOException {
      return readAntlr(in);
   }

   public static NgxConfig readJavaCC(InputStream input) throws IOException, ParseException {
      NginxConfigParser parser = new NginxConfigParser(input);
      return parser.parse();
   }

   public static NgxConfig readAntlr(InputStream in) throws IOException {
      ANTLRInputStream input = new ANTLRInputStream(in);
      NginxLexer lexer = new NginxLexer(input);
      CommonTokenStream tokens = new CommonTokenStream(lexer);
      NginxParser parser = new NginxParser(tokens);
      ParseTreeWalker walker = new ParseTreeWalker();
      ParseTree tree = parser.config();
      NginxListenerImpl listener = new NginxListenerImpl();
      walker.walk(listener, tree);
      return listener.getResult();
   }

   public Collection<NgxToken> getTokens() {
      throw new IllegalStateException("Not implemented");
   }

   public void addValue(NgxToken token) {
      throw new IllegalStateException("Not implemented");
   }

   public String toString() {
      return "Nginx Config (" + this.getEntries().size() + " entries)";
   }
}
