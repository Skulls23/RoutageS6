package Routage;

import Routage.ihm.EnumCSS;
import Routage.ihm.IHMGUI;
import Routage.metier.Metier;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import javax.swing.*;
import java.util.HashMap;
import java.util.TreeMap;

public class Main
{
    private final IHMGUI ihm;
    private final Metier metier;

    private final SingleGraph graph;

    public Main()
    {
        this.graph = new SingleGraph("Graph1");

        graph.addNode("RO1");
        graph.addNode("RO2");
        graph.addNode("RO3");
        graph.addNode("RO4");
        graph.addNode("RO5");
        graph.addNode("RO6");
        graph.addEdge("RO1RO2", "RO1", "RO2").setAttribute("length", 2.0);
        graph.addEdge("RO2RO3", "RO2", "RO3").setAttribute("length", 3.0);
        graph.addEdge("RO3RO6", "RO3", "RO6").setAttribute("length", 40.0);
        graph.addEdge("RO1RO5", "RO1", "RO5").setAttribute("length", 7.0);
        graph.addEdge("RO5RO6", "RO5", "RO6").setAttribute("length", 1.0);
        graph.addEdge("RO5RO3", "RO5", "RO3").setAttribute("length", 3.0);
        graph.addEdge("RO5RO4", "RO5", "RO4").setAttribute("length", 1.0);


        for(int i=0; i<graph.getEdgeCount(); i++)
        {
            graph.getEdge(i).setAttribute("label", graph.getEdge(i).getAttribute("length"));
            graph.getEdge(i).setAttribute("ui.style", EnumCSS.STYLE_EDGE.getS());
        }

        for(int i=0; i<graph.getNodeCount(); i++)
        {
            graph.getNode(i).setAttribute("label", graph.getNode(i).getId());
            graph.getNode(i).setAttribute("ui.style", EnumCSS.STYLE_ROUTEUR.getS());
        }
        //Metier.getPlusCourtCheminGraphique(graph, "A", "F");

        this.metier = new Metier(graph);
        this.ihm    = new IHMGUI(this, graph);
    }

    public static void main(String[] args)
    {
        //System.setProperty("org.graphstream.ui", "swing");

        SwingUtilities.invokeLater(Main::new);
    }

    public TreeMap<String, TreeMap<String, TreeMap<String, Double>>> getTableRoutage()
    {
        return this.metier.getTableRoutage();
    }

    public TreeMap<String, HashMap<String, HashMap<String, HashMap<String, Integer>>>> getVCI(Object[] cheminEnPlus )
    {
        return this.metier.getVCI(cheminEnPlus);
    }

    public int getNodeCountFor( boolean isPC )
    {
        return this.ihm.getNodeCountFor(isPC);
    }

    public int getValMaxNodeFor( boolean isPC )
    {
        return this.ihm.getValMaxNodeFor(isPC);
    }

    public int getNodeCount()
    {
        return this.graph.getNodeCount();
    }

    public Node getNode(int i)
    {
        return this.graph.getNode(i);
    }

    public String getPlusCourtCheminTextuelEtGraphique(String selectedItem, String selectedItem1)
    {
        return this.metier.getPlusCourtCheminTextuelEtGraphique(selectedItem, selectedItem1);
    }

    public void reinitialiserCouleurs()
    {
        this.metier.reinitialiserCouleurs();
    }

    public void resetVCI()
    {
        this.metier.resetVCI();
    }

    public void removeVCI(Node[] nodes)
    {
        this.metier.remove(nodes);
    }
}
