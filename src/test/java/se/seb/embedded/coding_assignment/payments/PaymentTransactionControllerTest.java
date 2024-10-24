package se.seb.embedded.coding_assignment.payments;

import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import se.seb.embedded.coding_assignment.payments.PaymentTransactionController.TransactionDTO;
import se.seb.embedded.coding_assignment.payments.TransactionService.ImportedSummary;

@WebMvcTest(PaymentTransactionController.class)
class PaymentTransactionControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  TransactionService transactionService;

  @Test
  void importTransactions() throws Exception {
    final List<TransactionDTO> transactions = List.of(
        createTransactionDTO("100.00", "1234567890", DebitCredit.DEBIT, 1, 12, 0),
        createTransactionDTO("200.00", "1234567890", DebitCredit.DEBIT, 1, 13, 0),
        createTransactionDTO("300.00", "1234567890", DebitCredit.DEBIT, 2, 14, 0),
        createTransactionDTO("400.00", "1234567890", DebitCredit.DEBIT, 3, 15, 0)
    );

    when(transactionService.importTransactions(any())).thenReturn(new ImportedSummary(transactions.size(),
        Collections.emptyList()));
    mockMvc.perform(post("/transactions/import")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(transactions)))
        .andExpect(status().isOk());
  }

  @Test
  void importTransactions_invalid_debit_credit() throws Exception {
    mockMvc.perform(post("/transactions/import")
            .contentType(MediaType.APPLICATION_JSON)
            .content(transactionTemplate.formatted("DEBITxx", "2024-10-23T12:51:50")))
        .andExpect(content().string(containsString("DEBITxx")))
        .andExpect(status().isBadRequest())
        .andDo(MockMvcResultHandlers.print());
  }

  private TransactionDTO createTransactionDTO(String amount, String accountNumber,
      DebitCredit debitCredit, int day, int hour, int minute) {
    LocalDateTime transactionDate = LocalDateTime.of(2024, 10, day, hour, minute, 0);
    return new TransactionDTO(Money.of(CurrencyUnit.of("SEK"), new BigDecimal(amount)),
        accountNumber, debitCredit, transactionDate);
  }

  String transactionTemplate = """
         [
           {
             "transactionAmount" : {
           	   "amount": 100.0,
           	   "currency": "SEK"
           	 },
             "accountNumber" : "1234567890",
             "debitCredit" : "%s",
             "transactionDate" : "%s"
           }
         ]
        """;

}