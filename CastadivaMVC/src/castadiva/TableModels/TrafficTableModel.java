/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package castadiva.TableModels;

import castadiva.TrafficRecords.TrafficRecord;
import java.util.Vector;

/**
 *
 * @author alvaro
 */
    /**
     * This model control the traffic data insertion into Castadiva. Generate a table
     * where an user can fill all the columns to declare a new flow of traffic.
     * Allow the GUI to interact with the traffic.
     * @see TrafficRecord
     */
    public class TrafficTableModel extends CastadivaTableModel {

        public static final int SOURCE_INDEX = 3;
        public static final int ADDRESS_INDEX = 4;
        public static final int TCPUDP_INDEX = 5;
        public static final int TRANSFERSIZE_INDEX = 6;
        public static final int SIZE_INDEX = 7;
        public static final int SEC_INDEX = 8;
        public static final int MAX_INDEX = 9;
        public static final int SPEED_INDEX = 10;
        public static final int RECEIVED_INDEX = 11;

        //QoS Modifications
        public static final int MEAN_DELAY_INDEX = 12;
        public static final int AC_INDEX = 13 ;
        public static final int DACME_INDEX = 14;
        public static final int DELAY_INDEX = 15;
        public static final int REDIRECT_INDEX = 16;

        public static final int HIDDEN_INDEX = 17;


        public final String[] columnNames = {
            "#", "Start (s)", "Stop (s)", "Source", "Address", "Traffic",
            "Transfer Size (B)", "Size of packet", "Pkts/Second",
            "Max Packets", "Throughput (Kb/s)","Pkts Received (%)",
            "Mean Delay (ms)", "Access Category", "DACME",
            "Delay(ms)","Redirect", ""
        };

        public TrafficTableModel(Vector dataVector) {
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
            if (column == RECEIVED_INDEX) {
                return false;
            }
            if (column == SPEED_INDEX) {
                return false;
            }
            if (column == MEAN_DELAY_INDEX) {
                return false;
            }
            //if (column == MAX_INDEX) return false;
            if (getValueAt(row, TCPUDP_INDEX) == "UDP" &&
                    (column == TRANSFERSIZE_INDEX)) {
                return false;
            }
            if (getValueAt(row, TCPUDP_INDEX) == "TCP" &&
                    (column == SIZE_INDEX || column == SEC_INDEX ||
                    column == MAX_INDEX)) {
                return false;
            }
            if (getValueAt(row, TCPUDP_INDEX) =="TCP" &&
                    (column == AC_INDEX || column == DACME_INDEX ||
                    column == DELAY_INDEX || column == REDIRECT_INDEX)) {
                return false;
            }
            if ((Boolean)getValueAt(row, DACME_INDEX) == false &&
                    (column == DELAY_INDEX)) {
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
                case MAX_INDEX:
                case RECEIVED_INDEX:
                case TRANSFERSIZE_INDEX:
                case DELAY_INDEX:
                    return Integer.class;
                case SPEED_INDEX:
                case MEAN_DELAY_INDEX:
                    return Float.class;
                case SOURCE_INDEX:
                case ADDRESS_INDEX:
                case TCPUDP_INDEX:
                case AC_INDEX:
                    return String.class;
                case DACME_INDEX:
                case REDIRECT_INDEX:
                    return Boolean.class;
                default:
                    return Object.class;
            }
        }

        public Object getValueAt(int row, int column) {
            TrafficRecord record = (TrafficRecord) datosTrafico.get(row);
            switch (column) {
                case NUMBER_INDEX:
                    return row + 1;
                case SOURCE_INDEX:
                    return record.getSource();
                case ADDRESS_INDEX:
                    return record.getAddress();
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
                        if (record.getStop() > 1) {
                            return record.getStop();
                        } else {
                            return 1;
                        }
                    } catch (NullPointerException ex) {
                        return 1;
                    }
                case MAX_INDEX:
                    try {
                        if (record.getMaxPackets() > 0) {
                            return record.getMaxPackets();
                        }
                    } catch (NullPointerException ex) {
                        return 1;
                    }
                case SPEED_INDEX:
                    return record.getLastSpeed();
                case RECEIVED_INDEX:
                    return record.getPacketsPerCentReceived();
                case AC_INDEX:
                    return record.getAccessCategory();
                case DACME_INDEX:
                    return record.getDacme();
                case DELAY_INDEX:
                    return record.getDelay();
                case REDIRECT_INDEX:
                    return record.getRedirect();
                case MEAN_DELAY_INDEX:
                    return record.getMeanDelay();
                default:
                    return new Object();
            }
        }

        @Override
        public void setValueAt(Object value, int row, int column) {
            TrafficRecord record;
            try {
                record = (TrafficRecord) datosTrafico.get(row);
            } catch (ArrayIndexOutOfBoundsException ex) {
                record = null;
            }
            switch (column) {
                case NUMBER_INDEX:
                    break;
                case SOURCE_INDEX:
                    try {
                        record.setSource((String) value);
                    } catch (ClassCastException ex) {
                        record.setSource("");
                    }
                    break;
                case ADDRESS_INDEX:
                    try {
                        record.setAddress((String) value);
                    } catch (ClassCastException ex) {
                        record.setAddress("");
                    }
                    break;
                case TCPUDP_INDEX:
                    try {
                        record.setTCPUDP((String) value);
                    } catch (ClassCastException ex) {
                        record.setTCPUDP("");
                    }
                    break;
                case AC_INDEX:
                    try {
                        record.setAccessCategory((String) value);
                    } catch (ClassCastException ex ){
                        record.setAccessCategory("");
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
                    break;
                case START_INDEX:
                    try {
                        record.setStart((Integer) value);
                    } catch (ClassCastException ex) {
                        record.setStart(0);
                    }
                    break;
                case STOP_INDEX:
                    try {
                        record.setStop((Integer) value);
                    } catch (ClassCastException ex) {
                        record.setStop(10);
                    }
                    break;
                case MAX_INDEX:
                    try {
                        if ((Integer) value > 0) {
                            record.setMaxPackets((Integer) value);
                        } else {
                            record.setMaxPackets((int) 0);
                        }
                    } catch (ClassCastException ex) {
                        record.setMaxPackets((int) 0);
                    }
                    break;
                case DACME_INDEX:
                    try {
                        record.setDacme((Boolean) value);
                    } catch (ClassCastException ex) {
                        record.setDacme(false);
                    }

                    break;
                case DELAY_INDEX:
                    try {
                        record.setDelay((Integer) value);
                    } catch (ClassCastException ex) {
                        record.setDelay(0);
                    }
                    break;
                case REDIRECT_INDEX:
                    try{
                    record.setRedirect((Boolean) value);
                    } catch (ClassCastException ex) {
                        record.setRedirect(false);
                    }
                    break;
                //En estos casos no se pueden editar a mano.
                case SPEED_INDEX:
                case RECEIVED_INDEX:
                case MEAN_DELAY_INDEX:
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
                TrafficRecord record = (TrafficRecord) datosTrafico.get(
                        datosTrafico.size() - 1);
                try {
                    if (record.getSource().trim().equals("") ||
                            record.getAddress().trim().equals("") ||
                            record.getTCPUDP().trim().equals("") ||
                            record.getSize().toString().trim().equals("null") ||
                            record.getPacketsSeconds().toString().trim().equals("null") ||
                            record.getPacketsReceived().toString().trim().equals("null") ||
                            record.getLastSpeed().toString().trim().equals("null") ||
                            record.getMaxPackets().toString().trim().equals("null") ||
                            record.getTransferSize().toString().trim().equals("null") ||
                            record.getStop().toString().trim().equals("null") ||
                            record.getStart().toString().trim().equals("null") ||
                 /*QoS*/    record.getAccessCategory().trim().equals("") ||
                            record.getDacme().toString().trim().equals("null") ||
                            record.getDelay().toString().trim().equals("null") ||
                            record.getMeanDelay().toString().trim().equals("null")) {
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
            datosTrafico.add(new TrafficRecord());
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
