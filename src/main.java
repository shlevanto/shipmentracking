/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.sql.*;
import java.util.Scanner;

public class main {

    public static void main(String[] args) throws SQLException {
        
        Scanner lukija = new Scanner(System.in);
        Pakettitietokanta p = new Pakettitietokanta("pakettitietokanta");
        
        // Tehokkuustestiä varten erillinen tietokanta
        Pakettitietokanta t = new Pakettitietokanta("tehokkuustesti");
        
        Kayttoliittyma k = new Kayttoliittyma(lukija,p,t);
        
        k.kaynnista();
    }
}
    
    

