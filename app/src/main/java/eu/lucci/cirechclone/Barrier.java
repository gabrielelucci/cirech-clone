/*
 * Copyright 2015 Gabriele Lucci
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.lucci.cirechclone;

/**
 * Created by Gabriele Lucci on 31/07/14.
 * Project: Cirech Clone
 */
public class Barrier {
    int color;
    volatile float position;

    public Barrier(int color, float position) {
        this.position = position;
        this.color = color;
    }

    /**
     * Muove la barriera della distanza specificata.
     * Il metodo è synchronized per evitare che più thread spostino la barriera allo stesso tempo.
     * @param speed la distanza da aggiungere alla posizione, quindi la velocità.
     */
    public synchronized void move(double speed) {
        position += speed;
    }
}
