/**
 * Copyright (c) 2009 i>clicker (R) <http://www.iclicker.com/dnn/>
 *
 * This file is part of i>clicker Sakai integrate.
 *
 * i>clicker Sakai integrate is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * i>clicker Sakai integrate is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with i>clicker Sakai integrate.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.sakaiproject.iclicker.api.logic;

import java.util.Observable;

/**
 * Special class for handling long running operations
 * 
 */
public interface BigRunner extends Runnable {
    public static final String RUNNER_TYPE_ADD = "add";
    public static final String RUNNER_TYPE_REMOVE = "remove";
    public static final String RUNNER_TYPE_SYNC = "sync";
    public static final String RUNNER_LOCK = "bigRunnerLock";

    public int getTotalItems();
    public int getItemsCompleted();
    public int getPercentCompleted();
    public boolean isComplete();
    public boolean isError();
    public void setFailure(Exception e);
    public String getType();
    public Observable getObservable();

}
