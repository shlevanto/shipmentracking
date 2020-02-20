import java.sql.*;
import java.util.Random;

public class Pakettitietokanta  {
    private Connection db;
    private Statement s;
    private ResultSet r;
    private PreparedStatement p;
    
    public Pakettitietokanta(String nimi) throws SQLException {
        this.db = DriverManager.getConnection("jdbc:sqlite:"+nimi+".db");
        this.s = db.createStatement();
        this.r = null;
        this.p = null;
    }
    
    // Luo sovelluksen tarvitsemat taulut tyhjään tietokantaan 
    // (tätä toimintoa voidaan käyttää, kun tietokantaa ei ole vielä olemassa).
    public void alusta() throws SQLException {        
        s.execute("CREATE TABLE Asiakkaat (id INTEGER PRIMARY KEY, nimi TEXT)");
        s.execute("CREATE TABLE Paikat (id INTEGER PRIMARY KEY, paikka TEXT)");
        s.execute("CREATE TABLE Paketit (id INTEGER PRIMARY KEY, "
                + "asiakas_id INTEGER, koodi TEXT, FOREIGN KEY(asiakas_id) "
                + "REFERENCES Asiakas(id))");
        s.execute("CREATE TABLE Tapahtumat (id INTEGER PRIMARY KEY, "
                + "paketti_id INTEGER, paikka_id INTEGER, aika TEXT, "
                + "kuvaus TEXT, FOREIGN KEY(paketti_id) REFERENCES Paketti(id), "
                + "FOREIGN KEY(paikka_id) REFERENCES Paikka(id))"); 
    }
    
    public void indeksoi() throws SQLException {
        s.execute("CREATE INDEX idx_paketti_id ON Tapahtumat (paketti_id)");
        s.execute("CREATE INDEX idx_asiakas_id ON Paketit (asiakas_id)");
    }
    
    // Lisää uusi paikka tietokantaan, kun annetaan paikan nimi.
    public void lisaaPaikka(String paikka) throws SQLException {
        db.setAutoCommit(false);

        // tarkista onko paikka jo olemassa
        p = db.prepareStatement("SELECT paikka FROM Paikat WHERE paikka = (?)");
        p.setString(1,paikka);
        r = p.executeQuery();

        if (r.next()) {
                throw new SQLException("Paikka " + paikka + 
                        " on jo tietokannassa.");
        }

        p = db.prepareStatement("INSERT INTO Paikat(paikka) VALUES (?)");
        p.setString(1,paikka);
        p.executeUpdate();
        db.commit();

        db.setAutoCommit(true);
    }
    
    // Lisää uusi asiakas tietokantaan, kun annetaan asiakkaan nimi.
    public void lisaaAsiakas(String nimi) throws SQLException {
        db.setAutoCommit(false);
        // tarkista onko paikka jo olemassa
        p = db.prepareStatement("SELECT nimi FROM Asiakkaat WHERE nimi = (?)");
        p.setString(1,nimi);
        r = p.executeQuery();

        if (r.next()) {
            throw new SQLException();
        } 

        p = db.prepareStatement("INSERT INTO Asiakkaat (nimi) VALUES (?)");
        p.setString(1,nimi);
        p.executeUpdate();

        db.commit();
        db.setAutoCommit(true);
    }
    
    // Lisää uusi paketti tietokantaan, kun annetaan paketin seurantakoodi 
    // ja asiakkaan nimi. Asiakkaan tulee olla valmiiksi tietokannassa.
    public void lisaaPaketti(String nimi, String koodi) throws SQLException {
        db.setAutoCommit(false);

        // tarkista onko koodi jo kannassa
        p = db.prepareStatement("SELECT koodi FROM Paketit WHERE koodi = (?)");
        p.setString(1,koodi);
        r = p.executeQuery();


        if (r.next()) {
            throw new SQLException("Koodi " + koodi + " on jo tietokannassa.");
        }  

       
       // tarkista onko asiakas kannnassa 
       p = db.prepareStatement("SELECT id FROM Asiakkaat WHERE nimi = (?)");
       p.setString(1,nimi);
       r = p.executeQuery();

        if (r.next()) {
            p = db.prepareStatement("INSERT INTO Paketit (asiakas_id, koodi) "
                    + "VALUES (?,?)");
            p.setInt(1,r.getInt("id"));
            p.setString(2,koodi);
            p.executeUpdate();
        } else {
            throw new SQLException("Asiakasta " + nimi + " ei löydy kannasta.");
        }

        db.commit();
        db.setAutoCommit(true);

    }
        
