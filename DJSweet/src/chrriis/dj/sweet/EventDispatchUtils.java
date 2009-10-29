/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.sweet;

import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.swt.widgets.Display;

/**
 * A utility class for event dispatching processes.
 * @author Christopher Deckers
 */
public class EventDispatchUtils {

  private EventDispatchUtils() {}

  /**
   * Sleep but dispatch the events currently in the queue if called from the event dispatch thread, until the timeout is reached.
   * @param timeout The maximum time this processing should take.
   */
  public static void sleepWithEventDispatch(int timeout) {
    sleepWithEventDispatch(new Condition() {
      public boolean getValue() {
        return false;
      }
    }, timeout);
  }

  public static interface Condition {
    public boolean getValue();
  }

  /**
   * Sleep but dispatch the events currently in the queue if called from the event dispatch thread, until the condition becomes true or the timeout is reached.
   * @param condition The condition that indicates whether to stop.
   * @param timeout The maximum time this processing should take.
   */
  public static void sleepWithEventDispatch(Condition condition, int timeout) {
    Display display = Display.getCurrent();
    boolean isEventDispatchThread = display != null && display.getThread() == Thread.currentThread();
    long time = System.currentTimeMillis();
    while(true) {
      if(condition.getValue() || System.currentTimeMillis() - time > timeout) {
        return;
      }
      if(isEventDispatchThread) {
        dispatchSWTEvents(display);
        if(condition.getValue() || System.currentTimeMillis() - time > timeout) {
          return;
        }
      }
      try {
        Thread.sleep(50);
      } catch(Exception e) {}
    }
  }

  private static void dispatchSWTEvents(Display display) {
    // Send an empty event to make sure there is at least one event to dispatch.
    final AtomicBoolean isProcessed = new AtomicBoolean();
    display.asyncExec(new Runnable() {
      public void run() {
        isProcessed.set(true);
      }
    });
    while(!isProcessed.get()) {
      display.readAndDispatch();
    }
  }

}
