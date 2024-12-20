/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.gestionstock.views;

import com.gestionstock.connection.ConnectionDB;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author Berrada
 */
public class AddArticleWindow extends javax.swing.JFrame {

    /**
     * Creates new form AddArticleWindow
     */
    public AddArticleWindow() {
        initComponents();
        this.setTitle("Ajout d'Article");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);  // Empêche la fermeture complète
        this.setLocationRelativeTo(null);
        this.pack();

        initTextFieldListeners();  // Ajoute les écouteurs de modification aux champs de texte

        // Charger les données des catégories et fournisseurs
        loadCategories();
        loadFournisseurs();
        /*
        // Calcul des prix de vente
        txtAchatHT.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                calculatePrices();
            }
        });

        txtMarge.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                calculatePrices();
            }
        });

        txtTVA.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                calculatePrices();
            }
        });
         */
        // Ajouter l'article à la base de données
        btnAddArticle.addActionListener(e -> addArticleToDB());

        // Annuler et vider les champs
        btnCancel.addActionListener(e -> clearFields());

        // Retourner à la fenêtre des articles
        btnArticle.addActionListener(e -> {
            dispose();  // Ferme cette fenêtre
            ArticleWindow.getInstance().setVisible(true);
        });

        // Retourner au menu
        btnMenu.addActionListener(e -> {
            MenuWindow.getInstance().setVisible(true);
            setVisible(false);
        });
    }

    // Charger les catégories depuis la base de données
    private void loadCategories() {
        String query = "SELECT nom_categorie FROM T_Categorie";
        try (Connection conn = ConnectionDB.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                cbCategorie.addItem(rs.getString("nom_categorie"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des catégories", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Méthode pour initialiser les écouteurs de texte
    private void initTextFieldListeners() {
        DocumentListener updateListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updatePrices();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updatePrices();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updatePrices();
            }
        };

        // Ajout de l'écouteur de modification aux champs nécessaires
        txtAchatHT.getDocument().addDocumentListener(updateListener);
        txtMarge.getDocument().addDocumentListener(updateListener);
        txtTVA.getDocument().addDocumentListener(updateListener);
    }

    // Charger les fournisseurs depuis la base de données
    private void loadFournisseurs() {
        String query = "SELECT nom_fournisseur FROM T_Fournisseur";
        try (Connection conn = ConnectionDB.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                cbFournisseur.addItem(rs.getString("nom_fournisseur"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des fournisseurs", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Calculer les prix de vente HT et TTC
    /*private void calculatePrices() {
        try {
            double prixAchatHT = Double.parseDouble(txtAchatHT.getText());
            double marge = Double.parseDouble(txtMarge.getText()) / 100;
            double tva = Double.parseDouble(txtTVA.getText()) / 100;

            double pvHT = prixAchatHT * (1 + marge);
            double pvTTC = pvHT * (1 + tva);

            lblPVHT.setText(String.format("%.2f", pvHT));
            lblPVTTC.setText(String.format("%.2f", pvTTC));
        } catch (NumberFormatException e) {
            // Si les champs sont vides ou incorrects, on ne fait rien
        }
    }
     */
    // Méthode pour mettre à jour les prix
    private void updatePrices() {
        try {
            double prixAchatHT = Double.parseDouble(txtAchatHT.getText());
            double marge = Double.parseDouble(txtMarge.getText()) / 100;
            double tva = Double.parseDouble(txtTVA.getText()) / 100;

            // Calcul du prix de vente HT et TTC
            double prixVenteHT = prixAchatHT * (1 + marge);
            double prixVenteTTC = prixVenteHT * (1 + tva);

            // Mise à jour des labels
            lblPVHT.setText(String.format("%.2f", prixVenteHT));
            lblPVTTC.setText(String.format("%.2f", prixVenteTTC));

        } catch (NumberFormatException e) {
            // Si la saisie n'est pas un nombre, laisser les labels vides ou gérer l'erreur ici
            lblPVHT.setText("N/A");
            lblPVTTC.setText("N/A");
        }
    }

    // Ajouter l'article à la base de données
    private void addArticleToDB() {
        String designation = txtDesignation.getText();
        String categorie = (String) cbCategorie.getSelectedItem();
        double tva = Double.parseDouble(txtTVA.getText()) / 100;
        double marge = Double.parseDouble(txtMarge.getText()) / 100;
        double prixAchatHT = Double.parseDouble(txtAchatHT.getText());

        // Calculs des prix de vente
        double prixVenteHT = prixAchatHT * (1 + marge);
        double prixVenteTTC = prixVenteHT * (1 + tva);

        // Récupérer les IDs de la catégorie et du fournisseur
        int idCategorie = getCategorieIdFromName(categorie);
        String fournisseur = (String) cbFournisseur.getSelectedItem();
        int idFournisseur = getFournisseurIdFromName(fournisseur);

        // Requête pour insérer l'article dans la table T_Articles
        String query = "INSERT INTO T_Articles (designation, id_Categorie, TVA, Marge, Prix_Vente_HT, Prix_Vente_TTC) VALUES (?, ?, ?, ?, ?, ?)";
        String queryFournisseur = "INSERT INTO T_Article_Fournisseur (id_Article, ID_Fournisseur, prix_achat_HT) VALUES (?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmtArticle = null;
        PreparedStatement stmtFournisseur = null;
        ResultSet rs = null;

        try {
            conn = ConnectionDB.getConnection();
            conn.setAutoCommit(false);  // Utilisation des transactions pour éviter des incohérences

            // Insertion dans T_Articles
            stmtArticle = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);  // Récupérer l'ID généré
            stmtArticle.setString(1, designation);
            stmtArticle.setInt(2, idCategorie);
            stmtArticle.setDouble(3, tva);
            stmtArticle.setDouble(4, marge);
            stmtArticle.setDouble(5, prixVenteHT);
            stmtArticle.setDouble(6, prixVenteTTC);
            stmtArticle.executeUpdate();

            // Récupérer l'ID généré de l'article inséré
            rs = stmtArticle.getGeneratedKeys();
            int idArticle = -1;
            if (rs.next()) {
                idArticle = rs.getInt(1);  // ID généré
            }

            // Insertion dans T_Article_Fournisseur
            stmtFournisseur = conn.prepareStatement(queryFournisseur);
            stmtFournisseur.setInt(1, idArticle);  // Utiliser l'ID de l'article inséré
            stmtFournisseur.setInt(2, idFournisseur);
            stmtFournisseur.setDouble(3, prixAchatHT);
            stmtFournisseur.executeUpdate();

            // Commit des transactions
            conn.commit();

            JOptionPane.showMessageDialog(this, "Article ajouté avec succès", "Succès", JOptionPane.INFORMATION_MESSAGE);

            /*
            // Fermer la fenêtre AddArticleWindow après ajout
            dispose();
            // Réafficher la fenêtre des articles
            ArticleWindow.getInstance().setVisible(true);
             */

            // Vider les champs
            clearFields();
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();  // Annuler les transactions en cas d'erreur
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout de l'article", "Erreur", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmtArticle != null) {
                    stmtArticle.close();
                }
                if (stmtFournisseur != null) {
                    stmtFournisseur.close();
                }
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Récupérer l'ID de la catégorie en fonction du nom
    private int getCategorieIdFromName(String categorieName) {
        String query = "SELECT id_Categorie FROM T_Categorie WHERE nom_categorie = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int idCategorie = -1; // Valeur par défaut si non trouvé

        try {
            conn = ConnectionDB.getConnection();
            stmt = conn.prepareStatement(query);
            stmt.setString(1, categorieName);  // Remplacer par le nom de la catégorie
            rs = stmt.executeQuery();

            if (rs.next()) {
                idCategorie = rs.getInt("id_Categorie");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return idCategorie;
    }

// Récupérer l'ID du fournisseur en fonction du nom
    private int getFournisseurIdFromName(String fournisseurName) {
        String query = "SELECT ID_Fournisseur FROM T_Fournisseur WHERE nom_fournisseur = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int idFournisseur = -1; // Valeur par défaut si non trouvé

        try {
            conn = ConnectionDB.getConnection();
            stmt = conn.prepareStatement(query);
            stmt.setString(1, fournisseurName);  // Remplacer par le nom du fournisseur
            rs = stmt.executeQuery();

            if (rs.next()) {
                idFournisseur = rs.getInt("ID_Fournisseur");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return idFournisseur;
    }

    // Vider les champs du formulaire
    private void clearFields() {
        txtDesignation.setText("");
        txtTVA.setText("");
        txtAchatHT.setText("");
        txtMarge.setText("");
        lblPVHT.setText("N/A");
        lblPVTTC.setText("N/A");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        btnAddArticle = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        btnArticle = new javax.swing.JButton();
        btnMenu = new javax.swing.JButton();
        cbCategorie = new javax.swing.JComboBox<>();
        txtTVA = new javax.swing.JTextField();
        txtDesignation = new javax.swing.JTextField();
        cbFournisseur = new javax.swing.JComboBox<>();
        txtAchatHT = new javax.swing.JTextField();
        txtMarge = new javax.swing.JTextField();
        lblPVHT = new javax.swing.JLabel();
        lblPVTTC = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Désignation");

        jLabel2.setText("Catégorie");

        jLabel3.setText("TVA");

        jLabel4.setText("Marge");

        jLabel5.setText("Fournisseur");

        jLabel6.setText("Prix Achat HT");

        jLabel7.setText("Prix Vente HT");

        jLabel8.setText("Prix Vente TTC");

        btnAddArticle.setText("Ajouter");
        btnAddArticle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddArticleActionPerformed(evt);
            }
        });

        btnCancel.setText("Annuler");

        btnArticle.setText("Articles");
        btnArticle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnArticleActionPerformed(evt);
            }
        });

        btnMenu.setText("Menu");

        lblPVHT.setText("PV HT");

        lblPVTTC.setText("PV TTC");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnAddArticle)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 79, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 87, Short.MAX_VALUE)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnCancel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnArticle)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnMenu))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(lblPVTTC, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblPVHT, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtMarge, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(txtAchatHT, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(cbFournisseur, javax.swing.GroupLayout.Alignment.LEADING, 0, 118, Short.MAX_VALUE)
                        .addComponent(txtDesignation, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(cbCategorie, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtTVA)))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtDesignation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(cbCategorie, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtTVA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(cbFournisseur, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtAchatHT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtMarge, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(lblPVHT))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(lblPVTTC))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAddArticle)
                    .addComponent(btnCancel)
                    .addComponent(btnArticle)
                    .addComponent(btnMenu))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnArticleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnArticleActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnArticleActionPerformed

    private void btnAddArticleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddArticleActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnAddArticleActionPerformed

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
            java.util.logging.Logger.getLogger(AddArticleWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AddArticleWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AddArticleWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AddArticleWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AddArticleWindow().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddArticle;
    private javax.swing.JButton btnArticle;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnMenu;
    private javax.swing.JComboBox<String> cbCategorie;
    private javax.swing.JComboBox<String> cbFournisseur;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel lblPVHT;
    private javax.swing.JLabel lblPVTTC;
    private javax.swing.JTextField txtAchatHT;
    private javax.swing.JTextField txtDesignation;
    private javax.swing.JTextField txtMarge;
    private javax.swing.JTextField txtTVA;
    // End of variables declaration//GEN-END:variables
}
