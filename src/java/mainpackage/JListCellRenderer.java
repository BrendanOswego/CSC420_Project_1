package mainpackage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import static javax.swing.plaf.synth.Region.SEPARATOR;

/**
 * Created by brendan<brendan.goldberg@cainkade.com> on 12/6/16.
 */
public class JListCellRenderer extends DefaultListCellRenderer {


    JSeparator separator;

    public JListCellRenderer() {
        setOpaque(true);
        setBorder(new EmptyBorder(1, 1, 1, 1));
        separator = new JSeparator(JSeparator.HORIZONTAL);
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

        Color color = Color.decode("#0000EE");
        String str = (value == null) ? "" : value.toString();
        if (SEPARATOR.equals(str)) {
            return separator;
        }
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        setFont(list.getFont());
        setText(str);
        return this;
    }
}
