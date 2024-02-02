/*     */ package freemarker.ext.ant;
/*     */ 
/*     */ import freemarker.core.Environment;
/*     */ import freemarker.ext.dom.NodeModel;
/*     */ import freemarker.ext.xml.NodeListModel;
/*     */ import freemarker.template.Configuration;
/*     */ import freemarker.template.ObjectWrapper;
/*     */ import freemarker.template.SimpleHash;
/*     */ import freemarker.template.SimpleScalar;
/*     */ import freemarker.template.Template;
/*     */ import freemarker.template.TemplateModel;
/*     */ import freemarker.template.TemplateNodeModel;
/*     */ import freemarker.template._TemplateAPI;
/*     */ import freemarker.template.utility.ClassUtil;
/*     */ import freemarker.template.utility.SecurityUtilities;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.Writer;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.StringTokenizer;
/*     */ import javax.xml.parsers.DocumentBuilder;
/*     */ import javax.xml.parsers.DocumentBuilderFactory;
/*     */ import javax.xml.parsers.ParserConfigurationException;
/*     */ import org.apache.tools.ant.BuildException;
/*     */ import org.apache.tools.ant.DirectoryScanner;
/*     */ import org.apache.tools.ant.taskdefs.MatchingTask;
/*     */ import org.w3c.dom.Document;
/*     */ import org.xml.sax.SAXParseException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/*     */ public class FreemarkerXmlTask
/*     */   extends MatchingTask
/*     */ {
/*     */   private JythonAntTask prepareModel;
/*     */   private JythonAntTask prepareEnvironment;
/*     */   private final DocumentBuilderFactory builderFactory;
/*     */   private DocumentBuilder builder;
/* 242 */   private Configuration cfg = new Configuration();
/*     */ 
/*     */ 
/*     */   
/*     */   private File destDir;
/*     */ 
/*     */   
/*     */   private File baseDir;
/*     */ 
/*     */   
/*     */   private File templateDir;
/*     */ 
/*     */   
/*     */   private String templateName;
/*     */ 
/*     */   
/*     */   private Template parsedTemplate;
/*     */ 
/*     */   
/* 261 */   private long templateFileLastModified = 0L;
/*     */ 
/*     */   
/* 264 */   private String projectAttribute = null;
/*     */   
/* 266 */   private File projectFile = null;
/*     */   
/*     */   private TemplateModel projectTemplate;
/*     */   
/*     */   private TemplateNodeModel projectNode;
/*     */   
/*     */   private TemplateModel propertiesTemplate;
/*     */   
/*     */   private TemplateModel userPropertiesTemplate;
/*     */   
/* 276 */   private long projectFileLastModified = 0L;
/*     */ 
/*     */   
/*     */   private boolean incremental = true;
/*     */ 
/*     */   
/* 282 */   private String extension = ".html";
/*     */   
/* 284 */   private String encoding = SecurityUtilities.getSystemProperty("file.encoding", "utf-8");
/* 285 */   private String templateEncoding = this.encoding;
/*     */   
/*     */   private boolean validation = false;
/* 288 */   private String models = "";
/* 289 */   private final Map modelsMap = new HashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FreemarkerXmlTask() {
/* 297 */     this.builderFactory = DocumentBuilderFactory.newInstance();
/* 298 */     this.builderFactory.setNamespaceAware(true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBasedir(File dir) {
/* 305 */     this.baseDir = dir;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setDestdir(File dir) {
/* 314 */     this.destDir = dir;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setExtension(String extension) {
/* 321 */     this.extension = extension;
/*     */   }
/*     */   
/*     */   public void setTemplate(String templateName) {
/* 325 */     this.templateName = templateName;
/*     */   }
/*     */   
/*     */   public void setTemplateDir(File templateDir) throws BuildException {
/* 329 */     this.templateDir = templateDir;
/*     */     try {
/* 331 */       this.cfg.setDirectoryForTemplateLoading(templateDir);
/* 332 */     } catch (Exception e) {
/* 333 */       throw new BuildException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setProjectfile(String projectAttribute) {
/* 341 */     this.projectAttribute = projectAttribute;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setIncremental(String incremental) {
/* 348 */     this.incremental = (!incremental.equalsIgnoreCase("false") && !incremental.equalsIgnoreCase("no") && !incremental.equalsIgnoreCase("off"));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setEncoding(String encoding) {
/* 355 */     this.encoding = encoding;
/*     */   }
/*     */   
/*     */   public void setTemplateEncoding(String inputEncoding) {
/* 359 */     this.templateEncoding = inputEncoding;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setValidation(boolean validation) {
/* 366 */     this.validation = validation;
/*     */   }
/*     */   
/*     */   public void setModels(String models) {
/* 370 */     this.models = models;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void execute() throws BuildException {
/* 378 */     if (this.baseDir == null) {
/* 379 */       this.baseDir = getProject().getBaseDir();
/*     */     }
/* 381 */     if (this.destDir == null) {
/* 382 */       String msg = "destdir attribute must be set!";
/* 383 */       throw new BuildException(msg, getLocation());
/*     */     } 
/*     */     
/* 386 */     File templateFile = null;
/*     */     
/* 388 */     if (this.templateDir == null) {
/* 389 */       if (this.templateName != null) {
/* 390 */         templateFile = new File(this.templateName);
/* 391 */         if (!templateFile.isAbsolute()) {
/* 392 */           templateFile = new File(getProject().getBaseDir(), this.templateName);
/*     */         }
/* 394 */         this.templateDir = templateFile.getParentFile();
/* 395 */         this.templateName = templateFile.getName();
/*     */       } else {
/* 397 */         this.templateDir = this.baseDir;
/*     */       } 
/* 399 */       setTemplateDir(this.templateDir);
/* 400 */     } else if (this.templateName != null) {
/* 401 */       if ((new File(this.templateName)).isAbsolute()) {
/* 402 */         throw new BuildException("Do not specify an absolute location for the template as well as a templateDir");
/*     */       }
/* 404 */       templateFile = new File(this.templateDir, this.templateName);
/*     */     } 
/* 406 */     if (templateFile != null) {
/* 407 */       this.templateFileLastModified = templateFile.lastModified();
/*     */     }
/*     */     
/*     */     try {
/* 411 */       if (this.templateName != null) {
/* 412 */         this.parsedTemplate = this.cfg.getTemplate(this.templateName, this.templateEncoding);
/*     */       }
/* 414 */     } catch (IOException ioe) {
/* 415 */       throw new BuildException(ioe.toString());
/*     */     } 
/*     */     
/* 418 */     log("Transforming into: " + this.destDir.getAbsolutePath(), 2);
/*     */ 
/*     */     
/* 421 */     if (this.projectAttribute != null && this.projectAttribute.length() > 0) {
/* 422 */       this.projectFile = new File(this.baseDir, this.projectAttribute);
/* 423 */       if (this.projectFile.isFile()) {
/* 424 */         this.projectFileLastModified = this.projectFile.lastModified();
/*     */       } else {
/* 426 */         log("Project file is defined, but could not be located: " + this.projectFile
/* 427 */             .getAbsolutePath(), 2);
/* 428 */         this.projectFile = null;
/*     */       } 
/*     */     } 
/*     */     
/* 432 */     generateModels();
/*     */ 
/*     */     
/* 435 */     DirectoryScanner scanner = getDirectoryScanner(this.baseDir);
/*     */     
/* 437 */     this.propertiesTemplate = wrapMap(this.project.getProperties());
/* 438 */     this.userPropertiesTemplate = wrapMap(this.project.getUserProperties());
/*     */     
/* 440 */     this.builderFactory.setValidating(this.validation);
/*     */     try {
/* 442 */       this.builder = this.builderFactory.newDocumentBuilder();
/* 443 */     } catch (ParserConfigurationException e) {
/* 444 */       throw new BuildException("Could not create document builder", e, getLocation());
/*     */     } 
/*     */ 
/*     */     
/* 448 */     String[] list = scanner.getIncludedFiles();
/*     */ 
/*     */     
/* 451 */     for (int i = 0; i < list.length; i++) {
/* 452 */       process(this.baseDir, list[i], this.destDir);
/*     */     }
/*     */   }
/*     */   
/*     */   public void addConfiguredJython(JythonAntTask jythonAntTask) {
/* 457 */     this.prepareEnvironment = jythonAntTask;
/*     */   }
/*     */   
/*     */   public void addConfiguredPrepareModel(JythonAntTask prepareModel) {
/* 461 */     this.prepareModel = prepareModel;
/*     */   }
/*     */   
/*     */   public void addConfiguredPrepareEnvironment(JythonAntTask prepareEnvironment) {
/* 465 */     this.prepareEnvironment = prepareEnvironment;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void process(File baseDir, String xmlFile, File destDir) throws BuildException {
/* 473 */     File outFile = null;
/* 474 */     File inFile = null;
/*     */     
/*     */     try {
/* 477 */       inFile = new File(baseDir, xmlFile);
/*     */ 
/*     */       
/* 480 */       outFile = new File(destDir, xmlFile.substring(0, xmlFile
/* 481 */             .lastIndexOf('.')) + this.extension);
/*     */ 
/*     */       
/* 484 */       if (!this.incremental || inFile
/* 485 */         .lastModified() > outFile.lastModified() || this.templateFileLastModified > outFile
/* 486 */         .lastModified() || this.projectFileLastModified > outFile
/* 487 */         .lastModified()) {
/* 488 */         ensureDirectoryFor(outFile);
/*     */ 
/*     */         
/* 491 */         log("Input:  " + xmlFile, 2);
/*     */         
/* 493 */         if (this.projectTemplate == null && this.projectFile != null) {
/* 494 */           Document doc = this.builder.parse(this.projectFile);
/* 495 */           this.projectTemplate = (TemplateModel)new NodeListModel(this.builder.parse(this.projectFile));
/* 496 */           this.projectNode = (TemplateNodeModel)NodeModel.wrap(doc);
/*     */         } 
/*     */ 
/*     */         
/* 500 */         Document docNode = this.builder.parse(inFile);
/*     */         
/* 502 */         NodeListModel nodeListModel = new NodeListModel(docNode);
/* 503 */         NodeModel nodeModel = NodeModel.wrap(docNode);
/* 504 */         HashMap<Object, Object> root = new HashMap<>();
/* 505 */         root.put("document", nodeListModel);
/* 506 */         insertDefaults(root);
/*     */ 
/*     */ 
/*     */         
/* 510 */         try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), this.encoding))) {
/*     */           
/* 512 */           if (this.parsedTemplate == null) {
/* 513 */             throw new BuildException("No template file specified in build script or in XML file");
/*     */           }
/* 515 */           if (this.prepareModel != null) {
/* 516 */             Map<Object, Object> vars = new HashMap<>();
/* 517 */             vars.put("model", root);
/* 518 */             vars.put("doc", docNode);
/* 519 */             if (this.projectNode != null) {
/* 520 */               vars.put("project", ((NodeModel)this.projectNode).getNode());
/*     */             }
/* 522 */             this.prepareModel.execute(vars);
/*     */           } 
/* 524 */           Environment env = this.parsedTemplate.createProcessingEnvironment(root, writer);
/* 525 */           env.setCurrentVisitorNode((TemplateNodeModel)nodeModel);
/* 526 */           if (this.prepareEnvironment != null) {
/* 527 */             Map<Object, Object> vars = new HashMap<>();
/* 528 */             vars.put("env", env);
/* 529 */             vars.put("doc", docNode);
/* 530 */             if (this.projectNode != null) {
/* 531 */               vars.put("project", ((NodeModel)this.projectNode).getNode());
/*     */             }
/* 533 */             this.prepareEnvironment.execute(vars);
/*     */           } 
/* 535 */           env.process();
/* 536 */           writer.flush();
/*     */         } 
/*     */         
/* 539 */         log("Output: " + outFile, 2);
/*     */       }
/*     */     
/* 542 */     } catch (SAXParseException spe) {
/* 543 */       Throwable rootCause = spe;
/* 544 */       if (spe.getException() != null)
/* 545 */         rootCause = spe.getException(); 
/* 546 */       log("XML parsing error in " + inFile.getAbsolutePath(), 0);
/* 547 */       log("Line number " + spe.getLineNumber());
/* 548 */       log("Column number " + spe.getColumnNumber());
/* 549 */       throw new BuildException(rootCause, getLocation());
/* 550 */     } catch (Throwable e) {
/* 551 */       if (outFile != null && 
/* 552 */         !outFile.delete() && outFile.exists()) {
/* 553 */         log("Failed to delete " + outFile, 1);
/*     */       }
/*     */       
/* 556 */       e.printStackTrace();
/* 557 */       throw new BuildException(e, getLocation());
/*     */     } 
/*     */   }
/*     */   
/*     */   private void generateModels() {
/* 562 */     StringTokenizer modelTokenizer = new StringTokenizer(this.models, ",; ");
/* 563 */     while (modelTokenizer.hasMoreTokens()) {
/* 564 */       String modelSpec = modelTokenizer.nextToken();
/* 565 */       String name = null;
/* 566 */       String clazz = null;
/*     */       
/* 568 */       int sep = modelSpec.indexOf('=');
/* 569 */       if (sep == -1) {
/*     */         
/* 571 */         clazz = modelSpec;
/* 572 */         int dot = clazz.lastIndexOf('.');
/* 573 */         if (dot == -1) {
/*     */           
/* 575 */           name = clazz;
/*     */         } else {
/* 577 */           name = clazz.substring(dot + 1);
/*     */         } 
/*     */       } else {
/* 580 */         name = modelSpec.substring(0, sep);
/* 581 */         clazz = modelSpec.substring(sep + 1);
/*     */       } 
/*     */       try {
/* 584 */         this.modelsMap.put(name, ClassUtil.forName(clazz).newInstance());
/* 585 */       } catch (Exception e) {
/* 586 */         throw new BuildException(e);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void ensureDirectoryFor(File targetFile) throws BuildException {
/* 595 */     File directory = new File(targetFile.getParent());
/* 596 */     if (!directory.exists() && 
/* 597 */       !directory.mkdirs()) {
/* 598 */       throw new BuildException("Unable to create directory: " + directory
/* 599 */           .getAbsolutePath(), getLocation());
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static TemplateModel wrapMap(Map table) {
/* 605 */     SimpleHash model = new SimpleHash((ObjectWrapper)_TemplateAPI.SAFE_OBJECT_WRAPPER);
/* 606 */     for (Iterator<Map.Entry> it = table.entrySet().iterator(); it.hasNext(); ) {
/* 607 */       Map.Entry entry = it.next();
/* 608 */       model.put(String.valueOf(entry.getKey()), new SimpleScalar(String.valueOf(entry.getValue())));
/*     */     } 
/* 610 */     return (TemplateModel)model;
/*     */   }
/*     */   
/*     */   protected void insertDefaults(Map<String, TemplateModel> root) {
/* 614 */     root.put("properties", this.propertiesTemplate);
/* 615 */     root.put("userProperties", this.userPropertiesTemplate);
/* 616 */     if (this.projectTemplate != null) {
/* 617 */       root.put("project", this.projectTemplate);
/* 618 */       root.put("project_node", this.projectNode);
/*     */     } 
/* 620 */     if (this.modelsMap.size() > 0)
/* 621 */       for (Iterator<Map.Entry> it = this.modelsMap.entrySet().iterator(); it.hasNext(); ) {
/* 622 */         Map.Entry entry = it.next();
/* 623 */         root.put((String)entry.getKey(), (TemplateModel)entry.getValue());
/*     */       }  
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\ant\FreemarkerXmlTask.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */