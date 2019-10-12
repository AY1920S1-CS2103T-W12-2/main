package thrift.logic.commands;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.*;
import static thrift.logic.commands.CommandTestUtil.assertCommandSuccess;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableList;
import thrift.commons.core.GuiSettings;
import thrift.commons.core.index.Index;
import thrift.logic.commands.exceptions.CommandException;
import thrift.model.Model;
import thrift.model.ModelManager;
import thrift.model.PastUndoableCommands;
import thrift.model.ReadOnlyThrift;
import thrift.model.ReadOnlyUserPrefs;
import thrift.model.Thrift;
import thrift.model.UserPrefs;
import thrift.model.tag.Tag;
import thrift.model.transaction.Expense;
import thrift.model.transaction.Income;
import thrift.model.transaction.Transaction;
import thrift.testutil.ExpenseBuilder;
import thrift.testutil.TagSetBuilder;
import thrift.testutil.TypicalIndexes;
import thrift.testutil.TypicalTransactions;
import thrift.testutil.UpdateTransactionDescriptorBuilder;

class TagCommandTest {


    @Test
    void execute_newTags_success() throws CommandException {
        Model model = new ModelManager(TypicalTransactions.getTypicalThrift(), new UserPrefs(),
                new PastUndoableCommands());
        Expense originalExpense = new ExpenseBuilder(model.getFilteredTransactionList().get(0))
                .build();

        Set<Tag> tagSet = new TagSetBuilder("Food", "Shopping").build();
        TagCommand tagCommand = new TagCommand(TypicalIndexes.INDEX_FIRST_TRANSACTION, tagSet);

        Set<Tag> expectedTag = new TagSetBuilder(originalExpense.getTags())
                .addTags("Food", "Shopping")
                .build();
        Expense updatedExpense = new Expense(
                originalExpense.getDescription(),
                originalExpense.getValue(),
                originalExpense.getRemark(),
                originalExpense.getDate(),
                expectedTag);

        assertNotEquals(updatedExpense.getTags(), model.getFilteredTransactionList().get(0).getTags()); //Before
        tagCommand.execute(model);
        assertEquals(updatedExpense.getTags(), model.getFilteredTransactionList().get(0).getTags()); //After
    }


    @Test
    void testEquals() {
        Tag arrOne[] = {new Tag("Food"), new Tag("Shopping")};
        Tag arrTwo[] = {new Tag("Debt"), new Tag("Fees")};
        Set<Tag> setOne = new HashSet<Tag>(Arrays.asList(arrOne));
        Set<Tag> setTwo = new HashSet<Tag>(Arrays.asList(arrTwo));
        TagCommand firstTagCommand = new TagCommand(TypicalIndexes.INDEX_FIRST_TRANSACTION, setOne);
        TagCommand secondTagCommand = new TagCommand(TypicalIndexes.INDEX_FIRST_TRANSACTION, setTwo);

        // same object -> returns true
        assertTrue(firstTagCommand.equals(firstTagCommand));

        // same values -> returns true
        TagCommand firstTagCommandCopy = new TagCommand(TypicalIndexes.INDEX_FIRST_TRANSACTION, setOne);
        assertTrue(firstTagCommand.equals(firstTagCommandCopy));

        // different types -> returns false
        assertFalse(firstTagCommand.equals(1));

        // null -> returns false
        assertFalse(firstTagCommand.equals(null));

        // different transaction -> returns false
        assertFalse(firstTagCommand.equals(secondTagCommand));
    }

}

