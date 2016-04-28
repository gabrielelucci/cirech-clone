/*
 * This file is part of cirech-clone.
 *
 * cirech-clone is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * cirech-clone is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with cirech-clone.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2016.
 */

package eu.lucci.cirechclone;

/**
 * Created by Gabriele Lucci on 31/07/14.
 * Project: Cirech Clone
 */
public class Barrier {

    boolean color;

    volatile float position;

    public Barrier(boolean color, float position) {
        this.position = position;
        this.color = color;
    }

    /**
     * Muove la barriera della distanza specificata.
     * Il metodo è synchronized per evitare che più thread spostino la barriera allo stesso tempo.
     *
     * @param speed la distanza da aggiungere alla posizione, quindi la velocità.
     */
    public synchronized void move(double speed) {
        position += speed;
    }

}
