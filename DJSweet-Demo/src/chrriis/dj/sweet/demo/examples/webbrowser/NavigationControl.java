/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 * 
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.sweet.demo.examples.webbrowser;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import chrriis.dj.sweet.components.JWebBrowser;
import chrriis.dj.sweet.components.JWebBrowserWindow;
import chrriis.dj.sweet.components.WebBrowserAdapter;
import chrriis.dj.sweet.components.WebBrowserNavigationEvent;
import chrriis.dj.sweet.components.WebBrowserWindowWillOpenEvent;

/**
 * @author Christopher Deckers
 */
public class NavigationControl extends Composite {

  protected static final String LS = System.getProperty("line.separator");

  public NavigationControl(Composite parent) {
    super(parent, SWT.NONE);
    setLayout(new FillLayout());
    final TabFolder tabbedPane = new TabFolder(this, SWT.NONE);
    final JWebBrowser webBrowser = new JWebBrowser(tabbedPane);
    TabItem tabItem = new TabItem(tabbedPane, SWT.NONE);
    tabItem.setControl(webBrowser);
    tabItem.setText("Controled Browser");
    webBrowser.setBarsVisible(false);
    webBrowser.setStatusBarVisible(true);
    webBrowser.setHTMLContent(
        "<html>" + LS +
        "  <body>" + LS +
        "    <a href=\"http://java.sun.com\">http://java.sun.com</a>: force link to open in a new tab.<br/>" + LS +
        "    <a href=\"http://www.google.com\">http://www.google.com</a>: force link to open in a new window.<br/>" + LS +
        "    <a href=\"http://www.eclipse.org\">http://www.eclipse.org</a>: block link. Context menu \"Open in new Window\" creates a new tab.<br/>" + LS +
        "    <a href=\"http://www.yahoo.com\" target=\"_blank\">http://www.yahoo.com</a>: link normally opens in a new window but creates a new tab.<br/>" + LS +
        "    <a href=\"http://www.microsoft.com\">http://www.microsoft.com</a>: link and \"Open in new Window\" are blocked.<br/>" + LS +
        "  </body>" + LS +
        "</html>");
    webBrowser.addWebBrowserListener(new WebBrowserAdapter() {
      @Override
      public void locationChanging(WebBrowserNavigationEvent e) {
        final String newResourceLocation = e.getNewResourceLocation();
        if(newResourceLocation.startsWith("http://www.google.com/")) {
          e.consume();
//          SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
              JWebBrowserWindow webBrowserWindow = new JWebBrowserWindow();
              webBrowserWindow.getWebBrowser().navigate(newResourceLocation);
              webBrowserWindow.setVisible(true);
//            }
//          });
        } else if(newResourceLocation.startsWith("http://java.sun.com/")) {
          e.consume();
//          SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
              JWebBrowser webBrowser = new JWebBrowser(tabbedPane);
              TabItem tabItem = new TabItem(tabbedPane, SWT.NONE);
              tabItem.setControl(webBrowser);
              tabItem.setText("java.sun.com");
              webBrowser.navigate(newResourceLocation);
//            }
//          });
        } else if(newResourceLocation.startsWith("http://www.eclipse.org/")) {
          e.consume();
        } else if(newResourceLocation.startsWith("http://www.microsoft.com/")) {
          e.consume();
        }
      }
      @Override
      public void windowWillOpen(WebBrowserWindowWillOpenEvent e) {
        // We let the window to be created, but we will check the first location that is set on it.
        e.getNewWebBrowser().addWebBrowserListener(new WebBrowserAdapter() {
          @Override
          public void locationChanging(WebBrowserNavigationEvent e) {
            final JWebBrowser webBrowser = e.getWebBrowser();
            webBrowser.removeWebBrowserListener(this);
            String newResourceLocation = e.getNewResourceLocation();
            boolean isBlocked = false;
            if(newResourceLocation.startsWith("http://www.microsoft.com/")) {
              isBlocked = true;
            } else if(newResourceLocation.startsWith("http://www.eclipse.org/")) {
              isBlocked = true;
              JWebBrowser newWebBrowser = new JWebBrowser(tabbedPane);
              TabItem tabItem = new TabItem(tabbedPane, SWT.NONE);
              tabItem.setControl(newWebBrowser);
              tabItem.setText("www.eclipse.org");
              JWebBrowser.copyAppearance(webBrowser, newWebBrowser);
              newWebBrowser.navigate(newResourceLocation);
            } else if(newResourceLocation.startsWith("http://www.yahoo.com/")) {
              isBlocked = true;
              JWebBrowser newWebBrowser = new JWebBrowser(tabbedPane);
              TabItem tabItem = new TabItem(tabbedPane, SWT.NONE);
              tabItem.setControl(newWebBrowser);
              tabItem.setText("www.yahoo.com");
              JWebBrowser.copyAppearance(webBrowser, newWebBrowser);
              newWebBrowser.navigate(newResourceLocation);
            }
            if(isBlocked) {
              e.consume();
              // The URL Changing event is special: it is synchronous so disposal must be deferred.
              getDisplay().asyncExec(new Runnable() {
                public void run() {
                  webBrowser.getWebBrowserWindow().dispose();
                }
              });
//              SwingUtilities.invokeLater(new Runnable() {
//                public void run() {
//                  webBrowser.getWebBrowserWindow().dispose();
//                }
//              });
            }
          }
        });
      }
    });
  }
  
  /* Standard main method to try that test as a standalone application. */
  public static void main(String[] args) {
    Display display = new Display();
    Shell shell = new Shell(display);
    shell.setLayout(new FillLayout());
    new NavigationControl(shell);
    shell.setSize(800, 600);
    shell.open();
    while(!shell.isDisposed()) {
      if(!display.readAndDispatch()) {
        display.sleep();
      }
    }
    display.dispose();
  }
  
}
