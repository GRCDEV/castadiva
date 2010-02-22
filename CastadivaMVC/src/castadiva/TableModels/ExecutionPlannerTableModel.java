/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package castadiva.TableModels;

import castadiva.TrafficRecords.ExecutionRecord;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author alvaro
 */
public class ExecutionPlannerTableModel extends AbstractTableModel{

    public static final int SOURCE_FOLDER_INDEX = 0;
    public static final int RESULTS_FOLDER_INDEX = 1;
    public static final int RUNS_INDEX = 2;
    public static final int STATUS_INDEX = 3;

    private String[] columnNames = {"Source folder", "Results folder", "Runs", "Status"};

    protected Vector rows = new Vector();

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
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
            case STATUS_INDEX:
                return String.class;
            case RUNS_INDEX:
                return Integer.class;
            default:
                return Object.class;
        }
    }

    @Override
    public Object getValueAt(int row, int column) {
        ExecutionRecord exe = (ExecutionRecord) rows.get(row);
        switch(column) {
            case SOURCE_FOLDER_INDEX:
                return exe.getSourceFolder();
            case RESULTS_FOLDER_INDEX:
                return exe.getResultsFolder();
            case RUNS_INDEX:
                return exe.getRuns();
            case STATUS_INDEX:
                return exe.getStatus();
            default:
                return new Object();
        }
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        ExecutionRecord exe;
        try {
            exe = (ExecutionRecord) rows.get(row);
        }catch(ArrayIndexOutOfBoundsException ex){
            ex.printStackTrace();
            exe = null;
        }
        if(exe != null) {
           switch(column) {
                case SOURCE_FOLDER_INDEX:
                    try {
                        exe.setSourceFolder((String)value);
                        break;
                    }catch(ClassCastException ex) {
                        ex.printStackTrace();
                        exe.setSourceFolder("");
                    }
                case RESULTS_FOLDER_INDEX:
                    try{
                        exe.setResultsFolder((String)value);
                        break;
                    }catch(ClassCastException ex) {
                        ex.printStackTrace();
                        exe.setResultsFolder("");
                    }
                case RUNS_INDEX:
                    try{
                        exe.setRuns((Integer)value);
                        break;
                    }catch(ClassCastException ex) {
                        ex.printStackTrace();
                        exe.setRuns(1);
                    }
               case STATUS_INDEX:
                    try{
                        exe.setStatus((String)value);
                        break;
                    }catch(ClassCastException ex) {
                        ex.printStackTrace();
                        exe.setStatus("");
                    }
                default:
                    System.err.println("setValueAt - ExecutionPlannerTableModel\n" +
                                       "---- Invalid Column Index ----");
            }
        }
    }


    public void delRow(int row) {
        if(row < rows.size()) {
            rows.remove(row);
        }
    }

    public void addRow(ExecutionRecord exe) {
        rows.add(exe);
    }

    public ExecutionRecord getRow(int row) {
        if(row < rows.size()) {
            return (ExecutionRecord) rows.get(row);
        }
        return new ExecutionRecord();
    }
}
