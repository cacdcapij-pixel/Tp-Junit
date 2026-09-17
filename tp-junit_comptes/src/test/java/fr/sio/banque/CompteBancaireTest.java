package fr.sio.banque;
import fr.sio.banque.exceptions.MontantInvalideException;
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
}

