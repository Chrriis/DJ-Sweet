/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.sweet.components;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;

/**
 * @author Christopher Deckers
 */
class BorderUtils {

  private BorderUtils() {}

  public static void addLoweredBevelBorderPaintListener(final Control control) {
    control.addPaintListener(new PaintListener() {
      public void paintControl(PaintEvent e) {
        Point size = control.getSize();
        Color color = e.display.getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW);
        e.gc.setForeground(color);
        e.gc.drawLine(0, 0, size.x - 1, 0);
        e.gc.drawLine(0, 1, 0, size.y - 1);
        color = e.display.getSystemColor(SWT.COLOR_WIDGET_DARK_SHADOW);
        e.gc.setForeground(color);
        e.gc.drawLine(1, 1, size.x - 2, 1);
        e.gc.drawLine(1, 2, 1, size.y - 2);
        color = e.display.getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW);
        e.gc.setForeground(color);
        e.gc.drawLine(size.x - 2, 2, size.x - 2, size.y - 2);
        e.gc.drawLine(2, size.y - 2, size.x - 2, size.y - 2);
        color = e.display.getSystemColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW);
        e.gc.setForeground(color);
        e.gc.drawLine(size.x - 1, 1, size.x - 1, size.y - 1);
        e.gc.drawLine(1, size.y - 1, size.x - 1, size.y - 1);
      }
    });
  }

}
