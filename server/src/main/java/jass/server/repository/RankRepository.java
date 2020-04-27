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

package jass.server.repository;

import com.j256.ormlite.dao.Dao;
import jass.lib.database.Repository;
import jass.server.entity.RankEntity;

/**
 * A model with all known ranks.
 *
 * @author Victor Hargrave
 * @version %I%, %G%
 * @since 0.0.1
 */
public final class RankRepository extends Repository<Dao<RankEntity, Integer>, RankEntity> {
    /**
     * The singleton.
     */
    private static RankRepository singleton = null;

    /**
     * Creates a new singleton or returns the existing one.
     *
     * @param dao The DAO to edit inside the database.
     *
     * @return Returns the Repository.
     */
    public static RankRepository getSingleton(final Dao<RankEntity, Integer> dao) {
        if (singleton == null) {
            singleton = new RankRepository(dao);
        }
        return singleton;
    }

    /**
     * @param dao The DAO to edit inside the database.
     */
    public RankRepository(final Dao<RankEntity, Integer> dao) {
        super(dao);
    }
}
