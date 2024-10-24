package se.seb.embedded.coding_assignment.payments;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.joda.money.Money;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import se.seb.embedded.coding_assignment.payments.TransactionService.ImportedSummary;
import se.seb.embedded.coding_assignment.payments.TransactionService.Transaction;

@RestController
public class PaymentTransactionController {

  protected static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

  private TransactionService transactionService;

  public PaymentTransactionController(TransactionService transactionService) {
    this.transactionService = transactionService;
  }

  @PostMapping("/transactions/import")
  ResponseEntity<ImportTransactionsResponse> importTransactions(
      @RequestBody @NotEmpty(message = "Transaction list cannot be empty") List<@Valid TransactionDTO> transactionDTOs) {

    final ImportedSummary importedSummary = transactionService.importTransactions(
        transactionDTOs
            .stream()
            .map(mapToTransaction())
            .collect(Collectors.toList())
    );

    return ResponseEntity
        .ok()
        .body(new ImportTransactionsResponse(importedSummary.numberOfTransactions()));
  }

  private static Function<TransactionDTO, Transaction> mapToTransaction() {
    return transactionDTO -> new Transaction(
        transactionDTO.transactionAmount,
        transactionDTO.accountNumber,
        transactionDTO.debitCredit,
        transactionDTO.transactionDate
    );
  }

  record ImportTransactionsResponse(int processedTransactions) {}

  record TransactionDTO(
      @NotNull
      Money transactionAmount,
      @NotEmpty
      String accountNumber,
      @NotNull
      DebitCredit debitCredit,
      @NotNull
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
      LocalDateTime transactionDate
  ) {}



}



