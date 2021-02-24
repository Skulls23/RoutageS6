package Routage;

import Routage.ihm.IHMGUI;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.graph.*;

import javax.swing.*;

public class Main
{
    private final IHMGUI ihm;

    private final SingleGraph graph;

    public Main()
    {
        this.graph = new SingleGraph("Graph1");

        graph.addNode("A");
        graph.addNode("B");
        graph.addNode("C");
        graph.addEdge("AB", "A", "B");
        graph.addEdge("BC", "B", "C");
        graph.addEdge("CA", "C", "A");

        this.ihm = new IHMGUI(this, graph);
    }

    public static void main(String[] args)
    {
        System.setProperty("org.graphstream.ui", "swing");

        SwingUtilities.invokeLater(Main::new);
    }
}
