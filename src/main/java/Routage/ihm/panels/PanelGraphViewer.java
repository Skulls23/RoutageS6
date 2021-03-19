package Routage.ihm.panels;

import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.ProxyPipe;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.swing_viewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerPipe;

import javax.swing.*;
import java.awt.*;

public class PanelGraphViewer extends JPanel
{
    private final ViewerPipe pipe;
    private final ViewPanel viewPanel;

    public PanelGraphViewer(SingleGraph graph)
    {
        this.setLayout( new GridLayout() );

        Viewer viewer = new SwingViewer(graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        viewer.enableAutoLayout();

        this.viewPanel = (ViewPanel) viewer.addDefaultView(false);

        this.pipe = viewer.newViewerPipe();

        this.add(viewPanel);
    }

    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension(640, 480);
    }

    public void refreshGraph()
    {
        this.viewPanel.printAll(this.getGraphics());
        this.pipe.pump();
    }
}
