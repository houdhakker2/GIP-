/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.friet.net.bestelling.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.PrintWriter;
import java.util.TreeMap;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import org.friet.net.UI.Button;
import org.friet.net.UI.NETableModel;
import org.friet.net.UI.Table;
import org.friet.net.UI.icons.Icone;
import org.friet.net.main.Main;

/**
 *
 * @author arne
 */
public class PanelBestelling extends JPanel {

    public TreeMap<String, TreeMap<String, Float>> items;
    public JScrollPane list;
    public DefaultTableModel model;
    public Table table;
    public JTextField ipfield;
    public JTabbedPane tabs;
    public JButton delete, cansel, nieuweKlant, confirm;
    public float prijs = 0;
    public boolean allowsBar = false;

    public PanelBestelling() {

        items = new TreeMap<String, TreeMap<String, Float>>();
        getItems();
        Event event = new Event();

        this.setLayout(new BorderLayout());

        JPanel p = new JPanel(new BorderLayout());
        model = new NETableModel(new Object[]{Main.config.getString("tblBestellingArtikel"), Main.config.getString("tblBestellingAantal"), Main.config.getString("tblBestellingPrijs")}, 0);
        table = new Table(model);
        
        list = new JScrollPane(table);
        table.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(20000);
        table.getTableHeader().getColumnModel().getColumn(0).setMinWidth(300);
        p.add(list);

        JPanel p2 = new JPanel(new GridLayout(2, 1));
        ipfield = new JTextField();
        ipfield.setText(Main.config.getString("BestellingPrijs").replace("[prijs]", "0.0"));
        UpdateText(prijs);
        ipfield.setFont(new Font("calibri", Font.BOLD, 20));
        p2.add(ipfield);
        JPanel p1 = new JPanel(new GridLayout(1, 3));
        p2.add(p1);
        p.add(p2, BorderLayout.SOUTH);
        delete = new Button("");
        delete.setVisible(true);
        delete.addActionListener(event);
        delete.setName("-");
        delete.setIcon(new Icone("Delete"));
        p1.add(delete);

        cansel = new Button("");
        cansel.setVisible(true);
        cansel.addActionListener(event);
        cansel.setIcon(new Icone("Cancel"));
        cansel.setSelectedIcon(new Icone("Cancel"));
        p1.add(cansel);

        nieuweKlant = new Button("");
        nieuweKlant.setVisible(true);
        nieuweKlant.addActionListener(event);
        nieuweKlant.setIcon(new Icone("Confirm"));
        p1.add(nieuweKlant);

        tabs = new JTabbedPane();
        tabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        for (String key : items.keySet()) {

            JPanel panel = new JPanel(new GridLayout(7, 7));

            panel.setVisible(true);
            tabs.addTab("<html><body leftmargin=20 topmargin=12 marginwidth=20 marginheight=8 style=\"font-size:20px\"><p>" + key + "<p></body></html>", panel);
            int x = -1;
            int y = 0;
            try {
                for (String naam : ((TreeMap<String, Float>) items.get(key)).keySet()) {
                    x++;
                    Button btn = new Button("<html><body style=\"font-size:20px\"><p>" + naam + "<p></body></html>");
                    btn.setName(naam);
                    btn.setVisible(true);
                    btn.addActionListener(event);
                    Color c = Main.db.randomKleur();
                    btn.setBackground(c);
                    btn.setC2(c);
                    panel.add(btn);

                    if (x == 5) {
                        x = -1;
                        y++;
                    }
                }
            } catch (Exception e) {

            }

        }
        this.add(p, BorderLayout.EAST);
        this.add(tabs, BorderLayout.CENTER);
        this.addComponentListener(new ComponentListener() {

            @Override
            public void componentResized(ComponentEvent e) {
            }

            @Override
            public void componentMoved(ComponentEvent e) {
            }

            @Override
            public void componentShown(ComponentEvent e) {
                allowsBar = true;
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                allowsBar = false;
            }

        });
    }

    protected void getItems() {
        items = Main.db.getItems();
    }

    public void UpdateText(float i) {
        ipfield.setText(Main.config.getString("BestellingPrijs").replace("[prijs]", String.format("%10.2f", prijs)));
    }

