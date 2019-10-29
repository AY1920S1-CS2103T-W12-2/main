package thrift.logic.parser;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static thrift.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static thrift.logic.parser.CommandParserTestUtil.assertParseFailure;

import org.junit.jupiter.api.Test;

import thrift.logic.commands.CommandTestUtil;
import thrift.logic.commands.ListCommand;
import thrift.logic.parser.exceptions.ParseException;

public class ListCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE);

    private ListCommandParser parser = new ListCommandParser();

    @Test
    public void parse_invalidPreamble_failure() {
        // invalid non empty preamble before valid prefix
        assertParseFailure(parser, CommandTestUtil.PREAMBLE_NON_EMPTY
                + CommandTestUtil.VALID_MONTH_JAN_19, MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(parser, "a", MESSAGE_INVALID_FORMAT);

        assertParseFailure(parser, " r/invalidprefix", MESSAGE_INVALID_FORMAT);

        assertParseFailure(parser, " invalidpreamble" + "m/10/2019", MESSAGE_INVALID_FORMAT);

    }

    @Test
    public void parse_optionalFieldsMissing_success() {
        // no month prefix and argument, (list all transactions)
        assertDoesNotThrow(() -> parser.parse(""));
    }

    @Test
    public void parse_allFieldsPresent_success() throws ParseException {
        // includes month prefix, list transactions by some specific month (jan 2019).
        assertDoesNotThrow(() -> parser.parse(CommandTestUtil.MONTH_JAN_19));
    }

}
