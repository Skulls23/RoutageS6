import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.graphicGraph.GraphicNode;

public class Main
{
    public static void main(String[] args)
    {
        System.out.println("YO");

        SingleGraph graph = new SingleGraph("Test");

        graph.addNode("N1");
        graph.addNode("N2");
        graph.addNode("N3");

        graph.addEdge("N1N2", "N1", "N2", true);
        graph.addEdge("N1N3", "N1", "N3", true);
        graph.addEdge("N2N3", "N2", "N3", true);

        graph.display();
    }
}
