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
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddBLWindow extends javax.swing.JFrame {

    /**
     * Creates new form AddBLWindow
     */
    private DefaultTableModel recapModel;
    private double totalHT = 0, totalTVA = 0, totalTTC = 0;

    public AddBLWindow() {
        initComponents();
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);  // Empêche la fermeture complète
        this.setLocationRelativeTo(null);
        this.pack();

        initializeBLNumber();
        populateClientComboBox();
        populateArticleComboBox();
        addEventListeners();

        recapModel = new DefaultTableModel(new Object[]{"Désignation", "Quantité", "Prix Unit. HT", "Sous Total HT", "Montant TVA"}, 0);
        tblRecapBL.setModel(recapModel);

    }

    private void initializeBLNumber() {
        String query = "SELECT MAX(id_BL) FROM t_bon_livraison";
        try (Connection conn = ConnectionDB.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            int nextNumBL = rs.next() ? rs.getInt(1) + 1 : 1;
            lblNumBL.setText(String.valueOf(nextNumBL));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void populateClientComboBox() {
        String query = "SELECT nom_client FROM T_Client";
        try (Connection conn = ConnectionDB.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                cbNomClient.addItem(rs.getString("nom_client"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void populateArticleComboBox() {
        String query = "SELECT designation FROM T_Articles";
        try (Connection conn = ConnectionDB.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                cbArticle.addItem(rs.getString("designation"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addEventListeners() {
        btnAddArticle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addArticleToRecapTable();
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tblRecapBL.getSelectedRow();
                if (selectedRow != -1) {
                    recapModel.removeRow(selectedRow);
                    updateTotals();
                }
            }
        });

        btnAddBL.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveBLToDatabase();
            }
        });
    }

    private void addArticleToRecapTable() {
    String articleName = (String) cbArticle.getSelectedItem();
    double quantite;

    try {
        quantite = Double.parseDouble(txtQuantite.getText());
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(null, "Quantité invalide, veuillez entrer un nombre.", "Erreur", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Vérifie si l'article existe déjà dans le tableau
    for (int i = 0; i < recapModel.getRowCount(); i++) {
        String existingArticleName = (String) recapModel.getValueAt(i, 0);
        if (existingArticleName.equals(articleName)) {
            JOptionPane.showMessageDialog(null, "Cet article a déjà été ajouté.", "Erreur", JOptionPane.WARNING_MESSAGE);
            return;  // Sort de la méthode si l'article est déjà présent
        }
    }

    double prixVenteHT = getArticlePriceFromDB(articleName);
    if (prixVenteHT == -1) {
        return;
    }

    double sousTotalHT = quantite * prixVenteHT;
    double montantTVA = sousTotalHT * 0.2;  // TVA à 20%
    recapModel.addRow(new Object[]{articleName, quantite, prixVenteHT, sousTotalHT, montantTVA});

    updateTotals();
    txtQuantite.setText("");
}

    private double getArticlePriceFromDB(String articleName) {
        double prixVenteHT = -1.0;
        String query = "SELECT prix_Vente_HT FROM T_Articles WHERE designation = ?";

        try (Connection conn = ConnectionDB.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, articleName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                prixVenteHT = rs.getDouble("prix_Vente_HT");
            } else {
                JOptionPane.showMessageDialog(null, "Article non trouvé dans la base de données.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur lors de la récupération du prix de l'article.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        return prixVenteHT;
    }

    private void updateTotals() {
        totalHT = 0;
        totalTVA = 0;

        for (int i = 0; i < recapModel.getRowCount(); i++) {
            totalHT += (double) recapModel.getValueAt(i, 3);
            totalTVA += (double) recapModel.getValueAt(i, 4);
        }
        totalTTC = totalHT + totalTVA;

        //lblTotalHT.setText(String.format("%.2f", totalHT));
        //lblTotalTVA.setText(String.format("%.2f", totalTVA));
        //lblTotalTTC.setText(String.format("%.2f", totalTTC));
    }

    private void saveBLToDatabase() {
        try (Connection conn = ConnectionDB.getConnection()) {
            conn.setAutoCommit(false);  // Démarrer une transaction

            // Format de date attendu par la BDD
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
            java.sql.Date sqlDate = null;
            try {
                Date parsedDate = inputFormat.parse(txtDateBL.getText());
                sqlDate = new java.sql.Date(parsedDate.getTime());
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(this, "Date invalide. Veuillez saisir au format jj/mm/aaaa.", "Erreur", JOptionPane.ERROR_MESSAGE);
                conn.rollback();  // Annuler la transaction en cas d'erreur
                return;
            }

            // Récupérer l'ID client à partir du nom dans la même connexion
            String clientName = cbNomClient.getSelectedItem().toString();
            int clientId = getClientIdFromName(conn, clientName);

            if (clientId == -1) {
                JOptionPane.showMessageDialog(this, "Client introuvable dans la base de données.", "Erreur", JOptionPane.ERROR_MESSAGE);
                conn.rollback();
                return;
            }

            String queryBL = "INSERT INTO T_Bon_livraison (numBL, dateBL, id_client, total_HT, total_TVA, total_TTC, etat_bl) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmtBL = conn.prepareStatement(queryBL, Statement.RETURN_GENERATED_KEYS)) {
                pstmtBL.setString(1, lblNumBL.getText());
                pstmtBL.setDate(2, sqlDate);
                pstmtBL.setInt(3, clientId);  // Utiliser l'ID du client récupéré
                pstmtBL.setDouble(4, totalHT);
                pstmtBL.setDouble(5, totalTVA);
                pstmtBL.setDouble(6, totalTTC);
                pstmtBL.setString(7, cbEtat_bl.getSelectedItem().toString());
                pstmtBL.executeUpdate();

                try (ResultSet rs = pstmtBL.getGeneratedKeys()) {
                    int blId = rs.next() ? rs.getInt(1) : -1;

                    String queryDetail = "INSERT INTO T_Detail_BL (id_BL, id_Article, quantite, prix_unit_HT, sous_total_HT, montant_TVA) VALUES (?, ?, ?, ?, ?, ?)";
                    for (int i = 0; i < recapModel.getRowCount(); i++) {
                        try (PreparedStatement pstmtDetail = conn.prepareStatement(queryDetail)) {
                            pstmtDetail.setInt(1, blId);
                            pstmtDetail.setInt(2, getArticleIdFromName(conn, (String) recapModel.getValueAt(i, 0)));
                            pstmtDetail.setInt(3, ((Double) recapModel.getValueAt(i, 1)).intValue());
                            pstmtDetail.setDouble(4, (double) recapModel.getValueAt(i, 2));
                            pstmtDetail.setDouble(5, (double) recapModel.getValueAt(i, 3));
                            pstmtDetail.setDouble(6, (double) recapModel.getValueAt(i, 4));
                            pstmtDetail.executeUpdate();
                        }
                    }
                }
                conn.commit();  // Valider la transaction si tout est OK
                JOptionPane.showMessageDialog(this, "BL ajouté avec succès !");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de l'enregistrement dans la base de données.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int getClientIdFromName(Connection conn, String clientName) {
        String query = "SELECT ID_Client FROM T_Client WHERE nom_client = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, clientName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("ID_Client");
            } else {
                System.out.println("Client non trouvé pour le nom : " + clientName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur lors de la récupération de l'ID client.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        return -1;
    }

    private int getClientIdFromName(String clientName) {
        String query = "SELECT ID_Client FROM T_Client WHERE nom_client = ?";
        try (Connection conn = ConnectionDB.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, clientName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("ID_Client");
            } else {
                System.out.println("Client non trouvé pour le nom : " + clientName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur lors de la récupération de l'ID client.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        return -1;
    }

    private int getArticleIdFromName(Connection conn, String articleName) {
        String query = "SELECT id_Article FROM T_Articles WHERE designation = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, articleName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_Article");
            } else {
                System.out.println("Article non trouvé pour le nom : " + articleName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erreur lors de la récupération de l'ID de l'article.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
        return -1;
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
        jLabel1 = new javax.swing.JLabel();
        lblNumBL = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtDateBL = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        cbNomClient = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        txtQuantite = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        cbArticle = new javax.swing.JComboBox<>();
        cbEtat_bl = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblRecapBL = new javax.swing.JTable();
        btnAddArticle = new javax.swing.JButton();
        btnAddBL = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnMenu = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Numéro BL");

        lblNumBL.setText("num BL");

        jLabel2.setText("Date BL");

        jLabel3.setText("Client");

        jLabel4.setText("Quantité");

        jLabel5.setText("Article");

        cbEtat_bl.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel6.setText("Etat BL");

        tblRecapBL.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblRecapBL);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblNumBL, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 64, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbNomClient, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDateBL, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(80, 80, 80))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 464, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 8, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbArticle, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbEtat_bl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtQuantite, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(lblNumBL))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtDateBL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(cbNomClient, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(cbArticle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtQuantite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbEtat_bl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 344, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnAddArticle.setText("Add Article");

        btnAddBL.setText("Add BL");

        btnDelete.setText("Delete");

        btnMenu.setText("Menu");
        btnMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMenuActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnAddArticle)
                    .addComponent(btnAddBL)
                    .addComponent(btnDelete)
                    .addComponent(btnMenu))
                .addContainerGap(38, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(104, 104, 104)
                .addComponent(btnAddArticle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnAddBL)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnDelete)
                .addGap(18, 18, 18)
                .addComponent(btnMenu)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMenuActionPerformed
        // TODO add your handling code here:
        MenuWindow.getInstance().setVisible(true);
        setVisible(false);
    }//GEN-LAST:event_btnMenuActionPerformed

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
            java.util.logging.Logger.getLogger(AddBLWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AddBLWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AddBLWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AddBLWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AddBLWindow().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddArticle;
    private javax.swing.JButton btnAddBL;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnMenu;
    private javax.swing.JComboBox<String> cbArticle;
    private javax.swing.JComboBox<String> cbEtat_bl;
    private javax.swing.JComboBox<String> cbNomClient;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblNumBL;
    private javax.swing.JTable tblRecapBL;
    private javax.swing.JTextField txtDateBL;
    private javax.swing.JTextField txtQuantite;
    // End of variables declaration//GEN-END:variables
}
