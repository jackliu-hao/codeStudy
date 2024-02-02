package org.h2.tools;

import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Label;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.PopupMenu;
import java.awt.SystemColor;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;
import org.h2.jdbc.JdbcConnection;
import org.h2.util.Utils;

public class GUIConsole extends Console implements ActionListener, MouseListener, WindowListener {
   private long lastOpenNs;
   private boolean trayIconUsed;
   private Font font;
   private Frame statusFrame;
   private TextField urlText;
   private Button startBrowser;
   private Frame createFrame;
   private TextField pathField;
   private TextField userField;
   private TextField passwordField;
   private TextField passwordConfirmationField;
   private Button createButton;
   private TextArea errorArea;
   private Object tray;
   private Object trayIcon;

   void show() {
      if (!GraphicsEnvironment.isHeadless()) {
         this.loadFont();

         try {
            if (!this.createTrayIcon()) {
               this.showStatusWindow();
            }
         } catch (Exception var2) {
            var2.printStackTrace();
         }
      }

   }

   private static Image loadImage(String var0) {
      try {
         byte[] var1 = Utils.getResource(var0);
         return var1 == null ? null : Toolkit.getDefaultToolkit().createImage(var1);
      } catch (IOException var2) {
         var2.printStackTrace();
         return null;
      }
   }

   public void shutdown() {
      super.shutdown();
      if (this.statusFrame != null) {
         this.statusFrame.dispose();
         this.statusFrame = null;
      }

      if (this.trayIconUsed) {
         try {
            Utils.callMethod(this.tray, "remove", this.trayIcon);
         } catch (Exception var6) {
         } finally {
            this.trayIcon = null;
            this.tray = null;
            this.trayIconUsed = false;
         }

         System.gc();
         String var1 = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
         if (var1.contains("mac")) {
            Iterator var2 = Thread.getAllStackTraces().keySet().iterator();

            while(var2.hasNext()) {
               Thread var3 = (Thread)var2.next();
               if (var3.getName().startsWith("AWT-")) {
                  var3.interrupt();
               }
            }
         }

         Thread.currentThread().interrupt();
      }

   }

   private void loadFont() {
      if (this.isWindows) {
         this.font = new Font("Dialog", 0, 11);
      } else {
         this.font = new Font("Dialog", 0, 12);
      }

   }

   private boolean createTrayIcon() {
      try {
         boolean var1 = (Boolean)Utils.callStaticMethod("java.awt.SystemTray.isSupported");
         if (!var1) {
            return false;
         } else {
            PopupMenu var2 = new PopupMenu();
            MenuItem var3 = new MenuItem("H2 Console");
            var3.setActionCommand("console");
            var3.addActionListener(this);
            var3.setFont(this.font);
            var2.add(var3);
            MenuItem var4 = new MenuItem("Create a new database...");
            var4.setActionCommand("showCreate");
            var4.addActionListener(this);
            var4.setFont(this.font);
            var2.add(var4);
            MenuItem var5 = new MenuItem("Status");
            var5.setActionCommand("status");
            var5.addActionListener(this);
            var5.setFont(this.font);
            var2.add(var5);
            MenuItem var6 = new MenuItem("Exit");
            var6.setFont(this.font);
            var6.setActionCommand("exit");
            var6.addActionListener(this);
            var2.add(var6);
            this.tray = Utils.callStaticMethod("java.awt.SystemTray.getSystemTray");
            Dimension var7 = (Dimension)Utils.callMethod(this.tray, "getTrayIconSize");
            String var8;
            if (var7.width >= 24 && var7.height >= 24) {
               var8 = "/org/h2/res/h2-24.png";
            } else if (var7.width >= 22 && var7.height >= 22) {
               var8 = "/org/h2/res/h2-64-t.png";
            } else {
               var8 = "/org/h2/res/h2.png";
            }

            Image var9 = loadImage(var8);
            this.trayIcon = Utils.newInstance("java.awt.TrayIcon", var9, "H2 Database Engine", var2);
            Utils.callMethod(this.trayIcon, "addMouseListener", this);
            Utils.callMethod(this.tray, "add", this.trayIcon);
            this.trayIconUsed = true;
            return true;
         }
      } catch (Exception var10) {
         return false;
      }
   }

