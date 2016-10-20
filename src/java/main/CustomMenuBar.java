package main;

import net.miginfocom.swing.MigLayout;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;


class CustomMenuBar extends JMenuBar {

    private MainSwing mainSwing = new MainSwing();
    private JDialog dialog;


    JMenuBar showMenuBar() {

        JMenuBar menuBar = new JMenuBar();

        JMenu menu = new JMenu("File");
        menuBar.add(menu);
        JMenuItem item = new JMenuItem("Add Song");
        menu.add(item);

        menu = new JMenu("Edit");
        menuBar.add(menu);

        item = new JMenuItem("Undo");
        menu.add(item);

        item = new JMenuItem("Redo");
        menu.add(item);

        menu.addSeparator();

        item = new JMenuItem("Test");
        menu.add(item);

        menu = new JMenu("Playlist");
        menuBar.add(menu);

        item = new JMenuItem("Create New");
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel panel = new JPanel(new MigLayout());
                dialog = new JDialog();
                int width = 300;
                int height = 200;
                Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
                int xPos = (size.width - width) / 2;
                int yPos = (size.height - height) / 2;
                dialog.setBounds(xPos, yPos, width, height);
                JLabel title = new JLabel("Enter new Playlist name");
                JButton submit = new JButton("Submit");
                JButton cancel = new JButton("Cancel");
                JTextField field = new JTextField(10);

                panel.add(title, "wrap");
                panel.add(field, "wrap");
                field.setAlignmentX(CENTER_ALIGNMENT);
                panel.add(submit);
                panel.add(cancel);

                dialog.add(panel);
                dialog.show();

                cancel.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {

                    }
                });
                submit.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (!field.getText().isEmpty()) {
                            System.out.println("Performing Action");
                            mainSwing.createPlaylist(field.getText());
                        }
                    }
                });

            }
        });
        menu.add(item);
        menu.addSeparator();
        JMenu subMenu = new JMenu("Add to Playlist");
        menu.add(subMenu);
        item = new JMenuItem("Test 1");
        subMenu.add(item);
        item = new JMenuItem("Test 2");
        subMenu.add(item);
        return menuBar;


    }
}
