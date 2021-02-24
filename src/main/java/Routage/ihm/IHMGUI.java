package Routage.ihm;

import Routage.Main;
import Routage.ihm.panels.DialogAjoutLien;
import Routage.ihm.panels.PanelGraphViewer;
import org.graphstream.graph.Node;
import org.graphstream.graph.NodeFactory;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.graphicGraph.GraphicNode;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;

import javax.swing.*;
import java.awt.*;

public class IHMGUI extends JFrame
{
    private final SingleGraph theGraph;
    private final Main ctrl;

    private final JButton ajoutPC;
    private final JButton ajoutRouteur;
    private final JButton ajoutLien;

    public IHMGUI( Main ctrl, SingleGraph graph )
    {
        this.ctrl     = ctrl;
        this.theGraph = graph;

        this.add(new PanelGraphViewer(graph), BorderLayout.CENTER);

        SpriteManager manager = new SpriteManager(this.theGraph);

        this.ajoutPC = new JButton("Ajouter PC");
        this.ajoutPC.addActionListener(event ->
        {
            int num = this.getNodeCountFor(true) + 1;

            this.theGraph.addNode("PC" + num).setAttribute("label", "PC" + num);
        });

        this.ajoutRouteur = new JButton("Ajouter Routeur");
        this.ajoutRouteur.addActionListener(event ->
        {
            int num = this.getNodeCountFor(false) + 1;
            this.theGraph.addNode("RO" + num).setAttribute("label", "RO" + num);
        });

        this.ajoutLien = new JButton("Ajout lien");
        this.ajoutLien.addActionListener(event -> new DialogAjoutLien(this.theGraph));

        JPanel panelTMP = new JPanel();
        panelTMP.setLayout(new BoxLayout(panelTMP, BoxLayout.Y_AXIS));

        panelTMP.add(this.ajoutPC);
        panelTMP.add(this.ajoutRouteur);
        panelTMP.add(this.ajoutLien);

        this.add(panelTMP, BorderLayout.EAST);

        this.pack();
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public int getNodeCountFor( boolean isPC )
    {
        int cpt = 0;

        for (int i = 0; i < this.theGraph.getNodeCount(); i++)
            if( this.theGraph.getNode(i).getId().contains(isPC ? "PC" : "RO") )
                cpt++;

        return cpt;
    }
}
