package main;

import javax.swing.*;


class CustomMenuBar extends JMenuBar {


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
