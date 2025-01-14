/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.gestionstock.views;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;

/**
 *
 * @author Berrada
 */
public class MenuWindow extends javax.swing.JFrame {

    /**
     * Creates new form Menu
     */
    // Instance unique de MenuWindow pour le singleton
    private static MenuWindow instance;

    public MenuWindow() {
        initComponents();
        this.setTitle("Menu");
        this.setLocationRelativeTo(null);
        this.pack();

        btnArticles.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Cache MenuWindow
                MenuWindow.getInstance().setVisible(false); // Masque la fenêtre Menu
                // Affiche ArticleWindow
                ArticleWindow articleWindow = ArticleWindow.getInstance();  // Utilise l'instance existante de ArticleWindow
                articleWindow.setVisible(true);
            }
        });
        
    }

    // Méthode getInstance pour obtenir l'instance unique de MenuWindow
    public static MenuWindow getInstance() {
        if (instance == null) {
            instance = new MenuWindow();
        }
        return instance;
    }

    // Affiche la fenêtre
    public void showWindow() {
        setVisible(true);
    }

    // Cache la fenêtre sans la fermer
    public void hideWindow() {
        setVisible(false);
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
        btnArticles = new javax.swing.JButton();
        btnBL = new javax.swing.JButton();
        btnFactures = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btnArticles.setText("Articles");
        btnArticles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnArticlesActionPerformed(evt);
            }
        });

        btnBL.setText("Bons de Livraison");
        btnBL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBLActionPerformed(evt);
            }
        });

        btnFactures.setText("Factures");
        btnFactures.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFacturesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(66, 66, 66)
                        .addComponent(btnArticles))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addComponent(btnBL))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(56, 56, 56)
                        .addComponent(btnFactures)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnArticles)
                .addGap(18, 18, 18)
                .addComponent(btnBL)
                .addGap(18, 18, 18)
                .addComponent(btnFactures)
                .addContainerGap(122, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(59, 59, 59))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnArticlesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnArticlesActionPerformed
   
    }//GEN-LAST:event_btnArticlesActionPerformed

    private void btnBLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBLActionPerformed
        // TODO add your handling code here:
        this.dispose();
        AddBLWindow addBL = new AddBLWindow(); 
        addBL.setVisible(true);
    }//GEN-LAST:event_btnBLActionPerformed

    private void btnFacturesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFacturesActionPerformed
        // TODO add your handling code here:
        this.dispose();
        AddFactWindow addFact = new AddFactWindow(); 
        addFact.setVisible(true);
    }//GEN-LAST:event_btnFacturesActionPerformed

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
            java.util.logging.Logger.getLogger(MenuWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MenuWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MenuWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MenuWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MenuWindow().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnArticles;
    private javax.swing.JButton btnBL;
    private javax.swing.JButton btnFactures;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
