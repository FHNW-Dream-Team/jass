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

package jass.client.controller;

import jass.client.mvc.Controller;
import jass.client.model.DashboardModel;
import jass.client.view.DashboardView;
import javafx.application.Platform;

/**
 * The controller for the dashboard (game) view.
 *
 * @author Manuele Vaccari
 * @version %I%, %G%
 * @since 0.0.1
 */
public class DashboardController extends Controller<DashboardModel, DashboardView> {
    /**
     * Initializes all event listeners for the view.
     *
     * @since 0.0.1
     */
    public DashboardController(DashboardModel model, DashboardView view) {
        super(model, view);

        // Register ourselves to handle window-closing event
        view.getStage().setOnCloseRequest(event -> Platform.exit());
    }
}