    // Lisää uusi tapahtuma tietokantaan, kun annetaan paketin seurantakoodi, 
    // tapahtuman paikka sekä kuvaus. 
    // Paketin ja paikan tulee olla valmiiksi tietokannassa.
    public void lisaaTapahtuma(String koodi, String paikka, String kuvaus) 
            throws SQLException {
        
        db.setAutoCommit(false);

        // tarkista koodin perusteella, onko paketti kannassa
        p = db.prepareStatement("SELECT koodi, id FROM Paketit "
                + "WHERE koodi = (?)");
        p.setString(1, koodi);
        r = p.executeQuery();
        
        int pakettiId;
        
        if (r.next()) {
            pakettiId = r.getInt("id");
        } else {
            throw new SQLException("Koodia " + koodi + " ei löydy kannasta.");
        }

        // tarkista onko paikka kannassa
        p = db.prepareStatement("SELECT paikka, id FROM Paikat "
                + "WHERE paikka = (?)");
        p.setString(1,paikka);
        r = p.executeQuery();

        int paikkaId;

        if (r.next()) {
            paikkaId = r.getInt("id");
        } else {
            throw new SQLException("Paikkaa " + paikka + " ei löydy kannasta.");
        }

        p = db.prepareStatement("INSERT INTO Tapahtumat "
                + "(paketti_id, paikka_id, aika, kuvaus) "
                + "VALUES (?,?,datetime('now','localtime'),?)");
        p.setInt(1, pakettiId);
        p.setInt(2, paikkaId);
        p.setString(3, kuvaus);

        p.executeUpdate();

        db.commit();
        db.setAutoCommit(true);
    }
    
    // Hae kaikki paketin tapahtumat seurantakoodin perusteella.
    public void haeTapahtumat(String koodi) throws SQLException {
        // hae koodiin liittyvä id
        p = db.prepareStatement("SELECT koodi, id FROM Paketit "
                + "WHERE koodi = (?)");
        p.setString(1, koodi);
        r = p.executeQuery();

        if (r.next()) {
            p = db.prepareStatement("SELECT L.paikka, T.aika, T.kuvaus "
                    + "FROM Paikat L, Tapahtumat T WHERE L.id = T.paikka_id "
                    + "AND T.paketti_id = (?)");
            p.setInt(1, r.getInt("id"));
            r = p.executeQuery();
        } else {
            throw new SQLException("Koodia " + koodi + " ei löydy kannasta.");
        }

        while (r.next()) {
            System.out.println(r.getString("paikka") + " " 
                    + r.getString("aika") + " " + r.getString("kuvaus"));
        }

    }


    // Hae kaikki asiakkaan paketit ja niihin liittyvien tapahtumien määrä.
    public void haeAsiakkaanPaketit (String nimi) throws SQLException {
       // tarkista onko asiakas kannnassa 
        p = db.prepareStatement("SELECT id FROM Asiakkaat WHERE nimi = (?)");
        p.setString(1,nimi);
        r = p.executeQuery();

         if (r.next()) {
             p = db.prepareStatement("SELECT P.koodi, COUNT(T.kuvaus) luku "
                     + "FROM (SELECT koodi,id FROM Paketit WHERE asiakas_id = (?) "
                     + "GROUP BY koodi) P LEFT JOIN Tapahtumat T "
                     + "ON T.paketti_id  = P.id GROUP BY P.koodi");
             p.setInt(1,r.getInt("id"));
             //p.setInt(2,r.getInt("id"));
             r = p.executeQuery();
         } else {
             throw new SQLException("Asiakasta " + nimi + " ei löydy kannasta.");
         }

         while (r.next()) {
             System.out.println(r.getString("koodi") + " " + r.getInt("luku"));
         }

     }
    
    // Hae annetusta paikasta tapahtumien määrä tiettynä päivänä.
    public void haePaivanTapahtumat(String paikka, String aika) throws SQLException {
        
        // tarkista onko paikka kannassa
        p = db.prepareStatement("SELECT paikka, id FROM Paikat "
                + "WHERE paikka = (?)");
        p.setString(1,paikka);
        r = p.executeQuery();

        if (r.next()) {
            p = db.prepareStatement("SELECT paikka, maara FROM "
                    + "(SELECT L.paikka paikka, COUNT(T.paikka_id) maara "
                    + "FROM Paikat L, Tapahtumat T "
                    + "WHERE aika LIKE (?) AND L.id = T.paikka_id "
                    + "GROUP BY paikka_id) WHERE paikka = (?)");
            p.setString(1,aika+"%");
            p.setString(2,paikka);
            r = p.executeQuery();
        } else {
            throw new SQLException("Paikkaa " + paikka + " ei löydy kannasta.");
        }
        
        while (r.next()) {
            System.out.println(r.getString("paikka") + " " + r.getInt("maara"));
        }
        
    }
    
    public void sulje() throws SQLException {
        this.db.close();
        this.db = null;
        
    }
    
