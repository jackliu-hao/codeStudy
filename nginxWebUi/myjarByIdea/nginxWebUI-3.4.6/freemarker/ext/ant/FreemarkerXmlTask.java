package freemarker.ext.ant;

import freemarker.core.Environment;
import freemarker.ext.dom.NodeModel;
import freemarker.ext.xml.NodeListModel;
import freemarker.template.Configuration;
import freemarker.template.SimpleHash;
import freemarker.template.SimpleScalar;
import freemarker.template.Template;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateNodeModel;
import freemarker.template._TemplateAPI;
import freemarker.template.utility.ClassUtil;
import freemarker.template.utility.SecurityUtilities;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.w3c.dom.Document;
import org.xml.sax.SAXParseException;

/** @deprecated */
@Deprecated
public class FreemarkerXmlTask extends MatchingTask {
   private JythonAntTask prepareModel;
   private JythonAntTask prepareEnvironment;
   private final DocumentBuilderFactory builderFactory;
   private DocumentBuilder builder;
   private Configuration cfg = new Configuration();
   private File destDir;
   private File baseDir;
   private File templateDir;
   private String templateName;
   private Template parsedTemplate;
   private long templateFileLastModified = 0L;
   private String projectAttribute = null;
   private File projectFile = null;
   private TemplateModel projectTemplate;
   private TemplateNodeModel projectNode;
   private TemplateModel propertiesTemplate;
   private TemplateModel userPropertiesTemplate;
   private long projectFileLastModified = 0L;
   private boolean incremental = true;
   private String extension = ".html";
   private String encoding = SecurityUtilities.getSystemProperty("file.encoding", "utf-8");
   private String templateEncoding;
   private boolean validation;
   private String models;
   private final Map modelsMap;

   public FreemarkerXmlTask() {
      this.templateEncoding = this.encoding;
      this.validation = false;
      this.models = "";
      this.modelsMap = new HashMap();
      this.builderFactory = DocumentBuilderFactory.newInstance();
      this.builderFactory.setNamespaceAware(true);
   }

   public void setBasedir(File dir) {
      this.baseDir = dir;
   }

   public void setDestdir(File dir) {
      this.destDir = dir;
   }

   public void setExtension(String extension) {
      this.extension = extension;
   }

   public void setTemplate(String templateName) {
      this.templateName = templateName;
   }

   public void setTemplateDir(File templateDir) throws BuildException {
      this.templateDir = templateDir;

      try {
         this.cfg.setDirectoryForTemplateLoading(templateDir);
      } catch (Exception var3) {
         throw new BuildException(var3);
      }
   }

   public void setProjectfile(String projectAttribute) {
      this.projectAttribute = projectAttribute;
   }

   public void setIncremental(String incremental) {
      this.incremental = !incremental.equalsIgnoreCase("false") && !incremental.equalsIgnoreCase("no") && !incremental.equalsIgnoreCase("off");
   }

   public void setEncoding(String encoding) {
      this.encoding = encoding;
   }

   public void setTemplateEncoding(String inputEncoding) {
      this.templateEncoding = inputEncoding;
   }

   public void setValidation(boolean validation) {
      this.validation = validation;
   }

   public void setModels(String models) {
      this.models = models;
   }

   public void execute() throws BuildException {
      if (this.baseDir == null) {
         this.baseDir = this.getProject().getBaseDir();
      }

      if (this.destDir == null) {
         String msg = "destdir attribute must be set!";
         throw new BuildException(msg, this.getLocation());
      } else {
         File templateFile = null;
         if (this.templateDir == null) {
            if (this.templateName != null) {
               templateFile = new File(this.templateName);
               if (!templateFile.isAbsolute()) {
                  templateFile = new File(this.getProject().getBaseDir(), this.templateName);
               }

               this.templateDir = templateFile.getParentFile();
               this.templateName = templateFile.getName();
            } else {
               this.templateDir = this.baseDir;
            }

            this.setTemplateDir(this.templateDir);
         } else if (this.templateName != null) {
            if ((new File(this.templateName)).isAbsolute()) {
               throw new BuildException("Do not specify an absolute location for the template as well as a templateDir");
            }

            templateFile = new File(this.templateDir, this.templateName);
         }

         if (templateFile != null) {
            this.templateFileLastModified = templateFile.lastModified();
         }

         try {
            if (this.templateName != null) {
               this.parsedTemplate = this.cfg.getTemplate(this.templateName, this.templateEncoding);
            }
         } catch (IOException var6) {
            throw new BuildException(var6.toString());
         }

         this.log("Transforming into: " + this.destDir.getAbsolutePath(), 2);
         if (this.projectAttribute != null && this.projectAttribute.length() > 0) {
            this.projectFile = new File(this.baseDir, this.projectAttribute);
            if (this.projectFile.isFile()) {
               this.projectFileLastModified = this.projectFile.lastModified();
            } else {
               this.log("Project file is defined, but could not be located: " + this.projectFile.getAbsolutePath(), 2);
               this.projectFile = null;
            }
         }

         this.generateModels();
         DirectoryScanner scanner = this.getDirectoryScanner(this.baseDir);
         this.propertiesTemplate = wrapMap(this.project.getProperties());
         this.userPropertiesTemplate = wrapMap(this.project.getUserProperties());
         this.builderFactory.setValidating(this.validation);

         try {
            this.builder = this.builderFactory.newDocumentBuilder();
         } catch (ParserConfigurationException var5) {
            throw new BuildException("Could not create document builder", var5, this.getLocation());
         }

         String[] list = scanner.getIncludedFiles();

         for(int i = 0; i < list.length; ++i) {
            this.process(this.baseDir, list[i], this.destDir);
         }

      }
   }

