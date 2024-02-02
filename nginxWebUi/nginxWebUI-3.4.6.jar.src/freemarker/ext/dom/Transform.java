/*     */ package freemarker.ext.dom;
/*     */ 
/*     */ import freemarker.core.Environment;
/*     */ import freemarker.template.Configuration;
/*     */ import freemarker.template.Template;
/*     */ import freemarker.template.TemplateNodeModel;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.Writer;
/*     */ import java.util.Locale;
/*     */ import java.util.StringTokenizer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Deprecated
/*     */ public class Transform
/*     */ {
/*     */   private File inputFile;
/*     */   private File ftlFile;
/*     */   private File outputFile;
/*     */   private String encoding;
/*     */   private Locale locale;
/*     */   private Configuration cfg;
/*     */   
/*     */   @Deprecated
/*     */   public static void main(String[] args) {
/*     */     try {
/*  54 */       Transform proc = transformFromArgs(args);
/*  55 */       proc.transform();
/*  56 */     } catch (IllegalArgumentException iae) {
/*  57 */       System.err.println(iae.getMessage());
/*  58 */       usage();
/*  59 */     } catch (Exception e) {
/*  60 */       e.printStackTrace();
/*     */     } 
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
/*     */   Transform(File inputFile, File ftlFile, File outputFile, Locale locale, String encoding) throws IOException {
/*  77 */     if (encoding == null) {
/*  78 */       encoding = System.getProperty("file.encoding");
/*     */     }
/*  80 */     if (locale == null) {
/*  81 */       locale = Locale.getDefault();
/*     */     }
/*  83 */     this.encoding = encoding;
/*  84 */     this.locale = locale;
/*  85 */     this.inputFile = inputFile;
/*  86 */     this.ftlFile = ftlFile;
/*  87 */     this.outputFile = outputFile;
/*  88 */     File ftlDirectory = ftlFile.getAbsoluteFile().getParentFile();
/*  89 */     this.cfg = new Configuration();
/*  90 */     this.cfg.setDirectoryForTemplateLoading(ftlDirectory);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void transform() throws Exception {
/*  97 */     String templateName = this.ftlFile.getName();
/*  98 */     Template template = this.cfg.getTemplate(templateName, this.locale);
/*  99 */     NodeModel rootNode = NodeModel.parse(this.inputFile);
/* 100 */     OutputStream outputStream = System.out;
/* 101 */     if (this.outputFile != null) {
/* 102 */       outputStream = new FileOutputStream(this.outputFile);
/*     */     }
/* 104 */     Writer outputWriter = new OutputStreamWriter(outputStream, this.encoding);
/*     */     try {
/* 106 */       template.process(null, outputWriter, null, (TemplateNodeModel)rootNode);
/*     */     } finally {
/* 108 */       if (this.outputFile != null)
/* 109 */         outputWriter.close(); 
/*     */     } 
/*     */   }
/*     */   
/*     */   static Transform transformFromArgs(String[] args) throws IOException {
/* 114 */     int i = 0;
/* 115 */     String input = null, output = null, ftl = null, loc = null, enc = null;
/* 116 */     while (i < args.length) {
/* 117 */       String dashArg = args[i++];
/* 118 */       if (i >= args.length) {
/* 119 */         throw new IllegalArgumentException("");
/*     */       }
/* 121 */       String arg = args[i++];
/* 122 */       if (dashArg.equals("-in")) {
/* 123 */         if (input != null) {
/* 124 */           throw new IllegalArgumentException("The input file should only be specified once");
/*     */         }
/* 126 */         input = arg; continue;
/* 127 */       }  if (dashArg.equals("-ftl")) {
/* 128 */         if (ftl != null) {
/* 129 */           throw new IllegalArgumentException("The ftl file should only be specified once");
/*     */         }
/* 131 */         ftl = arg; continue;
/* 132 */       }  if (dashArg.equals("-out")) {
/* 133 */         if (output != null) {
/* 134 */           throw new IllegalArgumentException("The output file should only be specified once");
/*     */         }
/* 136 */         output = arg; continue;
/* 137 */       }  if (dashArg.equals("-locale")) {
/* 138 */         if (loc != null) {
/* 139 */           throw new IllegalArgumentException("The locale should only be specified once");
/*     */         }
/* 141 */         loc = arg; continue;
/* 142 */       }  if (dashArg.equals("-encoding")) {
/* 143 */         if (enc != null) {
/* 144 */           throw new IllegalArgumentException("The encoding should only be specified once");
/*     */         }
/* 146 */         enc = arg; continue;
/*     */       } 
/* 148 */       throw new IllegalArgumentException("Unknown input argument: " + dashArg);
/*     */     } 
/*     */     
/* 151 */     if (input == null) {
/* 152 */       throw new IllegalArgumentException("No input file specified.");
/*     */     }
/* 154 */     if (ftl == null) {
/* 155 */       throw new IllegalArgumentException("No ftl file specified.");
/*     */     }
/* 157 */     File inputFile = (new File(input)).getAbsoluteFile();
/* 158 */     File ftlFile = (new File(ftl)).getAbsoluteFile();
/* 159 */     if (!inputFile.exists()) {
/* 160 */       throw new IllegalArgumentException("Input file does not exist: " + input);
/*     */     }
/* 162 */     if (!ftlFile.exists()) {
/* 163 */       throw new IllegalArgumentException("FTL file does not exist: " + ftl);
/*     */     }
/* 165 */     if (!inputFile.isFile() || !inputFile.canRead()) {
/* 166 */       throw new IllegalArgumentException("Input file must be a readable file: " + input);
/*     */     }
/* 168 */     if (!ftlFile.isFile() || !ftlFile.canRead()) {
/* 169 */       throw new IllegalArgumentException("FTL file must be a readable file: " + ftl);
/*     */     }
/* 171 */     File outputFile = null;
/* 172 */     if (output != null) {
/* 173 */       outputFile = (new File(output)).getAbsoluteFile();
/* 174 */       File outputDirectory = outputFile.getParentFile();
/* 175 */       if (!outputDirectory.exists() || !outputDirectory.canWrite()) {
/* 176 */         throw new IllegalArgumentException("The output directory must exist and be writable: " + outputDirectory);
/*     */       }
/*     */     } 
/*     */     
/* 180 */     Locale locale = Locale.getDefault();
/* 181 */     if (loc != null) {
/* 182 */       locale = localeFromString(loc);
/*     */     }
/* 184 */     return new Transform(inputFile, ftlFile, outputFile, locale, enc);
/*     */   }
/*     */   
/*     */   static Locale localeFromString(String ls) {
/* 188 */     if (ls == null) ls = ""; 
/* 189 */     String lang = "", country = "", variant = "";
/* 190 */     StringTokenizer st = new StringTokenizer(ls, "_-,");
/* 191 */     if (st.hasMoreTokens()) {
/* 192 */       lang = st.nextToken();
/* 193 */       if (st.hasMoreTokens()) {
/* 194 */         country = st.nextToken();
/* 195 */         if (st.hasMoreTokens()) {
/* 196 */           variant = st.nextToken();
/*     */         }
/*     */       } 
/* 199 */       return new Locale(lang, country, variant);
/*     */     } 
/* 201 */     return Locale.getDefault();
/*     */   }
/*     */ 
/*     */   
/*     */   static void usage() {
/* 206 */     System.err
/* 207 */       .println("Usage: java freemarker.ext.dom.Transform -in <xmlfile> -ftl <ftlfile> [-out <outfile>] [-locale <locale>] [-encoding <encoding>]");
/*     */     
/* 209 */     if (Environment.getCurrentEnvironment() == null)
/* 210 */       System.exit(-1); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\dom\Transform.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */