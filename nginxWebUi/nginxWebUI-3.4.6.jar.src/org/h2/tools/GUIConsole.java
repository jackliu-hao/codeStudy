/*     */ package org.h2.tools;
/*     */ 
/*     */ import java.awt.Button;
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.Frame;
/*     */ import java.awt.GraphicsEnvironment;
/*     */ import java.awt.GridBagConstraints;
/*     */ import java.awt.GridBagLayout;
/*     */ import java.awt.Image;
/*     */ import java.awt.Insets;
/*     */ import java.awt.Label;
/*     */ import java.awt.MenuItem;
/*     */ import java.awt.Panel;
/*     */ import java.awt.PopupMenu;
/*     */ import java.awt.SystemColor;
/*     */ import java.awt.TextArea;
/*     */ import java.awt.TextField;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.Window;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.awt.event.MouseListener;
/*     */ import java.awt.event.WindowEvent;
/*     */ import java.awt.event.WindowListener;
/*     */ import java.io.IOException;
/*     */ import java.util.Locale;
/*     */ import org.h2.jdbc.JdbcConnection;
/*     */ import org.h2.util.Utils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class GUIConsole
/*     */   extends Console
/*     */   implements ActionListener, MouseListener, WindowListener
/*     */ {
/*     */   private long lastOpenNs;
/*     */   private boolean trayIconUsed;
/*     */   private Font font;
/*     */   private Frame statusFrame;
/*     */   private TextField urlText;
/*     */   private Button startBrowser;
/*     */   private Frame createFrame;
/*     */   private TextField pathField;
/*     */   private TextField userField;
/*     */   private TextField passwordField;
/*     */   private TextField passwordConfirmationField;
/*     */   private Button createButton;
/*     */   private TextArea errorArea;
/*     */   private Object tray;
/*     */   private Object trayIcon;
/*     */   
/*     */   void show() {
/*  62 */     if (!GraphicsEnvironment.isHeadless()) {
/*  63 */       loadFont();
/*     */       try {
/*  65 */         if (!createTrayIcon()) {
/*  66 */           showStatusWindow();
/*     */         }
/*  68 */       } catch (Exception exception) {
/*  69 */         exception.printStackTrace();
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private static Image loadImage(String paramString) {
/*     */     try {
/*  76 */       byte[] arrayOfByte = Utils.getResource(paramString);
/*  77 */       if (arrayOfByte == null) {
/*  78 */         return null;
/*     */       }
/*  80 */       return Toolkit.getDefaultToolkit().createImage(arrayOfByte);
/*  81 */     } catch (IOException iOException) {
/*  82 */       iOException.printStackTrace();
/*  83 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void shutdown() {
/*  89 */     super.shutdown();
/*  90 */     if (this.statusFrame != null) {
/*  91 */       this.statusFrame.dispose();
/*  92 */       this.statusFrame = null;
/*     */     } 
/*  94 */     if (this.trayIconUsed) {
/*     */       
/*     */       try {
/*  97 */         Utils.callMethod(this.tray, "remove", new Object[] { this.trayIcon });
/*  98 */       } catch (Exception exception) {
/*     */       
/*     */       } finally {
/* 101 */         this.trayIcon = null;
/* 102 */         this.tray = null;
/* 103 */         this.trayIconUsed = false;
/*     */       } 
/* 105 */       System.gc();
/*     */       
/* 107 */       String str = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
/* 108 */       if (str.contains("mac")) {
/* 109 */         for (Thread thread : Thread.getAllStackTraces().keySet()) {
/* 110 */           if (thread.getName().startsWith("AWT-")) {
/* 111 */             thread.interrupt();
/*     */           }
/*     */         } 
/*     */       }
/* 115 */       Thread.currentThread().interrupt();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void loadFont() {
/* 121 */     if (this.isWindows) {
/* 122 */       this.font = new Font("Dialog", 0, 11);
/*     */     } else {
/* 124 */       this.font = new Font("Dialog", 0, 12);
/*     */     } 
/*     */   }
/*     */   
/*     */   private boolean createTrayIcon() {
/*     */     try {
/*     */       String str;
/* 131 */       boolean bool = ((Boolean)Utils.callStaticMethod("java.awt.SystemTray.isSupported", new Object[0])).booleanValue();
/*     */       
/* 133 */       if (!bool) {
/* 134 */         return false;
/*     */       }
/* 136 */       PopupMenu popupMenu = new PopupMenu();
/* 137 */       MenuItem menuItem1 = new MenuItem("H2 Console");
/* 138 */       menuItem1.setActionCommand("console");
/* 139 */       menuItem1.addActionListener(this);
/* 140 */       menuItem1.setFont(this.font);
/* 141 */       popupMenu.add(menuItem1);
/* 142 */       MenuItem menuItem2 = new MenuItem("Create a new database...");
/* 143 */       menuItem2.setActionCommand("showCreate");
/* 144 */       menuItem2.addActionListener(this);
/* 145 */       menuItem2.setFont(this.font);
/* 146 */       popupMenu.add(menuItem2);
/* 147 */       MenuItem menuItem3 = new MenuItem("Status");
/* 148 */       menuItem3.setActionCommand("status");
/* 149 */       menuItem3.addActionListener(this);
/* 150 */       menuItem3.setFont(this.font);
/* 151 */       popupMenu.add(menuItem3);
/* 152 */       MenuItem menuItem4 = new MenuItem("Exit");
/* 153 */       menuItem4.setFont(this.font);
/* 154 */       menuItem4.setActionCommand("exit");
/* 155 */       menuItem4.addActionListener(this);
/* 156 */       popupMenu.add(menuItem4);
/*     */ 
/*     */       
/* 159 */       this.tray = Utils.callStaticMethod("java.awt.SystemTray.getSystemTray", new Object[0]);
/*     */ 
/*     */       
/* 162 */       Dimension dimension = (Dimension)Utils.callMethod(this.tray, "getTrayIconSize", new Object[0]);
/*     */       
/* 164 */       if (dimension.width >= 24 && dimension.height >= 24) {
/* 165 */         str = "/org/h2/res/h2-24.png";
/* 166 */       } else if (dimension.width >= 22 && dimension.height >= 22) {
/*     */ 
/*     */ 
/*     */         
/* 170 */         str = "/org/h2/res/h2-64-t.png";
/*     */       } else {
/*     */         
/* 173 */         str = "/org/h2/res/h2.png";
/*     */       } 
/* 175 */       Image image = loadImage(str);
/*     */ 
/*     */ 
/*     */       
/* 179 */       this.trayIcon = Utils.newInstance("java.awt.TrayIcon", new Object[] { image, "H2 Database Engine", popupMenu });
/*     */ 
/*     */ 
/*     */       
/* 183 */       Utils.callMethod(this.trayIcon, "addMouseListener", new Object[] { this });
/*     */ 
/*     */       
/* 186 */       Utils.callMethod(this.tray, "add", new Object[] { this.trayIcon });
/*     */       
/* 188 */       this.trayIconUsed = true;
/*     */       
/* 190 */       return true;
/* 191 */     } catch (Exception exception) {
/* 192 */       return false;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void showStatusWindow() {
/* 197 */     if (this.statusFrame != null) {
/*     */       return;
/*     */     }
/* 200 */     this.statusFrame = new Frame("H2 Console");
/* 201 */     this.statusFrame.addWindowListener(this);
/* 202 */     Image image = loadImage("/org/h2/res/h2.png");
/* 203 */     if (image != null) {
/* 204 */       this.statusFrame.setIconImage(image);
/*     */     }
/* 206 */     this.statusFrame.setResizable(false);
/* 207 */     this.statusFrame.setBackground(SystemColor.control);
/*     */     
/* 209 */     GridBagLayout gridBagLayout = new GridBagLayout();
/* 210 */     this.statusFrame.setLayout(gridBagLayout);
/*     */ 
/*     */     
/* 213 */     Panel panel = new Panel(gridBagLayout);
/*     */     
/* 215 */     GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
/* 216 */     gridBagConstraints1.gridx = 0;
/* 217 */     gridBagConstraints1.weightx = 1.0D;
/* 218 */     gridBagConstraints1.weighty = 1.0D;
/* 219 */     gridBagConstraints1.fill = 1;
/* 220 */     gridBagConstraints1.insets = new Insets(0, 10, 0, 10);
/* 221 */     gridBagConstraints1.gridy = 0;
/*     */     
/* 223 */     GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
/* 224 */     gridBagConstraints2.gridx = 0;
/* 225 */     gridBagConstraints2.gridwidth = 2;
/* 226 */     gridBagConstraints2.insets = new Insets(10, 0, 0, 0);
/* 227 */     gridBagConstraints2.gridy = 1;
/* 228 */     gridBagConstraints2.anchor = 13;
/*     */     
/* 230 */     GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
/* 231 */     gridBagConstraints3.fill = 2;
/* 232 */     gridBagConstraints3.gridy = 0;
/* 233 */     gridBagConstraints3.weightx = 1.0D;
/* 234 */     gridBagConstraints3.insets = new Insets(0, 5, 0, 0);
/* 235 */     gridBagConstraints3.gridx = 1;
/*     */     
/* 237 */     GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
/* 238 */     gridBagConstraints4.gridx = 0;
/* 239 */     gridBagConstraints4.gridy = 0;
/*     */     
/* 241 */     Label label = new Label("H2 Console URL:", 0);
/* 242 */     label.setFont(this.font);
/* 243 */     panel.add(label, gridBagConstraints4);
/*     */     
/* 245 */     this.urlText = new TextField();
/* 246 */     this.urlText.setEditable(false);
/* 247 */     this.urlText.setFont(this.font);
/* 248 */     this.urlText.setText(this.web.getURL());
/* 249 */     if (this.isWindows) {
/* 250 */       this.urlText.setFocusable(false);
/*     */     }
/* 252 */     panel.add(this.urlText, gridBagConstraints3);
/*     */     
/* 254 */     this.startBrowser = new Button("Start Browser");
/* 255 */     this.startBrowser.setFocusable(false);
/* 256 */     this.startBrowser.setActionCommand("console");
/* 257 */     this.startBrowser.addActionListener(this);
/* 258 */     this.startBrowser.setFont(this.font);
/* 259 */     panel.add(this.startBrowser, gridBagConstraints2);
/* 260 */     this.statusFrame.add(panel, gridBagConstraints1);
/*     */     
/* 262 */     char c = 'Ĭ'; byte b = 120;
/* 263 */     this.statusFrame.setSize(c, b);
/* 264 */     Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
/* 265 */     this.statusFrame.setLocation((dimension.width - c) / 2, (dimension.height - b) / 2);
/*     */     
/*     */     try {
/* 268 */       this.statusFrame.setVisible(true);
/* 269 */     } catch (Throwable throwable) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 276 */       this.statusFrame.setAlwaysOnTop(true);
/* 277 */       this.statusFrame.setAlwaysOnTop(false);
/* 278 */     } catch (Throwable throwable) {}
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void startBrowser() {
/* 284 */     if (this.web != null) {
/* 285 */       String str = this.web.getURL();
/* 286 */       if (this.urlText != null) {
/* 287 */         this.urlText.setText(str);
/*     */       }
/* 289 */       long l = Utils.currentNanoTime();
/* 290 */       if (this.lastOpenNs == 0L || l - this.lastOpenNs > 100000000L) {
/* 291 */         this.lastOpenNs = l;
/* 292 */         openBrowser(str);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void showCreateDatabase() {
/* 298 */     if (this.createFrame != null) {
/*     */       return;
/*     */     }
/* 301 */     this.createFrame = new Frame("H2 Console");
/* 302 */     this.createFrame.addWindowListener(this);
/* 303 */     Image image = loadImage("/org/h2/res/h2.png");
/* 304 */     if (image != null) {
/* 305 */       this.createFrame.setIconImage(image);
/*     */     }
/* 307 */     this.createFrame.setResizable(false);
/* 308 */     this.createFrame.setBackground(SystemColor.control);
/*     */     
/* 310 */     GridBagLayout gridBagLayout = new GridBagLayout();
/* 311 */     this.createFrame.setLayout(gridBagLayout);
/*     */ 
/*     */     
/* 314 */     Panel panel = new Panel(gridBagLayout);
/*     */ 
/*     */ 
/*     */     
/* 318 */     GridBagConstraints gridBagConstraints = new GridBagConstraints();
/* 319 */     gridBagConstraints.fill = 2;
/* 320 */     gridBagConstraints.insets = new Insets(10, 0, 0, 0);
/* 321 */     gridBagConstraints.gridx = 0;
/* 322 */     gridBagConstraints.gridy = 0;
/* 323 */     Label label1 = new Label("Database path:", 0);
/* 324 */     label1.setFont(this.font);
/* 325 */     panel.add(label1, gridBagConstraints);
/*     */     
/* 327 */     gridBagConstraints = new GridBagConstraints();
/* 328 */     gridBagConstraints.fill = 2;
/* 329 */     gridBagConstraints.gridy = 0;
/* 330 */     gridBagConstraints.weightx = 1.0D;
/* 331 */     gridBagConstraints.insets = new Insets(10, 5, 0, 0);
/* 332 */     gridBagConstraints.gridx = 1;
/* 333 */     this.pathField = new TextField();
/* 334 */     this.pathField.setFont(this.font);
/* 335 */     this.pathField.setText("./test");
/* 336 */     panel.add(this.pathField, gridBagConstraints);
/*     */     
/* 338 */     gridBagConstraints = new GridBagConstraints();
/* 339 */     gridBagConstraints.fill = 2;
/* 340 */     gridBagConstraints.gridx = 0;
/* 341 */     gridBagConstraints.gridy = 1;
/* 342 */     Label label2 = new Label("Username:", 0);
/* 343 */     label2.setFont(this.font);
/* 344 */     panel.add(label2, gridBagConstraints);
/*     */     
/* 346 */     gridBagConstraints = new GridBagConstraints();
/* 347 */     gridBagConstraints.fill = 2;
/* 348 */     gridBagConstraints.gridy = 1;
/* 349 */     gridBagConstraints.weightx = 1.0D;
/* 350 */     gridBagConstraints.insets = new Insets(0, 5, 0, 0);
/* 351 */     gridBagConstraints.gridx = 1;
/* 352 */     this.userField = new TextField();
/* 353 */     this.userField.setFont(this.font);
/* 354 */     this.userField.setText("sa");
/* 355 */     panel.add(this.userField, gridBagConstraints);
/*     */     
/* 357 */     gridBagConstraints = new GridBagConstraints();
/* 358 */     gridBagConstraints.fill = 2;
/* 359 */     gridBagConstraints.gridx = 0;
/* 360 */     gridBagConstraints.gridy = 2;
/* 361 */     Label label3 = new Label("Password:", 0);
/* 362 */     label3.setFont(this.font);
/* 363 */     panel.add(label3, gridBagConstraints);
/*     */     
/* 365 */     gridBagConstraints = new GridBagConstraints();
/* 366 */     gridBagConstraints.fill = 2;
/* 367 */     gridBagConstraints.gridy = 2;
/* 368 */     gridBagConstraints.weightx = 1.0D;
/* 369 */     gridBagConstraints.insets = new Insets(0, 5, 0, 0);
/* 370 */     gridBagConstraints.gridx = 1;
/* 371 */     this.passwordField = new TextField();
/* 372 */     this.passwordField.setFont(this.font);
/* 373 */     this.passwordField.setEchoChar('*');
/* 374 */     panel.add(this.passwordField, gridBagConstraints);
/*     */     
/* 376 */     gridBagConstraints = new GridBagConstraints();
/* 377 */     gridBagConstraints.fill = 2;
/* 378 */     gridBagConstraints.gridx = 0;
/* 379 */     gridBagConstraints.gridy = 3;
/* 380 */     Label label4 = new Label("Password confirmation:", 0);
/* 381 */     label4.setFont(this.font);
/* 382 */     panel.add(label4, gridBagConstraints);
/*     */     
/* 384 */     gridBagConstraints = new GridBagConstraints();
/* 385 */     gridBagConstraints.fill = 2;
/* 386 */     gridBagConstraints.gridy = 3;
/* 387 */     gridBagConstraints.weightx = 1.0D;
/* 388 */     gridBagConstraints.insets = new Insets(0, 5, 0, 0);
/* 389 */     gridBagConstraints.gridx = 1;
/* 390 */     this.passwordConfirmationField = new TextField();
/* 391 */     this.passwordConfirmationField.setFont(this.font);
/* 392 */     this.passwordConfirmationField.setEchoChar('*');
/* 393 */     panel.add(this.passwordConfirmationField, gridBagConstraints);
/*     */     
/* 395 */     gridBagConstraints = new GridBagConstraints();
/* 396 */     gridBagConstraints.gridx = 0;
/* 397 */     gridBagConstraints.gridwidth = 2;
/* 398 */     gridBagConstraints.insets = new Insets(10, 0, 0, 0);
/* 399 */     gridBagConstraints.gridy = 4;
/* 400 */     gridBagConstraints.anchor = 13;
/* 401 */     this.createButton = new Button("Create");
/* 402 */     this.createButton.setFocusable(false);
/* 403 */     this.createButton.setActionCommand("create");
/* 404 */     this.createButton.addActionListener(this);
/* 405 */     this.createButton.setFont(this.font);
/* 406 */     panel.add(this.createButton, gridBagConstraints);
/*     */     
/* 408 */     gridBagConstraints = new GridBagConstraints();
/* 409 */     gridBagConstraints.fill = 2;
/* 410 */     gridBagConstraints.gridy = 5;
/* 411 */     gridBagConstraints.weightx = 1.0D;
/* 412 */     gridBagConstraints.insets = new Insets(10, 0, 0, 0);
/* 413 */     gridBagConstraints.gridx = 0;
/* 414 */     gridBagConstraints.gridwidth = 2;
/* 415 */     this.errorArea = new TextArea();
/* 416 */     this.errorArea.setFont(this.font);
/* 417 */     this.errorArea.setEditable(false);
/* 418 */     panel.add(this.errorArea, gridBagConstraints);
/*     */     
/* 420 */     gridBagConstraints = new GridBagConstraints();
/* 421 */     gridBagConstraints.gridx = 0;
/* 422 */     gridBagConstraints.weightx = 1.0D;
/* 423 */     gridBagConstraints.weighty = 1.0D;
/* 424 */     gridBagConstraints.fill = 1;
/* 425 */     gridBagConstraints.insets = new Insets(0, 10, 0, 10);
/* 426 */     gridBagConstraints.gridy = 0;
/* 427 */     this.createFrame.add(panel, gridBagConstraints);
/*     */     
/* 429 */     char c1 = 'Ɛ', c2 = 'Ɛ';
/* 430 */     this.createFrame.setSize(c1, c2);
/* 431 */     this.createFrame.pack();
/* 432 */     this.createFrame.setLocationRelativeTo((Component)null);
/*     */     try {
/* 434 */       this.createFrame.setVisible(true);
/* 435 */     } catch (Throwable throwable) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 442 */       this.createFrame.setAlwaysOnTop(true);
/* 443 */       this.createFrame.setAlwaysOnTop(false);
/* 444 */     } catch (Throwable throwable) {}
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void createDatabase() {
/* 450 */     if (this.web == null || this.createFrame == null) {
/*     */       return;
/*     */     }
/* 453 */     String str1 = this.pathField.getText(), str2 = this.userField.getText(), str3 = this.passwordField.getText();
/* 454 */     String str4 = this.passwordConfirmationField.getText();
/* 455 */     if (!str3.equals(str4)) {
/* 456 */       this.errorArea.setForeground(Color.RED);
/* 457 */       this.errorArea.setText("Passwords don't match");
/*     */       return;
/*     */     } 
/* 460 */     if (str3.isEmpty()) {
/* 461 */       this.errorArea.setForeground(Color.RED);
/* 462 */       this.errorArea.setText("Specify a password");
/*     */       return;
/*     */     } 
/* 465 */     String str5 = "jdbc:h2:" + str1;
/*     */     try {
/* 467 */       (new JdbcConnection(str5, null, str2, str3, false)).close();
/* 468 */       this.errorArea.setForeground(new Color(0, 153, 0));
/* 469 */       this.errorArea.setText("Database was created successfully.\n\nJDBC URL for H2 Console:\n" + str5);
/*     */     
/*     */     }
/* 472 */     catch (Exception exception) {
/* 473 */       this.errorArea.setForeground(Color.RED);
/* 474 */       this.errorArea.setText(exception.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void actionPerformed(ActionEvent paramActionEvent) {
/* 483 */     String str = paramActionEvent.getActionCommand();
/* 484 */     if ("exit".equals(str)) {
/* 485 */       shutdown();
/* 486 */     } else if ("console".equals(str)) {
/* 487 */       startBrowser();
/* 488 */     } else if ("showCreate".equals(str)) {
/* 489 */       showCreateDatabase();
/* 490 */     } else if ("status".equals(str)) {
/* 491 */       showStatusWindow();
/*     */     
/*     */     }
/* 494 */     else if (this.startBrowser == paramActionEvent.getSource()) {
/* 495 */       startBrowser();
/* 496 */     } else if (this.createButton == paramActionEvent.getSource()) {
/* 497 */       createDatabase();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void mouseClicked(MouseEvent paramMouseEvent) {
/* 507 */     if (paramMouseEvent.getButton() == 1) {
/* 508 */       startBrowser();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void mouseEntered(MouseEvent paramMouseEvent) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void mouseExited(MouseEvent paramMouseEvent) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void mousePressed(MouseEvent paramMouseEvent) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void mouseReleased(MouseEvent paramMouseEvent) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void windowClosing(WindowEvent paramWindowEvent) {
/* 549 */     if (this.trayIconUsed) {
/* 550 */       Window window = paramWindowEvent.getWindow();
/* 551 */       if (window == this.statusFrame) {
/* 552 */         this.statusFrame.dispose();
/* 553 */         this.statusFrame = null;
/* 554 */       } else if (window == this.createFrame) {
/* 555 */         this.createFrame.dispose();
/* 556 */         this.createFrame = null;
/*     */       } 
/*     */     } else {
/* 559 */       shutdown();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void windowActivated(WindowEvent paramWindowEvent) {}
/*     */   
/*     */   public void windowClosed(WindowEvent paramWindowEvent) {}
/*     */   
/*     */   public void windowDeactivated(WindowEvent paramWindowEvent) {}
/*     */   
/*     */   public void windowDeiconified(WindowEvent paramWindowEvent) {}
/*     */   
/*     */   public void windowIconified(WindowEvent paramWindowEvent) {}
/*     */   
/*     */   public void windowOpened(WindowEvent paramWindowEvent) {}
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\tools\GUIConsole.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */