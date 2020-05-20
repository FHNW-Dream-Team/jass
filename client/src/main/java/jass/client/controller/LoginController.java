/*
 * fhnw-jass is jass game programmed in java for a school project.
 * Copyright (C) 2020 Manuele Vaccari & Victor Hargrave & Thomas Weber & Sasa
 * Trajkova
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package jass.client.controller;

import com.j256.ormlite.stmt.QueryBuilder;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import jass.client.entity.LoginEntity;
import jass.client.entity.ServerEntity;
import jass.client.eventlistener.DisconnectEventListener;
import jass.client.message.Login;
import jass.client.mvc.Controller;
import jass.client.repository.LoginRepository;
import jass.client.util.DatabaseUtil;
import jass.client.util.EventUtil;
import jass.client.util.I18nUtil;
import jass.client.util.SocketUtil;
import jass.client.util.ViewUtil;
import jass.client.util.WindowUtil;
import jass.client.view.AboutView;
import jass.client.view.LobbyView;
import jass.client.view.RegisterView;
import jass.client.view.ServerConnectionView;
import jass.lib.message.LoginData;
import jass.lib.servicelocator.ServiceLocator;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Closeable;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The controller for the server connection view.
 *
 * @author Manuele Vaccari
 * @version %I%, %G%
 * @since 1.0.0
 */
public final class LoginController extends Controller implements Closeable, DisconnectEventListener {
    /**
     * The logger to print to console and save in a .log file.
     */
    private static final Logger logger = LogManager.getLogger(LoginController.class);

    /**
     * The root element of the view.
     */
    @FXML
    private VBox root;

    /**
     * The "File" element.
     */
    @FXML
    private Menu mFile;

    /**
     * The "File -> Change Language" element.
     */
    @FXML
    private Menu mFileChangeLanguage;

    /**
     * The "File -> Disconnect" element.
     */
    @FXML
    private MenuItem mFileDisconnect;

    /**
     * The "File -> Exit" element.
     */
    @FXML
    private MenuItem mFileExit;

    /**
     * The "Edit" element.
     */
    @FXML
    private Menu mEdit;

    /**
     * The "Edit -> Delete" element.
     */
    @FXML
    private MenuItem mEditDelete;

    /**
     * The "Help" element.
     */
    @FXML
    private Menu mHelp;

    /**
     * The "Help -> About" element.
     */
    @FXML
    private MenuItem mHelpAbout;

    /**
     * The navbar.
     */
    @FXML
    private Text navbar;

    /**
     * The error message.
     */
    @FXML
    private VBox errorMessage;

    /**
     * The username text field.
     */
    @FXML
    private JFXTextField username;

    /**
     * The password field.
     */
    @FXML
    private JFXPasswordField password;

    /**
     * The "remember me" checkbox.
     */
    @FXML
    private JFXCheckBox rememberMe;

    /**
     * The container for the buttons.
     */
    @FXML
    private HBox buttonGroup;

    /**
     * The login button.
     */
    @FXML
    private JFXButton loginBtn;

    /**
     * The register button.
     */
    @FXML
    private JFXButton register;

    /**
     * @author Manuele Vaccari
     * @since 1.0.0
     */
    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        /*
         * Register oneself for disconnect events
         */
        EventUtil.addDisconnectListener(this);

        /*
         * Bind all texts
         */
        mFile.textProperty().bind(I18nUtil.createStringBinding(mFile.getText()));
        mFileChangeLanguage.textProperty().bind(I18nUtil.createStringBinding(mFileChangeLanguage.getText()));
        ViewUtil.useLanguageMenuContent(mFileChangeLanguage);
        mFileDisconnect.textProperty().bind(I18nUtil.createStringBinding(mFileDisconnect.getText()));
        mFileExit.textProperty().bind(I18nUtil.createStringBinding(mFileExit.getText()));
        mFileExit.setAccelerator(KeyCombination.keyCombination("Alt+F4"));

        mEdit.textProperty().bind(I18nUtil.createStringBinding(mEdit.getText()));
        mEditDelete.textProperty().bind(I18nUtil.createStringBinding(mEditDelete.getText()));

