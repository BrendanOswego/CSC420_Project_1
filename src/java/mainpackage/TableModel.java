package mainpackage;


import javax.swing.table.DefaultTableModel;

/**
 * Created by brendan<brendan.goldberg@cainkade.com> on 11/15/16.
 */
public class TableModel extends DefaultTableModel {

    private boolean isEditable = false;
    private int row;
    private int col;

    public TableModel(Object[] columnNames, int rowCount) {
        super(columnNames, rowCount);
    }


    @Override
    public boolean isCellEditable(int row, int column) {
        if (isEditable()) {
            return super.isCellEditable(row, column);
        } else {
            return false;
        }

    }

    public boolean isEditable() {
        return isEditable;
    }

    public void setEditable(boolean editable, int row, int col) {
        this.row = row;
        this.col = col;
        isEditable = editable;
    }

    public Class getColumnClass(int column) {
        Class returnValue;
        if ((column >= 0) && (column < getColumnCount())) {
            returnValue = getValueAt(0, column).getClass();
        } else {
            returnValue = Object.class;
        }
        return returnValue;
    }
}



