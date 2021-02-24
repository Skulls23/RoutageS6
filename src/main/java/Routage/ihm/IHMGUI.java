package Routage.ihm;

import Routage.Main;
import Routage.ihm.panels.PanelGraphViewer;
import org.graphstream.graph.implementations.SingleGraph;

import javax.swing.*;
import java.awt.*;

public class IHMGUI extends JFrame
{
    private final SingleGraph theGraph;
    private final Main ctrl;

    private JButton ajoutPC;
    private JButton ajoutRouteur;

    public IHMGUI( Main ctrl, SingleGraph graph )
    {
        this.ctrl     = ctrl;
        this.theGraph = graph;

        this.add(new PanelGraphViewer(graph), BorderLayout.CENTER);

        this.ajoutPC = new JButton("Ajouter PC");
        this.ajoutPC.addActionListener(event ->
        {
            int num = this.theGraph.getNodeCount();
            this.theGraph.addNode("PC" + num);
            this.theGraph.getNode("PC" + num).setAttribute("label", "PC" + num);
        });

        this.ajoutRouteur = new JButton("Ajouter Routeur");
        this.ajoutRouteur.addActionListener(event ->
        {
            int num = this.theGraph.getNodeCount();
            this.theGraph.addNode("RO" + num);
            this.theGraph.getNode("RO" + num).setAttribute("label", "RO" + num);
        });

        JPanel panelTMP = new JPanel();
        panelTMP.add(this.ajoutPC);
        panelTMP.add(this.ajoutRouteur);

        this.add(panelTMP, BorderLayout.EAST);

        this.pack();
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
}
