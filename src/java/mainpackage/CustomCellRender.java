package mainpackage;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Extends the DefaultTableCellRenderer giving the ability to customize the cells in the JTable used for displaying the songs
 */
public class CustomCellRender extends DefaultTableCellRenderer {

    public CustomCellRender() {
        setOpaque(true);
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

            if(isSelected){
                setForeground(Color.white);
                setBackground(Color.blue);
            }else {
                if(row % 2 == 0) {
                    setForeground(Color.black);
                    setBackground(Color.white);
                }
                else {
                    setForeground(Color.black);
                    setBackground(Color.lightGray);
                }
            }
        if(value!= null) {
            setText(value.toString());
        }else {
            setText("");
        }

        return this;
    }
}

