/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 * 
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.sweet.components;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import chrriis.dj.sweet.NSOption;


/**
 * A web browser window.
 * @author Christopher Deckers
 */
public class JWebBrowserWindow extends Shell {

  private final ResourceBundle RESOURCES = ResourceBundle.getBundle(JWebBrowserWindow.class.getPackage().getName().replace('.', '/') + "/resource/WebBrowser");
  
  private JWebBrowser webBrowser;
  
  /**
   * Create a web browser shell.
   * @param options the options to configure the behavior of the web browser component.
   */
  public JWebBrowserWindow(NSOption... options) {
    setLayout(new FillLayout());
    this.webBrowser = new JWebBrowser(this, options);
    Menu fileMenu = webBrowser.getFileMenu();
    new MenuItem(fileMenu, SWT.SEPARATOR);
    MenuItem fileCloseMenuItem = new MenuItem(fileMenu, SWT.PUSH);
    fileCloseMenuItem.setText(RESOURCES.getString("FileCloseMenu"));
    fileCloseMenuItem.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        dispose();
      }
    });
    webBrowser.addWebBrowserListener(new WebBrowserAdapter() {
      @Override
      public void titleChanged(WebBrowserEvent e) {
        MessageFormat mf = new MessageFormat(RESOURCES.getString("BrowserTitle"));
        setText(mf.format(new Object[] {e.getWebBrowser().getPageTitle()}));
      }
    });
    Display display = getDisplay();
    String value = RESOURCES.getString("BrowserIcon");
    if(value.length() > 0) {
      setImage(new Image(display, JWebBrowserWindow.class.getResourceAsStream(value)));
    }
    Rectangle bounds = display.getPrimaryMonitor().getBounds();
    bounds.width = bounds.width * 80 / 100;
    bounds.height = bounds.height * 80 / 100;
    setSize(bounds.width, bounds.height);
  }
  
  /**
   * Get the web browser that this window presents.
   * @return the web browser.
   */
  public JWebBrowser getWebBrowser() {
    return webBrowser;
  }
  
  /**
   * Show or hide all the bars at once.
   * @param areBarsVisible true to show all bars, false to hide them all.
   */
  public void setBarsVisible(boolean areBarsVisible) {
    webBrowser.setBarsVisible(areBarsVisible);
  }
  
  /**
   * Set whether the status bar is visible.
   * @param isStatusBarVisible true if the status bar should be visible, false otherwise.
   */
  public void setStatusBarVisible(boolean isStatusBarVisible) {
    webBrowser.setStatusBarVisible(isStatusBarVisible);
  }
  
  /**
   * Indicate whether the status bar is visible.
   * @return true if the status bar is visible.
   */
  public boolean isStatusBarVisisble() {
    return webBrowser.isStatusBarVisible();
  }
  
  /**
   * Set whether the menu bar is visible.
   * @param isMenuBarVisible true if the menu bar should be visible, false otherwise.
   */
  public void setMenuBarVisible(boolean isMenuBarVisible) {
    webBrowser.setMenuBarVisible(isMenuBarVisible);
  }
  
  /**
   * Indicate whether the menu bar is visible.
   * @return true if the menu bar is visible.
   */
  public boolean isMenuBarVisisble() {
    return webBrowser.isMenuBarVisible();
  }
  
  /**
   * Set whether the button bar is visible.
   * @param isButtonBarVisible true if the button bar should be visible, false otherwise.
   */
  public void setButtonBarVisible(boolean isButtonBarVisible) {
    webBrowser.setButtonBarVisible(isButtonBarVisible);
  }
  
  /**
   * Indicate whether the button bar is visible.
   * @return true if the button bar is visible.
   */
  public boolean isButtonBarVisisble() {
    return webBrowser.isButtonBarVisible();
  }
  
  /**
   * Set whether the location bar is visible.
   * @param isLocationBarVisible true if the location bar should be visible, false otherwise.
   */
  public void setLocationBarVisible(boolean isLocationBarVisible) {
    webBrowser.setLocationBarVisible(isLocationBarVisible);
  }
  
  /**
   * Indicate whether the location bar is visible.
   * @return true if the location bar is visible.
   */
  public boolean isLocationBarVisisble() {
    return webBrowser.isLocationBarVisible();
  }
  
  @Override
  protected void checkSubclass() {
    // Do nothing
  }
  
}
