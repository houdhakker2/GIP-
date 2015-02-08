package org.friet.net.database;

import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.friet.net.login.panel.PanelLogin;
import org.friet.net.main.Main;

public class Database {

    final static String DBPAD = "src/res/DB.mdb";
    final static String DB = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=" + DBPAD;

    private int rand = 0;
    public Connection con;

    public Database() {

        try {
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");

            con = DriverManager.getConnection(DB, "", "");

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void addItem(String naam, float amount) {
        Statement stat;
        ResultSet set;
        try {
            stat = con.createStatement();
            stat.execute("SELECT Hoeveelheid FROM inhoud WHERE soortnaam='" + naam + "'");
            set = stat.getResultSet();
            float i = 0;
            while (set.next()) {
                i = set.getFloat("Hoeveelheid");
            }
            i += amount;
            stat = con.createStatement();
            stat.execute("UPDATE inhoud SET Hoeveelheid=" + i + " WHERE soortnaam='" + naam + "'");

        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public int checkPass(String user, String password) {

        Statement stat;
        ResultSet set;

        try {
            stat = con.createStatement();
            stat.execute("SELECT naam,pass,manager,loginid FROM login");
            set = stat.getResultSet();
            while (set.next()) {
                String naam = set.getString("naam");
                String pass = set.getString("pass");
                Main.Werknemmersnummer = set.getInt("loginid");
                if (naam.equals(user) && password.equals(pass)) {
                    if (set.getBoolean("manager")) {
                        return 2;
                    }
                    return 1;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }

        return 0;
    }

    public TreeMap<String, TreeMap<String, Float>> getItems() {

        TreeMap<String, TreeMap<String, Float>> items;
        items = new TreeMap<>(new Comparator<String>() {
            public String[] ok = new String[]{"frieten", "sauzen", "snacks", "drank"};

            @Override
            public int compare(String o1, String o2) {
                int first = 1, last = 1;

                for (int i = 0; i < ok.length; i++) {
                    if (o1.equals(ok[i])) {
                        first = i;
                    }

                    if (o2.equals(ok[i])) {
                        last = i;
                    }
                }

                return first > last ? 1 : first < last ? -1 : 0;
            }
        });
        Statement stat;
        ResultSet set;
        try {
            stat = con.createStatement();
            stat.execute("SELECT naam,PrijsPerItem,CategorieNaam FROM Item,soort WHERE soort.soortid = Item.soort");
            set = stat.getResultSet();
            while (set.next()) {
                String soort = set.getString("CategorieNaam");
                String naam = set.getString("naam");
                float value = set.getFloat("PrijsPerItem");

                if (items.containsKey(soort)) {
                    items.get(soort).put(naam.toLowerCase(), new Float(value));
                } else {
                    TreeMap<String, Float> item;
                    item = new TreeMap<>();
                    item.put(naam.toLowerCase(), new Float(value));
                    items.put(soort, item);
                }

            }
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }

        return items;
    }

    public Map<String, Map<String, Float>> getInhoud() {

        TreeMap<String, Map<String, Float>> items;
        items = new TreeMap<>(new Comparator<String>() {
            public String[] ok = new String[]{"frieten", "sauzen", "snacks", "drank"};

            @Override
            public int compare(String o1, String o2) {
                int first = 1, last = 1;

                for (int i = 0; i < ok.length; i++) {
                    if (o1.equals(ok[i])) {
                        first = i;
                    }

                    if (o2.equals(ok[i])) {
                        last = i;
                    }
                }

                return first > last ? 1 : first < last ? -1 : 0;
            }
        });
        Statement stat;
        ResultSet set;
        Statement stat2;
        ResultSet set2;
        try {
            stat = con.createStatement();
            stat.execute("SELECT soortnaam,CategorieNaam FROM Inhoud,soort Where Inhoud.Categorie = soort.soortid");
            set = stat.getResultSet();
            while (set.next()) {
                String naam = set.getString("soortnaam");
                String soort = set.getString("CategorieNaam");
                if (items.containsKey(soort)) {
                    items.get(soort).put(naam, 1f);
                } else {
                    TreeMap<String, Float> item;
                    item = new TreeMap<>();
                    item.put(naam, 1f);
                    items.put(soort, item);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }

        return items;
    }

    public void verminderItem(String naam) {
        Statement stat;
        ResultSet set;
        try {
            stat = con.createStatement();
            stat.execute("SELECT hoeveelheid,HoeveelheidPerItem FROM inhoud,item WHERE item.naam = '" + naam + "'and item.soort= inhoud.soortid");
            set = stat.getResultSet();
            float i = 0;
            float j = 0;
            while (set.next()) {
                i = set.getFloat("hoeveelheid");
                j = set.getFloat("HoeveelheidPerItem");
            }
            i -= j;
            stat = con.createStatement();
            stat.execute("UPDATE inhoud,item SET hoeveelheid=" + i + " WHERE item.naam='" + naam + "' and item.soort= inhoud.soortid");
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addBestelling(int klantId, int werknemerId, float totaalprijs) {
        Statement stat;
        ResultSet set;
        try {
            stat = con.createStatement();
            stat.execute("insert into bestelling(KlantID,WerknemerID,Totaalprijs) values(" + klantId + "," + werknemerId + "," + totaalprijs + ")");
            set = stat.getResultSet();
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addKlant(String email, String naam) {
        Statement stat;
        ResultSet set;
        try {
            stat = con.createStatement();
            stat.execute("insert into klanten values(" + email + "," + naam + ")");
            set = stat.getResultSet();
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addwerknemer(String naam, String Voornaam, String adres, String gemeente, String post, String email, String login, String telefoonnummer) {
        Statement stat;
        ResultSet set;
        try {
            stat = con.createStatement();
            stat.execute("insert into login (naam,pass) values ('" + Voornaam + "','" + PanelLogin.encode(login) + "')");
            stat = con.createStatement();
            stat.execute("SELECT loginId FROM login WHERE login.naam ='" + Voornaam + "'");
            set = stat.getResultSet();
            set.next();
            stat = con.createStatement();
            stat.execute("insert into werknemer (naam,voornaam,adres,gemeente,postcode,emailadres,loginid,telefoonnummer) values ('" + naam + "','" + Voornaam + "','" + adres + "','" + gemeente + "','" + post + "','" + email + "','" + set.getInt("loginid") + "','" + telefoonnummer + "')");

        } catch (Exception ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void delwerknemer(int id) {
        Statement stat;
        ResultSet set;
        try {
            stat = con.createStatement();
            stat.execute("DELETE FROM werknemer WHERE Werknemer.WerknemerID=" + id);
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean updatewerknemer(String naam, String Voornaam, String adres, String gemeente, String post, String email, String telefoonnummer, int ID) {
        Statement stat;
        ResultSet set;
        boolean i = false;
        try {
            stat = con.createStatement();
            stat.execute("UPDATE werknemer SET Naam='" + naam + "',Adres='" + adres + "',Gemeente='" + gemeente + "',Emailadres='" + email + "',Voornaam='" + Voornaam + "',Postcode='" + post + "',telefoonnummer='" + telefoonnummer + "' WHERE werknemer.WerknemerID=" + ID);
            set = stat.getResultSet();
            i = (set == null);
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        return i;
    }

    public ArrayList getwerknemers() {

        ArrayList hallo = new ArrayList();
        Statement stat;
        ResultSet set;
        try {
            stat = con.createStatement();
            stat.execute("SELECT * FROM werknemer ");
            set = stat.getResultSet();
            while (set.next()) {
                TreeMap<String, String> hash;
                hash = new TreeMap<>(new Comparator<String>() {
                    public String[] ok = new String[]{"Werknemer ID", "Voornaam", "Naam", "Adres", "Postcode", "Gemeente", "Telefoonnummer", "E-mail"};
                    @Override
                    public int compare(String o1, String o2) {
                        int first = 1, last = 1;

                        for (int i = 0; i < ok.length; i++) {
                            if (o1.equals(ok[i])) {
                                first = i;
                            }

                            if (o2.equals(ok[i])) {
                                last = i;
                            }
                        }

                        return first > last ? 1 : first < last ? -1 : 0;
                    }
                });
                hash.put("Voornaam", set.getString("voornaam"));
                hash.put("Adres", set.getString("adres"));
                hash.put("Gemeente",  set.getString("gemeente"));
                hash.put("E-mail", set.getString("emailadres"));
                hash.put("Werknemer ID", set.getString("WerknemerID"));
                hash.put("Naam", set.getString("naam"));
                hash.put("Postcode",  set.getString("postcode"));
                hash.put("Telefoonnummer",  set.getString("telefoonnummer"));
                hallo.add(hash);
            }

        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }

        return hallo;
    }

    public Color randomKleur() {

        ArrayList hallo = new ArrayList();
        Statement stat;
        ResultSet set;
        try {
            stat = con.createStatement();
            stat.execute("SELECT * FROM kleur ORDER BY ID");
            set = stat.getResultSet();
            while (set.next()) {
                hallo.add(new Color(set.getInt("r"), set.getInt("g"), set.getInt("b")));
            }

        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        rand++;
        if (rand == hallo.size()) {
            rand = 0;
        }
        return (Color) hallo.get(rand);
    }
}
