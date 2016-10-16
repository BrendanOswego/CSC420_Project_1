package main;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Created by brendan on 10/16/16.
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

        setText(value.toString());
        return this;
    }
}

