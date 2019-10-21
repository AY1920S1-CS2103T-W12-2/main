package thrift.model.util;

import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import java.util.Map;

import thrift.model.transaction.Value;

/**
 * Contains utility methods for dealing with currencies.
 */
public class CurrencyUtil {

    private static Map<String, Double> currencyMappings;

    public static Map<String, Double> getCurrencyMap() {
        if (currencyMappings == null) {
            currencyMappings = new HashMap<String, Double>(0);
            currencyMappings.put("SGD", 1.0);
            currencyMappings.put("MYR", 3.03);
            currencyMappings.put("USD", 0.73);
            currencyMappings.put("EUR", 0.66);
        }
        return currencyMappings;
    }

    public static void setCurrencyMap(Map<String, Double> currencyMappings) {
        CurrencyUtil.currencyMappings = currencyMappings;
    }

    /**
     * Converts input currency amount from {@link Value#DEFAULT_CURRENCY} denomination
     * to the currency specified in the input.
     *
     * @param currencyMappings Mapping of currency rates scaling from
     *     {@link Value#DEFAULT_CURRENCY}.
     * @param value Amount to convert.
     * @param currency Target currency to convert to.
     * @return Amount in target currency as a double.
     */
    public static double convertFromDefaultCurrency(Map<String, Double> currencyMappings, double value,
            String currency) {
        requireNonNull(currencyMappings);
        requireNonNull(value);
        requireNonNull(currency);
        if (currencyMappings.containsKey(currency.toUpperCase())) {
            return (value * currencyMappings.get(currency.toUpperCase()));
            //return value;
        } else {
            return value;
        }
    }

    /**
     * Converts input currency amount to {@link Value#DEFAULT_CURRENCY} denomination
     * from the currency specified in the input.
     *
     * @param currencyMappings Mapping of currency rates scaling from
     *     {@link Value#DEFAULT_CURRENCY}.
     * @param value Amount to convert.
     * @param currency Target currency to convert from.
     * @return Amount in {@link Value#DEFAULT_CURRENCY} denomination as a double.
     */
    public static double convertToDefaultCurrency(Map<String, Double> currencyMappings, double value, String currency) {
        requireNonNull(currencyMappings);
        requireNonNull(value);
        requireNonNull(currency);
        if (currencyMappings.containsKey(currency.toUpperCase())) {
            return (value / currencyMappings.get(currency.toUpperCase()));
        } else {
            return value;
        }
    }

    /**
     * Converts input currency amount from {@code currencyFrom} to {@code currencyTo} via {@link Value#DEFAULT_CURRENCY}
     * @param currencyMappings Mapping of currency rates scaling from
     *      *     {@link Value#DEFAULT_CURRENCY}.
     * @param value Amount to convert
     * @param currencyFrom Target currency to convert from
     * @param currencyTo Target currency to convert to
     * @return Amount in {@code currencyTo} denomination as a double.
     */
    public static double convert(Map<String, Double> currencyMappings, double value,
                 String currencyFrom, String currencyTo) {
        requireNonNull(currencyMappings);
        requireNonNull(value);
        requireNonNull(currencyTo);
        requireNonNull(currencyFrom);

        double valueInDefaultCurrency = convertToDefaultCurrency(currencyMappings, value, currencyFrom);

        return convertFromDefaultCurrency(currencyMappings, valueInDefaultCurrency, currencyTo);
    }

}
