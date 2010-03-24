/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package castadiva;

import castadiva.observers.*;
import castadiva.pluginLoader.pluginLoader;
import java.util.Vector;
import lib.IMobilityPluginCastadiva;
import lib.IPluginCastadiva;

/**
 *
 * @author nacho
 */
public class PluginDetector implements ISubject{
    Vector<IObserver> observers = new Vector<IObserver>();
    public IPluginCastadiva[] routing_protocols = new IPluginCastadiva[]{};
    public IMobilityPluginCastadiva[] mob_plugins = new IMobilityPluginCastadiva[]{};

    PluginDetector() {
        loadPlugins();
    }

    public void registerObservers(IObserver observer) {
        observers.add(observer);
    }

    public void notifyObserversMob(String news) {
        for (int i = 0; i < observers.size(); ++i) {
            observers.get(i).updateMob(news);
        }

        loadPlugins();
    }

    public void notifyInitialObservers() {

        for (int i = 0; i < observers.size(); ++i) {
            for (int j = 0; j < mob_plugins.length; ++j) {
                observers.get(i).updateMob(mob_plugins[j].getClass().getSimpleName());
            }
        }
    }

        public void notifyInitialObserversRout() {

        for (int i = 0; i < observers.size(); ++i) {
            for (int j = 0; j < routing_protocols.length; ++j) {
                observers.get(i).updateRout(routing_protocols[j].getClass().getSimpleName());
            }
        }
    }

    private void loadPlugins() {
        boolean loaded = pluginLoader.loadPLugins();
        if (loaded) {
            try {
                routing_protocols = pluginLoader.getPlugins();
                System.out.println(routing_protocols.length+" Routing protocol(s) loaded");
            } catch (Exception ex) {
                System.err.println("Exception: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
        boolean loaded2 = pluginLoader.loadMobilityPlugins();
        if (loaded2) {
            try {
                mob_plugins = pluginLoader.getMobilityPlugins();
            } catch (Exception ex) {
                System.err.println("Excepcion: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    public IPluginCastadiva[] getProtocolPlugins() {
        return routing_protocols;
    }

    public IMobilityPluginCastadiva[] getMobilityPlugins() {
        return mob_plugins;
    }

    public void notifyObserversRout(String news) {
        for (int i = 0; i < observers.size(); ++i) {
            observers.get(i).updateRout(news);
        }
        loadPlugins();
    }
}
