package org.example;

import org.example.model.Produit;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Persistence;
import java.math.BigDecimal;
import java.util.List;

public class App {
    public static void main(String[] args) {
        // Création de l'EntityManagerFactory
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hibernate-demo");

        // Insertion de produits
        insererProduits(emf);

        // Lecture des produits
        lireProduits(emf);

        updateProduit(emf,2L,"gaming",new BigDecimal("299.99"));

        deleteProduitWithReference(emf, 2L);

        // Fermeture de l'EntityManagerFactory
        emf.close();
    }

    private static void insererProduits(EntityManagerFactory emf) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            // Création de quelques produits
            Produit p1 = new Produit("Laptop", new BigDecimal("999.99"));
            Produit p2 = new Produit("Smartphone", new BigDecimal("499.99"));
            Produit p3 = new Produit("Tablette", new BigDecimal("299.99"));

            // Persistance des produits
            em.persist(p1);
            em.persist(p2);
            em.persist(p3);

            em.getTransaction().commit();
            System.out.println("Produits insérés avec succès !");
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    private static void lireProduits(EntityManagerFactory emf) {
        EntityManager em = emf.createEntityManager();
        try {
            // Requête JPQL pour récupérer tous les produits
            List<Produit> produits = em.createQuery("SELECT p FROM Produit p", Produit.class)
                    .getResultList();

            System.out.println("\nListe des produits :");
            for (Produit produit : produits) {
                System.out.println(produit);
            }

            // Recherche d'un produit par ID
            System.out.println("\nRecherche du produit avec ID=2 :");
            Produit produit = em.find(Produit.class, 2L);
            if (produit != null) {
                System.out.println(produit);
            } else {
                System.out.println("Produit non trouvé");
            }
        } finally {
            em.close();
        }
    }

    private static void updateProduit(EntityManagerFactory emf,
                                      Long produitId,
                                      String nouveauNom,
                                      BigDecimal nouveauPrix) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            Produit produit = em.find(Produit.class, produitId);
            if (produit != null) {
                produit.setNom(nouveauNom);
                produit.setPrix(nouveauPrix);
                em.merge(produit); // merge is optional here since entity is managed
                em.getTransaction().commit();
                System.out.println("Produit ID " + produitId + " mis à jour avec succès !");
            } else {
                em.getTransaction().rollback();
                System.out.println("Produit avec ID " + produitId + " non trouvé !");
            }

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    private static void deleteProduitWithReference(EntityManagerFactory emf, Long produitId) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            // getReference() ne charge pas l'entité en mémoire
            Produit produit = em.getReference(Produit.class, produitId);
            em.remove(produit);

            em.getTransaction().commit();
            System.out.println("Produit ID " + produitId + " supprimé avec succès !");

        } catch (EntityNotFoundException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.out.println("Produit avec ID " + produitId + " n'existe pas !");
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}