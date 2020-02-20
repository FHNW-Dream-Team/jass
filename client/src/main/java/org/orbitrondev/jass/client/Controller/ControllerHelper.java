/*
 * fhnw-jass is jass game programmed in java for a school project.
 * Copyright (C) 2020 Manuele Vaccari
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

package org.orbitrondev.jass.client.Controller;

import javafx.application.Platform;
import javafx.stage.Stage;
import org.orbitrondev.jass.client.View.RegisterView;
import org.orbitrondev.jass.client.View.ServerConnectionView;
import org.orbitrondev.jass.client.Model.DashboardModel;
import org.orbitrondev.jass.client.View.DashboardView;
import org.orbitrondev.jass.client.View.LoginView;

/**
 * A helper class for the controllers to switch between windows easily.
 *
 * @author Manuele Vaccari
 * @version %I%, %G%
 * @since 0.0.1
 */
public class ControllerHelper {
    // Reuse the same window
    private static Stage stage = new Stage();

    public static void switchToServerConnectionWindow() {
        Platform.runLater(() -> {
            ServerConnectionView view = new ServerConnectionView(stage);
            view.start();
        });
    }

    public static void switchToLoginWindow() {
        Platform.runLater(() -> {
            LoginView view = new LoginView(stage);
            view.start();
        });
    }

    public static void switchToRegisterWindow() {
        Platform.runLater(() -> {
            RegisterView view = new RegisterView(stage);
            view.start();
        });
    }

    public static void switchToDashboardWindow() {
        Platform.runLater(() -> {
            Stage stage = new Stage();
            DashboardModel model = new DashboardModel();
            DashboardView newView = new DashboardView(stage, model);
            new DashboardController(model, newView);

            newView.start();
        });
    }
}
