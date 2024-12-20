/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.gestionstock.views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;

/**
 *
 * @author Berrada
 */
public class AddFactWindow extends javax.swing.JFrame {

    /**
     * Creates new form AddFactWindow
     */
   // public AddFactWindow() {
   //     initComponents();
   // }

     // Connexion à la base de données
    private Connection conn;

    // Liste des BL pour le JComboBox
    private ArrayList<String> listBL;

    public AddFactWindow() {
        
        initComponents();
        // Initialisation de la connexion à la base de données
        conn = getConnection();

        // Récupération de la liste des BL
        listBL = getBLList();
        
        initGUIComponents();
    }

    private void initGUIComponents() {
    // Remplir cbBL avec la liste des BL
    for (String bl : listBL) {
        cbBL.addItem(bl);
    }
    }
    
    // Méthode pour établir la connexion à la base de données
    private Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/gestionstock", "root", "");
        } catch (Exception e) {
            System.out.println("Erreur de connexion à la base de données : " + e.getMessage());
            return null;
        }
    }

    // Méthode pour récupérer la liste des BL
    private ArrayList<String> getBLList() {
        ArrayList<String> list = new ArrayList<>();
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT NumBL FROM T_Bon_livraison")) {
            while (rs.next()) {
                list.add(rs.getString("NumBL"));
            }
        } catch (SQLException e) {
            System.out.println("Erreur de récupération des BL : " + e.getMessage());
        }
        return list;
    }

    // Méthode pour générer les détails de la facture
    public void generateFactDetails(String numBL) {
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM T_Bon_livraison WHERE NumBL = '" + numBL + "'")) {
            if (rs.next()) {
                // Récupération des informations du client
                String client = rs.getString("IdClient");

                // Génération du numéro de facture
                String numFact = generateFactNumber();

                // Date de la facture (peut être personnalisée)
                String date = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());

                // Récupération des détails des articles
                StringBuilder details = new StringBuilder();
                try (Statement stmtDetails = conn.createStatement(); ResultSet rsDetails = stmtDetails.executeQuery("SELECT * FROM T_Detail_BL WHERE id_BL = '" + numBL + "'")) {
                    while (rsDetails.next()) {
                        details.append("Article : ").append(rsDetails.getString("id_Article")).append(", Quantité : ").append(rsDetails.getString("quantite")).append(", Prix Unitaire HT : ").append(rsDetails.getString("prix_unit_HT")).append("\n");
                    }
                }

                // Calcul des totaux
                double totalHT = calculateTotalHT(numBL);
                double totalTVA = calculateTotalTVA(numBL);
                double totalTTC = calculateTotalTTC(numBL);

                // Affichage des informations de la facture
                System.out.println("Numéro de Facture : " + numFact);
                System.out.println("Client : " + client);
                System.out.println("Date : " + date);
                System.out.println("Détails : \n" + details.toString());
                System.out.println("Total HT : " + totalHT);
                System.out.println("Total TVA : " + totalTVA);
                System.out.println("Total TTC : " + totalTTC);
            }
        } catch (SQLException ex) {
            System.out.println("Erreur de génération des détails de la facture : " + ex.getMessage());
        }
    }

    // Méthode pour générer le numéro de facture
    private String generateFactNumber() {
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT MAX(ID_Facture) FROM T_Facture")) {
            if (rs.next()) {
                return String.valueOf(rs.getInt(1) + 1);
            }
        } catch (SQLException ex) {
            System.out.println("Erreur de génération du numéro de facture : " + ex.getMessage());
        }
        return "Erreur";
    }

    // Méthode pour calculer le total HT
    private double calculateTotalHT(String numBL) {
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT SUM(sous_total_HT) FROM T_Detail_BL WHERE id_BL = '" + numBL + "'")) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException ex) {
            System.out.println("Erreur de calcul du total HT : " + ex.getMessage());
        }
        return 0;
    }

    // Méthode pour calculer le total TVA
    private double calculateTotalTVA(String numBL) {
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT SUM(montant_TVA) FROM T_Detail_BL WHERE id_BL = '" + numBL + "'")) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException ex) {
            System.out.println("Erreur de calcul du total TVA : " + ex.getMessage());
        }
        return 0;
    }

    // Méthode pour calculer le total TTC
    private double calculateTotalTTC(String numBL) {
        return calculateTotalHT(numBL) + calculateTotalTVA(numBL);
    }

   /* public static void main(String[] args) {
        AddFactWindow addFactWindow = new AddFactWindow();
        // Exemple d'utilisation : générer les détails de la facture pour un BL spécifique
        addFactWindow.generateFactDetails("BL001"); // Remplacez "BL001" par le numéro de BL que vous souhaitez utiliser
    }
   */
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelBL = new javax.swing.JPanel();
        lblSelectBL = new javax.swing.JLabel();
        cbBL = new javax.swing.JComboBox<>();
        btnGenerateFact = new javax.swing.JButton();
        panelFactDetails = new javax.swing.JPanel();
        lblFactNumber = new javax.swing.JLabel();
        tfFactNumber = new javax.swing.JTextField();
        lblClient = new javax.swing.JLabel();
        tfClient = new javax.swing.JTextField();
        lblDate = new javax.swing.JLabel();
        tfDate = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        taDetails = new javax.swing.JTextArea();
        lblTotalHT = new javax.swing.JLabel();
        tfTotalHT = new javax.swing.JTextField();
        lblTotalTVA = new javax.swing.JLabel();
        tfTotalTVA = new javax.swing.JTextField();
        lblTotalTTC = new javax.swing.JLabel();
        tfTotalTTC = new javax.swing.JTextField();
        panelButtons = new javax.swing.JPanel();
        btnModify = new javax.swing.JButton();
        btnValidate = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        lblSelectBL.setText("Sélectionner un BL :");

        cbBL.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        btnGenerateFact.setText("Générer Facture");

        javax.swing.GroupLayout panelBLLayout = new javax.swing.GroupLayout(panelBL);
        panelBL.setLayout(panelBLLayout);
        panelBLLayout.setHorizontalGroup(
            panelBLLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBLLayout.createSequentialGroup()
                .addGap(85, 85, 85)
                .addComponent(btnGenerateFact)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(panelBLLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(lblSelectBL, javax.swing.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbBL, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        panelBLLayout.setVerticalGroup(
            panelBLLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBLLayout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addGroup(panelBLLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSelectBL)
                    .addComponent(cbBL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(38, 38, 38)
                .addComponent(btnGenerateFact)
                .addContainerGap(56, Short.MAX_VALUE))
        );

        lblFactNumber.setText("Fact Number");

        tfFactNumber.setText("Fact Number");

        lblClient.setText("Client");

        tfClient.setText("Client");

        lblDate.setText("Date");

        tfDate.setText("Date");

        taDetails.setColumns(20);
        taDetails.setRows(5);
        jScrollPane1.setViewportView(taDetails);

        lblTotalHT.setText("Total HT");

        tfTotalHT.setText("Total HT");

        lblTotalTVA.setText("Total TVA");

        tfTotalTVA.setText("Total TVA");

        lblTotalTTC.setText("Total TTC");

        tfTotalTTC.setText("Total TTC");

        javax.swing.GroupLayout panelFactDetailsLayout = new javax.swing.GroupLayout(panelFactDetails);
        panelFactDetails.setLayout(panelFactDetailsLayout);
        panelFactDetailsLayout.setHorizontalGroup(
            panelFactDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFactDetailsLayout.createSequentialGroup()
                .addGap(73, 73, 73)
                .addGroup(panelFactDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(panelFactDetailsLayout.createSequentialGroup()
                        .addGroup(panelFactDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblDate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblClient, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblFactNumber, javax.swing.GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE))
                        .addGap(40, 40, 40)
                        .addGroup(panelFactDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(tfFactNumber, javax.swing.GroupLayout.DEFAULT_SIZE, 96, Short.MAX_VALUE)
                            .addComponent(tfClient)
                            .addComponent(tfDate)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelFactDetailsLayout.createSequentialGroup()
                        .addGroup(panelFactDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelFactDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(panelFactDetailsLayout.createSequentialGroup()
                                    .addComponent(lblTotalHT, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFactDetailsLayout.createSequentialGroup()
                                    .addComponent(lblTotalTVA, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(43, 43, 43)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelFactDetailsLayout.createSequentialGroup()
                                .addComponent(lblTotalTTC, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(43, 43, 43)))
                        .addGroup(panelFactDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(tfTotalHT)
                            .addComponent(tfTotalTVA, javax.swing.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
                            .addComponent(tfTotalTTC))))
                .addContainerGap(69, Short.MAX_VALUE))
        );
        panelFactDetailsLayout.setVerticalGroup(
            panelFactDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelFactDetailsLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(panelFactDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFactNumber)
                    .addComponent(tfFactNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelFactDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblClient)
                    .addComponent(tfClient, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelFactDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDate)
                    .addComponent(tfDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(panelFactDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTotalHT)
                    .addComponent(tfTotalHT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(panelFactDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTotalTVA)
                    .addComponent(tfTotalTVA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(panelFactDetailsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTotalTTC)
                    .addComponent(tfTotalTTC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(39, Short.MAX_VALUE))
        );

        btnModify.setText("Modifier");

        btnValidate.setText("Valider");

        javax.swing.GroupLayout panelButtonsLayout = new javax.swing.GroupLayout(panelButtons);
        panelButtons.setLayout(panelButtonsLayout);
        panelButtonsLayout.setHorizontalGroup(
            panelButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelButtonsLayout.createSequentialGroup()
                .addGap(89, 89, 89)
                .addGroup(panelButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnValidate)
                    .addComponent(btnModify))
                .addContainerGap(92, Short.MAX_VALUE))
        );
        panelButtonsLayout.setVerticalGroup(
            panelButtonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelButtonsLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(btnModify)
                .addGap(18, 18, 18)
                .addComponent(btnValidate)
                .addContainerGap(18, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(panelBL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(panelButtons, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panelFactDetails, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(38, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelFactDetails, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panelBL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(panelButtons, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(35, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(AddFactWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AddFactWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AddFactWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AddFactWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                 //AddFactWindow addFactWindow = new AddFactWindow();
                AddFactWindow addFactWindow = new AddFactWindow();
                addFactWindow.initGUIComponents(); // Initialisation des composants
                new AddFactWindow().setVisible(true);
                //addFactWindow.generateFactDetails("BL001");
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGenerateFact;
    private javax.swing.JButton btnModify;
    private javax.swing.JButton btnValidate;
    private javax.swing.JComboBox<String> cbBL;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblClient;
    private javax.swing.JLabel lblDate;
    private javax.swing.JLabel lblFactNumber;
    private javax.swing.JLabel lblSelectBL;
    private javax.swing.JLabel lblTotalHT;
    private javax.swing.JLabel lblTotalTTC;
    private javax.swing.JLabel lblTotalTVA;
    private javax.swing.JPanel panelBL;
    private javax.swing.JPanel panelButtons;
    private javax.swing.JPanel panelFactDetails;
    private javax.swing.JTextArea taDetails;
    private javax.swing.JTextField tfClient;
    private javax.swing.JTextField tfDate;
    private javax.swing.JTextField tfFactNumber;
    private javax.swing.JTextField tfTotalHT;
    private javax.swing.JTextField tfTotalTTC;
    private javax.swing.JTextField tfTotalTVA;
    // End of variables declaration//GEN-END:variables
}
