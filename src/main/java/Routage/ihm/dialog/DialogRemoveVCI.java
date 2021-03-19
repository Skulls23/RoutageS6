package Routage.ihm.dialog;

import Routage.ihm.IHMGUI;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class DialogRemoveVCI extends JDialog
{
    private final IHMGUI ihm;

    private final JButton ok;

    private final JComboBox<String> destination;
    private final JComboBox<String> depart;

    public DialogRemoveVCI(IHMGUI ctrl)
    {
        this.ihm = ctrl;

        this.setTitle("Ajout d'un VCI puis Affichage");

        this.ok = new JButton("valider");

        ArrayList<String> allNodeId = new ArrayList<>();
        for (int i = 0; i < this.ihm.getNodeCount(); i++)
            allNodeId.add(this.ihm.getNode(i).getId());

        String[] arrays = allNodeId.toArray(new String[0]);
        Arrays.sort(arrays);

        this.depart      = new JComboBox<>(arrays);
        this.destination = new JComboBox<>(arrays);

        this.ok.addActionListener(event ->
        {
            String dep = this.depart.getSelectedItem()      == null ? "" : this.depart.getSelectedItem().toString();
            String des = this.destination.getSelectedItem() == null ? "" : this.destination.getSelectedItem().toString();

            if( des.isEmpty() || dep.isEmpty() || des.equals(dep) )
            {
                Toolkit.getDefaultToolkit().beep();
                return;
            }

            this.ihm.removeVCI(dep, des);
        });

        JPanel list = new JPanel();

        list.add(new JLabel("départ: "));
        list.add(this.depart);
        list.add(new JLabel("destination: "));
        list.add(this.destination);

        this.add(new JLabel("supprime la premiere connexion saisie"), BorderLayout.NORTH);
        this.add(list);

        JPanel panelBtn = new JPanel();
        panelBtn.add(this.ok);

        this.add(panelBtn, BorderLayout.SOUTH);

        this.pack();
        this.setLocationRelativeTo(null);
        this.setModal(true);
        this.setVisible(true);
    }
}
