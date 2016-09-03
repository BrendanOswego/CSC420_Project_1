package com.example.mainpackage;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by brendan on 9/2/16.
 */
public class MainSwing {
    private JPanel mainPanel;
    private JLabel title;
    private JLabel artist;
    private JLabel album;


    public MainSwing() {

        title.setText("Title");
        artist.setText("Artist");
        album.setText("Album");
    }

    public static void main(String[] args) {
        JFrame jFrame = new JFrame("MainSwing");
        jFrame.setSize(500, 500);
        jFrame.setContentPane(new MainSwing().mainPanel);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setVisible(true);


    }




}
