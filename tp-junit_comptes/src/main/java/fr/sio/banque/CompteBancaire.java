package fr.sio.banque;

import fr.sio.banque.exceptions.MontantInvalideException;
import fr.sio.banque.exceptions.SoldeInsuffisantException;

/**
 * Compte bancaire simple : dépôt, retrait avec découvert autorisé, calcul d'intérêts.
 */
public class CompteBancaire {

    private final String iban;
    private final String titulaire;
    private double solde;
    private final double decouvertAutorise;

    /**
     * Crée un compte sans découvert autorisé.
     */
    public CompteBancaire(String iban, String titulaire) {
        this(iban, titulaire, 0);
    }

    /**
     * Crée un compte avec un solde initial de 0.
     *
     * @param decouvertAutorise montant positif ou nul (ex : 200 = solde minimum de -200)
     */
    public CompteBancaire(String iban, String titulaire, double decouvertAutorise) {
        if (iban == null || iban.isBlank()) {
            throw new IllegalArgumentException("L'IBAN est obligatoire");
        }
        if (titulaire == null || titulaire.isBlank()) {
            throw new IllegalArgumentException("Le titulaire est obligatoire");
        }
        if (!(decouvertAutorise >= 0) || Double.isInfinite(decouvertAutorise)) {
            throw new IllegalArgumentException("Le découvert autorisé doit être positif ou nul");
        }
        this.iban = iban;
        this.titulaire = titulaire;
        this.decouvertAutorise = decouvertAutorise;
        this.solde = 0;
    }

    /**
     * Ajoute le montant au solde.
     *
     * @throws MontantInvalideException si le montant est négatif, nul ou non numérique
     */
    public void deposer(double montant) {
        verifierMontant(montant);
        solde = arrondirAuCentime(solde + montant);
    }

    /**
     * Retire le montant du solde, dans la limite du découvert autorisé.
     *
     * @throws MontantInvalideException   si le montant est négatif, nul ou non numérique
     * @throws SoldeInsuffisantException  si le solde passerait sous -decouvertAutorise
     */
    public void retirer(double montant) {
        verifierMontant(montant);
        double nouveauSolde = arrondirAuCentime(solde - montant);
        if (nouveauSolde < -decouvertAutorise) {
            throw new SoldeInsuffisantException(
                    "Retrait de " + montant + " refusé : solde " + solde
                            + ", découvert autorisé " + decouvertAutorise);
        }
        solde = nouveauSolde;
    }

    /**
     * Calcule les intérêts (solde × taux) sans modifier le solde.
     *
     * @param taux taux positif ou nul (ex : 0.03 pour 3 %)
     * @return les intérêts si le solde est positif, 0 sinon
     * @throws IllegalArgumentException si le taux est négatif ou non numérique
     */
    public double calculerInterets(double taux) {
        if (!(taux >= 0) || Double.isInfinite(taux)) {
            throw new IllegalArgumentException("Le taux doit être positif ou nul");
        }
        if (solde <= 0) {
            return 0;
        }
        return solde * taux;
    }

    /**
     * @return true si le solde est strictement négatif
     */
    public boolean estEnDecouvert() {
        return solde < 0;
    }

    public double getSolde() {
        return solde;
    }

    public String getTitulaire() {
        return titulaire;
    }

    public String getIban() {
        return iban;
    }

    public double getDecouvertAutorise() {
        return decouvertAutorise;
    }

    /**
     * "!(montant > 0)" rejette aussi NaN, contrairement à "montant <= 0".
     */
    private static void verifierMontant(double montant) {
        if (!(montant > 0) || Double.isInfinite(montant)) {
            throw new MontantInvalideException("Montant invalide : " + montant);
        }
    }

    /**
     * Les double accumulent des erreurs (0.1 + 0.2 = 0.30000000000000004).
     * On arrondit le solde au centime après chaque opération pour que les
     * comparaisons avec le découvert restent justes.
     */
    private static double arrondirAuCentime(double valeur) {
        return Math.round(valeur * 100) / 100.0;
    }

    @Override
    public String toString() {
        return "CompteBancaire{iban='" + iban + "', titulaire='" + titulaire
                + "', solde=" + solde + ", decouvertAutorise=" + decouvertAutorise + "}";
    }
}
