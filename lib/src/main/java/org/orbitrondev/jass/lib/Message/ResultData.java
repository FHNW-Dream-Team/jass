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

package org.orbitrondev.jass.lib.Message;

import org.json.JSONObject;

/**
 * The data model for the result message.
 *
 * @author Manuele Vaccari
 * @version %I%, %G%
 * @since 0.0.1
 */
public class ResultData extends MessageData {
    private final boolean result;
    private final JSONObject resultData;

    /**
     * This constructor is used by most messages
     */
    public ResultData(boolean result) {
        super("Result");
        this.result = result;
        resultData = new JSONObject();
    }

    public ResultData(boolean result, JSONObject resultData) {
        super("Result");
        this.result = result;
        this.resultData = resultData;
    }

    public ResultData(JSONObject data) {
        super(data);
        result = data.getBoolean("result");
        resultData = data.getJSONObject("resultData");
    }

    public boolean getResult() {
        return result;
    }

    public JSONObject getResultData() {
        return resultData;
    }
}
