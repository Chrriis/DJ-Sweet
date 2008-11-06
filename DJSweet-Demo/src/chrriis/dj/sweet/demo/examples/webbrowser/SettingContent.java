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
import org.eclipse.swt.widgets.Text;

import chrriis.dj.sweet.components.JWebBrowser;

/**
 * @author Christopher Deckers
 */
public class SettingContent extends Composite {

  protected static final String LS = System.getProperty("line.separator");

  public SettingContent(Composite parent) {
    super(parent, SWT.NONE);
    setLayout(new GridLayout());
    Group configurationPanel = new Group(this, SWT.NONE);
    configurationPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    configurationPanel.setLayout(new GridLayout());
    configurationPanel.setText("Configuration");
    final Text configurationTextArea = new Text(configurationPanel, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
    configurationTextArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    configurationTextArea.setText(
        "<html>" + LS +
        "  <body>" + LS +
        "    <h1>Some header</h1>" + LS +
        "    <p>A paragraph with a <a href=\"http://www.google.com\">link</a>.</p>" + LS +
        "  </body>" + LS +
        "</html>");
    Button setContentButton = new Button(configurationPanel, SWT.PUSH);
    setContentButton.setText("Set Content");
    setContentButton.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, false));
    Group webBrowserPanel = new Group(this, SWT.NONE);
    webBrowserPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    webBrowserPanel.setLayout(new FillLayout());
    webBrowserPanel.setText("Native Web Browser component");
    final JWebBrowser webBrowser = new JWebBrowser(webBrowserPanel);
    webBrowser.setBarsVisible(false);
    webBrowser.setStatusBarVisible(true);
    setContentButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        webBrowser.setHTMLContent(configurationTextArea.getText());
      }
    });
  }
  
  /* Standard main method to try that test as a standalone application. */
  public static void main(String[] args) {
    Display display = new Display();
    Shell shell = new Shell(display);
    shell.setLayout(new FillLayout());
    new SettingContent(shell);
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
