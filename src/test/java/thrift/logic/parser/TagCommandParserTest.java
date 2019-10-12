package thrift.logic.parser;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import static thrift.logic.parser.CommandParserTestUtil.assertParseFailure;
import static thrift.logic.parser.CommandParserTestUtil.assertParseSuccess;

import thrift.logic.commands.TagCommand;
import thrift.testutil.TagSetBuilder;
import thrift.testutil.TypicalIndexes;


class TagCommandParserTest {

    private TagCommandParser parser = new TagCommandParser();
    @Test
    void parse_with_Tags() {
        String input = "tag i/1 t/Food t/Shopping";

        TagCommand expectedCommand = new TagCommand(TypicalIndexes.INDEX_FIRST_TRANSACTION,
                new TagSetBuilder("Food", "Shopping").build());

        assertParseSuccess(parser, input, expectedCommand);
    }

    @Test
    void parse_with_Blank_Tags() {
        String input = "tag i/1 t/";

        assertParseFailure(parser, input, TagCommand.MESSAGE_NOT_TAGGED);
    }
}