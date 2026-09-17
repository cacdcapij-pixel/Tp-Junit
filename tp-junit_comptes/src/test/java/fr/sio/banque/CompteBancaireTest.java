package fr.sio.banque;
import fr.sio.banque.exceptions.MontantInvalideException;
import fr.sio.banque.exceptions.SoldeInsuffisantException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CompteBancaireTest {
    @Test
    void deposer_augmente_le_solde() {
        CompteBancaire compte = new CompteBancaire("FR7612345", "Charles");

        compte.deposer(100);

        assertEquals(100, compte.getSolde(), 0.001);
    }
    @Test
    void deposer_montant_negatif_leve_une_exception() {
        CompteBancaire compte = new CompteBancaire("FR7612345", "Charles");

        assertThrows(MontantInvalideException.class, () -> compte.deposer(-50));
    }

    @Test
    void deposer_montant_zero_leve_une_exception() {
        CompteBancaire compte = new CompteBancaire("FR7612345", "Charles");

        assertThrows(MontantInvalideException.class, () -> compte.deposer(0));
    }

    @Test
    void retirer_diminue_le_solde() {
        CompteBancaire compte = new CompteBancaire("FR7612345", "Charles");
        compte.deposer(200);

        compte.retirer(50);

        assertEquals(150, compte.getSolde(), 0.001);
    }

    @Test
    void retirer_jusqu_au_decouvert_autorise_est_accepte() {
        CompteBancaire compte = new CompteBancaire("FR7612345", "Charles", 200);

        compte.retirer(200);

        assertEquals(-200, compte.getSolde(), 0.001);
    }

    @Test
    void retirer_un_centime_de_plus_que_le_decouvert_leve_une_exception() {
        CompteBancaire compte = new CompteBancaire("FR7612345", "Charles", 200);

        assertThrows(SoldeInsuffisantException.class, () -> compte.retirer(200.01));
        assertEquals(0, compte.getSolde(), 0.001);
    }

    @Test
    void retirer_montant_negatif_leve_une_exception() {
        CompteBancaire compte = new CompteBancaire("FR7612345", "Charles");

        assertThrows(MontantInvalideException.class, () -> compte.retirer(-50));
    }
    @Test
    void calculerInterets_sur_un_solde_positif() {
        CompteBancaire compte = new CompteBancaire("FR7612345", "Charles");
        compte.deposer(1000);

        double interets = compte.calculerInterets(0.05);

        assertEquals(50, interets, 0.001);
        assertEquals(1000, compte.getSolde(), 0.001);
    }

    @Test
    void calculerInterets_sur_un_solde_negatif_retourne_zero() {
        CompteBancaire compte = new CompteBancaire("FR7612345", "Charles", 200);
        compte.retirer(100);

        assertEquals(0, compte.calculerInterets(0.05), 0.001);
    }

    @Test
    void calculerInterets_avec_un_taux_negatif_leve_une_exception() {
        CompteBancaire compte = new CompteBancaire("FR7612345", "Charles");
        compte.deposer(1000);

        assertThrows(IllegalArgumentException.class, () -> compte.calculerInterets(-0.05));
    }

    @Test
    void estEnDecouvert_est_faux_sur_un_solde_positif() {
        CompteBancaire compte = new CompteBancaire("FR7612345", "Charles");
        compte.deposer(100);

        assertFalse(compte.estEnDecouvert());
    }

    @Test
    void estEnDecouvert_est_vrai_sur_un_solde_negatif() {
        CompteBancaire compte = new CompteBancaire("FR7612345", "Charles", 200);
        compte.retirer(50);

        assertTrue(compte.estEnDecouvert());
    }
}

