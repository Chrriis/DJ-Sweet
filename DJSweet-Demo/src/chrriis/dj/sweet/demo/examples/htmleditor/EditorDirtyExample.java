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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import chrriis.dj.sweet.components.HTMLEditorAdapter;
import chrriis.dj.sweet.components.HTMLEditorEvent;
import chrriis.dj.sweet.components.HTMLEditorSaveEvent;
import chrriis.dj.sweet.components.JHTMLEditor;

/**
 * @author Christopher Deckers
 */
public class EditorDirtyExample extends Composite {

  protected static final String LS = System.getProperty("line.separator");

  public EditorDirtyExample(Composite parent) {
    super(parent, SWT.NONE);
    setLayout(new GridLayout());
    final JHTMLEditor htmlEditor = new JHTMLEditor(this, JHTMLEditor.HTMLEditorImplementation.TinyMCE);
    htmlEditor.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    htmlEditor.addHTMLEditorListener(new HTMLEditorAdapter() {
      @Override
      public void saveHTML(HTMLEditorSaveEvent e) {
        MessageBox messageBox = new MessageBox(getShell(), SWT.ICON_INFORMATION | SWT.OK);
        messageBox.setMessage("The data of the HTML editor could be saved anywhere...");
        messageBox.open();
      }
    });
    Group dirtyPanel = new Group(this, SWT.NONE);
    dirtyPanel.setLayout(new GridLayout());
    dirtyPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    dirtyPanel.setText("Dirty State");
    Composite dirtyMiddlePanel = new Composite(dirtyPanel, SWT.NONE);
    dirtyMiddlePanel.setLayout(new GridLayout(2, true));
    dirtyMiddlePanel.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, false));
    final Label dirtyLabel = new Label(dirtyMiddlePanel, SWT.NONE);
    dirtyLabel.setText("Dirty: false");
    htmlEditor.addHTMLEditorListener(new HTMLEditorAdapter() {
      @Override
      public void notifyDirtyStateChanged(HTMLEditorEvent e, boolean isDirty) {
        dirtyLabel.setText("Dirty: " + isDirty);
      }
    });
    Button markAsCleanButton = new Button(dirtyMiddlePanel, SWT.PUSH);
    markAsCleanButton.setText("Mark as clean");
    markAsCleanButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        htmlEditor.clearDirtyState();
      }
    });
    Group controlsPanel = new Group(this, SWT.NONE);
    controlsPanel.setLayout(new GridLayout());
    controlsPanel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    controlsPanel.setText("Custom Controls");
    Composite controlsMiddlePanel = new Composite(controlsPanel, SWT.NONE);
    controlsMiddlePanel.setLayout(new GridLayout(2, true));
    controlsMiddlePanel.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, false));
    Button setHTMLButton = new Button(controlsMiddlePanel, SWT.PUSH);
    setHTMLButton.setText("Set HTML");
    Button getHTMLButton = new Button(controlsMiddlePanel, SWT.PUSH);
    getHTMLButton.setText("Get HTML");
    final Text htmlTextArea = new Text(controlsPanel, SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
    htmlTextArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    htmlTextArea.setText(
        "<p style=\"text-align: center\">This is an <b>HTML editor</b>, in a <u><i>Swing</i></u> application.<br />" + LS +
        "<img alt=\"DJ Project Logo\" src=\"http://djproject.sourceforge.net/common/logo.png\" /><br />" + LS +
        "<a href=\"http://djproject.sourceforge.net/ns/\">DJ Project - Native Swing</a></p>"
    );
    getHTMLButton.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        htmlTextArea.setText(htmlEditor.getHTMLContent());
      }
    });
    setHTMLButton.addSelectionListener(new SelectionAdapter() {
      @Override
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
    new EditorDirtyExample(shell);
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
