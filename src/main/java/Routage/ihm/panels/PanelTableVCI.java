package Routage.ihm.panels;

import org.graphstream.graph.implementations.SingleGraph;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.*;

public class PanelTableVCI extends JScrollPane
{
    private final JPanel panelPose;
    private final SingleGraph graph;

    public PanelTableVCI(SingleGraph graph)
    {
        super(new JPanel(new BorderLayout()));

        this.graph = graph;

        this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.setPreferredSize(new Dimension(900,150));//.setBounds(50, 30, 300, 50);

        this.panelPose = new JPanel();

        this.panelPose.setLayout(new BorderLayout());

        this.getViewport().add(this.panelPose, BorderLayout.CENTER);
    }

    public void init(TreeMap<String, HashMap<String, HashMap<String, HashMap<String, Integer>>>> tableVCI)
    {
        this.panelPose.removeAll();

        JPanel panelFirstGrid  = new JPanel(new GridLayout(tableVCI.size()+3, 1));
        JPanel panelGaucheGrid = new JPanel(new GridLayout(tableVCI.size()+3, 1));

        TreeSet<String> routeurs = tableVCI.size() > 0 ? new TreeSet<>(tableVCI.get(tableVCI.keySet().toArray(new String[0])[0]).keySet()) : new TreeSet<>();

        if( routeurs.size() == 0 )
        {
            this.graph.forEach(node ->
            {
                if( node.getId().contains("RO") )
                    routeurs.add(node.getId());
            });
        }

        JPanel panelGridHaut = new JPanel(new GridLayout(1, routeurs.size()));

        panelGaucheGrid.add(this.getLabelWithBorder("chemin", Color.BLUE));

        for (String routeur : routeurs)
            panelGridHaut.add(this.getLabelWithBorder(routeur, Color.BLUE));

        panelFirstGrid.add(panelGridHaut);

        JPanel panelGridInOut = new JPanel(new GridLayout(1, routeurs.size()*2));

        panelGaucheGrid.add(this.getLabelWithBorder(" ", Color.BLUE));

        for (String ignored : routeurs)
        {
            panelGridInOut.add(this.getLabelWithBorder("IN" , Color.BLUE));
            panelGridInOut.add(this.getLabelWithBorder("OUT", Color.BLUE));
        }

        panelFirstGrid.add(panelGridInOut);

        JPanel panelPORTVCI = new JPanel(new GridLayout(1, routeurs.size()*2*2));

        panelGaucheGrid.add(this.getLabelWithBorder(" ", Color.BLUE));

        for (String ignored : routeurs)
        {
            for (int i = 0; i < 2; i++)
            {
                panelPORTVCI.add(this.getLabelWithBorder("PORT", Color.BLUE));
                panelPORTVCI.add(this.getLabelWithBorder("VCI" , Color.BLUE));
            }
        }

        panelFirstGrid.add(panelPORTVCI);

        if( tableVCI.keySet().size() > 0 )
        for (String chemin : tableVCI.keySet())
        {
            JPanel panelGrid = new JPanel(new GridLayout(1, tableVCI.get(chemin).size()*2*2));

            JLabel label = this.getLabelWithBorder(chemin.substring(chemin.indexOf(" ")+1, chemin.lastIndexOf(" ")), Color.BLACK);

            panelGaucheGrid.add(label);

            HashMap<String, HashMap<String, HashMap<String, Integer>>> routeursMap = tableVCI.get(chemin);

            for (String routeur : routeurs)
            {
                HashMap<String, HashMap<String, Integer>> INOUT = routeursMap.get(routeur);

                for (String s : INOUT.keySet())
                {
                    HashMap<String, Integer> PORTVCI = INOUT.get(s);

                    for (String ss : PORTVCI.keySet())
                    {
                        int val = PORTVCI.get(ss);

                        String txt = chemin.contains("-/>") && ss.contains("VCI") ? "-" + val + "-" : String.valueOf(val);

                        panelGrid.add(this.getLabelWithBorder( val == 0 ? "" : txt, Color.BLACK));
                    }
                }
            }

            panelFirstGrid.add(panelGrid);
        }

        this.panelPose.add(panelGaucheGrid, BorderLayout.WEST);
        this.panelPose.add(panelFirstGrid, BorderLayout.CENTER);

        this.paintComponents(this.getGraphics());
    }

    private JLabel getLabelWithBorder(String texte, Color c)
    {
        Border border = BorderFactory.createLineBorder(c);

        JLabel labelTMP = new JLabel(texte);
        labelTMP.setBorder(border);
        labelTMP.setVerticalAlignment(JLabel.CENTER);
        labelTMP.setHorizontalAlignment(JLabel.CENTER);

        return labelTMP;
    }
}
