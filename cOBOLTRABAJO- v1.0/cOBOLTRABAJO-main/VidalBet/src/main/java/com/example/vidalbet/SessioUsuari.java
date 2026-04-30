package com.example.vidalbet;
import java.util.ArrayList;
import java.util.List;
public class SessioUsuari {
    // Credencials d'accés
    public static String usuari = "";
    public static String password = "";

    // Dades Personals
    public static String nom = "";
    public static String cognoms = "";
    public static String pais = "";
    public static String comunitat = "";
    public static String cp = "";

    // Dades de Pagament
    public static String numeroTargeta = "";
    public static String titular = "";
    public static String venciment = "";
    public static String cvv = "";

    // Funció per saber si ha omplert la targeta
    public static boolean teTargeta() {
        return numeroTargeta != null && !numeroTargeta.trim().isEmpty() && !cvv.trim().isEmpty();
    }
    public static double saldo = 0.0; // Comença amb 0 euros
    public static double saldoBono = 0.0;
    public static boolean primerDepositRealitzat = false;
    public static List<String> misApuestas = new ArrayList<>();
}