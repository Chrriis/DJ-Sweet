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

import chrriis.common.WebServer;
import chrriis.dj.sweet.components.JWebBrowser;

/**
 * @author Christopher Deckers
 */
public class ClasspathPages extends Composite {

  public ClasspathPages(Composite parent) {
    super(parent, SWT.NONE);
    setLayout(new FillLayout());
    JWebBrowser webBrowser = new JWebBrowser(this);
    webBrowser.navigate(WebServer.getDefaultWebServer().getClassPathResourceURL(getClass().getName(), "resource/page1.html"));
    webBrowser.setBarsVisible(false);
  }

  /* Standard main method to try that test as a standalone application. */
  public static void main(String[] args) {
    Display display = new Display();
    Shell shell = new Shell(display);
    shell.setLayout(new FillLayout());
    new ClasspathPages(shell);
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
