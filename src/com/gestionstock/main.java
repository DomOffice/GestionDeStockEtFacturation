/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Berrada
 */
package com.gestionstock;

import com.gestionstock.connection.ConnectionDB;
import com.gestionstock.views.MenuWindow;

public class main {
    public static void main(String[] args) {
         // Connexion à la base de données
        ConnectionDB.getConnection();

         // Afficher la fenêtre du menu
        java.awt.EventQueue.invokeLater(() -> {
            new MenuWindow().setVisible(true);
        });
      }
}
