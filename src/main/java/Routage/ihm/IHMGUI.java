package Routage.ihm;

import Routage.Main;
import Routage.ihm.panels.PanelTableRoutage;
import Routage.ihm.panels.PanelGraphViewer;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import javax.swing.*;
import java.awt.*;

public class IHMGUI extends JFrame
{
    private final SingleGraph theGraph;
    private final Main ctrl;

    private final JButton ajoutPC;
    private final JButton ajoutRouteur;
    private final JButton ajoutLien;
    private final JButton afficherTableRoutage;
    private final JButton calculChemin;
    private final PanelTableRoutage panelRoutage;

    public IHMGUI( Main ctrl, SingleGraph graph )
    {
        this.ctrl     = ctrl;
        this.theGraph = graph;

        this.add(new PanelGraphViewer(graph), BorderLayout.CENTER);

        this.panelRoutage = new PanelTableRoutage();
        this.add(this.panelRoutage, BorderLayout.SOUTH);

        this.ajoutPC = new JButton("Ajouter un PC");
        this.ajoutPC.addActionListener(event ->
        {
            int num = this.getNodeCountFor(true) + 1;

            Node n = this.theGraph.addNode("PC" + num);
            n.setAttribute("label", "PC" + num);
            n.setAttribute("ui.style", "text-background-mode: plain; text-background-color: white;text-alignment: under;text-size: 15;");
        });

        this.ajoutRouteur = new JButton("Ajouter un routeur");
        this.ajoutRouteur.addActionListener(event ->
        {
            int num = this.getNodeCountFor(false) + 1;

            Node n = this.theGraph.addNode("RO" + num);
            n.setAttribute("label", "RO" + num);
            n.setAttribute("ui.style", "text-background-mode: plain; text-background-color: white;text-alignment: under;text-size: 15;");
        });

        this.ajoutLien = new JButton("Ajout un lien");
        this.ajoutLien.addActionListener(event -> new DialogAjoutLien(this.theGraph));

        this.afficherTableRoutage = new JButton("Afficher la table de routage");
        this.afficherTableRoutage.addActionListener(event -> this.panelRoutage.setHashMapSites(this.ctrl.getTableRoutage()));

        this.calculChemin = new JButton("Calcul chemin");
        this.calculChemin.addActionListener(event -> new DialogCalculChemin(this.ctrl));

        JPanel panelTMP = new JPanel();
        panelTMP.setLayout(new BoxLayout(panelTMP, BoxLayout.Y_AXIS));

        panelTMP.add(this.ajoutPC);
        panelTMP.add(this.ajoutRouteur);
        panelTMP.add(this.ajoutLien);
        panelTMP.add(this.calculChemin);

        JPanel panelDroite = new JPanel(new BorderLayout());

        panelDroite.add(panelTMP, BorderLayout.CENTER);
        panelDroite.add(this.afficherTableRoutage, BorderLayout.SOUTH);

        this.add(panelDroite, BorderLayout.EAST);

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