        mHelp.textProperty().bind(I18nUtil.createStringBinding(mHelp.getText()));
        mHelpAbout.textProperty().bind(I18nUtil.createStringBinding(mHelpAbout.getText()));

        navbar.textProperty().bind(I18nUtil.createStringBinding(navbar.getText()));

        username.promptTextProperty().bind(I18nUtil.createStringBinding(username.getPromptText()));
        password.promptTextProperty().bind(I18nUtil.createStringBinding(password.getPromptText()));

        rememberMe.textProperty().bind(I18nUtil.createStringBinding(rememberMe.getText()));

        buttonGroup.prefWidthProperty().bind(Bindings.subtract(root.widthProperty(), new SimpleDoubleProperty(40.0)));
        loginBtn.textProperty().bind(I18nUtil.createStringBinding(loginBtn.getText()));
        loginBtn.prefWidthProperty().bind(Bindings.divide(root.widthProperty(), buttonGroup.getChildren().size()));
        register.textProperty().bind(I18nUtil.createStringBinding(register.getText()));
        register.prefWidthProperty().bind(Bindings.divide(root.widthProperty(), buttonGroup.getChildren().size()));

        /*
         * Disable/Enable the "Connect"-button depending on if the inputs are
         * valid
         */
        AtomicBoolean usernameValid = new AtomicBoolean(false);
        AtomicBoolean passwordValid = new AtomicBoolean(false);
        Runnable updateButtonClickable = () -> loginBtn.setDisable(!usernameValid.get() || !passwordValid.get());
        username.textProperty().addListener((o, oldVal, newVal) -> {
            if (!oldVal.equals(newVal)) {
                usernameValid.set(username.validate());
                updateButtonClickable.run();
            }
        });
        password.textProperty().addListener((o, oldVal, newVal) -> {
            if (!oldVal.equals(newVal)) {
                passwordValid.set(password.validate());
                updateButtonClickable.run();
            }
        });

