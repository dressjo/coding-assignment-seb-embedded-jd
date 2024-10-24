package se.seb.embedded.coding_assignment.payments;

import static javax.xml.datatype.DatatypeFactory.newInstance;

import generated.AmountAndCurrency;
import generated.Document;
import generated.Header;
import generated.ObjectFactory;
import generated.Summary;
import generated.TransactionDetail;
import generated.TransactionSummary;
import jakarta.annotation.PostConstruct;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import org.joda.money.Money;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

  private static final DateTimeFormatter DATE_FORMAT_YYMMDD = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  private final ObjectFactory factory = new ObjectFactory();

  private final Marshaller transactionMarshaller;

  private String transactionsFileDirectory;

  private File outputDirectory;

  public TransactionService(Marshaller transactionMarshaller, @Value("${app.transaction.files.path}") String transactionsFileDirectory) {
    this.transactionMarshaller = transactionMarshaller;
    this.transactionsFileDirectory = transactionsFileDirectory;
  }

  @PostConstruct
  public void init() {
    outputDirectory = new File(transactionsFileDirectory);
    if(!Files.exists(outputDirectory.toPath())) {
      try {
        Files.createDirectory(outputDirectory.toPath()).toFile();
      } catch (IOException e) {
        throw new IllegalStateException("Failed to create transaction files directory [%s]".formatted(transactionsFileDirectory));
      }
    }
  }

  public ImportedSummary importTransactions(List<Transaction> transactions) {
    ArrayList createdFiles = new ArrayList();

    record DateCurrency(LocalDateTime date, String currency) {}

    final Map<DateCurrency, List<Transaction>> transactionsPerDate = transactions
        .stream()
        .collect(Collectors
            .groupingBy(transaction ->
                new DateCurrency(transaction.transactionDate().truncatedTo(ChronoUnit.DAYS), transaction.amount().getCurrencyUnit().getCode())));

    transactionsPerDate.forEach((dateCurrency, dateTransactions) -> {
      final Document document = factory.createDocument();
      final Header header = factory.createHeader();
      final TransactionSummary transactionSummary = factory.createTransactionSummary();
      header.setDate(createDate(dateCurrency.date().toLocalDate()));
      header.setNumberOfTransactions(dateTransactions.size());
      header.setFileId(createFileId(dateCurrency.date().toLocalDate()));
      header.setTransactionSummary(transactionSummary);
      document.setHeader(header);

      final List<TransactionDetail> transactionDetails = dateTransactions
          .stream()
          .sorted(Comparator.comparing(Transaction::accountNumber))
          .collect(Collectors
              .groupingBy(Transaction::accountNumber))
          .values()
          .stream()
          .map(this::createTransactionDetail)
          .collect(Collectors.toList());

      document.getTransactions().addAll(transactionDetails);

      transactionSummary.setCreditSummary(createSummary(dateTransactions, DebitCredit.CREDIT));
      transactionSummary.setDebitSummary(createSummary(dateTransactions, DebitCredit.DEBIT));

      createdFiles.add(writeToFile(document, header.getFileId()));
    });

    return new ImportedSummary(transactions.size(), createdFiles);
  }

  private Summary createSummary(List<Transaction> transactions, DebitCredit debitCredit) {
    return transactions.stream()
        .filter(transaction -> transaction.debitCredit() == debitCredit)
        .collect(new SummaryCollector());
  }

  private String createFileId(LocalDate date) {
    return "transactions-%s-%s".formatted(date.format(DATE_FORMAT_YYMMDD),
        UUID.randomUUID());
  }

  private File writeToFile(Document document, String fileId) {
    try {
      final File file = new File(outputDirectory, fileId + ".xml");
      transactionMarshaller.marshal(document, file);
      return file;
    } catch (JAXBException e) {
      throw new RuntimeException("Failed to write transaction file", e);
    }
  }

  private TransactionDetail createTransactionDetail(List<Transaction> transactions) {
    final TransactionDetail transactionDetail = factory.createTransactionDetail();

    final AmountAndCurrency debitAmount = factory.createAmountAndCurrency();
    debitAmount.setValue(BigDecimal.ZERO);
    transactionDetail.setDebit(debitAmount);
    final AmountAndCurrency creditAmount = factory.createAmountAndCurrency();
    creditAmount.setValue(BigDecimal.ZERO);
    transactionDetail.setCredit(creditAmount);

    for(Transaction transaction : transactions) {
      transactionDetail.setAccountNumber(transaction.accountNumber());
      switch(transaction.debitCredit()) {
        case DEBIT -> debitAmount.setValue(debitAmount.getValue().add(transaction.amount().getAmount()));
        case CREDIT -> creditAmount.setValue(creditAmount.getValue().add(transaction.amount().getAmount()));
      }
      creditAmount.setCurrency(transaction.amount().getCurrencyUnit().getCode());
      debitAmount.setCurrency(transaction.amount().getCurrencyUnit().getCode());
    }

    return transactionDetail;
  }

  private XMLGregorianCalendar createDate(LocalDate date) {
    try {
      final XMLGregorianCalendar xmlGregorianCalendar = newInstance().newXMLGregorianCalendar(
          new GregorianCalendar());
      final LocalDateTime now = LocalDateTime.now();
      xmlGregorianCalendar.setYear(date.getYear());
      xmlGregorianCalendar.setMonth(date.getMonthValue());
      xmlGregorianCalendar.setDay(date.getDayOfMonth());
      return xmlGregorianCalendar;
    } catch (DatatypeConfigurationException e) {
      throw new TransactionsServiceException("Failed to process transactions", e);
    }
  }

  public record ImportedSummary (
      int numberOfTransactions,
      List<File> files
  ) {}

  public record Transaction(
      Money amount,
      String accountNumber,
      DebitCredit debitCredit,
      LocalDateTime transactionDate
  ) {}

}
