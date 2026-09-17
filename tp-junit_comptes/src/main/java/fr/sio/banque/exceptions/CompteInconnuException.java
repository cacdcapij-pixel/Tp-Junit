package fr.sio.banque.exceptions;

/**
 * Levée lorsqu'aucun compte ne correspond à l'IBAN recherché.
 */
public class CompteInconnuException extends RuntimeException {

    public CompteInconnuException(String message) {
        super(message);
    }
}
