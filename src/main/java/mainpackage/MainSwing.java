package mainpackage;

import javax.swing.*;

/**
 * Created by brendan on 9/2/16.
 */
public class MainSwing {
    private JPanel MainPanel;
    private JPanel TitlePanel;
    private JLabel artist;
    private JLabel title;
    private JLabel album;


    public MainSwing(){

    }

    public static void main(String[] args){
        JFrame jFrame = new JFrame("MainSwing");
        jFrame.setContentPane(new MainSwing().MainPanel);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setSize(400,200);
        jFrame.setVisible(true);
    }
}
