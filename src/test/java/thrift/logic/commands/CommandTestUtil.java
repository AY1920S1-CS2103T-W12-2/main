package thrift.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static thrift.logic.parser.CliSyntax.PREFIX_COST;
import static thrift.logic.parser.CliSyntax.PREFIX_NAME;
import static thrift.logic.parser.CliSyntax.PREFIX_TAG;
import static thrift.testutil.Assert.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import thrift.commons.core.index.Index;
import thrift.logic.commands.exceptions.CommandException;
import thrift.model.Model;
import thrift.model.Thrift;
import thrift.model.transaction.DescriptionContainsKeywordsPredicate;
import thrift.model.transaction.Transaction;
import thrift.testutil.EditTransactionDescriptorBuilder;

/**
 * Contains helper methods for testing commands.
 */
public class CommandTestUtil {

    public static final String VALID_DESCRIPTION_LAKSA = "Laksa";
    public static final String VALID_DESCRIPTION_PENANG_LAKSA = "Penang Laksa";
    public static final String VALID_DESCRIPTION_AIRPODS = "Airpods";
    public static final String VALID_VALUE_LAKSA = "3.50";
    public static final String VALID_VALUE_AIRPODS = "350";
    public static final String VALID_DATE_LAKSA = "13/03/1937";
    public static final String VALID_DATE_AIRPODS = "14/03/1937";
    public static final String VALID_TAG_LUNCH = "Lunch";
    public static final String VALID_TAG_BRUNCH = "Brunch";
    public static final String VALID_TAG_ACCESSORY = "Accessory";

    public static final String DESC_LAKSA = " " + PREFIX_NAME + VALID_DESCRIPTION_LAKSA;
    public static final String DESC_AIRPODS = " " + PREFIX_NAME + VALID_DESCRIPTION_AIRPODS;
    public static final String VALUE_LAKSA = " " + PREFIX_COST + VALID_VALUE_LAKSA;
    public static final String VALUE_AIRPODS = " " + PREFIX_COST + VALID_VALUE_AIRPODS;
    public static final String TAG_LAKSA = " " + PREFIX_TAG + VALID_TAG_LUNCH;
    public static final String TAG_BRUNCH = " " + PREFIX_TAG + VALID_TAG_BRUNCH;
    public static final String TAG_AIRPODS = " " + PREFIX_TAG + VALID_TAG_ACCESSORY;

    public static final String INVALID_VALUE = " " + PREFIX_COST + ".00"; // missing whole number
    public static final String INVALID_TAG = " " + PREFIX_TAG + "A+"; //illegal character

    public static final String PREAMBLE_WHITESPACE = "\t  \r  \n";
    public static final String PREAMBLE_NON_EMPTY = "NonEmptyPreamble";

    public static final EditCommand.EditTransactionDescriptor DESC_MEAL;
    public static final EditCommand.EditTransactionDescriptor DESC_PURCHASE;

    static {
        DESC_MEAL = new EditTransactionDescriptorBuilder().withDescription(VALID_DESCRIPTION_LAKSA)
                .withValue(VALID_VALUE_LAKSA).withDate(VALID_DATE_LAKSA).withTags(VALID_TAG_LUNCH).build();

        DESC_PURCHASE = new EditTransactionDescriptorBuilder().withDescription(VALID_DESCRIPTION_AIRPODS)
                .withValue(VALID_VALUE_AIRPODS).withDate(VALID_DATE_AIRPODS).withTags(VALID_TAG_ACCESSORY).build();
    }

    /**
     * Executes the given {@code command}, confirms that <br>
     * - the returned {@link CommandResult} matches {@code expectedCommandResult} <br>
     * - the {@code actualModel} matches {@code expectedModel}
     */
    public static void assertCommandSuccess(Command command, Model actualModel, CommandResult expectedCommandResult,
            Model expectedModel) {
        try {
            CommandResult result = command.execute(actualModel);
            assertEquals(expectedCommandResult, result);
            assertEquals(expectedModel, actualModel);
        } catch (CommandException ce) {
            throw new AssertionError("Execution of command should not fail.", ce);
        }
    }

    /**
     * Convenience wrapper to {@link #assertCommandSuccess(Command, Model, CommandResult, Model)}
     * that takes a string {@code expectedMessage}.
     */
    public static void assertCommandSuccess(Command command, Model actualModel, String expectedMessage,
            Model expectedModel) {
        CommandResult expectedCommandResult = new CommandResult(expectedMessage);
        assertCommandSuccess(command, actualModel, expectedCommandResult, expectedModel);
    }

    /**
     * Executes the given {@code command}, confirms that <br>
     * - a {@code CommandException} is thrown <br>
     * - the CommandException message matches {@code expectedMessage} <br>
     * - the address book, filtered transaction list and selected transaction in {@code actualModel} remain unchanged
     */
    public static void assertCommandFailure(Command command, Model actualModel, String expectedMessage) {
        // we are unable to defensively copy the model for comparison later, so we can
        // only do so by copying its components.

        Thrift expectedThrift = new Thrift(actualModel.getThrift());
        List<Transaction> expectedFilteredList = new ArrayList<>(actualModel.getFilteredTransactionList());

        assertThrows(CommandException.class, expectedMessage, () -> command.execute(actualModel));
        assertEquals(expectedThrift, actualModel.getThrift());
        assertEquals(expectedFilteredList, actualModel.getFilteredTransactionList());
    }
    /**
     * Updates {@code model}'s filtered list to show only the transaction at the given {@code targetIndex} in the
     * {@code model}'s address book.
     */
    public static void showTransactionAtIndex(Model model, Index targetIndex) {
        assertTrue(targetIndex.getZeroBased() < model.getFilteredTransactionList().size());

        Transaction transaction = model.getFilteredTransactionList().get(targetIndex.getZeroBased());
        final String[] splitDescription = transaction.getDescription().toString().split("\\s+");
        model.updateFilteredTransactionList(new DescriptionContainsKeywordsPredicate(
                Arrays.asList(splitDescription[0])));
        assertEquals(1, model.getFilteredTransactionList().size());
    }

}
