/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package castadiva.TableModels;

import java.util.Vector;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author alvaro
 */
    /**
     * This abstract class is a schema to generate a table to define the traffic
     * between the nodes for the application.
     */
    public abstract class CastadivaTableModel extends AbstractTableModel {

        public static final int NUMBER_INDEX = 0;
        public static final int START_INDEX = 1;
        public static final int STOP_INDEX = 2;
        protected Vector datosTrafico;
        boolean enable = true;

        public int getRowCount() {
            return datosTrafico.size();
        }

        public void UpdateData(Vector dataVector) {
            datosTrafico = dataVector;
        }

        public void SetEnable(boolean value) {
            enable = value;
        }
    }
