package Routage.ihm.panels;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class DialogAjoutLien extends JDialog
{
    private final JComboBox<String> destination;
    private final JComboBox<String> depart;
    private final JTextField        cout;
    private final JButton           valider;

    private final SingleGraph graph;

    public DialogAjoutLien( SingleGraph graph )
    {
        this.setTitle("Ajout de lien");

        this.graph = graph;

        ArrayList<String> allNodeId = new ArrayList<>();
        for (int i = 0; i < this.graph.getNodeCount(); i++)
            allNodeId.add(this.graph.getNode(i).getId());

        this.depart      = new JComboBox<>(allNodeId.toArray(new String[0]));
        this.destination = new JComboBox<>(allNodeId.toArray(new String[0]));
        this.valider     = new JButton("Valider");
        this.cout        = new JTextField();

        this.destination.addItemListener(new ListListener());
        this.depart     .addItemListener(new ListListener());

        this.valider.addActionListener(event ->
        {
            String dep = this.depart.getSelectedItem()      == null ? "" : this.depart.getSelectedItem().toString();
            String des = this.destination.getSelectedItem() == null ? "" : this.destination.getSelectedItem().toString();
            int cout   = Integer.parseInt(this.cout.getText().isEmpty() ? "0" : this.cout.getText());

            if( dep.contains("PC") || des.contains("PC") ) // ne devrai pas arriver si ihm bien faite
                cout = 0;

            if( dep.isEmpty() || des.isEmpty() || dep.equals(des) || this.graph.getEdge(dep+des) != null || this.graph.getEdge(des+dep) != null )
            {
                Toolkit.getDefaultToolkit().beep();
                return;
            }

            Edge edge = this.graph.addEdge( dep+des, dep, des, false);

            if( cout > 0 ) edge.setAttribute("label", String.valueOf(cout));

            this.setVisible(false);
        });

        this.cout.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyTyped(KeyEvent e)
            {
                char key = e.getKeyChar();

                if( !Character.isDigit(key) )
                {
                    if( key != KeyEvent.VK_BACK_SPACE )
                        Toolkit.getDefaultToolkit().beep();

                    e.consume();
                }
            }
        });

        JPanel tmp  = new JPanel();
        JPanel list = new JPanel();

        tmp.setLayout(new BoxLayout(tmp, BoxLayout.Y_AXIS));

        list.add(new JLabel("départ: "));
        list.add(this.depart);
        list.add(new JLabel("destination: "));
        list.add(this.destination);

        tmp.add(list);
        tmp.add(this.cout);

        this.add(tmp, BorderLayout.CENTER);

        tmp = new JPanel();
        tmp.add(this.valider);

        this.add(tmp, BorderLayout.SOUTH);

        this.pack();
        this.setLocationRelativeTo(null);

        this.cout.setVisible(false);

        this.setModal(true);
        this.setVisible(true);
    }

    private class ListListener implements ItemListener
    {
        @Override
        public void itemStateChanged(ItemEvent e)
        {
            System.out.println("Item change");
            String dep = DialogAjoutLien.this.depart.getSelectedItem()      == null ? "" : DialogAjoutLien.this.depart.getSelectedItem().toString();
            String des = DialogAjoutLien.this.destination.getSelectedItem() == null ? "" : DialogAjoutLien.this.destination.getSelectedItem().toString();

            boolean isVisible = dep.contains("RO") && des.contains("RO");

            if( DialogAjoutLien.this.cout.isVisible() != isVisible )
            {
                DialogAjoutLien.this.cout.setVisible(isVisible);
                DialogAjoutLien.this.paintComponents(DialogAjoutLien.this.getGraphics());
            }
        }
    }

}
