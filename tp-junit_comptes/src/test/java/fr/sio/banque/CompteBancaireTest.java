package fr.sio.banque;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CompteBancaireTest {
    @Test
    void deposer_augmente_le_solde() {
        CompteBancaire compte = new CompteBancaire("FR7612345", "Charles");

        compte.deposer(100);

        assertEquals(100, compte.getSolde(), 0.001);
    }
}
