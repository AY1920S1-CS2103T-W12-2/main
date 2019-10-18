package thrift.ui;

import static thrift.model.transaction.Value.DECIMAL_FORMATTER;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;

/**
 * A ui for displaying the remaining balance for the month. It sits right on top of the status bar footer.
 */
public class BalanceBar extends UiPart<Region> {

    private static final String FXML = "BalanceBar.fxml";

    @FXML
    private Label monthlyBudgetLabel;

    @FXML
    private Label monthlyBudget;

    @FXML
    private Label balanceRemainingLabel;

    @FXML
    private Label balance;

    public BalanceBar(double monthlyBudget, double balanceRemaining) {
        super(FXML);
        monthlyBudgetLabel.setText("Monthly Budget: ");
        this.monthlyBudget.setText("$" + monthlyBudget);
        balanceRemainingLabel.setText("Balance: ");

        StringBuilder sb = new StringBuilder();
        if (balanceRemaining < 0) {
            sb.append("-$").append(DECIMAL_FORMATTER.format(balanceRemaining * (-1)));
            balance.setStyle("-fx-text-fill: #ff6c4f;");
        } else {
            sb.append("$").append(DECIMAL_FORMATTER.format(balanceRemaining));
            balance.setStyle("-fx-text-fill: #69ff4f;");
        }
        balance.setText(sb.toString());

        this.monthlyBudget.setWrapText(true);
        balance.setWrapText(true);
    }
}
