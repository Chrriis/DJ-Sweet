/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.sweet.demo.examples.syntaxhighlighter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import chrriis.dj.sweet.common.Utils;
import chrriis.dj.sweet.components.JSyntaxHighlighter;

/**
 * @author Christopher Deckers
 */
public class SimpleSyntaxHighlighterExample extends Composite {

  private static final String LS = Utils.LINE_SEPARATOR;

  public SimpleSyntaxHighlighterExample(Composite parent) {
    super(parent, SWT.NONE);
    setLayout(new FillLayout());
    JSyntaxHighlighter syntaxHighlighter = new JSyntaxHighlighter(this);
    syntaxHighlighter.setContent(
        "/************************" + LS +
        " * This is some C# code *" + LS +
        " ************************/" + LS +
        "public class Foo" + LS +
        "{" + LS +
        "    /// <summary>A summary of the method.</summary>" + LS +
        "    /// <param name=\"firstParam\">A description of the parameter.</param>" + LS +
        "    /// <remarks>Remarks about the method.</remarks>" + LS +
        "    public static void Bar(int firstParam) {}" + LS +
        "}"
        , JSyntaxHighlighter.ContentLanguage.CSharp);
  }

  /* Standard main method to try that test as a standalone application. */
  public static void main(String[] args) {
    Display display = new Display();
    Shell shell = new Shell(display);
    shell.setLayout(new FillLayout());
    new SimpleSyntaxHighlighterExample(shell);
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
