package fr.sio.banque.ui;

import fr.sio.banque.CompteBancaire;
import fr.sio.banque.GestionnaireComptes;

import java.util.List;
import java.util.Scanner;

/**
 * Interface console permettant de manipuler les comptes a la main.
 *
 * Cette classe ne contient aucune regle metier : elle lit les saisies,
 * appelle les methodes de CompteBancaire et GestionnaireComptes, puis
 * affiche les resultats. Les erreurs metier remontent sous forme
 * d'exceptions et sont attrapees ici pour afficher un message lisible
 * plutot qu'une trace d'erreur.
 */
public class Main {

    private static final Scanner clavier = new Scanner(System.in);
    private static final GestionnaireComptes gestionnaire = new GestionnaireComptes();

    public static void main(String[] args) {
        boolean continuer = true;
        while (continuer) {
            afficherMenu();
            String choix = clavier.nextLine().trim();
            try {
                switch (choix) {
                    case "1" -> creerCompte();
                    case "2" -> deposer();
                    case "3" -> retirer();
                    case "4" -> virement();
                    case "5" -> afficherCompte();
                    case "6" -> afficherSoldeTotal();
                    case "7" -> afficherComptesEnDecouvert();
                    case "0" -> continuer = false;
                    default -> System.out.println("Choix inconnu.");
                }
            } catch (RuntimeException e) {
                System.out.println("Erreur : " + e.getMessage());
            }
            System.out.println();
        }
        System.out.println("Au revoir.");
    }

    private static void afficherMenu() {
        System.out.println("=== Gestion de comptes bancaires ===");
        System.out.println("1. Creer un compte");
        System.out.println("2. Deposer sur un compte");
        System.out.println("3. Retirer d'un compte");
        System.out.println("4. Virement entre deux comptes");
        System.out.println("5. Afficher un compte");
        System.out.println("6. Solde total");
        System.out.println("7. Comptes en decouvert");
        System.out.println("0. Quitter");
        System.out.print("Votre choix : ");
    }

    private static void creerCompte() {
        String iban = demanderTexte("IBAN : ");
        String titulaire = demanderTexte("Titulaire : ");
        double decouvert = demanderMontant("Decouvert autorise (0 si aucun) : ");

        gestionnaire.ajouterCompte(new CompteBancaire(iban, titulaire, decouvert));
        System.out.println("Compte cree.");
    }

    private static void deposer() {
        CompteBancaire compte = gestionnaire.rechercherCompte(demanderTexte("IBAN : "));
        compte.deposer(demanderMontant("Montant a deposer : "));
        System.out.println("Nouveau solde : " + compte.getSolde());
    }

    private static void retirer() {
        CompteBancaire compte = gestionnaire.rechercherCompte(demanderTexte("IBAN : "));
        compte.retirer(demanderMontant("Montant a retirer : "));
        System.out.println("Nouveau solde : " + compte.getSolde());
    }

    private static void virement() {
        String source = demanderTexte("IBAN source : ");
        String destination = demanderTexte("IBAN destination : ");
        double montant = demanderMontant("Montant : ");

        gestionnaire.virement(source, destination, montant);
        System.out.println("Virement effectue.");
    }

    private static void afficherCompte() {
        CompteBancaire compte = gestionnaire.rechercherCompte(demanderTexte("IBAN : "));
        System.out.println(decrire(compte));
    }

    private static void afficherSoldeTotal() {
        System.out.println("Solde total : " + gestionnaire.soldeTotal());
    }

    private static void afficherComptesEnDecouvert() {
        List<CompteBancaire> comptes = gestionnaire.listeComptesEnDecouvert();
        if (comptes.isEmpty()) {
            System.out.println("Aucun compte en decouvert.");
            return;
        }
        for (CompteBancaire compte : comptes) {
            System.out.println(decrire(compte));
        }
    }

    private static String decrire(CompteBancaire compte) {
        return compte.getIban() + " | " + compte.getTitulaire()
                + " | solde " + compte.getSolde()
                + " | decouvert autorise " + compte.getDecouvertAutorise();
    }

    private static String demanderTexte(String question) {
        System.out.print(question);
        return clavier.nextLine().trim();
    }

    /** Redemande tant que la saisie n'est pas un nombre. Accepte la virgule. */
    private static double demanderMontant(String question) {
        while (true) {
            System.out.print(question);
            String saisie = clavier.nextLine().trim().replace(',', '.');
            try {
                return Double.parseDouble(saisie);
            } catch (NumberFormatException e) {
                System.out.println("Saisie invalide, entrez un nombre (exemple : 150.50).");
            }
        }
    }
}
