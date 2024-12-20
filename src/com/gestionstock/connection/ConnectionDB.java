/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gestionstock.connection;

/**
 *
 * @author Berrada
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class ConnectionDB {

    private static Connection connection = null;

    // Méthode pour établir la connexion
    public static Connection getConnection() {
    try {
        // Si la connexion est null ou fermée, crée une nouvelle connexion
        if (connection == null || connection.isClosed()) {
            // Informations de connexion
            String url = "jdbc:mysql://localhost:3306/gestionstock";
            String user = "root";  // Remplace "root" par ton nom d'utilisateur MySQL
            String password = "";  // Remplace "" par ton mot de passe MySQL

            // Connexion à la base de données
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connexion à la BDD établie avec succès");

            // Vérification et création des tables
            verifyAndCreateTables();
        }
    } catch (SQLException e) {
        System.err.println("Erreur de connexion : " + e.getMessage());
    }
    return connection;
}


    // Méthode pour vérifier l'existence des tables et les créer si nécessaire
    private static void verifyAndCreateTables() {
        try (Statement stmt = connection.createStatement()) {

            // Vérification et création de chaque table
            createTableIfNotExists(stmt, "T_Articles",
                    "CREATE TABLE T_Articles ("
                    + "idArticle INT PRIMARY KEY AUTO_INCREMENT, "
                    + "designation VARCHAR(100) NOT NULL, "
                    + "idCategorie INT, "
                    + "TVA DECIMAL(5,2), "
                    + "Marge DECIMAL(5,2), "
                    + "Prix_Vente_HT DECIMAL(10,2), "
                    + "Prix_Vente_TTC DECIMAL(10,2))");

            createTableIfNotExists(stmt, "T_Bon_livraison",
                    "CREATE TABLE T_Bon_livraison ("
                    + "idBL INT PRIMARY KEY AUTO_INCREMENT, "
                    + "NumBL VARCHAR(50), "
                    + "DateBL DATE, "
                    + "IdClient INT, "
                    + "Total_HT DECIMAL(10,2), "
                    + "Total_TVA DECIMAL(10,2), "
                    + "Total_TTC DECIMAL(10,2), "
                    + "etat_bl VARCHAR(20))");

            createTableIfNotExists(stmt, "T_Categorie",
                    "CREATE TABLE T_Categorie ("
                    + "id_categorie INT PRIMARY KEY AUTO_INCREMENT, "
                    + "nom_categorie VARCHAR(100))");

            createTableIfNotExists(stmt, "T_Client",
                    "CREATE TABLE T_Client ("
                    + "id_client INT PRIMARY KEY AUTO_INCREMENT, "
                    + "nom_client VARCHAR(100), "
                    + "ICE VARCHAR(15), "
                    + "adresse VARCHAR(255), "
                    + "ville VARCHAR(50), "
                    + "tel VARCHAR(15), "
                    + "mail VARCHAR(100))");

             createTableIfNotExists(stmt, "T_Detail_BL",
                "CREATE TABLE T_Detail_BL ("
                + "ID_Detail_BL INT PRIMARY KEY AUTO_INCREMENT, "
                + "id_BL INT, "
                + "id_Article INT, "
                + "quantite INT, "
                + "prix_unit_HT DECIMAL(10,2), "
                + "sous_total_HT DECIMAL(10,2), "
                + "montant_TVA DECIMAL(10,2), "
                + "sous_total_TVA DECIMAL(10,2), "
                + "FOREIGN KEY (id_BL) REFERENCES T_Bon_livraison(idBL), "
                + "FOREIGN KEY (id_Article) REFERENCES T_Articles(idArticle))");

        // Table T_detail_facture
        createTableIfNotExists(stmt, "T_detail_facture",
                "CREATE TABLE T_detail_facture ("
                + "ID_Detail_Facture INT PRIMARY KEY AUTO_INCREMENT, "
                + "id_facture INT, "
                + "id_article INT, "
                + "quantité INT, "
                + "prix_unit_HT DECIMAL(10,2), "
                + "sous_total_HT DECIMAL(10,2), "
                + "montant_TVA DECIMAL(10,2), "
                + "sous_total_TVA DECIMAL(10,2), "
                + "FOREIGN KEY (id_facture) REFERENCES T_Facture(ID_Facture), "
                + "FOREIGN KEY (id_article) REFERENCES T_Articles(idArticle))");

        // Table T_Facture
        createTableIfNotExists(stmt, "T_Facture",
                "CREATE TABLE T_Facture ("
                + "ID_Facture INT PRIMARY KEY AUTO_INCREMENT, "
                + "num_Facture VARCHAR(50), "
                + "date_facture DATE, "
                + "Id_client INT, "
                + "total_HT DECIMAL(10,2), "
                + "total_TVA DECIMAL(10,2), "
                + "Total_TTC DECIMAL(10,2), "
                + "etat_facture VARCHAR(20), "
                + "FOREIGN KEY (Id_client) REFERENCES T_Client(id_client))");

        // Table T_Facture_BL
        createTableIfNotExists(stmt, "T_Facture_BL",
                "CREATE TABLE T_Facture_BL ("
                + "ID_Facture_BL INT PRIMARY KEY AUTO_INCREMENT, "
                + "ID_Facture INT, "
                + "ID_BL INT, "
                + "FOREIGN KEY (ID_Facture) REFERENCES T_Facture(ID_Facture), "
                + "FOREIGN KEY (ID_BL) REFERENCES T_Bon_livraison(idBL))");

        // Table T_Fournisseur
        createTableIfNotExists(stmt, "T_Fournisseur",
                "CREATE TABLE T_Fournisseur ("
                + "ID_Fournisseur INT PRIMARY KEY AUTO_INCREMENT, "
                + "nom_fournisseur VARCHAR(100), "
                + "contact VARCHAR(50), "
                + "tel VARCHAR(15))");

        // Table T_Article_fournisseur
        createTableIfNotExists(stmt, "T_Article_fournisseur",
                "CREATE TABLE T_Article_fournisseur ("
                + "ID_Article_fournisseur INT PRIMARY KEY AUTO_INCREMENT, "
                + "id_Article INT, "
                + "ID_Fournisseur INT, "
                + "prix_achat_HT DECIMAL(10,2), "
                + "FOREIGN KEY (id_Article) REFERENCES T_Articles(idArticle), "
                + "FOREIGN KEY (ID_Fournisseur) REFERENCES T_Fournisseur(ID_Fournisseur))");

    } catch (SQLException e) {
        System.err.println("Erreur lors de la vérification/création des tables : " + e.getMessage());
    }
    }

    // Méthode pour créer une table si elle n'existe pas
    private static void createTableIfNotExists(Statement stmt, String tableName, String createSQL) {
        try {
            ResultSet rs = stmt.executeQuery("SHOW TABLES LIKE '" + tableName + "'");
            if (!rs.next()) {
                stmt.execute(createSQL);
                System.out.println("Table " + tableName + " créée.");
            } else {
                System.out.println("Table " + tableName + " existe déjà.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création de la table " + tableName + " : " + e.getMessage());
        }
    }
}

