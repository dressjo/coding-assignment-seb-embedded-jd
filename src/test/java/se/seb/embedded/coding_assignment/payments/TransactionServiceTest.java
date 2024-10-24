package se.seb.embedded.coding_assignment.payments;

import generated.Document;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import java.io.File;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import se.seb.embedded.coding_assignment.CodingAssignmentConfig;
import se.seb.embedded.coding_assignment.payments.TransactionService.ImportedSummary;
import se.seb.embedded.coding_assignment.payments.TransactionService.Transaction;

class TransactionServiceTest {

  TransactionService transactionService;

  @BeforeEach
  void setUp() {
    transactionService = new TransactionService(new CodingAssignmentConfig().transactionMarshaller(), System.getProperty("java.io.tmpdir"));
    transactionService.init();
  }

  @Test
  void processTransaction() throws Exception {
    final LocalDateTime localDateTime = LocalDateTime.of(2024, 12, 1, 12, 0, 0);
    final LocalDateTime localDateTimeNextDay = localDateTime.plusDays(1);
    final List<Transaction> transactions = List.of(
        new Transaction(Money.of(CurrencyUnit.of("SEK"), 100),
            "11111",
            DebitCredit.CREDIT,
            localDateTime),
        new Transaction(Money.of(CurrencyUnit.of("SEK"), 200),
            "11111",
            DebitCredit.DEBIT,
            localDateTime),
        new Transaction(Money.of(CurrencyUnit.of("SEK"), 100),
            "11111",
            DebitCredit.CREDIT,
            localDateTimeNextDay),
        new Transaction(Money.of(CurrencyUnit.of("SEK"), 200),
            "11111",
            DebitCredit.DEBIT,
            localDateTimeNextDay),
        new Transaction(Money.of(CurrencyUnit.of("SEK"), 200),
            "22222",
            DebitCredit.DEBIT,
            localDateTimeNextDay)
    );

    final ImportedSummary importedSummary = transactionService.importTransactions(transactions);

    for(File file : importedSummary.files()) {
      validateFile(file);
      file.delete();
    }

  }

  private boolean validateFile(File file) throws Exception {
    final URL xsdUrl = new ClassPathResource("output.xsd").getURL();
    JAXBContext context = JAXBContext.newInstance(Document.class);
    SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
    Schema schema = schemaFactory.newSchema(xsdUrl);

    Unmarshaller unmarshaller = context.createUnmarshaller();
    unmarshaller.setSchema(schema);

    Document validatedDocument = (Document) unmarshaller.unmarshal(file);
    return true;
  }


}