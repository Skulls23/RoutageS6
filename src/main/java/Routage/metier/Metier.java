package Routage.metier;

import Routage.Main;
import org.graphstream.algorithm.Dijkstra;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

/*
https://graphstream-project.org/doc/Algorithms/Shortest-path/Dijkstra/
https://javadoc.io/doc/org.graphstream
 */
public class Metier
{
    private final Main ctrl;
    private final SingleGraph graph;

    public Metier(Main ctrl, SingleGraph graph)
    {
        this.ctrl = ctrl;
        this.graph = graph;
    }

    public String getPlusCourtCheminTextuel(String pointDebut, String pointFin)
    {
        Dijkstra dijkstra = Metier.setupDijkstra(graph, pointDebut);
        return dijkstra.getPath(graph.getNode(pointFin)) + " " + dijkstra.getPathLength(graph.getNode(pointFin));
    }

    public void getPlusCourtCheminGraphique(String pointDebut, String pointFin)
    {
        this.reinitialiserCouleurs();

        Dijkstra dijkstra = Metier.setupDijkstra(this.graph, pointDebut);

        for (Node node : dijkstra.getPathNodes(this.graph.getNode(pointFin)))
            node.setAttribute("ui.style", "fill-color: green;");

        for (Edge edge : dijkstra.getPathEdges(this.graph.getNode(pointFin)))
            edge.setAttribute("ui.style", "fill-color: green;");
    }

    public void reinitialiserCouleurs()
    {
        for (int i = 0; i < this.graph.getNodeCount(); i++)
            this.graph.getNode(i).setAttribute("ui.style", "fill-color: black;");

        for (int i = 0; i < this.graph.getEdgeCount(); i++)
            this.graph.getEdge(i).setAttribute("ui.style", "fill-color: black;");
    }

    public String getPlusCourtCheminTextuelEtGraphique(String pointDebut, String pointFin)
    {
        this.getPlusCourtCheminGraphique(pointDebut, pointFin);
        return this.getPlusCourtCheminTextuel(pointDebut, pointFin);
    }

    public HashMap<String, HashMap<String, TreeMap<String, Double>>> getTableRoutage()
    {
        StringBuilder ret = new StringBuilder();
        Dijkstra dijkstra = new Dijkstra(Dijkstra.Element.EDGE, null, "length");

        HashMap<String, HashMap<String, TreeMap<String, Double>>> hashSite = new HashMap<>();

        //on trouve le noeud de depart
        for(int i=0; i<this.ctrl.getNodeCountFor(false); i++)
        {
            Node pointDebut = graph.getNode("RO" + (i+1));

            //if( pointDebut.getId().contains("PC") ) continue; // n'est plus utile (normalement)

            ret.append("\n\t").append(pointDebut.getId()).append("\n");
            dijkstra.init(graph);
            dijkstra.setSource(pointDebut);
            dijkstra.compute();

            Object[] tabVoisin = pointDebut.neighborNodes().toArray();
            double[] tabPoidsVersVoisin = new double[tabVoisin.length];
            //on definit ses voisins
            for (int k = 0; k < tabVoisin.length; k++)
            {
                tabPoidsVersVoisin[k] = (double) (int) pointDebut.getEdge(k).getAttribute("length");
            }

            HashMap<String, TreeMap<String, Double>> listAllDest = new HashMap<>();

            //on trouve le noeud de fin
            for(int j=0; j<this.ctrl.getNodeCountFor(false); j++)
            {
                if(j == i && j+1 == graph.getNodeCount())
                    break;
                else if (j == i)
                    j++;
                Node pointFin = graph.getNode("RO" + (j+1));

                //if( pointFin.getId().contains("PC") ) continue; // n'est plus utile (normalement)

                ret.append("\t").append(pointDebut.getId());

                Metier.setupDijkstra(graph, pointDebut.getId());

                HashMap<String, Double> mapPoidsVoisinVersFinal = new HashMap<>(); //lie un poids de chemin a un nom de noeud
                //on parcours du premier voisin du point de depart vers le point de fin, puis on prend le deuxieme voisin, ...
                for (int k = 0; k < tabVoisin.length; k++)
                {
                    dijkstra.setSource((Node)tabVoisin[k]);
                    dijkstra.compute();

                    mapPoidsVoisinVersFinal.put(((Node)tabVoisin[k]).getId(), dijkstra.getPathLength(pointFin) + tabPoidsVersVoisin[k]);
                }
                TreeMap<String, Double> treeMap = new TreeMap<>(mapPoidsVoisinVersFinal); //tri la hashmap par poids de chemin
                ret.append(pointFin.getId()).append(" : ").append(treeMap).append("\n");
                listAllDest.put(pointFin.getId(), treeMap);
            }

            hashSite.put(pointDebut.getId(), listAllDest);
        }
        //System.out.println(ret.toString());

        return hashSite;
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
