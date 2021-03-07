package Routage.ihm;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class DialogSupprimer extends JDialog
{
    private final SingleGraph graph;

    private final JComboBox<String> listDepart;
    private final JComboBox<String> listArriver;

    private final JButton valider;

    public DialogSupprimer(SingleGraph graph, boolean isForPoint )
    {
        this.graph = graph;

        ArrayList<String> allNodeId = new ArrayList<>();
        for (int i = 0; i < graph.getNodeCount(); i++)
            allNodeId.add(graph.getNode(i).getId());

        String[] arrays = allNodeId.toArray(new String[0]);
        Arrays.sort(arrays);

        this.listDepart  = new JComboBox<>(arrays);
        this.listArriver = new JComboBox<>(arrays);

        this.valider = new JButton("valider");

        JPanel panel = new JPanel();
        panel.add(this.listDepart);

        if( !isForPoint ) panel.add(this.listArriver);

        this.add(panel, BorderLayout.CENTER);

        panel = new JPanel();
        panel.add(this.valider);

        this.add(panel, BorderLayout.SOUTH);

        this.valider.addActionListener(event ->
        {
            String id = Objects.requireNonNull(this.listDepart.getSelectedItem()).toString();

            Object tmp = null;

            if( isForPoint )
            {
                Node n = this.graph.getNode(id);

                if( n != null ) tmp = this.graph.removeNode(n);
                else            Toolkit.getDefaultToolkit().beep();
            }
            else
            {
                Edge e = this.graph.getEdge(id + Objects.requireNonNull(this.listArriver.getSelectedItem()).toString());

                if( e == null )
                    e = this.graph.getEdge(this.listArriver.getSelectedItem().toString() + id);

                if( e != null ) tmp = this.graph.removeEdge(e);
                else            Toolkit.getDefaultToolkit().beep();
            }

            if ( tmp == null )
                JOptionPane.showMessageDialog(this, isForPoint ? "Point inconnu" : "Lien inconnu", "Erreur", JOptionPane.ERROR_MESSAGE);
            else
                this.setVisible(false);
        });

        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
}
