package Routage.ihm.panels;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class PanelTableVCI extends JScrollPane
{
    private final JPanel panelPose;

    public PanelTableVCI()
    {
        super(new JPanel(new BorderLayout()));

        this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.setPreferredSize(new Dimension(900,150));//.setBounds(50, 30, 300, 50);

        this.panelPose = new JPanel();

        this.panelPose.setLayout(new BoxLayout(this.panelPose, BoxLayout.Y_AXIS));

        this.getViewport().add(this.panelPose, BorderLayout.CENTER);
    }

    public void init(HashMap<String, HashMap<String, HashMap<String, HashMap<String, Integer>>>> tableVCI)
    {

    }
}
