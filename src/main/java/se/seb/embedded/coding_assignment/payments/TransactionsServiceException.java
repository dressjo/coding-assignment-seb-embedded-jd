package se.seb.embedded.coding_assignment.payments;

public class TransactionsServiceException extends RuntimeException {

  public TransactionsServiceException(String message) {
    super(message);
  }

  public TransactionsServiceException(String message, Throwable cause) {
    super(message, cause);
  }

}
