import java.util.Scanner;

public class Kayttoliittyma {
    private Scanner lukija;
    private String syote;
    private String nimi;
    private String paikka;
    private String koodi;
    private String kuvaus;
    private String aika;
    private Pakettitietokanta p;
    private Pakettitietokanta t;
    
    public Kayttoliittyma(Scanner lukija, Pakettitietokanta p, Pakettitietokanta t) {
        this.lukija = lukija;
        this.p = p;
        this.t = t;
    }
    
    public void kaynnista() {
        while (true) {
            System.out.println("");
            System.out.println("**********************");
            System.out.println("Pakettitietokanta \n");
            System.out.println("Valitse toiminto:");
            System.out.println("[1] Alusta tietokanta");
            System.out.println("[2] Lisää paikka");
            System.out.println("[3] Lisää asiakas");
            System.out.println("[4] Lisää paketti asiakkaalle");
            System.out.println("[5] Lisää tapahtuma");
            System.out.println("[6] Näytä paketin tapahtumat");
            System.out.println("[7] Näytä asiakkaan paketit ja tapahtumien määrä");
            System.out.println("[8] Näytä tapahtumien määrä paikassa tiettynä päivänä");
            System.out.println("[9] Suorita tehokkuustesti");
            System.out.println("[x] Poistu");
            
            System.out.print("> ");
            
            syote = lukija.nextLine();
            
            if (syote.equals("x")) {
                break;
            }
            
            // Luo sovelluksen tarvitsemat taulut tyhjään tietokantaan (tätä toimintoa voidaan käyttää, kun tietokantaa ei ole vielä olemassa).
            if (syote.equals("1")) {
                try {
                p.alusta();
                } catch (Exception e) {
                System.out.println("Tietokanta on jo alustettu.");
                   }
            }


            // Lisää uusi paikka tietokantaan, kun annetaan paikan nimi.
            if (syote.equals("2")) {
            
                System.out.println("Anna paikan nimi: ");
                System.out.print("> ");

                paikka = lukija.nextLine();

                try {
                    p.lisaaPaikka(paikka);
                } catch (Exception e) {
                    System.out.println(e);
                    }
            }

            // Lisää uusi asiakas tietokantaan, kun annetaan asiakkaan nimi.
            if (syote.equals("3")) { 
                System.out.println("Anna asiakkaan nimi: ");
                System.out.print("> ");
                
                nimi = lukija.nextLine();

                
                try {
                p.lisaaAsiakas(nimi);
                } catch (Exception e) {
                    System.out.println(e);
                    }
            }
            
            // Lisää uusi paketti tietokantaan, kun annetaan paketin seurantakoodi ja asiakkaan nimi. Asiakkaan tulee olla valmiiksi tietokannassa.
            if (syote.equals("4")) {
                System.out.println("Anna paketin seurantakoodi: ");
                System.out.print("> ");
                
                koodi = lukija.nextLine();
                
                System.out.println("Anna asiakkaan nimi: ");
                System.out.print("> ");
                
                nimi = lukija.nextLine();
                
                try {
                p.lisaaPaketti(nimi,koodi);
                } catch (Exception e) {
                    System.out.println(e);
                    }
            }

            // Lisää uusi tapahtuma tietokantaan, kun annetaan paketin seurantakoodi, tapahtuman paikka sekä kuvaus. Paketin ja paikan tulee olla valmiiksi tietokannassa.
            if (syote.equals("5")) {
                System.out.println("Anna paketin seurantakoodi: ");
                System.out.print("> ");

                koodi = lukija.nextLine();
                
                System.out.println("Anna paikka: ");
                System.out.print("> ");

                paikka = lukija.nextLine();
                
                System.out.println("Kirjoita kuvaus: ");
                System.out.print("> ");

                kuvaus = lukija.nextLine();

                try {
                    p.lisaaTapahtuma(koodi, paikka, kuvaus);
                } catch (Exception e) {
                    System.out.println(e);
                    }
            }

            // Hae kaikki paketin tapahtumat seurantakoodin perusteella.
            if (syote.equals("6")) {
                System.out.println("Anna paketin seurantakoodi: ");
                System.out.print("> ");

                koodi = lukija.nextLine();
                
                try {
                    p.haeTapahtumat(koodi);
                } catch (Exception e) {
                    System.out.println(e);
                    }
            }
            // Hae kaikki asiakkaan paketit ja niihin liittyvien tapahtumien määrä.
            if (syote.equals("7")) { 
                System.out.println("Anna asiakkaan nimi: ");
                System.out.print("> ");
                
                nimi = lukija.nextLine();
            
            try {
                p.haeAsiakkaanPaketit(nimi);
            } catch (Exception e) {
                System.out.println(e);
                }
            }
            
            // Hae annetusta paikasta tapahtumien määrä tiettynä päivänä.
            if (syote.equals("8")) { 
                System.out.println("Anna paikka: ");
                System.out.print("> ");
                
                paikka = lukija.nextLine();
                
                System.out.println("Anna päivä muodossa VVVV-KK-PP: ");
                System.out.print("> ");
                
                aika = lukija.nextLine();
            
            try {
                p.haePaivanTapahtumat(paikka, aika);
            } catch (Exception e) {
                System.out.println(e);
                }
            }
            
            // Suorita tietokannan tehokkuustesti (tästä lisää alempana).
            if (syote.equals("9")) { 
                System.out.println("Tehokkuustesti: ");
                
                try {
                    t.alusta();
                } catch (Exception e) {
                    System.out.println(e);
                }
                
                System.out.println("Indeksoidaanko (k = kyllä)?");
                System.out.print("> ");
                
                boolean indeksi = false;
                
                syote = lukija.nextLine();
                
                if (syote.equals("k")) {
                    indeksi = true;
                    
                    try {
                        t.indeksoi();
                    } catch (Exception e) {
                        System.out.println("Indeksointiongelma " + e);
                    }
                }
               
                System.out.println("Testitulokset, indeksit = " + indeksi);
                
                try {
                    t.tehokkuusTesti();
                } catch (Exception e) {
                    System.out.println(e);
                }

            }
           
        }
    }
}
