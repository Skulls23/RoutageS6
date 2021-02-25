package Routage;

import Routage.ihm.IHMGUI;
import org.graphstream.graph.implementations.SingleGraph;

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


        for(int i=0; i<graph.getEdgeCount(); i++)
        {
            graph.getEdge(i).setAttribute("label", graph.getEdge(i).getAttribute("length"));
            //graph.getEdge(i).setAttribute("ui.stylesheet", "url('ihm/style.css')"); //on peut opti via un fichier css, mais j'ai pas encore trouvé
            //graph.getEdge(i).setAttribute("ui.class", ".texteVisible");
            graph.getEdge(i).setAttribute("ui.style", "text-background-mode: plain; text-background-color: white;text-size: 15;");
        }

        for(int i=0; i<graph.getNodeCount(); i++)
        {
            graph.getNode(i).setAttribute("label", graph.getNode(i).getId());
            graph.getNode(i).setAttribute("ui.style", "text-background-mode: plain; text-background-color: white;text-alignment: under;text-size: 15;");
        }
        //Metier.getPlusCourtCheminGraphique(graph, "A", "F");

        this.ihm = new IHMGUI(this, graph);
    }

    public static void main(String[] args)
    {
        //System.setProperty("org.graphstream.ui", "swing");

        SwingUtilities.invokeLater(Main::new);
    }
}
