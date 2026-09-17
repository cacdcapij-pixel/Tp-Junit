package fr.sio.banque;

import fr.sio.banque.exceptions.CompteDejaExistantException;
import fr.sio.banque.exceptions.CompteInconnuException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Regroupe des comptes bancaires indexés par IBAN et permet les virements entre eux.
 */
public class GestionnaireComptes {

    /** LinkedHashMap : recherche rapide par IBAN et ordre d'ajout conservé. */
    private final Map<String, CompteBancaire> comptes = new LinkedHashMap<>();

    /**
     * @throws CompteDejaExistantException si un compte avec le même IBAN est déjà géré
     */
    public void ajouterCompte(CompteBancaire compte) {
        Objects.requireNonNull(compte, "Le compte ne peut pas être null");
        if (comptes.containsKey(compte.getIban())) {
            throw new CompteDejaExistantException("IBAN déjà présent : " + compte.getIban());
        }
        comptes.put(compte.getIban(), compte);
    }

    /**
     * @throws CompteInconnuException si aucun compte ne porte cet IBAN
     */
    public CompteBancaire rechercherCompte(String iban) {
        CompteBancaire compte = comptes.get(iban);
        if (compte == null) {
            throw new CompteInconnuException("Aucun compte avec l'IBAN : " + iban);
        }
        return compte;
    }

    /**
     * Transfère un montant du compte source vers le compte destination.
     * Atomique : si une étape échoue, aucun solde n'est modifié.
     *
     * @throws CompteInconnuException                                  si un des IBAN est inconnu
     * @throws IllegalArgumentException                                si source et destination sont identiques
     * @throws fr.sio.banque.exceptions.MontantInvalideException      si le montant est invalide
     * @throws fr.sio.banque.exceptions.SoldeInsuffisantException     si la source ne peut pas payer
     */
    public void virement(String ibanSource, String ibanDestination, double montant) {
        // 1. On résout les deux comptes AVANT de toucher aux soldes :
        //    si la destination est inconnue, rien n'a encore été retiré.
        CompteBancaire source = rechercherCompte(ibanSource);
        CompteBancaire destination = rechercherCompte(ibanDestination);

        if (source == destination) {
            throw new IllegalArgumentException("La source et la destination doivent être différentes");
        }

        // 2. Le retrait valide le montant et le découvert ; s'il lève une exception,
        //    on sort avant le dépôt.
        source.retirer(montant);

        // 3. Le montant a déjà été validé par retirer(), le dépôt ne peut plus échouer.
        destination.deposer(montant);
    }

    /**
     * @return la somme des soldes de tous les comptes gérés (0 si aucun compte)
     */
    public double soldeTotal() {
        double total = 0;
        for (CompteBancaire compte : comptes.values()) {
            total += compte.getSolde();
        }
        return Math.round(total * 100) / 100.0;
    }

    /**
     * @return une nouvelle liste des comptes dont le solde est négatif
     */
    public List<CompteBancaire> listeComptesEnDecouvert() {
        List<CompteBancaire> resultat = new ArrayList<>();
        for (CompteBancaire compte : comptes.values()) {
            if (compte.estEnDecouvert()) {
                resultat.add(compte);
            }
        }
        return resultat;
    }
}
