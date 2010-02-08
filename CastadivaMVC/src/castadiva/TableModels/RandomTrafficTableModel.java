/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package castadiva.TableModels;

import castadiva.TrafficRecords.RandomTrafficRecord;
import java.util.Vector;


    /**
     * This model control the random traffic data insertion into Castadiva. The user
     * define the partial structure and Castadiva randomize it.
     * @see TrafficRecord
     */
public class RandomTrafficTableModel extends CastadivaTableModel {

        public static final int TCPUDP_INDEX = 3;
        public static final int TRANSFERSIZE_INDEX = 4;
        public static final int SIZE_INDEX = 5;
        public static final int SEC_INDEX = 6;
        //public static final int MAX_INDEX = 7;
        public static final int FLOWS_INDEX = 7;
        public static final int HIDDEN_INDEX = 8;
        public final String[] columnNames = {
            "#", "Start (s)", "Stop (s)", "Traffic",
            "Transfer Size (B)", "Size of packet", "Pkts/Second",
            "Flows", ""
        };

        public RandomTrafficTableModel(Vector dataVector) {
            datosTrafico = dataVector;
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            if (!enable) {
                return false;
            }
            if (column == NUMBER_INDEX) {
                return false;
            }
            if (column == HIDDEN_INDEX) {
                return false;
            }
            if (getValueAt(row, TCPUDP_INDEX) == "UDP" &&
                    (column == TRANSFERSIZE_INDEX)) {
                return false;
            }
            if (getValueAt(row, TCPUDP_INDEX) == "TCP" &&
                    (column == SIZE_INDEX || column == SEC_INDEX)) {
                return false;
            }
            return true;
        }

        @Override
        public Class getColumnClass(int column) {
            switch (column) {
                case SIZE_INDEX:
                case SEC_INDEX:
                case START_INDEX:
                case STOP_INDEX:
                //case MAX_INDEX:
                case TRANSFERSIZE_INDEX:
                case FLOWS_INDEX:
                    return Integer.class;
                case TCPUDP_INDEX:
                    return String.class;
                default:
                    return Object.class;
            }
        }

        public Object getValueAt(int row, int column) {
            RandomTrafficRecord record = (RandomTrafficRecord) datosTrafico.get(row);
            switch (column) {
                case NUMBER_INDEX:
                    return row + 1;
                case TCPUDP_INDEX:
                    return record.getTCPUDP();
                case TRANSFERSIZE_INDEX:
                    try {
                        if (record.getTransferSize() > 0) {
                            return record.getTransferSize();
                        } else {
                            return 1;
                        }
                    } catch (NullPointerException ex) {
                        return 1;
                    }
                case SIZE_INDEX:
                    try {
                        if (record.getSize() > 0) {
                            return record.getSize();
                        } else {
                            return 1;
                        }
                    } catch (NullPointerException ex) {
                        return 1;
                    }
                case SEC_INDEX:
                    try {
                        if (record.getPacketsSeconds() > 0) {
                            return record.getPacketsSeconds();
                        } else {
                            return 1;
                        }
                    } catch (NullPointerException ex) {
                        return 1;
                    }
                case START_INDEX:
                    try {
                        if (record.getStart() > 0) {
                            return record.getStart();
                        } else {
                            return 0;
                        }
                    } catch (NullPointerException ex) {
                        return 0;
                    }
                case STOP_INDEX:
                    try {
                        if (record.getStop() > 0) {
                            return record.getStop();
                        } else {
                            return 0;
                        }
                    } catch (NullPointerException ex) {
                        return 0;
                    }
                /*case MAX_INDEX:
                try{
                if(record.getMaxPackets() > 0)
                return record.getMaxPackets();
                else return 1;
                }catch(NullPointerException ex){
                return 1;
                }*/
                case FLOWS_INDEX:
                    try {
                        if (record.getFlows() > 0) {
                            return record.getFlows();
                        } else {
                            return 1;
                        }
                    } catch (NullPointerException ex) {
                        return 1;
                    }
                default:
                    return new Object();
            }
        }

        @Override
        public void setValueAt(Object value, int row, int column) {
            RandomTrafficRecord record;
            try {
                record = (RandomTrafficRecord) datosTrafico.get(row);
            } catch (ArrayIndexOutOfBoundsException ex) {
                record = null;
            }
            switch (column) {
                case NUMBER_INDEX:
                    break;
                case TCPUDP_INDEX:
                    try {
                        record.setTCPUDP((String) value);
                    } catch (ClassCastException ex) {
                        record.setTCPUDP("");
                    }
                    break;
                case TRANSFERSIZE_INDEX:
                    try {
                        record.setTransferSize((Integer) value);
                    } catch (ClassCastException ex) {
                        record.setTransferSize(10000);
                    }
                    break;
                case SIZE_INDEX:
                    try {
                        record.setSize((Integer) value);
                    } catch (ClassCastException ex) {
                        record.setSize(64);
                    }
                    break;
                case SEC_INDEX:
                    try {
                        record.setPacketsSeconds((Integer) value);
                    } catch (ClassCastException ex) {
                        record.setPacketsSeconds(0);
                    }
                    record.setMaxPackets((record.getStop() - record.getStart()) * record.getPacketsSeconds());
                    break;
                case START_INDEX:
                    try {
                        record.setStart((Integer) value);
                    } catch (ClassCastException ex) {
                        record.setStart(0);
                    }
                    record.setMaxPackets((record.getStop() - record.getStart()) * record.getPacketsSeconds());
                    break;
                case STOP_INDEX:
                    try {
                        record.setStop((Integer) value);
                    } catch (ClassCastException ex) {
                        record.setStop(10);
                    }
                    record.setMaxPackets((record.getStop() - record.getStart()) * record.getPacketsSeconds());
                    break;
                /*case MAX_INDEX:
                try{
                record.setMaxPackets((Integer)value);
                }catch(ClassCastException ex){
                record.setMaxPackets(0);
                }
                break;*/
                case FLOWS_INDEX:
                    try {
                        record.setFlows((Integer) value);
                    } catch (ClassCastException ex) {
                        record.setFlows(1);
                    }
                    break;
                default:
                    System.out.println("invalid index");
            }
            fireTableCellUpdated(row, column);
        }

        public int getColumnCount() {
            return columnNames.length;
        }

        public boolean hasEmptyRow() {
            try {
                if (datosTrafico.size() == 0) {
                    return false;
                }
                RandomTrafficRecord record = (RandomTrafficRecord) datosTrafico.get(
                        datosTrafico.size() - 1);
                try {
                    if (record.getStop() == 0 ||
                            record.getTCPUDP().trim().equals("") ||
                            record.getSize().toString().trim().equals("null") ||
                            record.getPacketsSeconds().toString().trim().equals("null") ||
                            record.getMaxPackets().toString().trim().equals("null") ||
                            record.getTransferSize().toString().trim().equals("null") ||
                            record.getStop().toString().trim().equals("null") ||
                            record.getStart().toString().trim().equals("null")) {
                        return true;
                    } else {
                        return false;
                    }
                } catch (NullPointerException e) {
                    return true;
                }
            } catch (NullPointerException e) {
                return true;
            }
        }

        public void addEmptyRow() {
            datosTrafico.add(new RandomTrafficRecord());
            fireTableRowsInserted(
                    datosTrafico.size() - 1,
                    datosTrafico.size() - 1);

        }

        public void delRow(int row) {
            if (row < datosTrafico.size()) {
                datosTrafico.remove(row);
            }
            if (datosTrafico.size() < 1) {
                addEmptyRow();
            }
        }
    }