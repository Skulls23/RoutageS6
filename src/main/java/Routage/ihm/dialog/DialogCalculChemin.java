package Routage.ihm.dialog;

import Routage.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;

public class DialogCalculChemin extends JDialog
{
    private final Main ctrl;

    private final JComboBox<String> destination;
    private final JComboBox<String> depart;

    public DialogCalculChemin(Main ctrl)
    {
        this.ctrl = ctrl;

        this.setTitle("Calcule du chemin le plus court");

        JButton ok = new JButton("valider");

        ArrayList<String> allNodeId = new ArrayList<>();
        for (int i = 0; i < this.ctrl.getNodeCount(); i++)
            allNodeId.add(this.ctrl.getNode(i).getId());

        String[] arrays = allNodeId.toArray(new String[0]);
        Arrays.sort(arrays);

        this.depart      = new JComboBox<>(arrays);
        this.destination = new JComboBox<>(arrays);

        ok.addActionListener(event ->
        {
            this.ctrl.reinitialiserCouleurs();
            String dep = this.depart.getSelectedItem()      == null ? "" : this.depart.getSelectedItem().toString();
            String des = this.destination.getSelectedItem() == null ? "" : this.destination.getSelectedItem().toString();

            if( des.isEmpty() || dep.isEmpty() || des.equals(dep) )
            {
                Toolkit.getDefaultToolkit().beep();
                return;
            }

            this.ctrl.getPlusCourtCheminTextuelEtGraphique(dep, des);
        });

        //Reset des couleurs avant la fermeture
        this.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                ctrl.reinitialiserCouleurs();
            }
        });

        JPanel list = new JPanel();

        list.add(new JLabel("départ: "));
        list.add(this.depart);
        list.add(new JLabel("destination: "));
        list.add(this.destination);

        this.add(list);

        JPanel panelBtn = new JPanel();
        panelBtn.add(ok);

        this.add(panelBtn, BorderLayout.SOUTH);

        this.pack();
        this.setLocationRelativeTo(null);
        this.setModal(true);
        this.setVisible(true);
    }
}
