package Routage.metier;

import Routage.Main;
import org.graphstream.algorithm.Dijkstra;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.Path;
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

    private Iterable<Node> getCheminParNode(String pointDebut, String pointFin)
    {
        Dijkstra dijkstra = setupDijkstra(pointDebut);
        return dijkstra.getPathNodes(graph.getNode(pointFin));
    }

    public HashMap<String, HashMap<String, HashMap<String, HashMap<String, Integer>>>> getVCI( Node[] cheminEnPlus )
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

        this.listchemin.add(cheminEnPlus);

        for (Node[] chemin : this.listchemin)
        {
            String keyPath = chemin[0].getId() + "->" + chemin[1].getId();

            HashMap<String, HashMap<String, HashMap<String, Integer>>> routeurMap = new HashMap<>();

            for (Node n : routeurs)
            {
                String routeurKey = n.getId();

                HashMap<String, HashMap<String, Integer>> hashINOUT = new HashMap<>();

                String in = "IN";
                String out = "OUT";

                HashMap<String, Integer> mapIN  = new HashMap<>();
                HashMap<String, Integer> mapOUT = new HashMap<>();

                mapIN.put("PORT", 0);
                mapIN.put("VCI" , 0);

                mapOUT.put("PORT", 0);
                mapOUT.put("VCI" , 0);

                hashINOUT.put( in, mapIN);
                hashINOUT.put(out, mapOUT);

                routeurMap.put(routeurKey, hashINOUT);
            }

            tableVCI.put(keyPath, routeurMap);
        }

        for (String chemin : tableVCI.keySet() )
        {
            String[] che      = chemin.split("->");
            Iterable<Node> it = this.getCheminParNode(che[0], che[1]);

            ArrayList<Node> listNode = new ArrayList<>();

            for (Node p : it) listNode.add(p);


            HashMap<String, HashMap<String, HashMap<String, Integer>>> routeurMap = tableVCI.get(chemin);

            Node avant = null;
            for (int cpt = 0; cpt < listNode.size(); cpt++ )
            {
                if( listNode.get(cpt).getId().contains("PC") )
                {
                    if( avant != null )
                        continue;
                }
                else // c'est un routeur
                {
                    HashMap<String, HashMap<String, Integer>> hashINOUT = routeurMap.get(listNode.get(cpt).getId());

                    if( avant != null )
                    {
                        HashMap<String, Integer> hashIN = hashINOUT.get("IN");

                        ArrayList<Node> list = listNode.get(cpt).neighborNodes().collect(Collectors.toCollection(ArrayList::new));

                        int port = list.indexOf(avant);
                        int vci  = this.getMaxVCIForNodeAndPort(listNode.get(cpt), true, port, tableVCI) + 1;

                        hashIN.replace("PORT", port);
                        hashIN.replace("VCI", vci);
                    }

                    if( cpt < listNode.size()-1 ) // il y as un apres
                    {
                        HashMap<String, Integer> hashIN = hashINOUT.get("OUT");

                        ArrayList<Node> list = listNode.get(cpt).neighborNodes().collect(Collectors.toCollection(ArrayList::new));

                        int port = list.indexOf(list.get(cpt+1));
                        int vci  = this.getMaxVCIForNodeAndPort(listNode.get(cpt), false, port, tableVCI) + 1;

                        hashIN.replace("PORT", port);
                        hashIN.replace("VCI", vci);
                    }
                }

                avant = listNode.get(cpt);
            }
        }

        return tableVCI;
    }

    private int getMaxVCIForNodeAndPort( Node node, boolean bIn, int port, HashMap<String, HashMap<String, HashMap<String, HashMap<String, Integer>>>> tableVCI)
    {
        int max = 0;

        for (String key : tableVCI.keySet())
        {
            HashMap<String, HashMap<String, Integer>> routeur = tableVCI.get(key).get(node.getId());

            HashMap<String, Integer> PortVCI = routeur.get(bIn ? "IN" : "OUT" );

            if( PortVCI.get("PORT") == port )
            {
                if( PortVCI.get("VCI") > max )
                    max = PortVCI.get("VCI");
            }
        }

        return max;
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
