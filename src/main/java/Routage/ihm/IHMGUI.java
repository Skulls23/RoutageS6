package Routage.ihm;

import Routage.Main;
import Routage.ihm.panels.PanelTableRoutage;
import Routage.ihm.panels.PanelGraphViewer;
import Routage.ihm.panels.PanelTableVCI;
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
    private final JButton afficherTableVCI;
    private final JButton calculChemin;
    private final JButton supprimerNode;
    private final JButton supprimerEdge;

    private final PanelTableRoutage panelRoutage;
    private final PanelTableVCI     panelTableVCI;

    public IHMGUI( Main ctrl, SingleGraph graph )
    {
        this.ctrl     = ctrl;
        this.theGraph = graph;

        this.add(new PanelGraphViewer(graph), BorderLayout.CENTER);

        this.panelRoutage  = new PanelTableRoutage();
        this.panelTableVCI = new PanelTableVCI();

        JPanel panelTmp = new JPanel(new BorderLayout());

        panelTmp.add(panelRoutage , BorderLayout.NORTH);
        panelTmp.add(panelTableVCI, BorderLayout.SOUTH);

        this.add(panelTmp, BorderLayout.SOUTH);

        this.ajoutPC = new JButton("Ajouter un PC");
        this.ajoutPC.addActionListener(event ->
        {
            int num = this.getNodeCountFor(true) + 1;

            Node n = this.theGraph.addNode("PC" + num);
            n.setAttribute("label", "PC" + num);
            n.setAttribute("ui.style", EnumCSS.STYLE_PC.getS());
        });

        this.ajoutRouteur = new JButton("Ajouter un routeur");
        this.ajoutRouteur.addActionListener(event ->
        {
            int num = this.getNodeCountFor(false) + 1;

            Node n = this.theGraph.addNode("RO" + num);
            n.setAttribute("label", "RO" + num);
            n.setAttribute("ui.style", EnumCSS.STYLE_ROUTEUR.getS());
        });

        this.ajoutLien = new JButton("Ajout un lien");
        this.ajoutLien.addActionListener(event -> new DialogAjoutLien(this.theGraph));

        this.afficherTableRoutage = new JButton("Afficher la table de routage");
        this.afficherTableRoutage.addActionListener(event -> this.panelRoutage.setHashMapSites(this.ctrl.getTableRoutage()));

        this.calculChemin = new JButton("Calcul chemin");
        this.calculChemin.addActionListener(event -> new DialogCalculChemin(this.ctrl));

        this.supprimerNode = new JButton("Supprimer PC/RO");
        this.supprimerNode.addActionListener(e -> new DialogSupprimer(this.theGraph, true));

        this.supprimerEdge = new JButton("Supprimer lien");
        this.supprimerEdge.addActionListener(e -> new DialogSupprimer(this.theGraph, false));

        this.afficherTableVCI = new JButton("Afficher VCI");

        JPanel panelTMP = new JPanel();
        panelTMP.setLayout(new BoxLayout(panelTMP, BoxLayout.Y_AXIS));

        panelTMP.add(this.ajoutPC);
        panelTMP.add(this.ajoutRouteur);
        panelTMP.add(this.ajoutLien);
        panelTMP.add(this.calculChemin);
        panelTMP.add(this.supprimerNode);
        panelTMP.add(this.supprimerEdge);

        JPanel panelDroite = new JPanel(new BorderLayout());

        JPanel panelSouthDroite = new JPanel();
        panelSouthDroite.setLayout(new BoxLayout(panelSouthDroite, BoxLayout.Y_AXIS));

        panelSouthDroite.add(this.afficherTableRoutage);
        panelSouthDroite.add(this.afficherTableVCI);

        panelDroite.add(panelTMP, BorderLayout.CENTER);
        panelDroite.add(panelSouthDroite, BorderLayout.SOUTH);

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

    public int getValMaxNodeFor( boolean isPC )
    {
        int max = 0;

        for (int i = 0; i < this.theGraph.getNodeCount(); i++)
        {
            if( this.theGraph.getNode(i).getId().contains(isPC ? "PC" : "RO") )
            {
                int val = Integer.parseInt(this.theGraph.getNode(i).getId().substring(2));

                if( max < val ) max = val;
            }
        }

        return max;
    }
}
