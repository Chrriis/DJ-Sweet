/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 * 
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.sweet.demo.examples.htmleditor;

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
import org.eclipse.swt.widgets.Text;

import chrriis.dj.sweet.components.HTMLEditorListener;
import chrriis.dj.sweet.components.HTMLEditorSaveEvent;
import chrriis.dj.sweet.components.JHTMLEditor;

/**
 * @author Christopher Deckers
 */
public class SimpleHTMLEditorExample extends Composite {

  protected static final String LS = System.getProperty("line.separator");

  public SimpleHTMLEditorExample(Composite parent) {
    super(parent, SWT.NONE);
    setLayout(new GridLayout());
    final JHTMLEditor htmlEditor = new JHTMLEditor(this);
    htmlEditor.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    htmlEditor.addHTMLEditorListener(new HTMLEditorListener() {
      public void saveHTML(HTMLEditorSaveEvent e) {
        MessageBox messageBox = new MessageBox(getShell(), SWT.ICON_INFORMATION | SWT.OK);
        messageBox.setMessage("The data of the HTML editor could be saved anywhere...");
        messageBox.open();
      }
    });
    Group southPanel = new Group(this, SWT.NONE);
    southPanel.setLayout(new GridLayout());
    southPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    southPanel.setText("Custom Controls");
    Composite middlePanel = new Composite(southPanel, SWT.NONE);
    middlePanel.setLayout(new GridLayout(2, true));
    middlePanel.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, false));
    Button setHTMLButton = new Button(middlePanel, SWT.PUSH);
    setHTMLButton.setText("Set HTML");
    Button getHTMLButton = new Button(middlePanel, SWT.PUSH);
    getHTMLButton.setText("Get HTML");
    final Text htmlTextArea = new Text(southPanel, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
    htmlTextArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    htmlTextArea.setText(
        "<p style=\"text-align: center\">This is an <b>HTML editor</b>, in a <u><i>Swing</i></u> application.<br />" + LS +
        "<img alt=\"DJ Project Logo\" src=\"http://djproject.sourceforge.net/common/logo.png\" /><br />" + LS +
        "<a href=\"http://djproject.sourceforge.net/ns/\">DJ Project - Native Swing</a></p>"
    );
//    htmlTextArea.setCaretPosition(0);
//    scrollPane.setPreferredSize(new Dimension(0, 100));
//    southPanel.add(scrollPane, BorderLayout.CENTER);
    getHTMLButton.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        htmlTextArea.setText(htmlEditor.getHTMLContent());
//        htmlTextArea.setCaretPosition(0);
      }
    });
    setHTMLButton.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent e) {
        htmlEditor.setHTMLContent(htmlTextArea.getText());
      }
    });
    htmlEditor.setHTMLContent(htmlTextArea.getText());
  }
  
  /* Standard main method to try that test as a standalone application. */
  public static void main(String[] args) {
    Display display = new Display();
    Shell shell = new Shell(display);
    shell.setLayout(new FillLayout());
    new SimpleHTMLEditorExample(shell);
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
