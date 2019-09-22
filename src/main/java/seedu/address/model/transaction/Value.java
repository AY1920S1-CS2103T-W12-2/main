package seedu.address.model.transaction;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Map;

import seedu.address.model.util.CurrencyUtil;

/**
 * Represents a Transaction's monetary value in the Transactions list.
 * Guarantees: immutable; is valid as declared in {@link #isValidValue(String)}
 */
public class Value {

    public static final String MESSAGE_CONSTRAINTS =
            "Cost should only contain numbers and an optional decimal point, which if specified, accepts up to 2"
            + " decimal digits";
    public static final String VALIDATION_REGEX = "^\\d{1,}\\.{0,1}\\d{1,2}$";
    public static final String DEFAULT_CURRENCY = "SGD";
    public final Double amount;
    public final String currency;

    /**
     * Constructs a {@code Value} with the default currency {@link #DEFAULT_CURRENCY}.
     *
     * @param amount Monetary cost describing the value.
     */
    public Value(String amount) {
        requireNonNull(amount);
        checkArgument(isValidValue(amount), MESSAGE_CONSTRAINTS);
        this.amount = Double.parseDouble(amount);
        this.currency = DEFAULT_CURRENCY;
    }

    /**
     * Constructs a {@code Value} with a specified currency.
     *
     * @param amount Monetary cost describing the value.
     * @param currency Currency the amount is in.
     */
    public Value(String amount, String currency) {
        requireNonNull(amount);
        requireNonNull(currency);
        checkArgument(isValidValue(amount), MESSAGE_CONSTRAINTS);
        this.amount = Double.parseDouble(amount);
        this.currency = currency;
    }

    /**
     * Returns true if a given String is a valid monetary value.
     */
    public static boolean isValidValue(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    /**
     * Returns the value in {@link #DEFAULT_CURRENCY} currency amount, if the currency is supported in
     * {@link CurrencyUtil}.
     *
     * @return Value in {@link #DEFAULT_CURRENCY} denomination.
     */
    public double getMonetaryValue() {
        Map<String, Double> currencyMappings = CurrencyUtil.getCurrencyMap();
        return (amount * CurrencyUtil.convertFromDefaultCurrency(currencyMappings, amount, currency));
    }

    @Override
    public String toString() {
        return String.valueOf(getMonetaryValue());
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Value // instanceof handles nulls
                && amount == (((Value) other).amount)
                && currency.equals(((Value) other).currency)); // state check
    }

    @Override
    public int hashCode() {
        return amount.hashCode();
    }

}
