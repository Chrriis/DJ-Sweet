/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 * 
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.sweet.demo.examples.htmleditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import chrriis.dj.sweet.components.JHTMLEditor;

/**
 * @author Christopher Deckers
 */
public class CustomConfiguration extends Composite {

  public CustomConfiguration(Composite parent) {
    super(parent, SWT.NONE);
    setLayout(new FillLayout());
    String configurationScript =
      "FCKConfig.ToolbarSets[\"Default\"] = [\n" +
      "['Source','DocProps','-','Save','NewPage','Preview','-','Templates']\n" +
      "];\n" +
      "FCKConfig.ToolbarCanCollapse = false;\n";
    JHTMLEditor htmlEditor = new JHTMLEditor(this, JHTMLEditor.setCustomJavascriptConfiguration(configurationScript));
    htmlEditor.setHTMLContent("<p>The toolbar was modified using custom configuration.</p>");
  }
  
  /* Standard main method to try that test as a standalone application. */
  public static void main(String[] args) {
    Display display = new Display();
    Shell shell = new Shell(display);
    shell.setLayout(new FillLayout());
    new CustomConfiguration(shell);
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