   private void showStatusWindow() {
      if (this.statusFrame == null) {
         this.statusFrame = new Frame("H2 Console");
         this.statusFrame.addWindowListener(this);
         Image var1 = loadImage("/org/h2/res/h2.png");
         if (var1 != null) {
            this.statusFrame.setIconImage(var1);
         }

         this.statusFrame.setResizable(false);
         this.statusFrame.setBackground(SystemColor.control);
         GridBagLayout var2 = new GridBagLayout();
         this.statusFrame.setLayout(var2);
         Panel var3 = new Panel(var2);
         GridBagConstraints var4 = new GridBagConstraints();
         var4.gridx = 0;
         var4.weightx = 1.0;
         var4.weighty = 1.0;
         var4.fill = 1;
         var4.insets = new Insets(0, 10, 0, 10);
         var4.gridy = 0;
         GridBagConstraints var5 = new GridBagConstraints();
         var5.gridx = 0;
         var5.gridwidth = 2;
         var5.insets = new Insets(10, 0, 0, 0);
         var5.gridy = 1;
         var5.anchor = 13;
         GridBagConstraints var6 = new GridBagConstraints();
         var6.fill = 2;
         var6.gridy = 0;
         var6.weightx = 1.0;
         var6.insets = new Insets(0, 5, 0, 0);
         var6.gridx = 1;
         GridBagConstraints var7 = new GridBagConstraints();
         var7.gridx = 0;
         var7.gridy = 0;
         Label var8 = new Label("H2 Console URL:", 0);
         var8.setFont(this.font);
         var3.add(var8, var7);
         this.urlText = new TextField();
         this.urlText.setEditable(false);
         this.urlText.setFont(this.font);
         this.urlText.setText(this.web.getURL());
         if (this.isWindows) {
            this.urlText.setFocusable(false);
         }

         var3.add(this.urlText, var6);
         this.startBrowser = new Button("Start Browser");
         this.startBrowser.setFocusable(false);
         this.startBrowser.setActionCommand("console");
         this.startBrowser.addActionListener(this);
         this.startBrowser.setFont(this.font);
         var3.add(this.startBrowser, var5);
         this.statusFrame.add(var3, var4);
         short var9 = 300;
         byte var10 = 120;
         this.statusFrame.setSize(var9, var10);
         Dimension var11 = Toolkit.getDefaultToolkit().getScreenSize();
         this.statusFrame.setLocation((var11.width - var9) / 2, (var11.height - var10) / 2);

         try {
            this.statusFrame.setVisible(true);
         } catch (Throwable var14) {
         }

         try {
            this.statusFrame.setAlwaysOnTop(true);
            this.statusFrame.setAlwaysOnTop(false);
         } catch (Throwable var13) {
         }

      }
   }

   private void startBrowser() {
      if (this.web != null) {
         String var1 = this.web.getURL();
         if (this.urlText != null) {
            this.urlText.setText(var1);
         }

         long var2 = Utils.currentNanoTime();
         if (this.lastOpenNs == 0L || var2 - this.lastOpenNs > 100000000L) {
            this.lastOpenNs = var2;
            this.openBrowser(var1);
         }
      }

   }

   private void showCreateDatabase() {
      if (this.createFrame == null) {
         this.createFrame = new Frame("H2 Console");
         this.createFrame.addWindowListener(this);
         Image var1 = loadImage("/org/h2/res/h2.png");
         if (var1 != null) {
            this.createFrame.setIconImage(var1);
         }

         this.createFrame.setResizable(false);
         this.createFrame.setBackground(SystemColor.control);
         GridBagLayout var2 = new GridBagLayout();
         this.createFrame.setLayout(var2);
         Panel var3 = new Panel(var2);
         GridBagConstraints var4 = new GridBagConstraints();
         var4.fill = 2;
         var4.insets = new Insets(10, 0, 0, 0);
         var4.gridx = 0;
         var4.gridy = 0;
         Label var5 = new Label("Database path:", 0);
         var5.setFont(this.font);
         var3.add(var5, var4);
         var4 = new GridBagConstraints();
         var4.fill = 2;
         var4.gridy = 0;
         var4.weightx = 1.0;
         var4.insets = new Insets(10, 5, 0, 0);
         var4.gridx = 1;
         this.pathField = new TextField();
         this.pathField.setFont(this.font);
         this.pathField.setText("./test");
         var3.add(this.pathField, var4);
         var4 = new GridBagConstraints();
         var4.fill = 2;
         var4.gridx = 0;
         var4.gridy = 1;
         Label var6 = new Label("Username:", 0);
         var6.setFont(this.font);
         var3.add(var6, var4);
         var4 = new GridBagConstraints();
         var4.fill = 2;
         var4.gridy = 1;
         var4.weightx = 1.0;
         var4.insets = new Insets(0, 5, 0, 0);
         var4.gridx = 1;
         this.userField = new TextField();
         this.userField.setFont(this.font);
         this.userField.setText("sa");
         var3.add(this.userField, var4);
         var4 = new GridBagConstraints();
         var4.fill = 2;
         var4.gridx = 0;
         var4.gridy = 2;
         Label var7 = new Label("Password:", 0);
         var7.setFont(this.font);
         var3.add(var7, var4);
         var4 = new GridBagConstraints();
         var4.fill = 2;
         var4.gridy = 2;
         var4.weightx = 1.0;
         var4.insets = new Insets(0, 5, 0, 0);
         var4.gridx = 1;
         this.passwordField = new TextField();
         this.passwordField.setFont(this.font);
         this.passwordField.setEchoChar('*');
         var3.add(this.passwordField, var4);
         var4 = new GridBagConstraints();
         var4.fill = 2;
         var4.gridx = 0;
         var4.gridy = 3;
         Label var8 = new Label("Password confirmation:", 0);
         var8.setFont(this.font);
         var3.add(var8, var4);
         var4 = new GridBagConstraints();
         var4.fill = 2;
         var4.gridy = 3;
         var4.weightx = 1.0;
         var4.insets = new Insets(0, 5, 0, 0);
         var4.gridx = 1;
         this.passwordConfirmationField = new TextField();
         this.passwordConfirmationField.setFont(this.font);
         this.passwordConfirmationField.setEchoChar('*');
         var3.add(this.passwordConfirmationField, var4);
         var4 = new GridBagConstraints();
         var4.gridx = 0;
         var4.gridwidth = 2;
         var4.insets = new Insets(10, 0, 0, 0);
         var4.gridy = 4;
         var4.anchor = 13;
         this.createButton = new Button("Create");
         this.createButton.setFocusable(false);
         this.createButton.setActionCommand("create");
         this.createButton.addActionListener(this);
         this.createButton.setFont(this.font);
         var3.add(this.createButton, var4);
         var4 = new GridBagConstraints();
         var4.fill = 2;
         var4.gridy = 5;
         var4.weightx = 1.0;
         var4.insets = new Insets(10, 0, 0, 0);
         var4.gridx = 0;
         var4.gridwidth = 2;
         this.errorArea = new TextArea();
         this.errorArea.setFont(this.font);
         this.errorArea.setEditable(false);
         var3.add(this.errorArea, var4);
         var4 = new GridBagConstraints();
         var4.gridx = 0;
         var4.weightx = 1.0;
         var4.weighty = 1.0;
         var4.fill = 1;
         var4.insets = new Insets(0, 10, 0, 10);
         var4.gridy = 0;
         this.createFrame.add(var3, var4);
         short var9 = 400;
         short var10 = 400;
         this.createFrame.setSize(var9, var10);
         this.createFrame.pack();
         this.createFrame.setLocationRelativeTo((Component)null);

         try {
            this.createFrame.setVisible(true);
         } catch (Throwable var13) {
         }

         try {
            this.createFrame.setAlwaysOnTop(true);
            this.createFrame.setAlwaysOnTop(false);
         } catch (Throwable var12) {
         }

      }
   }

