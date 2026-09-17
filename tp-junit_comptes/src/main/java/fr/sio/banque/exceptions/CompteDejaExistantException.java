package fr.sio.banque.exceptions;

/**
 * Levée lorsqu'on ajoute au gestionnaire un compte dont l'IBAN est déjà présent.
 */
public class CompteDejaExistantException extends RuntimeException {

    public CompteDejaExistantException(String message) {
        super(message);
    }
}
