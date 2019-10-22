package thrift.logic.commands;

import java.util.List;
import java.util.Map;

import thrift.logic.parser.CliSyntax;
import thrift.model.Model;
import thrift.model.util.CurrencyUtil;

/**
 * Converts currencies
 */
public class ConvertCommand extends NonScrollingCommand {

    public static final String COMMAND_WORD = "convert";

    public static final String DEFAULT_VALUE = "1.00";
    public static final String DEFAULT_CURRENCY = "SGD";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Converts amount "
            + "from the first specified currency to the rest of the specified currencies.\n"
            + "If no value is specified, it converts with a value of " + DEFAULT_VALUE + ".\n"
            + "If only one currency is specified, it converts from " + DEFAULT_CURRENCY + " to that currency.\n"
            + "Parameters: [" + CliSyntax.PREFIX_VALUE + "amount] (must be a double) "
            + CliSyntax.PREFIX_CURRENCY + "CURRENCIES...\n"
            + "Example: " + COMMAND_WORD + " "
            + CliSyntax.PREFIX_VALUE + "1000 "
            + CliSyntax.PREFIX_CURRENCY + "SGD "
            + CliSyntax.PREFIX_CURRENCY + "MYR";
    public static final String CURRENCY_SOURCE_URL =
            "https://www.ecb.europa.eu/"
                    + "stats/policy_and_exchange_rates/euro_reference_exchange_rates/html/index.en.html";
    public static final String CURRENCY_SOURCE =
            "European Central Bank (" + CURRENCY_SOURCE_URL + ")";
    public static final String MESSAGE_CREDITS = "\nExchange rates sourced from " + CURRENCY_SOURCE + ".";

    public static final String MESSAGE_BASE_CURRENCY_FORMAT = "Converting from %1$S%2$.2f\n";
    public static final String MESSAGE_TARGET_CURRENCY_FORMAT = "To %1$S: %1$S%2$.2f\n";

    private double amount;
    private List<String> currencies;

    public ConvertCommand(double amount, List<String> currencies) {
        this.amount = amount;
        this.currencies = currencies;
    }

    @Override
    public CommandResult execute(Model model) {

        return new CommandResult(generateConvertResult());
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof ConvertCommand // instanceof handles nulls
                && amount == (((ConvertCommand) other).amount)
                && currencies.equals(((ConvertCommand) other).currencies));
    }

    /**
     * Generates the result message for conversions using instance variables
     *
     * @return Result message for conversions
     */
    private String generateConvertResult() {
        return generateConvertResult(amount, currencies);
    }

    /**
     * Generates the result message for conversions
     *
     * @return Result message for conversions
     */
    public static String generateConvertResult(double amount, List<String> currencies) {
        StringBuilder convertResultMsgSb = new StringBuilder();
        String baseCurrency = currencies.get(0);

        convertResultMsgSb.append(String.format(MESSAGE_BASE_CURRENCY_FORMAT, baseCurrency, amount));

        for (int i = 1; i < currencies.size(); i++) {
            Map<String, Double> currencyMap = CurrencyUtil.getCurrencyMap();

            String targetCurrency = currencies.get(i);
            double convertedValue = CurrencyUtil.convert(currencyMap, amount, baseCurrency, targetCurrency);

            convertResultMsgSb.append(String.format(MESSAGE_TARGET_CURRENCY_FORMAT, targetCurrency, convertedValue));
        }

        convertResultMsgSb.append(MESSAGE_CREDITS);
        return convertResultMsgSb.toString();
    }
}
