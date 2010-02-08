/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package castadiva.observers;
import java.util.Vector;
/**
 *
 * @author nacho
 */
public interface ISubject {
    public void registerObservers(IObserver observer);
    public void notifyObserversMob(String news);
    public void notifyObserversRout(String news);
}
