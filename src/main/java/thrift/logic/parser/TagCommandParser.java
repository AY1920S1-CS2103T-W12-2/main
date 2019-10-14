package thrift.logic.parser;

import static java.util.Objects.requireNonNull;
import static thrift.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static thrift.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT_WITH_PE;

import java.util.HashSet;
import java.util.Set;

import thrift.commons.core.index.Index;
import thrift.logic.commands.TagCommand;
import thrift.logic.parser.exceptions.ParseException;
import thrift.model.tag.Tag;

/**
 * Parses input arguments and creates a new TagCommand object.
 */
public class TagCommandParser implements Parser<TagCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the TagCommand
     * and returns an TagCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public TagCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, CliSyntax.PREFIX_INDEX, CliSyntax.PREFIX_TAG);

        Index index;
        Set<Tag> tagSet = new HashSet<Tag>();

        try {
            String indexStr = argMultimap.getSingleValue(CliSyntax.PREFIX_INDEX).orElse("");
            if (indexStr.length() == 0) {
                throw new ParseException("");
            }
            index = ParserUtil.parseIndex(indexStr);
            for (String tagName : argMultimap.getAllValues(CliSyntax.PREFIX_TAG)) {
                if (!tagName.isEmpty()) {
                    Tag tag = new Tag(tagName);
                    tagSet.add(tag);
                }
            }
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT_WITH_PE, TagCommand.MESSAGE_USAGE, pe.getMessage()),
                    pe);
        } catch (IllegalArgumentException iae) {
            throw new ParseException(iae.getMessage(), iae);
        }

        if (tagSet.isEmpty()) {
            throw new ParseException(TagCommand.MESSAGE_NOT_TAGGED);
        }

        return new TagCommand(index, tagSet);
    }
}
