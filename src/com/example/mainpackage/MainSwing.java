package com.example.mainpackage;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by brendan on 9/2/16.
 */
public class MainSwing {
    private JCheckBox checkBox1;
    private JPanel panel1;


    public MainSwing(){

        checkBox1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Clicked");
            }
        });

    }

    public static void main(String[] args){
        JFrame jFrame = new JFrame("MainSwing");
        jFrame.setSize(500,500);
        jFrame.setContentPane(new MainSwing().panel1);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setVisible(true);
    }


}
