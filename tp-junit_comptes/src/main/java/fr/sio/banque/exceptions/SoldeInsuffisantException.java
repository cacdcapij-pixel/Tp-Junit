package fr.sio.banque.exceptions;

/**
 * Levée lorsqu'un retrait ferait passer le solde sous -decouvertAutorise.
 */
public class SoldeInsuffisantException extends RuntimeException {

    public SoldeInsuffisantException(String message) {
        super(message);
    }
}
