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
package org.sakaiproject.iclicker.impl.logic;

import java.util.Observable;

import org.sakaiproject.iclicker.api.logic.BigRunner;



/**
 * Handles the execution of large scale tasks and the tracking thereof
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public abstract class BigRunnerImpl extends Observable implements BigRunner {
    private int total = 0;
    private int completed = 0;
    private int lastCheck = 0;
    private int lastCheckInc = 0;
    private boolean complete = false;
    private Exception failure;
    private String type = "none";

    public BigRunnerImpl() { }
    public BigRunnerImpl(String type) {
        this.type = type;
    }

    public void setCompleted(int totalCompleted) {
        this.completed = totalCompleted;
        checkAndNotify();
    }
    public void addCompleted(int completedChunk) {
        this.completed = completedChunk;
        checkAndNotify();
    }
    public void incrementCompleted() {
        completed++;
        checkAndNotify();
    }
    public void setTotal(int total) {
        this.total = total;
    }
    public void setComplete() {
        this.complete = true;
        this.completed = this.total;
    }
    public void setType(String type) {
        this.type = type;
    }
    public void setFailure(Exception e) {
        this.failure = e;
    }

    public int getItemsCompleted() {
        return completed;
    }
    public int getPercentCompleted() {
        int percent = completed;
        if (complete) {
            percent = 100;
        } else {
            if (total > 0) {
                percent = (completed * 100) / total;
            }
        }
        return percent;
    }
    public boolean isComplete() {
        return complete;
    }
    public boolean isError() {
        return failure != null;
    }
    public int getTotalItems() {
        return total;
    }
    public String getType() {
        return type;
    }
    public Exception getFailure() {
        return failure;
    }
    public Observable getObservable() {
        return this;
    }

    private void checkAndNotify() {
        boolean notify = false;
        if (lastCheckInc == 0) {
            if (total > 100) {
                // every 5%
                lastCheckInc = total / 20;
            } else {
                lastCheckInc = 5;
            }
        }
        if (completed > lastCheck + lastCheckInc) {
            notify = true;
            lastCheck = completed;
        }
        if (notify) {
            notifyObservers(this.getPercentCompleted());
        }
    }

    @Override
    public String toString() {
        return "RUNNER:type="+type+",complete="+complete+",total="+total+",complete="+completed+" "+super.toString();
    }
}
