package Routage.ihm;

import Routage.Main;
import Routage.ihm.panels.PanelGraphViewer;
import org.graphstream.graph.implementations.SingleGraph;

import javax.swing.*;

public class IHMGUI extends JFrame
{
    private SingleGraph theGraph;
    private Main ctrl;

    public IHMGUI( Main ctrl, SingleGraph graph )
    {
        this.ctrl     = ctrl;
        this.theGraph = graph;

        this.add(new PanelGraphViewer(graph));

        this.pack();
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
}
