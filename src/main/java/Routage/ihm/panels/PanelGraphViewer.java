package Routage.ihm.panels;

import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.swing_viewer.ViewPanel;
import org.graphstream.ui.view.Viewer;

import javax.swing.*;
import java.awt.*;

public class PanelGraphViewer extends JPanel
{
    public PanelGraphViewer(SingleGraph graph)
    {
        this.setLayout( new GridLayout() );

        Viewer viewer = new SwingViewer(graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        viewer.enableAutoLayout();

        ViewPanel viewPanel = (ViewPanel) viewer.addDefaultView(false);
        this.add(viewPanel);
    }

    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension(640, 480);
    }
}
