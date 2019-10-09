package thrift.logic.parser;

import static thrift.model.transaction.TransactionDate.DATE_FORMATTER;

import java.util.Date;
import java.util.Set;
import java.util.stream.Stream;

import thrift.logic.parser.exceptions.ParseException;
import thrift.model.tag.Tag;
import thrift.model.transaction.Description;
import thrift.model.transaction.TransactionDate;
import thrift.model.transaction.Value;

/**
 * Parses common parameters that are necessary for either AddExpenseCommand or AddIncomeCommand.
 */
public abstract class AddTransactionCommandParser {

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    protected static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

    protected Description parseTransactionDescription(ArgumentMultimap argMultimap) {
        return ParserUtil.parseDescription(argMultimap.getValue(CliSyntax.PREFIX_NAME).get());
    }

    protected Value parseTransactionValue(ArgumentMultimap argMultimap) throws ParseException {
        return ParserUtil.parseValue(argMultimap.getValue(CliSyntax.PREFIX_COST).get());
    }

    protected TransactionDate parseTransactionDate(ArgumentMultimap argMultimap) {
        return new TransactionDate(DATE_FORMATTER.format(new Date()));
    }

    protected Set<Tag> parseTransactionTags(ArgumentMultimap argMultimap) throws ParseException {
        return ParserUtil.parseTags(argMultimap.getAllValues(CliSyntax.PREFIX_TAG));
    }

}
