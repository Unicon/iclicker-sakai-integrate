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

package org.sakaiproject.iclicker.logic.stubs;

import java.util.Properties;

import org.sakaiproject.tool.api.Placement;
import org.sakaiproject.tool.api.Tool;

/**
 * Test class for the Sakai Placement object<br/>
 * This has to be here since I cannot create a Placement object in Sakai for some reason... sure
 * would be nice if I could though -AZ
 * 
 * @author Sakai App Builder -AZ
 */
public class FakePlacement implements Placement {

    private String id = "FAKE12345";
    private String context; // a.k.a. siteId
    private String title;
    private Tool tool;
    private String toolId;

    public FakePlacement() {
    }

    /**
     * Construct a test Placement object with a context (siteId) set
     * 
     * @param context
     *            a String representing a site context (siteId)
     */
    public FakePlacement(String context) {
        this.context = context;
    }

    public Properties getConfig() {

        return null;
    }

    public String getContext() {
        return context;
    }

    public String getId() {
        return id;
    }

    public Properties getPlacementConfig() {

        return null;
    }

    public String getTitle() {
        return this.title;
    }

    public Tool getTool() {
        return tool;
    }

    public String getToolId() {
        return toolId;
    }

    public void save() {

    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTool(String toolId, Tool tool) {
        this.tool = tool;
        this.toolId = toolId;
    }

}