    // Tehokkuustesti ei käytä luokan metodeja
    public void tehokkuusTesti() throws SQLException {
        Random q = new Random(80085);
        
        PreparedStatement paikat  = null;
        PreparedStatement asiakkaat = null;
        PreparedStatement paketit = null ;
        PreparedStatement tapahtumat = null;
        PreparedStatement hae = null;
        
        long alku;
        long loppu;
        
        db.setAutoCommit(false);
        
        alku = System.nanoTime();
        
        // Tietokantaan lisätään tuhat paikkaa nimillä P1, P2, P3, jne.
        paikat = db.prepareStatement("INSERT INTO Paikat (paikka) VALUES (?)");
        for (int i = 0; i < 1000; i++) {          
            paikat.setString(1, "P" + Integer.toString(i));
            paikat.executeUpdate();
        }
        
        loppu = System.nanoTime();
        System.out.println("Lisää 1000 paikkaa: " + (loppu-alku)/1E6 + "ms");

        alku = System.nanoTime();
        
        // Tietokantaan lisätään tuhat asiakasta nimillä A1, A2, A3, jne.         
        asiakkaat = db.prepareStatement("INSERT INTO Asiakkaat (nimi) VALUES (?)");
        for (int i = 0; i < 1000; i++) {          
            asiakkaat.setString(1, "A" + Integer.toString(i));
            asiakkaat.executeUpdate();
        }
        loppu = System.nanoTime();
        System.out.println("Lisää 1000 asiakasta: " + (loppu-alku)/1E6 + "ms");

        alku = System.nanoTime();
        // Tietokantaan lisätään tuhat pakettia, jokaiselle jokin asiakas.
        paketit = db.prepareStatement("INSERT INTO Paketit "
                + "(koodi, asiakas_id) VALUES (?,?)");

        for (int i = 0; i < 1000; i++) {          
            int a = q.nextInt(1000);
            paketit.setString(1, "PK" + Integer.toString(i));
            paketit.setInt(2, a);
            paketit.executeUpdate();
        }
        loppu = System.nanoTime();
        System.out.println("Lisää 1000 pakettia, : " + (loppu-alku)/1E6 + "ms");

        alku = System.nanoTime();
        // Tietokantaan lisätään miljoona tapahtumaa, jokaiselle jokin paketti.
        tapahtumat = db.prepareStatement("INSERT INTO Tapahtumat "
                + "(paketti_id, paikka_id, aika, kuvaus) VALUES (?,?,?,?)");
        for (int i = 0; i < 1000000; i++) {    
            int a = q.nextInt(1000);
            int b = q.nextInt(1000);
            int c = q.nextInt(28) + 1;
            
            //satunnaistetaan päivä niin että formaatti on vvvv-kk-pp
            String cString = "";
            
            if (c < 10) {
                cString = "0" + Integer.toString(c);
            } else {
                cString = Integer.toString(c);
            }
            
            tapahtumat.setInt(1,a);
            tapahtumat.setInt(2,b);
            tapahtumat.setString(3,"2020-02-" + cString);
            tapahtumat.setString(4,"kuvaus" + Integer.toString(i));
            
            tapahtumat.executeUpdate();
        }
        
        db.commit();
        
        loppu = System.nanoTime();
        System.out.println("Lisää 1 000 000 tapahtumaa: " + (loppu-alku)/1E6 + "ms");

        
        
        alku = System.nanoTime();
        // Suoritetaan tuhat kyselyä, joista jokaisessa 
        // haetaan jonkin asiakkaan pakettien määrä.
        for (int i = 0; i < 1000; i++) {
            int f = q.nextInt(1000);
            String g = "A" + Integer.toString(f);

            hae = db.prepareStatement("SELECT id FROM Asiakkaat WHERE nimi = (?)");
            hae.setString(1,g);
            r = hae.executeQuery();

            if (r.next()) {
            p = db.prepareStatement("SELECT A.nimi, COUNT(P.id) FROM Asiakkaat A "
                    + "LEFT JOIN Paketit P ON P.asiakas_id = (?) "
                    + "AND A.id = P.asiakas_id;");
            hae.setInt(1,r.getInt("id"));
            r = hae.executeQuery();
            } else {
                continue;
            }
        }
        
        db.commit();
        
        loppu = System.nanoTime();
        System.out.println("Hae 1000 kertaa satunnaisen asiakkaan pakettien "
                + "määrä: " + (loppu-alku)/1E6 + "ms");
      
        alku = System.nanoTime();
        // Suoritetaan tuhat kyselyä, joista jokaisessa haetaan 
        // jonkin paketin tapahtumien määrä pakettikoodin perusteella.
        for (int i = 0; i < 1000; i++) {
            int f = q.nextInt(1000);
            String g = "PK" + Integer.toString(f);
            
            // varmistetaan, että pakettikoodi on kannassa
            hae = db.prepareStatement("SELECT koodi, id FROM Paketit "
                    + "WHERE koodi = (?)");
            hae.setString(1, g);
            r = hae.executeQuery();
            
            if (r.next()) {
                p = db.prepareStatement("SELECT P.koodi, COUNT(T.id) maara "
                        + "FROM Paketit P, Tapahtumat T WHERE T.paketti_id = (?) "
                        + "AND T.paketti_id = P.id");
                p.setInt(1, f);
                r = p.executeQuery();
            } else {
                continue;
                }
        }
        
        db.commit();
      
        loppu = System.nanoTime();
        System.out.println("Hae 1000 kertaa satunnaisen paketin "
                + "tapahtumien määrä: " + (loppu-alku)/1E6 + "ms");    
    }
    
}
