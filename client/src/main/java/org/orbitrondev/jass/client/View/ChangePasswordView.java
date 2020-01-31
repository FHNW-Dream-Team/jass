package org.orbitrondev.jass.client.View;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.orbitrondev.jass.client.Model.ChangePasswordModel;
import org.orbitrondev.jass.client.Utils.I18nUtil;
import org.orbitrondev.jass.lib.MVC.View;

public class ChangePasswordView extends View<ChangePasswordModel> {
    private VBox errorMessage;
    private JFXPasswordField oldPassword;
    private JFXPasswordField newPassword;
    private JFXPasswordField repeatNewPassword;
    private JFXButton btnChange;
    private JFXButton btnCancel;

    public ChangePasswordView(Stage stage, ChangePasswordModel model) {
        super(stage, model);
        stage.titleProperty().bind(I18nUtil.createStringBinding("gui.changePassword.title"));
        stage.setWidth(300);
        stage.setResizable(false);
    }

    @Override
    protected Scene create_GUI() {
        // Create root
        VBox root = new VBox();
        root.getStyleClass().add("background-white");

        // Create body
        VBox body = new VBox();
        body.getStyleClass().add("custom-container");

        // Create error message container
        errorMessage = new VBox();

        // Create old password input field
        oldPassword = ViewHelper.usePasswordField("gui.changePassword.oldPassword");
        oldPassword.getValidators().addAll(
            ViewHelper.useRequiredValidator("gui.changePassword.oldPassword.empty")
        );

        // Create password input field
        newPassword = ViewHelper.usePasswordField("gui.changePassword.newPassword");
        newPassword.getValidators().addAll(
            ViewHelper.useRequiredValidator("gui.changePassword.newPassword.empty")
        );

        repeatNewPassword = ViewHelper.usePasswordField("gui.changePassword.repeatNewPassword");
        repeatNewPassword.getValidators().addAll(
            ViewHelper.useRequiredValidator("gui.changePassword.repeatNewPassword.empty"),
            ViewHelper.useIsSameValidator(newPassword, "gui.changePassword.repeatNewPassword.notSame")
        );

        // Create body
        HBox btnRow = new HBox();
        btnRow.setSpacing(4); // Otherwise the login and register are right beside each other

        // Create button to register
        btnChange = ViewHelper.usePrimaryButton("gui.changePassword.change");
        btnChange.setDisable(true);

        // Create button to change
        btnCancel = ViewHelper.useSecondaryButton("gui.changePassword.cancel");

        // Add buttons to btnRow
        btnRow.getChildren().addAll(
            btnChange,
            ViewHelper.useHorizontalSpacer(1),
            btnCancel
        );

        // Add body content to body
        body.getChildren().addAll(
            errorMessage,
            ViewHelper.useSpacer(10),
            oldPassword,
            ViewHelper.useSpacer(25),
            newPassword,
            ViewHelper.useSpacer(25),
            repeatNewPassword,
            ViewHelper.useSpacer(25),
            btnRow
        );

        // Add body to root
        root.getChildren().addAll(
            ViewHelper.useDefaultMenuBar(),
            ViewHelper.useNavBar("gui.changePassword.title"),
            body
        );

        Scene scene = new Scene(root);
        // https://stackoverflow.com/questions/29962395/how-to-write-a-keylistener-for-javafx
        scene.setOnKeyPressed(event -> {
            // Click the connect button by clicking ENTER
            if (event.getCode() == KeyCode.ENTER) {
                if (!btnChange.isDisable()) {
                    btnChange.fire();
                }
            }
        });
        scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());
        return scene;
    }

    public VBox getErrorMessage() {
        return errorMessage;
    }

    public JFXPasswordField getOldPassword() {
        return oldPassword;
    }

    public JFXPasswordField getNewPassword() {
        return newPassword;
    }

    public JFXPasswordField getRepeatNewPassword() {
        return repeatNewPassword;
    }

    public JFXButton getBtnChange() {
        return btnChange;
    }

    public JFXButton getBtnCancel() {
        return btnCancel;
    }
}