   public void addConfiguredJython(JythonAntTask jythonAntTask) {
      this.prepareEnvironment = jythonAntTask;
   }

   public void addConfiguredPrepareModel(JythonAntTask prepareModel) {
      this.prepareModel = prepareModel;
   }

   public void addConfiguredPrepareEnvironment(JythonAntTask prepareEnvironment) {
      this.prepareEnvironment = prepareEnvironment;
   }

   private void process(File baseDir, String xmlFile, File destDir) throws BuildException {
      File outFile = null;
      File inFile = null;

      try {
         inFile = new File(baseDir, xmlFile);
         outFile = new File(destDir, xmlFile.substring(0, xmlFile.lastIndexOf(46)) + this.extension);
         if (!this.incremental || inFile.lastModified() > outFile.lastModified() || this.templateFileLastModified > outFile.lastModified() || this.projectFileLastModified > outFile.lastModified()) {
            this.ensureDirectoryFor(outFile);
            this.log("Input:  " + xmlFile, 2);
            Document docNode;
            if (this.projectTemplate == null && this.projectFile != null) {
               docNode = this.builder.parse(this.projectFile);
               this.projectTemplate = new NodeListModel(this.builder.parse(this.projectFile));
               this.projectNode = NodeModel.wrap(docNode);
            }

            docNode = this.builder.parse(inFile);
            TemplateModel document = new NodeListModel(docNode);
            TemplateNodeModel docNodeModel = NodeModel.wrap(docNode);
            HashMap root = new HashMap();
            root.put("document", document);
            this.insertDefaults(root);
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), this.encoding));
            Throwable var11 = null;

            try {
               if (this.parsedTemplate == null) {
                  throw new BuildException("No template file specified in build script or in XML file");
               }

               if (this.prepareModel != null) {
                  Map vars = new HashMap();
                  vars.put("model", root);
                  vars.put("doc", docNode);
                  if (this.projectNode != null) {
                     vars.put("project", ((NodeModel)this.projectNode).getNode());
                  }

                  this.prepareModel.execute(vars);
               }

               Environment env = this.parsedTemplate.createProcessingEnvironment(root, writer);
               env.setCurrentVisitorNode(docNodeModel);
               if (this.prepareEnvironment != null) {
                  Map vars = new HashMap();
                  vars.put("env", env);
                  vars.put("doc", docNode);
                  if (this.projectNode != null) {
                     vars.put("project", ((NodeModel)this.projectNode).getNode());
                  }

                  this.prepareEnvironment.execute(vars);
               }

               env.process();
               writer.flush();
            } catch (Throwable var23) {
               var11 = var23;
               throw var23;
            } finally {
               if (writer != null) {
                  if (var11 != null) {
                     try {
                        writer.close();
                     } catch (Throwable var22) {
                        var11.addSuppressed(var22);
                     }
                  } else {
                     writer.close();
                  }
               }

            }

            this.log("Output: " + outFile, 2);
         }

      } catch (SAXParseException var25) {
         Throwable rootCause = var25;
         if (var25.getException() != null) {
            rootCause = var25.getException();
         }

         this.log("XML parsing error in " + inFile.getAbsolutePath(), 0);
         this.log("Line number " + var25.getLineNumber());
         this.log("Column number " + var25.getColumnNumber());
         throw new BuildException((Throwable)rootCause, this.getLocation());
      } catch (Throwable var26) {
         if (outFile != null && !outFile.delete() && outFile.exists()) {
            this.log("Failed to delete " + outFile, 1);
         }

         var26.printStackTrace();
         throw new BuildException(var26, this.getLocation());
      }
   }

   private void generateModels() {
      StringTokenizer modelTokenizer = new StringTokenizer(this.models, ",; ");

      while(modelTokenizer.hasMoreTokens()) {
         String modelSpec = modelTokenizer.nextToken();
         String name = null;
         String clazz = null;
         int sep = modelSpec.indexOf(61);
         if (sep == -1) {
            clazz = modelSpec;
            int dot = modelSpec.lastIndexOf(46);
            if (dot == -1) {
               name = modelSpec;
            } else {
               name = modelSpec.substring(dot + 1);
            }
         } else {
            name = modelSpec.substring(0, sep);
            clazz = modelSpec.substring(sep + 1);
         }

         try {
            this.modelsMap.put(name, ClassUtil.forName(clazz).newInstance());
         } catch (Exception var7) {
            throw new BuildException(var7);
         }
      }

   }

   private void ensureDirectoryFor(File targetFile) throws BuildException {
      File directory = new File(targetFile.getParent());
      if (!directory.exists() && !directory.mkdirs()) {
         throw new BuildException("Unable to create directory: " + directory.getAbsolutePath(), this.getLocation());
      }
   }

   private static TemplateModel wrapMap(Map table) {
      SimpleHash model = new SimpleHash(_TemplateAPI.SAFE_OBJECT_WRAPPER);
      Iterator it = table.entrySet().iterator();

      while(it.hasNext()) {
         Map.Entry entry = (Map.Entry)it.next();
         model.put(String.valueOf(entry.getKey()), new SimpleScalar(String.valueOf(entry.getValue())));
      }

      return model;
   }

   protected void insertDefaults(Map root) {
      root.put("properties", this.propertiesTemplate);
      root.put("userProperties", this.userPropertiesTemplate);
      if (this.projectTemplate != null) {
         root.put("project", this.projectTemplate);
         root.put("project_node", this.projectNode);
      }

      if (this.modelsMap.size() > 0) {
         Iterator it = this.modelsMap.entrySet().iterator();

         while(it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            root.put(entry.getKey(), entry.getValue());
         }
      }

   }
}
