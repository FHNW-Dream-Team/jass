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

package org.orbitrondev.jass.client.Model;

import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.orbitrondev.jass.client.Entity.LoginEntity;
import org.orbitrondev.jass.client.Entity.LoginRepository;
import org.orbitrondev.jass.client.Entity.ServerEntity;
import org.orbitrondev.jass.client.Entity.ServerRepository;
import org.orbitrondev.jass.client.Main;
import org.orbitrondev.jass.client.Message.Login;
import org.orbitrondev.jass.client.Utils.BackendUtil;
import org.orbitrondev.jass.client.Utils.DatabaseUtil;
import org.orbitrondev.jass.lib.MVC.Model;
import org.orbitrondev.jass.lib.Message.LoginData;
import org.orbitrondev.jass.lib.ServiceLocator.ServiceLocator;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * The model for the splash view.
 *
 * @author Manuele Vaccari
 * @version %I%, %G%
 * @since 0.0.1
 */
public class SplashModel extends Model {
    private static final Logger logger = LogManager.getLogger(SplashModel.class);

    private boolean connected = false;
    private boolean loggedIn = false;

    public final Task<Void> initializer = new Task<Void>() {
        @Override
        protected Void call() {
            // Create the service locator to hold our resources

            // List of all tasks
            ArrayList<Runnable> tasks = new ArrayList<>();

            // Initialize the db connection in the service locator
            tasks.add(() -> {
                try {
                    DatabaseUtil db = new DatabaseUtil(Main.dbLocation);
                    ServiceLocator.add(db);
                    logger.info("Connection to database created");
                } catch (SQLException e) {
                    logger.fatal("Error creating connection to database");
                }
            });
            // Check whether we can already connect to the server automatically
            tasks.add(() -> {
                ServerEntity server = ServerRepository.findConnectAutomatically();
                if (server != null) {
                    logger.info("Server to connect automatically found");
                    try {
                        BackendUtil backend = new BackendUtil(server.getIp(), server.getPort(), server.isSecure());
                        ServiceLocator.add(backend);
                        connected = true;
                        logger.info("Connected to server");
                    } catch (IOException e) { /* Ignore and continue */ }
                }
            });
            // Check whether we can already login
            tasks.add(() -> {
                LoginEntity login = LoginRepository.findConnectAutomatically();
                if (login != null) {
                    logger.info("Automatic login found");
                    BackendUtil backend = (BackendUtil) ServiceLocator.get("backend");
                    if (backend != null) {
                        logger.info("Backend for login is available...");
                        Login loginMsg = new Login(new LoginData(login.getUsername(), login.getPassword()));

                        // Send the login request to the server. Update locally if successful.
                        if (loginMsg.process(backend)) {
                            login.setToken(loginMsg.getToken());
                            loggedIn = true;
                            logger.info("Logged in");
                        }
                    }
                }
            });


            // First, take some time, update progress
            this.updateProgress(1, tasks.size() + 1); // Start the progress bar with 1 instead of 0
            for (int i = 0; i < tasks.size(); i++) {
                tasks.get(i).run();
                this.updateProgress(i + 2, tasks.size() + 1);
            }

            // For better UX, let the user see the full progress bar
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) { /* Ignore */ }
            return null;
        }
    };

    public void initialize() {
        new Thread(initializer).start();
    }

    public boolean isConnected() {
        return connected;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }
}
