package fr.sio.banque.exceptions;

/**
 * Levée lorsqu'un montant de dépôt ou de retrait est négatif, nul ou non numérique.
 */
public class MontantInvalideException extends RuntimeException {

    public MontantInvalideException(String message) {
        super(message);
    }
}
