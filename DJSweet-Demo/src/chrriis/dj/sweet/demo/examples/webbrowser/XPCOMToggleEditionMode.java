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
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMNSHTMLDocument;
import org.mozilla.interfaces.nsIDOMWindow;
import org.mozilla.interfaces.nsIWebBrowser;

import chrriis.dj.sweet.components.JWebBrowser;
import chrriis.dj.sweet.components.MozillaXPCOM;

/**
 * @author Christopher Deckers
 */
public class XPCOMToggleEditionMode extends Composite {

  public XPCOMToggleEditionMode(Composite parent) {
    super(parent, SWT.NONE);
    setLayout(new GridLayout());
    Group webBrowserPanel = new Group(this, SWT.NONE);
    webBrowserPanel.setLayout(new FillLayout());
    webBrowserPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    webBrowserPanel.setText("Native Web Browser component");
    final JWebBrowser webBrowser = new JWebBrowser(webBrowserPanel, JWebBrowser.useXULRunnerRuntime());
    webBrowser.navigate("http://www.google.com");
    // Create an additional bar allowing to toggle the edition mode of the web browser.
    Composite buttonPanel = new Composite(this, SWT.NONE);
    buttonPanel.setLayout(new GridLayout());
    buttonPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    final Button designModeCheckBox = new Button(buttonPanel, SWT.CHECK);
    designModeCheckBox.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, false));
    designModeCheckBox.setText("Edition Mode (allows to type text or resize elements directly in the page)");
    designModeCheckBox.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        nsIWebBrowser iWebBrowser = MozillaXPCOM.getWebBrowser(webBrowser);
        if(iWebBrowser == null) {
          MessageBox messageBox = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.OK);
          messageBox.setText("XPCOM interface");
          messageBox.setMessage("The XPCOM nsIWebBrowser interface could not be obtained.\nPlease check your XULRunner configuration.");
          messageBox.open();
          return;
        }
        nsIDOMWindow window = iWebBrowser.getContentDOMWindow();
        nsIDOMDocument document = window.getDocument();
        nsIDOMNSHTMLDocument nsDocument = (nsIDOMNSHTMLDocument)document.queryInterface(nsIDOMNSHTMLDocument.NS_IDOMNSHTMLDOCUMENT_IID);
        nsDocument.setDesignMode(designModeCheckBox.getSelection()? "on": "off");
      }
    });
  }
  
  /* Standard main method to try that test as a standalone application. */
  public static void main(String[] args) {
    Display display = new Display();
    Shell shell = new Shell(display);
    shell.setLayout(new FillLayout());
    new XPCOMToggleEditionMode(shell);
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