        /*
         * Validate input fields
         */
        username.getValidators().addAll(
            ViewUtil.useRequiredValidator("gui.login.username.empty")
        );
        password.getValidators().addAll(
            ViewUtil.useRequiredValidator("gui.login.password.empty")
        );
    }

    /**
     * As the view contains an error message field, this updates the text and
     * the window appropriately.
     *
     * @param translatorKey The key of the translation.
     *
     * @author Manuele Vaccari
     * @since 1.0.0
     */
    public void setErrorMessage(final String translatorKey) {
        Platform.runLater(() -> {
            if (errorMessage.getChildren().size() == 0) {
                // Make window larger, so it doesn't become crammed, only if we
                // haven't done so yet
                Stage stage = getView().getStage();
                stage.setMinHeight(stage.getMinHeight() + 40);
                errorMessage.setPrefHeight(40);
            }
            Text text = ViewUtil.useText(translatorKey);
            text.setFill(Color.RED);
            errorMessage.getChildren().clear();
            errorMessage.getChildren().addAll(text, ViewUtil.useSpacer(20));
        });
    }

    /**
     * Disconnect from the server and returns to the server connection window.
     *
     * @author Manuele Vaccari
     * @since 1.0.0
     */
    @FXML
    private void clickOnDisconnect() {
        onDisconnectEvent();
    }

    /**
     * Shuts down the application.
     *
     * @author Manuele Vaccari
     * @since 1.0.0
     */
    @FXML
    private void clickOnExit() {
        Platform.exit();
    }

    /**
     * Opens the about window.
     *
     * @author Manuele Vaccari
     * @since 1.0.0
     */
    @FXML
    public void clickOnAbout() {
        WindowUtil.openInNewWindow(AboutView.class);
    }

    /**
     * Handles the click on the login button. Inputs should already be checked.
     * This will send it to the server, and update local values if successful.
     *
     * @author Manuele Vaccari
     * @since 1.0.0
     */
    @FXML
    private void clickOnLogin() {
        // Disable everything to prevent something while working on the data
        username.setDisable(true);
        password.setDisable(true);
        rememberMe.setDisable(true);
        loginBtn.setDisable(true);
        register.setDisable(true);

        // Connection would freeze window (and the animations) so do it in a
        // different thread.
        new Thread(() -> {
            SocketUtil backend = ServiceLocator.get(SocketUtil.class);
            assert backend != null;
            ServerEntity server = ServiceLocator.get(ServerEntity.class);
            assert server != null;

            boolean newLogin = true;
            LoginEntity login = (new LoginEntity())
                .setServer(server)
                .setUsername(username.getText())
                .setPassword(password.getText())
                .setRememberMe(rememberMe.isSelected());

            // Try to find existing login
            DatabaseUtil db = ServiceLocator.get(DatabaseUtil.class);
            if (db != null) {
                try {
                    QueryBuilder<LoginEntity, Integer> findSameLoginStmt = LoginRepository.getSingleton(null).getDao().queryBuilder();
                    findSameLoginStmt.where()
                        .like(LoginEntity.SERVER_FIELD_NAME, server)
                        .and().like(LoginEntity.USERNAME_FIELD_NAME, username.getText());
                    List<LoginEntity> findSameLoginResult = LoginRepository.getSingleton(null).getDao().query(findSameLoginStmt.prepare());

                    if (findSameLoginResult.size() != 0) {
                        // Otherwise check if we need to overwrite
                        newLogin = false;
                        login = findSameLoginResult.get(0);

                        // Update remember me
                        if (rememberMe.isSelected() && !login.isRememberMe()) {
                            if (!LoginRepository.getSingleton(null).setToRememberMe(login)) {
                                logger.error("Couldn't set user remember me.");
                            }
                        } else if (!rememberMe.isSelected() && login.isRememberMe()) {
                            login.setRememberMe(false);
                            LoginRepository.getSingleton(null).update(login);
                        }

                        // Update password
                        if (!password.getText().equals(login.getPassword())) {
                            login.setPassword(password.getText());
                            LoginRepository.getSingleton(null).update(login);
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            // Send the login request to the server
            Login loginMsg = new Login(new LoginData(login.getUsername(), login.getPassword()));
            if (loginMsg.process(backend)) {
                login.setToken(loginMsg.getToken());

                // Save the login in the db
                if (db != null) {
                    if (newLogin) {
                        if (!LoginRepository.getSingleton(null).add(login)) {
                            logger.error("Couldn't save login data to local database.");
                        }
                    }
                }

                // Go to lobby
                close();
                WindowUtil.switchTo(getView(), LobbyView.class);
            } else {
                // Show an appropriate error message
                LoginData.Result reason = loginMsg.getResultData().getResultData().optEnum(LoginData.Result.class, "reason");
                if (reason == null) {
                    setErrorMessage("gui.login.login.failed");
                } else {
                    switch (reason) {
                        case USER_DOES_NOT_EXIST:
                            setErrorMessage("gui.login.login.failed.user_does_not_exist");
                            break;
                        case WRONG_PASSWORD:
                            setErrorMessage("gui.login.login.failed.wrong_password");
                            break;
                        case USER_ALREADY_LOGGED_IN:
                            setErrorMessage("gui.login.login.failed.already_logged_in");
                            break;
                        default:
                            setErrorMessage("gui.login.login.failed");
                            break;
                    }
                }
            }

            // Enable all inputs again
            username.setDisable(false);
            password.setDisable(false);
            rememberMe.setDisable(false);
            loginBtn.setDisable(false);
            register.setDisable(false);
        }).start();
    }

    /**
     * After clicking on register, switch to the register window.
     *
     * @author Manuele Vaccari
     * @since 1.0.0
     */
    @FXML
    private void clickOnRegister() {
        close();
        WindowUtil.switchTo(getView(), RegisterView.class);
    }

    /**
     * @author Manuele Vaccari
     * @since 1.0.0
     */
    @Override
    public void close() {
        EventUtil.removeDisconnectListener(this);
    }

    /**
     * @author Manuele Vaccari
     * @since 1.0.0
     */
    @Override
    public void onDisconnectEvent() {
        close();
        WindowUtil.switchTo(getView(), ServerConnectionView.class);
    }
}
