package Routage.metier;

import org.graphstream.algorithm.Dijkstra;
import org.graphstream.graph.Edge;
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
    private final SingleGraph graph;
    private final ArrayList<Node[]> listchemin;


    /**
     * Constructeur
     * @param graph Le graph
     */
    public Metier(SingleGraph graph)
    {
        this.graph = graph;

        this.listchemin = new ArrayList<>();
    }

    /**
     * Calcule le plus court chemin entre pointDebut et pointFin
     * @param pointDebut Le Node de départ
     * @param pointFin Le Node d'arrivé
     * @return String du chemin le plus court
     */
    public String getPlusCourtCheminTextuel(String pointDebut, String pointFin)
    {
        Dijkstra dijkstra = this.setupDijkstra(pointDebut);
        return dijkstra.getPath(graph.getNode(pointFin)) + " " + dijkstra.getPathLength(graph.getNode(pointFin));
    }

    /**
     * Colorie en vert les Nodes qui ont servi de point de passage entre pointDebut et pointFin
     * @param pointDebut Le Node de départ
     * @param pointFin Le Node d'arrivé
     */
    public void getPlusCourtCheminGraphique(String pointDebut, String pointFin)
    {
        this.reinitialiserCouleurs();

        Dijkstra dijkstra = this.setupDijkstra(pointDebut);

        for (Node node : dijkstra.getPathNodes(this.graph.getNode(pointFin)))
            node.setAttribute("ui.style", "fill-color: green;");

        for (Edge edge : dijkstra.getPathEdges(this.graph.getNode(pointFin)))
            edge.setAttribute("ui.style", "fill-color: green;");
    }

    /**
     * Remet les couleurs des noeuds en noir
     */
    public void reinitialiserCouleurs()
    {
        for (int i = 0; i < this.graph.getNodeCount(); i++)
            this.graph.getNode(i).setAttribute("ui.style", "fill-color: black;");

        for (int i = 0; i < this.graph.getEdgeCount(); i++)
            this.graph.getEdge(i).setAttribute("ui.style", "fill-color: black;");
    }

    /**
     * Execute getPlusCourtCheminTextuel suivi de getPlusCourtCheminGraphique
     * @param pointDebut Le Node de départ
     * @param pointFin Le Node d'arrivé
     * @return String resultant de getPlusCourtCheminTextuel
     */
    public String getPlusCourtCheminTextuelEtGraphique(String pointDebut, String pointFin)
    {
        this.getPlusCourtCheminGraphique(pointDebut, pointFin);
        return this.getPlusCourtCheminTextuel(pointDebut, pointFin);
    }

    /**
     * Affiche la table de routage résultant du graph
     * @return une TreeMap qui lie un nom de routeur de depart a un nom de routeur d'arrivé
     * qui est lui meme lié a tout ses voisins et leurs poids
     */
    public TreeMap<String, TreeMap<String, TreeMap<String, Double>>> getTableRoutage()
    {
        SingleGraph graphTemp = getCopieGraph();

        StringBuilder ret      = new StringBuilder();
        Dijkstra      dijkstra = new Dijkstra(Dijkstra.Element.EDGE, null, "length");

        TreeMap<String, TreeMap<String, TreeMap<String, Double>>> hashSite = new TreeMap<>();

        //Parcours de tout les Nodes du graph
        graphTemp.nodes().forEachOrdered(pointDebut ->
        {
            if( pointDebut.getId().contains("PC") )
                return;

            ret.append("\n\t").append(pointDebut.getId()).append("\n");
            dijkstra.init(graphTemp);
            dijkstra.setSource(pointDebut);
            dijkstra.compute();

            Object[] tabVoisin          = pointDebut.neighborNodes().toArray();
            Object[] tabEdgeVoisin      = pointDebut.edges().toArray();
            double[] tabPoidsVersVoisin = new double[tabVoisin.length];

            //on definit les Nodes voisins
            for (int k = 0; k < tabVoisin.length; k++)
            {
                Edge e = pointDebut.getEdge(k);

                tabPoidsVersVoisin[k] = e.getAttribute("length") == null ? Double.NEGATIVE_INFINITY : (double) e.getAttribute("length");
            }

            //on enleve les liaisons avec les voisins pour eviter qu'un paquet repasse 2x par le meme Node
            for (Object o : tabEdgeVoisin) graphTemp.removeEdge((Edge) o);

            TreeMap<String, TreeMap<String, Double>> listAllDest = new TreeMap<>();

            //Parcours des Nodes d'arrivé
            graphTemp.nodes().forEachOrdered( pointFin ->
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
                // seule solution trouver, trier "manuellement" et mettre un comparateur "inutile".

                Double[] tab = mapPoidsVoisinVersFinal.values().toArray(new Double[0]);
                Arrays.sort(tab);

                ArrayList<String> set = new ArrayList<>(mapPoidsVoisinVersFinal.keySet());
                ArrayList<Double> col = new ArrayList<>(mapPoidsVoisinVersFinal.values());

                //liaison des couts en fonctions des chemins
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

            //Remise de toutes les liaisons avec les Nodes voisins pour reboucler proprement
            for (Object o : tabEdgeVoisin)
            {
                Edge e = ((Edge) o);
                graphTemp.addEdge(e.getId(), e.getNode0(), e.getNode1());
                graphTemp.getEdge(e.getId()).setAttribute("length", e.getAttribute("length"));
                graphTemp.getEdge(e.getId()).setAttribute("label", e.getAttribute("label"));
                graphTemp.getEdge(e.getId()).setAttribute("ui.style", e.getAttribute("ui.style"));
            }
        });

        //System.out.println(ret.toString());
        return hashSite;
    }

    /**
     * Permet de definir le point de depart de l'algorithme de Dijkstra
     * @param pointDebut Le Node de départ
     * @return l'objet qui pourra executer l'algorithme de Dijkstra
     */
    private Dijkstra setupDijkstra(String pointDebut)
    {
        Dijkstra dijkstra = new Dijkstra(Dijkstra.Element.EDGE, null, "length");
        dijkstra.init(graph);
        dijkstra.setSource(graph.getNode(pointDebut));
        dijkstra.compute();
        return dijkstra;
    }

    /**
     * Permet un equivalent de constructeur par recopie
     * @return une copie de this.graph
     */
    private SingleGraph getCopieGraph()
    {
        SingleGraph   graphTemp = new SingleGraph("temporaire");

        for(int i=0; i<graph.getNodeCount(); i++)
            graphTemp.addNode(graph.getNode(i).getId());

        for(int i=0; i<graph.getEdgeCount(); i++)
        {
            graphTemp.addEdge(graph.getEdge(i).getId(), graphTemp.getNode(graph.getEdge(i).getNode0().getId()), graphTemp.getNode(graph.getEdge(i).getNode1().getId()));
            graphTemp.getEdge(i).setAttribute("length"  , graph.getEdge(i).getAttribute("length"));
        }
        return graphTemp;
    }

    /**
     * Permet d'avoir tout les nodes qui ont servi de points de passsages entre pointDebut et pointFin
     * @param pointDebut Le Node de départ
     * @param pointFin Le Node d'arrivé
     * @return Un Iterable de Node
     */
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

            int numero = 0;
            do
            {
                numero++;
            }
            while (tableVCI.containsKey(keyPath + " " + numero));

            keyPath += " " + numero;

            tableVCI.put(keyPath, routeurMap);
        }

        for (String chemin : tableVCI.keySet() )
        {
            String[] che      = chemin.split("->");

            if( che[1].contains(" ") )
                che[1] = che[1].substring(0, che[1].length()-2).trim();

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
                        HashMap<String, Integer> hashOut = hashINOUT.get("OUT");

                        ArrayList<Node> list = listNode.get(cpt).neighborNodes().collect(Collectors.toCollection(ArrayList::new));

                        int port = list.indexOf(listNode.get(cpt+1))+1;
                        int vci  = this.getMaxVCIForNodeAndPort(listNode.get(cpt), false, port, tableVCI) + 1;

                        hashOut.replace("PORT", port);
                        hashOut.replace("VCI", vci);
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