    public void barcode(String s) {
        for (TreeMap<String, Float> key : items.values()) {
            for (String naam : key.keySet()) {
                System.out.println(naam);
                if (naam.equals(Main.db.getnaam(s))) {
                    System.out.println(s);
                    float f = key.get(naam);
                    model.addRow(new Object[]{"  " + naam, "   " + 1, String.format("%10.2f", f)});
                    prijs += f;
                    UpdateText(prijs);
                }
            }
        }
    }

    public void refresh() {
        getItems();
        this.remove(tabs);
        tabs = new JTabbedPane();
        tabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        for (String key : items.keySet()) {

            JPanel panel = new JPanel(new GridLayout(7, 7));

            panel.setVisible(true);
            tabs.addTab("<html><body leftmargin=20 topmargin=12 marginwidth=20 marginheight=8 style=\"font-size:20px\"><p>" + key + "<p></body></html>", panel);
            int x = -1;
            int y = 0;
            try {
                for (String naam : ((TreeMap<String, Float>) items.get(key)).keySet()) {
                    x++;
                    Button btn = new Button("<html><body style=\"font-size:20px\"><p>" + naam + "<p></body></html>");
                    btn.setName(naam);
                    btn.setVisible(true);
                    btn.addActionListener(new Event());
                    Color c = Main.db.randomKleur();
                    btn.setBackground(c);
                    btn.setC2(c);
                    panel.add(btn);

                    if (x == 5) {
                        x = -1;
                        y++;
                    }
                }
            } catch (Exception e) {

            }

        }
        this.add(tabs, BorderLayout.CENTER);
    }

    public class Event implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            if (e.getSource().getClass() == Button.class) {
                if (e.getActionCommand().startsWith("<html><body style=\"font-size:20px\"><p>")) {
                    for (TreeMap<String, Float> key : items.values()) {
                        for (String naam :  key.keySet()) {
                            if (naam.equals(((Button) e.getSource()).getName())) {
                                float f = new Float(key.get(naam));
                                model.addRow(new Object[]{"  " + naam, "   " + 1, String.format("%10.2f", f)});
                                prijs += f;
                                UpdateText(prijs);
                            }
                        }
                    }
                }

                if (e.getSource() == delete) {
                    int[] ints = table.getSelectedRows();

                    for (int i : ints) {
                        prijs -= new Float(((Vector) model.getDataVector().get(i)).get(2).toString().replace(",", "."));
                        model.removeRow(i);
                        UpdateText(prijs);
                    }
                }

                if (e.getSource() == cansel) {
                    model.setRowCount(0);
                    prijs = 0;
                    UpdateText(prijs);
                }

                if (e.getSource() == nieuweKlant) {
                    PrintWriter writer;
                    try {
                        writer = new PrintWriter("klant" + Main.Klantnummer + ".txt", "UTF-8");

                        for (Object i : model.getDataVector().toArray()) {
                            Vector is = (Vector) i;
                            writer.println(is.get(0) + " --- " + is.get(2) + " euro");
                            
                            float i2 = new Float(is.get(2).toString().replace(",", "."));
                        }
                        
                        writer.close();
                    } catch (Exception ex) {
                        Logger.getLogger(PanelBestelling.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    int bool = JOptionPane.showConfirmDialog(nieuweKlant, Main.config.getString("Bestelling").replace("[prijs]", String.format("%10.2f", prijs)), Main.config.getString("BestellingHeader"), 0);
                    if (bool == 0) {
                        int bestellingID = Main.db.addBestelling( Main.Werknemmersnummer, prijs);
                        for (Object i : model.getDataVector().toArray()) {
                            Vector i2 = (Vector) i;
                            Main.db.verminderItem(((String) i2.get(0)).split("  ")[1]);
                            Main.db.addItemBestelling(Main.db.getIdFromItemName(i2.get(0).toString().split("  ")[1]), bestellingID);
                        }
                        
                        Main.Klantnummer++;
                        model.setRowCount(0);
                        prijs = 0;
                        UpdateText(prijs);
                    }

                    Main.frame.main.Inhoud.refresh();
                    Main.frame.main.items.refresh();
                    Main.frame.main.InfoBestellingen.refresh();
                }
            }
        }
    }
}
