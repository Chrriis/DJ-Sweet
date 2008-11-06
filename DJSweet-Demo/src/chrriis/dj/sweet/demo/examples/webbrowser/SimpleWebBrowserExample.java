/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 * 
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.sweet.demo.examples.webbrowser;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

import chrriis.dj.sweet.components.JWebBrowser;

/**
 * @author Christopher Deckers
 */
public class SimpleWebBrowserExample extends Composite {

  public SimpleWebBrowserExample(Composite parent) {
    super(parent, SWT.NONE);
    setLayout(new GridLayout());
    Group webBrowserPanel = new Group(this, SWT.NONE);
    webBrowserPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    webBrowserPanel.setLayout(new FillLayout());
    webBrowserPanel.setText("Web Browser component");
    final JWebBrowser webBrowser = new JWebBrowser(webBrowserPanel);
    webBrowser.navigate("http://www.google.com");
    // Create an additional bar allowing to show/hide the various bars of the web browser.
    Composite buttonPanel = new Composite(this, SWT.NONE);
    buttonPanel.setLayout(new GridLayout(3, true));
    buttonPanel.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, false, false));
    final Button buttonBarCheckBox = new Button(buttonPanel, SWT.CHECK);
    buttonBarCheckBox.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, false));
    buttonBarCheckBox.setText("Button Bar");
    buttonBarCheckBox.setSelection(webBrowser.isButtonBarVisible());
    buttonBarCheckBox.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        webBrowser.setButtonBarVisible(buttonBarCheckBox.getSelection());
      }
    });
    final Button locationBarCheckBox = new Button(buttonPanel, SWT.CHECK);
    locationBarCheckBox.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, false));
    locationBarCheckBox.setText("Location Bar");
    locationBarCheckBox.setSelection(webBrowser.isLocationBarVisible());
    locationBarCheckBox.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        webBrowser.setLocationBarVisible(locationBarCheckBox.getSelection());
      }
    });
    final Button statusBarCheckBox = new Button(buttonPanel, SWT.CHECK);
    statusBarCheckBox.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, false));
    statusBarCheckBox.setText("Status Bar");
    statusBarCheckBox.setSelection(webBrowser.isLocationBarVisible());
    statusBarCheckBox.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        webBrowser.setStatusBarVisible(statusBarCheckBox.getSelection());
      }
    });
    webBrowserPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
  }
  
  /* Standard main method to try that test as a standalone application. */
  public static void main(String[] args) {
    Display display = new Display();
    Shell shell = new Shell(display);
    shell.setLayout(new FillLayout());
    new SimpleWebBrowserExample(shell);
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
