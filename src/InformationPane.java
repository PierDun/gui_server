import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class InformationPane {

    private String msg, state;
    private JFrame parentFrame;

    InformationPane(String msg, JFrame parentFrame, String state){
        this.msg = msg;
        this.parentFrame = parentFrame;
        this.state = state;
        init();
    }

    private void init(){
        JFrame infoFrame = new JFrame();

        infoFrame.setLocationByPlatform(true);

        infoFrame.setMinimumSize(new Dimension(420,200));
        infoFrame.setResizable(false);
        infoFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        JLabel infoText = new JLabel();
        infoText.setText(msg);
        infoText.setFont(new Font("Courier New", Font.ITALIC, 25));

        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                infoFrame.dispose();
                Main.ServerGUI.resetFrame();
            }
        });

        infoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoText.setAlignmentX(Component.CENTER_ALIGNMENT);
        okButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        infoPanel.add(new ImagePanel(state));
        infoPanel.add(infoText);
        infoPanel.add(okButton);

        infoFrame.add(infoPanel);

        infoFrame.pack();
        infoFrame.setLocationRelativeTo(parentFrame);
        infoFrame.setVisible(true);

        infoFrame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowIconified(WindowEvent wEvt) {
                Main.ServerGUI.resetFrame();
                ((JFrame)wEvt.getSource()).dispose();
            }

            @Override
            public void windowDeactivated(WindowEvent wEvt) {
                Main.ServerGUI.resetFrame();
                ((JFrame)wEvt.getSource()).dispose();
            }

        });
    }
}
