package se.seb.embedded.coding_assignment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jodamoney.JodaMoneyModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import generated.Document;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import java.time.LocalDateTime;
import java.util.List;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.seb.embedded.coding_assignment.payments.DebitCredit;
import se.seb.embedded.coding_assignment.payments.TransactionService;
import se.seb.embedded.coding_assignment.payments.TransactionService.Transaction;

@Configuration
public class CodingAssignmentConfig {

  @Bean
  public ObjectMapper objectMapper() {
    return JsonMapper.builder()
        .addModule(new JodaMoneyModule())
        .addModule(new JavaTimeModule())
        .build();
  }

  @Bean
  public Marshaller transactionMarshaller() {
    try {
      JAXBContext context = JAXBContext.newInstance(Document.class);
      Marshaller marshaller = context.createMarshaller();
      marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
      return marshaller;
    } catch (Exception e) {
      throw new RuntimeException("Filed to create marshaller", e);
    }
  }

  @Bean
  @ConditionalOnProperty(name = "app.startup.import-test-transactions", havingValue = "true", matchIfMissing = false)
  ApplicationRunner importTransactions(@Autowired TransactionService service) {
    return args -> {
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
      service.importTransactions(transactions);
    };
  }

}
