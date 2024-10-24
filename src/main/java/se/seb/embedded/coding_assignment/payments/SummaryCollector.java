package se.seb.embedded.coding_assignment.payments;

import generated.AmountAndCurrency;
import generated.Summary;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import se.seb.embedded.coding_assignment.payments.TransactionService.Transaction;

public class SummaryCollector implements
    Collector<Transaction, Summary, Summary> {
    @Override
    public Supplier<Summary> supplier() {
        return () -> new Summary();
    }

    @Override
    public BiConsumer<Summary, Transaction> accumulator() {
        return (s, t) -> {
          AmountAndCurrency totalAmount = s.getTotalAmount();
          if(totalAmount == null) {
            totalAmount = new AmountAndCurrency();
            totalAmount.setValue(t.amount().getAmount());
            totalAmount.setCurrency(t.amount().getCurrencyUnit().getCode());
            s.setTotalAmount(totalAmount);
            s.setNumberOfTransactions(BigInteger.ONE);
          } else {
            totalAmount.setValue(totalAmount.getValue().add(t.amount().getAmount()));
            s.setNumberOfTransactions(s.getNumberOfTransactions().add(BigInteger.ONE));
          }
        };
    }

    @Override
    public BinaryOperator<Summary> combiner() {
        return (s1, s2) -> {
          s1.getTotalAmount().setValue(s1.getTotalAmount().getValue().add(s2.getTotalAmount().getValue()));
          s1.setNumberOfTransactions(s1.getNumberOfTransactions().add(s2.getNumberOfTransactions()));
          return  s1;
        };
    }

    @Override
    public Function<Summary, Summary> finisher() {
        return (summary) ->{
            return summary;
        };
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.emptySet();
    }
}