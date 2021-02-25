package Routage.ihm.panels;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.TreeMap;

public class PanelTableRoutage extends JScrollPane
{
    private final JPanel panelPose;

    public PanelTableRoutage()
    {
        super(new JPanel(new BorderLayout()));

        this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.setPreferredSize(new Dimension(900,200));//.setBounds(50, 30, 300, 50);

        this.panelPose = new JPanel();

        this.panelPose.setLayout(new BoxLayout(this.panelPose, BoxLayout.X_AXIS));

        this.getViewport().add(this.panelPose);
    }

    public PanelTableRoutage(HashMap<String, HashMap<String, TreeMap<String, Double>>> hashSite)
    {
        this();

        this.setHashMapSites(hashSite);
    }

    public void setHashMapSites(HashMap<String, HashMap<String, TreeMap<String, Double>>> hashSite)
    {
        this.panelPose.removeAll();

        for (String keyDep : hashSite.keySet())
        {
            String[] allLines = new String[hashSite.get(keyDep).size()];

            HashMap<String, TreeMap<String, Double>> allDest = hashSite.get(keyDep);

            int cpt = 0;
            for (String keyDest : allDest.keySet())
                allLines[cpt++] = keyDep + keyDest + " : " + allDest.get(keyDest);

            StringBuilder tmp = new StringBuilder();

            for (String line : allLines)
                tmp.append(line).append("<br/>");

            tmp.insert(0, "<center>" + keyDep + "</center><br/>");

            JLabel label = new JLabel("<html><body>" + tmp.toString() + "</body></html>");

            label.setBorder(BorderFactory.createLineBorder(Color.BLACK));

            this.panelPose.add(label);

            this.paintComponents(this.getGraphics());
        }
    }
}
