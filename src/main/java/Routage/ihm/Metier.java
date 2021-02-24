package Routage.ihm;

import org.graphstream.algorithm.Dijkstra;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

/*
https://graphstream-project.org/doc/Algorithms/Shortest-path/Dijkstra/
https://javadoc.io/doc/org.graphstream
 */
public class Metier
{
    public static String getPlusCourtCheminTextuel(Graph graph, String pointDebut, String pointFin)
    {
        Dijkstra dijkstra = Metier.setupDijkstra(graph, pointDebut);
        return dijkstra.getPath(graph.getNode(pointFin)) + " " + dijkstra.getPathLength(graph.getNode(pointFin));
    }

    public static void getPlusCourtCheminGraphique(Graph graph, String pointDebut, String pointFin)
    {
        Dijkstra dijkstra = Metier.setupDijkstra(graph, pointDebut);
        for (Node node : dijkstra.getPathNodes(graph.getNode(pointFin)))
            node.setAttribute("ui.style", "fill-color: green;");
    }

    public static String getPlusCourtCheminTextuelEtGraphique(Graph graph, String pointDebut, String pointFin)
    {
        Metier.getPlusCourtCheminGraphique(graph, pointDebut, pointFin);
        return Metier.getPlusCourtCheminTextuel(graph, pointDebut, pointFin);
    }

    private static Dijkstra setupDijkstra(Graph graph, String pointDebut)
    {
        Dijkstra dijkstra = new Dijkstra(Dijkstra.Element.EDGE, null, "length");
        dijkstra.init(graph);
        dijkstra.setSource(graph.getNode(pointDebut));
        dijkstra.compute();
        return dijkstra;
    }
}
