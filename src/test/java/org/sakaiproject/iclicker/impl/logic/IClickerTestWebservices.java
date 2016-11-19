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

import junit.framework.TestCase;

import org.sakaiproject.iclicker.exception.ClickerIdInvalidException;
import org.sakaiproject.iclicker.logic.IClickerLogic;
import org.sakaiproject.iclicker.logic.stubs.ExternalLogicStub;

/**
 * For testing the webservices calls (should nor run during standard build)
 */
public class IClickerTestWebservices extends TestCase {

    protected IClickerLogic logicImpl;

    public void setUp() throws Exception {
        super.setUp();

        // setup the mock objects
        ExternalLogicStub externalLogic = new ExternalLogicStub();

        // create and setup the object to be tested
        logicImpl = new IClickerLogic();
        logicImpl.setExternalLogic(externalLogic);

        // run the init
        logicImpl.init();
    }

    public void tearDown() throws Exception {
        logicImpl = null;
    }

    public void testWsGoVerifyId() throws Exception {
        boolean result;

        result = logicImpl.wsGoVerifyId("88C0A608A266", "1704");
        assertTrue(result);

        try {
            logicImpl.wsGoVerifyId("XXXXXXXXXXXX", "XXXXX");
            fail("should have died");
        } catch (ClickerIdInvalidException e) {
            assertNotNull(e.getMessage());
            assertEquals(ClickerIdInvalidException.Failure.GO_NO_MATCH, e.failure);
        }

        try {
            logicImpl.wsGoVerifyId("88C0A608A266", "XXXXX");
            fail("should have died");
        } catch (ClickerIdInvalidException e) {
            assertNotNull(e.getMessage());
            assertEquals(ClickerIdInvalidException.Failure.GO_LASTNAME, e.failure);
        }
    }

}
