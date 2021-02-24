package Routage;

import Routage.ihm.IHMGUI;
import Routage.ihm.Metier;
import org.graphstream.algorithm.Dijkstra;
import org.graphstream.algorithm.Dijkstra.Element;
import org.graphstream.graph.Graph;
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
        graph.addNode("D");
        graph.addNode("E");
        graph.addNode("F");
        graph.addEdge("AB", "A", "B").setAttribute("length", 2);
        graph.addEdge("BC", "B", "C").setAttribute("length", 3);
        graph.addEdge("CF", "C", "F").setAttribute("length", 40);
        graph.addEdge("AE", "A", "E").setAttribute("length", 7);
        graph.addEdge("EF", "E", "F").setAttribute("length", 1);
        graph.addEdge("EC", "E", "C").setAttribute("length", 3);
        graph.addEdge("ED", "E", "D").setAttribute("length", 1);

        Dijkstra dijkstra = new Dijkstra(Element.EDGE, null, "length");
        dijkstra.init(graph);
        dijkstra.setSource(graph.getNode("A"));
        dijkstra.compute();

        Metier.getPlusCourtCheminGraphique(graph, "A", "F");

        this.ihm = new IHMGUI(this, graph);
    }

    public static void main(String[] args)
    {
        System.setProperty("org.graphstream.ui", "swing");

        SwingUtilities.invokeLater(Main::new);
    }
}
