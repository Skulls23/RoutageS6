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

    private final ArrayList<Node[]> listchemin;

    public Metier(Main ctrl, SingleGraph graph)
    {
        this.ctrl = ctrl;
        this.graph = graph;

        this.listchemin = new ArrayList<>();
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

    public TreeMap<String, TreeMap<String, TreeMap<String, Double>>> getTableRoutage()
    {
        StringBuilder ret = new StringBuilder();
        Dijkstra dijkstra = new Dijkstra(Dijkstra.Element.EDGE, null, "length");

        TreeMap<String, TreeMap<String, TreeMap<String, Double>>> hashSite = new TreeMap<>();

        this.graph.nodes().forEachOrdered(pointDebut ->
        {
            if( pointDebut.getId().contains("PC") )
                return;

            ret.append("\n\t").append(pointDebut.getId()).append("\n");
            dijkstra.init(graph);
            dijkstra.setSource(pointDebut);
            dijkstra.compute();

            Object[] tabVoisin          = pointDebut.neighborNodes().toArray();
            Object[] tabEdgeVoisin      = pointDebut.edges().toArray();
            double[] tabPoidsVersVoisin = new double[tabVoisin.length];
            //on definit ses voisins et on les enleve pour les remettre plus tard
            for (int k = 0; k < tabVoisin.length; k++)
            {
                Edge e = pointDebut.getEdge(k);

                tabPoidsVersVoisin[k] = e.getAttribute("length") == null ? Double.NEGATIVE_INFINITY : (double) e.getAttribute("length");
                //graph.removeEdge((Edge)tabEdgeVoisin[k]);
            }

            TreeMap<String, TreeMap<String, Double>> listAllDest = new TreeMap<>();

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

                for (Double aDouble : tab)
                {
                    int i = col.indexOf(aDouble);

                    treeMap.put(set.get(i), aDouble);

                    set.remove(i);
                    col.remove(i);
                }

                ret.append(pointFin.getId()).append(" : ").append(treeMap).append("\n");
                listAllDest.put(pointFin.getId(), treeMap);
            });

            hashSite.put(pointDebut.getId(), listAllDest);
            /*for (int k = 0; k < tabVoisin.length; k++)
                graph.addEdge(((Edge)tabEdgeVoisin[k]).getId(), ((Edge)tabEdgeVoisin[k]).getNode0(), ((Edge)tabEdgeVoisin[k]).getNode1());*/

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

    private Iterable getCheminParNode(String pointDebut, String pointFin)
    {
        Dijkstra dijkstra = setupDijkstra(pointDebut);
        return dijkstra.getAllPaths(graph.getNode(pointFin));
    }

    public void getVCI( Node[] cheminEnPlus )
    {
        ArrayList<Node> routeurs = new ArrayList<>();

        this.graph.nodes().forEach(node ->
        {
            if (node.getId().contains("RO"))
                routeurs.add(node);
        });

        /**
         *  First HM :{
         *      key  : RO1->RO2 | RO2->RO4 ect...
         *      value: {
         *              key: RO1 | RO2 | RO3 ect...
         *              value: {
         *                      key: IN / OUT
         *                      value: {
         *                              key: PORT / VCI
         *                              value: Integer (0, 1, 2, 3, ect...)
         *                          }
         *                  }
         *          }
         *  }
         */
        HashMap<String, HashMap<String, HashMap<String, HashMap<String, Integer>>>> tableVCI = new HashMap<>();

        for (int i = 0; i < this.listchemin.size(); i++)
        {
            Node[] chemin = this.listchemin.get(i);
        }
    }

    public void resetVCI()
    {
        this.listchemin.clear();
    }

    public void remove( Node[] chemin )
    {
        this.listchemin.remove(chemin);
    }
}
