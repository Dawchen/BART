package it.unibas.bartgui.egtaskdataobject.notifier;

import javax.swing.event.ChangeListener;
import org.openide.util.ChangeSupport;
import org.openide.util.WeakListeners;

/**
 *
 * @author Grandinetti Giovanni <grandinetti.giovanni13@gmail.com>
 */
public class DataBaseConfigurationNotifier {
    
    private static final ChangeSupport cs = new ChangeSupport(DataBaseConfigurationNotifier.class);

    public static void addChangeListener(ChangeListener listener) {
        cs.addChangeListener(WeakListeners.change(listener, DataBaseConfigurationNotifier.class));
    }

    public static void removeChangeListener(ChangeListener listener) {
        cs.removeChangeListener(WeakListeners.change(listener, DataBaseConfigurationNotifier.class));
    }

    public static void fire() {
        cs.fireChange();
    }
}