   private void createDatabase() {
      if (this.web != null && this.createFrame != null) {
         String var1 = this.pathField.getText();
         String var2 = this.userField.getText();
         String var3 = this.passwordField.getText();
         String var4 = this.passwordConfirmationField.getText();
         if (!var3.equals(var4)) {
            this.errorArea.setForeground(Color.RED);
            this.errorArea.setText("Passwords don't match");
         } else if (var3.isEmpty()) {
            this.errorArea.setForeground(Color.RED);
            this.errorArea.setText("Specify a password");
         } else {
            String var5 = "jdbc:h2:" + var1;

            try {
               (new JdbcConnection(var5, (Properties)null, var2, var3, false)).close();
               this.errorArea.setForeground(new Color(0, 153, 0));
               this.errorArea.setText("Database was created successfully.\n\nJDBC URL for H2 Console:\n" + var5);
            } catch (Exception var7) {
               this.errorArea.setForeground(Color.RED);
               this.errorArea.setText(var7.getMessage());
            }

         }
      }
   }

   public void actionPerformed(ActionEvent var1) {
      String var2 = var1.getActionCommand();
      if ("exit".equals(var2)) {
         this.shutdown();
      } else if ("console".equals(var2)) {
         this.startBrowser();
      } else if ("showCreate".equals(var2)) {
         this.showCreateDatabase();
      } else if ("status".equals(var2)) {
         this.showStatusWindow();
      } else if (this.startBrowser == var1.getSource()) {
         this.startBrowser();
      } else if (this.createButton == var1.getSource()) {
         this.createDatabase();
      }

   }

   public void mouseClicked(MouseEvent var1) {
      if (var1.getButton() == 1) {
         this.startBrowser();
      }

   }

   public void mouseEntered(MouseEvent var1) {
   }

   public void mouseExited(MouseEvent var1) {
   }

   public void mousePressed(MouseEvent var1) {
   }

   public void mouseReleased(MouseEvent var1) {
   }

   public void windowClosing(WindowEvent var1) {
      if (this.trayIconUsed) {
         Window var2 = var1.getWindow();
         if (var2 == this.statusFrame) {
            this.statusFrame.dispose();
            this.statusFrame = null;
         } else if (var2 == this.createFrame) {
            this.createFrame.dispose();
            this.createFrame = null;
         }
      } else {
         this.shutdown();
      }

   }

   public void windowActivated(WindowEvent var1) {
   }

   public void windowClosed(WindowEvent var1) {
   }

   public void windowDeactivated(WindowEvent var1) {
   }

   public void windowDeiconified(WindowEvent var1) {
   }

   public void windowIconified(WindowEvent var1) {
   }

   public void windowOpened(WindowEvent var1) {
   }
}
