/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.sweet.demo.examples.webbrowser;

import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import chrriis.dj.sweet.components.JWebBrowser;
import chrriis.dj.sweet.components.WebBrowserAdapter;
import chrriis.dj.sweet.components.WebBrowserEvent;

/**
 * @author Christopher Deckers
 */
public class SendingCommands extends Composite {

  protected static final String LS = System.getProperty("line.separator");

  public SendingCommands(Composite parent) {
    super(parent, SWT.NONE);
    setLayout(new GridLayout());
    Group webBrowserPanel = new Group(this, SWT.NONE);
    webBrowserPanel.setLayout(new FillLayout());
    webBrowserPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    webBrowserPanel.setText("Native Web Browser component");
    final JWebBrowser webBrowser = new JWebBrowser(webBrowserPanel);
    webBrowser.setBarsVisible(false);
    webBrowser.setStatusBarVisible(true);
    Composite commandPanel = new Composite(this, SWT.NONE);
    commandPanel.setLayout(new GridLayout(2, false));
    commandPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    Label receivedCommandLabel = new Label(commandPanel, SWT.NONE);
    receivedCommandLabel.setText("Received command: ");
    final Text receivedCommandTextField = new Text(commandPanel, SWT.BORDER);
    receivedCommandTextField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    webBrowser.addWebBrowserListener(new WebBrowserAdapter() {
      @Override
      public void commandReceived(WebBrowserEvent e, String command, String[] args) {
        String commandText = command;
        if(args.length > 0) {
          commandText += " " + Arrays.toString(args);
        }
        receivedCommandTextField.setText(commandText);
        if("store".equals(command)) {
          String data = args[0];
          MessageBox messageBox = new MessageBox(getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
          messageBox.setText("Data received from the web browser");
          messageBox.setMessage("Do you want to store \"" + data + "\" in a database?\n(Not for real of course!)");
          if(messageBox.open() == SWT.YES) {
            // Data should be used here
          }
        }
      }
    });
    webBrowser.setHTMLContent(
        "<html>" + LS +
        "  <head>" + LS +
        "    <script language=\"JavaScript\" type=\"text/javascript\">" + LS +
        "      <!--" + LS +
        "      function sendCommand(command) {" + LS +
        "        var s = 'command://' + encodeURIComponent(command);" + LS +
        "        for(var i=1; i<arguments.length; s+='&'+encodeURIComponent(arguments[i++]));" + LS +
        "        window.location = s;" + LS +
        "      }" + LS +
        "      //-->" + LS +
        "    </script>" + LS +
        "  </head>" + LS +
        "  <body>" + LS +
        "    <a href=\"command://A%20static%20command\">A static link, with a predefined command</a><br/>" + LS +
        "    <form name=\"form\" onsubmit=\"sendCommand(form.commandField.value); return false\">" + LS +
        "      A dynamic command, sent through Javascript:<br/>" + LS +
        "      <input name=\"commandField\" type=\"text\" value=\"some command\"/>" + LS +
        "      <input type=\"button\" value=\"Send\" onclick=\"sendCommand(form.commandField.value)\"/>" + LS +
        "    </form>" + LS +
        "    <form name=\"form2\" onsubmit=\"sendCommand('store', form2.commandField.value); return false\">" + LS +
        "      A more concrete example: ask the application to store some data in a database, by sending a command with some arguments:<br/>" + LS +
        "      Client: <input name=\"commandField\" type=\"text\" value=\"John Smith\"/>" + LS +
        "      <input type=\"button\" value=\"Send\" onclick=\"sendCommand('store', form2.commandField.value)\"/>" + LS +
        "    </form>" + LS +
        "  </body>" + LS +
        "</html>");
  }

  /* Standard main method to try that test as a standalone application. */
  public static void main(String[] args) {
    Display display = new Display();
    Shell shell = new Shell(display);
    shell.setLayout(new FillLayout());
    new SendingCommands(shell);
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
