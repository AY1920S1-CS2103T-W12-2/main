package thrift.logic.parser;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static thrift.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static thrift.logic.parser.CommandParserTestUtil.assertParseFailure;
import static thrift.logic.parser.CommandParserTestUtil.assertParseSuccess;

import thrift.commons.core.Messages;
import thrift.logic.commands.TagCommand;
import thrift.testutil.TagSetBuilder;
import thrift.testutil.TypicalIndexes;


class TagCommandParserTest {

    private TagCommandParser parser = new TagCommandParser();
    @Test
    void parse_with_Tags() {
        String input = "tag i/1 t/Food t/Shopping";
        String input2 = "tag i/1 t/Shopping t/Food" ;

        TagCommand expectedCommand = new TagCommand(TypicalIndexes.INDEX_FIRST_TRANSACTION,
                new TagSetBuilder("Food", "Shopping").build());

        assertParseSuccess(parser, input, expectedCommand);
        assertParseSuccess(parser, input2, expectedCommand);
    }

    @Test
    void parse_with_Blank_Tags() {
        String input = "tag i/1 t/";

        assertParseFailure(parser, input, TagCommand.MESSAGE_NOT_TAGGED);
    }

    @Test
    void parse_wrongSyntax() {
        String input = "tag";

        assertParseFailure(parser, input, String.format(MESSAGE_INVALID_COMMAND_FORMAT, TagCommand.MESSAGE_USAGE));
    }
}