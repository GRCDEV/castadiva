/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package castadiva.pluginLoader;

import castadiva.classpath.classpathModifier;
import lib.IPluginCastadiva;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.Vector;
import lib.IMobilityPluginCastadiva;

public class pluginLoader {

    private static final String EXTENSION_JAR = ".jar";
    // private static final String DIRECTORIO_PLUGINS = "plugins";  
    private static final String DIRECTORIO_PLUGINS = "src/castadiva/Plugins/";
    private static final String DIRECTORIO_PLUGINS_MOVILIDAD = "src/castadiva/MobilityPlugins/";

    //    CARGADORES DE PLUGINS //////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    /** 
     * carga los plugins encontrados al classpath 
     * @return true si se cargaron los plugins, 
     *         false en caso de existir algun error 
     */
    public static boolean loadPLugins() {
        boolean cargados = true;
        try {
            //obtiene el listado de archivos .jar dentro del directorio  
            File[] jars = buscarPlugins();

            if (jars.length > 0) {
                classpathModifier cp = new classpathModifier();
                //a cada jar lo incluye al classpath  
                for (File jar : jars) {
                    try {
                        cp.addFile(jar);
                    } catch (MalformedURLException ex) {
                        System.err.println("URL incorrecta: " +
                                ex.getMessage());
                    }
                }
            }
        } catch (Exception ex) {
            cargados = false;
            System.err.println(ex.getMessage());
        }
        return cargados;
    }

    public static boolean loadMobilityPlugins() {
        boolean cargados = true;
        try {
            //obtiene el listado de archivos .jar dentro del directorio  
            File[] jars = buscarPluginsMovilidad();

            if (jars.length > 0) {
                classpathModifier cp = new classpathModifier();

                //a cada jar lo incluye al classpath  
                for (File jar : jars) {
                    try {
                        cp.addFile(jar);
                    } catch (MalformedURLException ex) {
                        System.err.println("URL incorrecta: " +
                                ex.getMessage());
                    }
                }
            }
        } catch (Exception ex) {
            cargados = false;
            System.err.println(ex.getMessage());
        }
        return cargados;
    }

    //    BUSCADORES DE PLUGINS //////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    /** 
     * Busca todos los jars de en el directorio de plugins 
     * @return jars del directorio de plugins 
     */
    private static File[] buscarPlugins() {
        //crea lista vacia de archivos  
        Vector<File> vUrls = new Vector<File>();

        //si existe el directorio "plugins" continua  
        File directorioPlugins = new File(DIRECTORIO_PLUGINS);
        if (directorioPlugins.exists() && directorioPlugins.isDirectory()) {
            //obtiene todos los archivos con la extension .jar  
            File[] jars = directorioPlugins.listFiles(new FilenameFilter() {

                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(EXTENSION_JAR);
                }
            });
            //los agrega a la lista de archivos  
            for (File jar : jars) {
                vUrls.add(jar);
            }
        }
        /*
        File fi = vUrls.get(0);
        System.out.println(fi.getPath());
         */

        //retorna todos los archivos encontrados  
        return vUrls.toArray(new File[0]);
    }

    /** 
     * Busca todos los jars de en el directorio de plugins 
     * @return jars del directorio de plugins 
     */
    private static File[] buscarPluginsMovilidad() {
        //crea lista vacia de archivos  
        Vector<File> vUrls = new Vector<File>();

        //si existe el directorio "plugins" continua  
        File directorioPlugins = new File(DIRECTORIO_PLUGINS_MOVILIDAD);
        if (directorioPlugins.exists() && directorioPlugins.isDirectory()) {
            //obtiene todos los archivos con la extension .jar  
            File[] jars = directorioPlugins.listFiles(new FilenameFilter() {

                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(EXTENSION_JAR);
                }
            });
            //los agrega a la lista de archivos  
            for (File jar : jars) {
                vUrls.add(jar);
            }
        }
        /*
        File fi = vUrls.get(0);
        System.out.println(fi.getPath());
         */

        //retorna todos los archivos encontrados  
        return vUrls.toArray(new File[0]);
    }

     //    OBTENER   PLUGINS //////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    /** 
     * Obtiene todos los plugins IPluginMensaje encontrados en el classpath 
     * @return lista de plugins encontrados e instanciados 
     */
    public static IPluginCastadiva[] getPlugins() {

        //cargamos todas las implementaciones de IPluginMensaje  
        //encontradas en el classpath  
        ServiceLoader<IPluginCastadiva> sl =
                ServiceLoader.load(IPluginCastadiva.class);
        sl.reload();

        //crea una lista vacia de plugins IPluginMensaje  
        Vector<IPluginCastadiva> vAv = new Vector<IPluginCastadiva>();

        //cada plugin encontrado es agregado a la lista  
        for (Iterator<IPluginCastadiva> it = sl.iterator(); it.hasNext();) {
            try {
                IPluginCastadiva pl = it.next();
                vAv.add(pl);
            } catch (Exception ex) {
                System.err.println("Excepcion al obtener plugin: " +
                        ex.getMessage());
            }
        }
        // System.out.println(vAv.get(0).getMensaje());


        //retorna los plugins encontrados y cargados  
        return vAv.toArray(new IPluginCastadiva[0]);
    }

    public static IMobilityPluginCastadiva[] getMobilityPlugins() {

        //cargamos todas las implementaciones de IPluginMensaje  
        //encontradas en el classpath  
        ServiceLoader<IMobilityPluginCastadiva> sl =
                ServiceLoader.load(IMobilityPluginCastadiva.class);
        sl.reload();

        //crea una lista vacia de plugins IPluginMensaje  
        Vector<IMobilityPluginCastadiva> vAv = new Vector<IMobilityPluginCastadiva>();

        //cada plugin encontrado es agregado a la lista  
        for (Iterator<IMobilityPluginCastadiva> it = sl.iterator(); it.hasNext();) {
            try {
                IMobilityPluginCastadiva pl = it.next();
                vAv.add(pl);
            } catch (Exception ex) {
                System.err.println("Excepcion al obtener plugin: " +
                        ex.getMessage());
            }
        }
        // System.out.println(vAv.get(0).getMensaje());


        //retorna los plugins encontrados y cargados  
        return vAv.toArray(new IMobilityPluginCastadiva[0]);
    }
}  
