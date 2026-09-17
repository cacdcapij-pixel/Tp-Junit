package fr.sio.banque;
import fr.sio.banque.exceptions.MontantInvalideException;
import fr.sio.banque.exceptions.SoldeInsuffisantException;

/**
 * Compte bancaire : depot, retrait avec decouvert autorise, calcul d'interets.
 *
 * Squelette repris en TDD : les tests sont ecrits en premier, chaque methode
 * est implementee ensuite pour faire passer ses tests.
 */
public class CompteBancaire {

    private final String iban;
    private final String titulaire;
    private double solde;
    private final double decouvertAutorise;

    /** Cree un compte sans decouvert autorise. */
    public CompteBancaire(String iban, String titulaire) {
        this(iban, titulaire, 0);
    }

    /**
     * Cree un compte avec un solde initial de 0.
     *
     * @param decouvertAutorise montant positif ou nul (ex : 200 = solde minimum de -200)
     */
    public CompteBancaire(String iban, String titulaire, double decouvertAutorise) {
        this.iban = iban;
        this.titulaire = titulaire;
        this.decouvertAutorise = decouvertAutorise;
        this.solde = 0;
    }

    /**
     * Ajoute le montant au solde.
     * Rejette un montant negatif ou nul (MontantInvalideException).
     */
    public void deposer(double montant) {
        if (montant <= 0) {
            throw new MontantInvalideException("Montant invalide : " + montant);
        }
        solde += montant;
    }

    /**
     * Retire le montant du solde, dans la limite du decouvert autorise.
     * Rejette un montant negatif ou nul (MontantInvalideException) et un retrait
     * qui ferait passer le solde sous -decouvertAutorise (SoldeInsuffisantException).
     */
    public void retirer(double montant) {
        if (montant <= 0) {
            throw new MontantInvalideException("Montant invalide : " + montant);
        }

        double nouveauSolde = solde - montant;

        if (nouveauSolde < -decouvertAutorise) {
            throw new SoldeInsuffisantException("Retrait refuse : solde " + solde
                    + ", decouvert autorise " + decouvertAutorise);
        }

        solde = nouveauSolde;
    }

    /**
     * Retourne solde * taux si le solde est positif, 0 sinon, sans modifier le solde.
     * Rejette un taux negatif.
     */
    public double calculerInterets(double taux) {
        throw new UnsupportedOperationException("pas encore implemente");
    }

    /** @return true si le solde est strictement negatif */
    public boolean estEnDecouvert() {
        throw new UnsupportedOperationException("pas encore implemente");
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
}
