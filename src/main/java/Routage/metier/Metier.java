package Routage.metier;

import Routage.Main;
import org.graphstream.algorithm.Dijkstra;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import java.util.*;
import java.util.stream.Collectors;

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
        Dijkstra dijkstra = this.setupDijkstra(pointDebut);
        return dijkstra.getPath(graph.getNode(pointFin)) + " " + dijkstra.getPathLength(graph.getNode(pointFin));
    }

    public void getPlusCourtCheminGraphique(String pointDebut, String pointFin)
    {
        this.reinitialiserCouleurs();

        Dijkstra dijkstra = this.setupDijkstra(pointDebut);

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

        this.graph.nodes().forEachOrdered(pointDebut ->
        {
            if( pointDebut.getId().contains("PC") )
                return;

            ret.append("\n\t").append(pointDebut.getId()).append("\n");
            dijkstra.init(graph);
            dijkstra.setSource(pointDebut);
            dijkstra.compute();

            Object[] tabVoisin = pointDebut.neighborNodes().toArray();
            double[] tabPoidsVersVoisin = new double[tabVoisin.length];
            //on definit ses voisins
            for (int k = 0; k < tabVoisin.length; k++)
            {
                Edge e = pointDebut.getEdge(k);

                tabPoidsVersVoisin[k] = e.getAttribute("length") == null ? Double.NEGATIVE_INFINITY : (double) e.getAttribute("length");
            }

            HashMap<String, TreeMap<String, Double>> listAllDest = new HashMap<>();

            //on trouve le noeud de fin
            this.graph.nodes().forEachOrdered( pointFin ->
            {
                if( pointDebut == pointFin || pointFin.getId().contains("PC") )
                    return;

                ret.append("\t").append(pointDebut.getId());

                this.setupDijkstra(pointDebut.getId());

                HashMap<String, Double> mapPoidsVoisinVersFinal = new HashMap<>(); //lie un poids de chemin a un nom de noeud
                //on parcours du premier voisin du point de depart vers le point de fin, puis on prend le deuxieme voisin, ...
                for (int k = 0; k < tabVoisin.length; k++)
                {
                    if ( ((Node) tabVoisin[k]).getId().contains("PC") )
                        continue;

                    dijkstra.setSource((Node)tabVoisin[k]);
                    dijkstra.compute();

                    mapPoidsVoisinVersFinal.put(((Node)tabVoisin[k]).getId(), dijkstra.getPathLength(pointFin) + tabPoidsVersVoisin[k]);
                }
                TreeMap<String, Double> treeMap = new TreeMap<>((o1, o2) -> 1);// si on met autre chose que treeMap, certaines valeur manquent...
                // si on change le comparateur pour comparer directement les Double, pareil.
                // seule solution trouver, trier "manuellement" et mettre un comparateur inutile.

                Double[] tab = mapPoidsVoisinVersFinal.values().toArray(new Double[0]);
                Arrays.sort(tab);

                ArrayList<String> set = new ArrayList<>(mapPoidsVoisinVersFinal.keySet());
                ArrayList<Double> col = new ArrayList<>(mapPoidsVoisinVersFinal.values());

                for (Double aDouble : tab) treeMap.put(set.get(col.indexOf(aDouble)), aDouble);

                ret.append(pointFin.getId()).append(" : ").append(treeMap).append("\n");
                listAllDest.put(pointFin.getId(), treeMap);
            });

            hashSite.put(pointDebut.getId(), listAllDest);
        });

        //System.out.println(ret.toString());

        return hashSite;
    }

    private Dijkstra setupDijkstra(String pointDebut)
    {
        Dijkstra dijkstra = new Dijkstra(Dijkstra.Element.EDGE, null, "length");
        dijkstra.init(graph);
        dijkstra.setSource(graph.getNode(pointDebut));
        dijkstra.compute();
        return dijkstra;
    }
}
