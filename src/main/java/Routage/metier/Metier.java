package Routage.metier;

import org.graphstream.algorithm.Dijkstra;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

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

    public static HashMap<String, HashMap<String, TreeMap<String, Double>>> getTableRoutage(Graph graph)
    {
        StringBuilder ret = new StringBuilder();
        Dijkstra dijkstra = new Dijkstra(Dijkstra.Element.EDGE, null, "length");

        HashMap<String, HashMap<String, TreeMap<String, Double>>> hashSite = new HashMap<>();

        //on trouve le noeud de depart
        for(int i=0; i<graph.getNodeCount(); i++)
        {
            Node pointDebut = graph.getNode(i);
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
            for(int j=0; j<graph.getNodeCount(); j++)
            {
                if(j == i && j+1 == graph.getNodeCount())
                    break;
                else if (j == i)
                    j++;
                Node pointFin = graph.getNode(j);

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
