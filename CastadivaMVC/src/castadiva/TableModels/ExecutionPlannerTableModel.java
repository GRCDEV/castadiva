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
public class ExecutionPlannerTableModel extends AbstractTableModel{
    public static final int SOURCE_FOLDER_INDEX = 1;
    public static final int RESULTS_FOLDER_INDEX = 2;
    public static final int RUNS_INDEX = 3;

    private String[] columnNames = {"Source folder", "Results folder", "Runs"};

    protected Vector rows;

    public int getRowCount() {
        return rows.size();
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public Object getValueAt(int row, int column) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    @Override
    public Class getColumnClass(int column){
        switch(column) {
            case SOURCE_FOLDER_INDEX:
            case RESULTS_FOLDER_INDEX:
                return String.class;
            case RUNS_INDEX:
                return Integer.class;
            default:
                return Object.class;
        }
    }

}
