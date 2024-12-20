/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.gestionstock.views;

/**
 *
 * @author Berrada
 */
import com.gestionstock.connection.ConnectionDB;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.TableRowSorter;

public class ArticleWindow extends javax.swing.JFrame {

    /**
     * Creates new form ArticleWindow
     */
// Instance unique d'ArticleWindow pour le singleton
    private static ArticleWindow instance;

    public ArticleWindow() {
        initComponents();
        this.setTitle("Articles");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);  // Empêche la fermeture complète
        this.setLocationRelativeTo(null);
        this.pack();

        // Ajouter un écouteur pour le champ de recherche
        txtRecherche.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filterArticles();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filterArticles();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filterArticles();
            }
        });

        // Action pour le bouton "Ajouter un Article"
        btnAddArticle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AddArticleWindow().setVisible(true);
                setState(JFrame.ICONIFIED);  // Réduit la fenêtre des articles
            }
        });

        // Action pour le bouton "Menu"
        btnMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Cache cette fenêtre
                dispose();  // Ferme cette fenêtre (ou setVisible(false) si tu veux juste la cacher)
                // Affiche MenuWindow
                MenuWindow.getInstance().showWindow();  // Réouvre MenuWindow
            }
        });

        // Initialisation du modèle de table avec les noms des colonnes
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"Désignation", "TVA", "Fournisseur", "Prix d'achat HT"}, 0
        );
        tblArticles.setModel(model);

        // Ajouter un WindowListener pour gérer la fermeture de la fenêtre
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Lorsque la fenêtre est fermée, réouvrir MenuWindow
                MenuWindow.getInstance().setVisible(true);
                setVisible(false);  // Masquer ArticleWindow sans la fermer
            }
        });

        // Charger les articles au démarrage
        loadArticles();
    }

    // Méthode getInstance pour obtenir l'instance unique d'ArticleWindow
    public static ArticleWindow getInstance() {
        if (instance == null) {
            instance = new ArticleWindow();
        }
        return instance;
    }

    // Méthode pour charger tous les articles
    private void loadArticles() {
        String query = "SELECT a.designation, a.TVA, f.nom_fournisseur, af.prix_achat_HT "
                + "FROM T_Articles a "
                + "LEFT JOIN T_Article_Fournisseur af ON a.id_Article = af.id_Article "
                + "LEFT JOIN T_Fournisseur f ON af.ID_Fournisseur = f.ID_Fournisseur";
        Connection conn = null; // Déclare la connexion ici pour gérer manuellement
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            // Ouvre la connexion
            conn = ConnectionDB.getConnection();

            // Vérifier si la connexion est fermée avant d'exécuter la requête
            if (conn.isClosed()) {
                System.err.println("La connexion à la base de données est fermée.");
                return;
            }

            stmt = conn.prepareStatement(query);
            rs = stmt.executeQuery();

            DefaultTableModel model = (DefaultTableModel) tblArticles.getModel();
            model.setRowCount(0);  // Vide le tableau

            while (rs.next()) {
                String designation = rs.getString("designation");
                double tva = rs.getDouble("TVA");
                String fournisseur = rs.getString("nom_fournisseur");
                double prixAchatHT = rs.getDouble("prix_achat_HT");
                model.addRow(new Object[]{designation, tva, fournisseur, prixAchatHT});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des articles", "Erreur", JOptionPane.ERROR_MESSAGE);
        } finally {
            // Ferme les ressources manuellement
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
    }

    // Méthode pour filtrer les articles selon la saisie dans txtRecherche
    private void filterArticles() {
        String searchText = txtRecherche.getText().toLowerCase();
        DefaultTableModel model = (DefaultTableModel) tblArticles.getModel();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        tblArticles.setRowSorter(sorter);
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        txtRecherche = new javax.swing.JTextField();
        tblArticles2 = new javax.swing.JScrollPane();
        tblArticles = new javax.swing.JTable();
        btnAddArticle = new javax.swing.JButton();
        btnMenu = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        tblArticles.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tblArticles2.setViewportView(tblArticles);

        btnAddArticle.setText("Ajouter");

        btnMenu.setText("Menu");

        jLabel1.setText("Recherche");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(tblArticles2, javax.swing.GroupLayout.PREFERRED_SIZE, 283, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtRecherche, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnMenu, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnAddArticle, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(54, 54, 54))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtRecherche, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(tblArticles2, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnAddArticle)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnMenu)))
                .addContainerGap(28, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 380, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
            java.util.logging.Logger.getLogger(ArticleWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ArticleWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ArticleWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ArticleWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ArticleWindow().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddArticle;
    private javax.swing.JButton btnMenu;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTable tblArticles;
    private javax.swing.JScrollPane tblArticles2;
    private javax.swing.JTextField txtRecherche;
    // End of variables declaration//GEN-END:variables
}
