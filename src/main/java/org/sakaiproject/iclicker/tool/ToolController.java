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
package org.sakaiproject.iclicker.tool;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.azeckoski.reflectutils.ArrayUtils;
import org.sakaiproject.iclicker.api.logic.BigRunner;
import org.sakaiproject.iclicker.exception.ClickerIdInvalidException;
import org.sakaiproject.iclicker.exception.ClickerLockException;
import org.sakaiproject.iclicker.exception.ClickerRegisteredException;
import org.sakaiproject.iclicker.exception.ClickerIdInvalidException.Failure;
import org.sakaiproject.iclicker.logic.ExternalLogic;
import org.sakaiproject.iclicker.logic.IClickerLogic;
import org.sakaiproject.iclicker.model.Course;
import org.sakaiproject.iclicker.model.dao.ClickerRegistration;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

/**
 * This is the singleton controller for the application,
 * this handles all the display related logic and processing and communicates
 * with the services for the view layer
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public class ToolController {

    private static final Log log = LogFactory.getLog(ToolController.class);

    private IClickerLogic logic;
    private ExternalLogic externalLogic;
    private ReloadableResourceBundleMessageSource messageSource;

    public void init() {
        log.info("INIT");
    }

    public ToolController() {
    }

    /**************************************************************************
     * VIEW handling
     **************************************************************************
     */

    public static final String VIEW_REGISTRATION = "registration";
    public static final String VIEW_INSTRUCTOR = "instructor";
    public static final String VIEW_INSTRUCTOR_SSO = "instructor_sso";
    public static final String VIEW_ADMIN = "admin";
    
    public static final String[] VIEWS = { VIEW_REGISTRATION, VIEW_INSTRUCTOR, VIEW_INSTRUCTOR_SSO, VIEW_ADMIN };


    /**************************************************************************
     * View handling methods
     **************************************************************************
     */

    public void processRegistration(PageContext pageContext, HttpServletRequest request) {
        // Handle the POST if there is one
        pageContext.setAttribute("newRegistration", false);
        pageContext.setAttribute("clickerIdText", "");
        if ( "POST".equalsIgnoreCase(request.getMethod()) ) {
            if ( (request.getParameter("register") != null) ) {
                // we are registering a clicker
                if ( (request.getParameter("clickerId") == null) ) {
                    ToolController.addMessage(pageContext, ToolController.KEY_ERROR,
                            "reg.activate.clickerId.empty", (Object[])null);
                } else {
                    String clickerId = request.getParameter("clickerId");
                    pageContext.setAttribute("clickerIdText", clickerId);
                    // save a new clicker registration
                    try {
                        this.getLogic().createRegistration(clickerId);
                        ToolController.addMessage(pageContext, ToolController.KEY_INFO,
                                "reg.registered.success", clickerId);
                        ToolController.addMessage(pageContext, ToolController.KEY_BELOW,
                                "reg.registered.below.success", (Object[])null);
                        pageContext.setAttribute("newRegistration", true);
                    } catch (ClickerRegisteredException e) {
                        ToolController.addMessage(pageContext, ToolController.KEY_ERROR,
                                "reg.registered.clickerId.duplicate", clickerId);
                        ToolController.addMessage(pageContext, ToolController.KEY_BELOW,
                                "reg.registered.below.duplicate", clickerId);
                    } catch (ClickerIdInvalidException e) {
                        if (Failure.EMPTY.equals(e.failure)) {
                            ToolController.addMessage(pageContext, ToolController.KEY_ERROR, "reg.registered.clickerId.empty", (Object[])null);
                        } else if (Failure.LENGTH.equals(e.failure)) {
                            ToolController.addMessage(pageContext, ToolController.KEY_ERROR, "reg.registered.clickerId.wrong.length", (Object[])null);
                        } else if (Failure.GO_NO_USER.equals(e.failure)) {
                            ToolController.addMessage(pageContext, ToolController.KEY_ERROR, "reg.registered.clickerId.failure", clickerId);
                        } else if (Failure.GO_LASTNAME.equals(e.failure)) {
                            ToolController.addMessage(pageContext, ToolController.KEY_ERROR, "reg.registered.clickerId.go.wrong.lastname", (Object[])null);
                        } else if (Failure.GO_NO_MATCH.equals(e.failure)) {
                            ToolController.addMessage(pageContext, ToolController.KEY_ERROR, "reg.registered.clickerId.go.invalid", clickerId);
                        } else {
                            ToolController.addMessage(pageContext, ToolController.KEY_ERROR, "reg.registered.clickerId.invalid", clickerId);
                        }
                    }
                }

            } else if ( (request.getParameter("activate") != null) ) {
                // First arrived at this page
                boolean activate = Boolean.parseBoolean( request.getParameter("activate") );
                if ( (request.getParameter("registrationId") == null) ) {
                    ToolController.addMessage(pageContext, ToolController.KEY_ERROR,
                            "reg.activate.registrationId.empty", (Object[])null);
                } else {
                    try {
                        Long registrationId = Long.parseLong( request.getParameter("registrationId") );
                        // save a new clicker registration
                        ClickerRegistration cr = this.getLogic().setRegistrationActive(registrationId, activate);
                        if (cr != null) {
                            ToolController.addMessage(pageContext, ToolController.KEY_INFO,
                                    "reg.activate.success."+cr.isActivated(), cr.getClickerId());
                        }
                    } catch (NumberFormatException e) {
                        ToolController.addMessage(pageContext, ToolController.KEY_ERROR,
                                "reg.activate.registrationId.nonnumeric", 
                                request.getParameter("registrationId") );
                    }
                }

            } else if ( (request.getParameter("remove") != null) ) {
                if ( (request.getParameter("registrationId") == null) ) {
                    ToolController.addMessage(pageContext, ToolController.KEY_ERROR,
                            "reg.activate.registrationId.empty", (Object[])null);
                } else {
                    try {
                        Long registrationId = Long.parseLong( request.getParameter("registrationId") );
                        // remove a new clicker registration by deactivating it
                        ClickerRegistration cr = this.getLogic().setRegistrationActive(registrationId, false);
                        if (cr != null) {
                            ToolController.addMessage(pageContext, ToolController.KEY_INFO,
                                    "reg.remove.success", cr.getClickerId());
                        }
                    } catch (NumberFormatException e) {
                        ToolController.addMessage(pageContext, ToolController.KEY_ERROR,
                                "reg.activate.registrationId.nonnumeric", 
                                request.getParameter("registrationId") );
                    }
                }

            } else {
                // invalid POST
                System.err.println("WARN: Invalid POST: does not contain register or activate, nothing to do");
            }
        }

        pageContext.setAttribute("regs", this.getAllVisibleItems(null));
        pageContext.setAttribute("isInstructor", this.isInstructor(), PageContext.REQUEST_SCOPE);
        // SSO handling
        pageContext.setAttribute("ssoEnabled", logic.isSingleSignOnEnabled());
        // added to allow special messages below the forms
        pageContext.setAttribute("belowMessages", 
                ToolController.getMessages(pageContext, ToolController.KEY_BELOW), 
                PageContext.REQUEST_SCOPE);
    }

    public void processInstructor(PageContext pageContext, HttpServletRequest request) {
        // admin/instructor check
        if (! this.isAdmin() && ! this.isInstructor()) {
            throw new SecurityException("Current user is not an instructor and cannot access the instructor view");
        }
        String courseId = request.getParameter("courseId");
        pageContext.setAttribute("courseId", courseId );
        if (courseId != null) {
            pageContext.setAttribute("courseTitle", this.getLogic().getCourseTitle(courseId) );
        }
        List<Course> courses = logic.getCoursesForInstructorWithStudents(courseId);
        pageContext.setAttribute("courses", courses );
        pageContext.setAttribute("coursesCount", courses.size());
        pageContext.setAttribute("showStudents", false );
        if (courseId != null && courses.size() == 1) {
            Course course = courses.get(0);
            pageContext.setAttribute("showStudents", true );
            pageContext.setAttribute("course", course );
            pageContext.setAttribute("students", course.students );
            pageContext.setAttribute("studentsCount", course.students.size() );
        }
        // SSO handling
        pageContext.setAttribute("ssoEnabled", logic.isSingleSignOnEnabled());
    }

    public void processInstructorSSO(PageContext pageContext, HttpServletRequest request) {
        // admin/instructor check
        if (! this.isAdmin() && ! this.isInstructor()) {
            throw new SecurityException("Current user is not an instructor and cannot access the instructor view");
        }
        // SSO handling
        boolean ssoEnabled = logic.isSingleSignOnEnabled();
        pageContext.setAttribute("ssoEnabled", ssoEnabled);
        if (ssoEnabled) {
            String userKey = null;
            if ( "POST".equalsIgnoreCase(request.getMethod()) ) {
                if ( (request.getParameter("generateKey") != null) ) {
                    userKey = logic.makeUserKey(null, true);
                    ToolController.addMessage(pageContext, ToolController.KEY_INFO,
                            "inst.sso.generated.new.key", (Object[])null);
                }
            }
            if (userKey == null) {
                userKey = logic.makeUserKey(null, false);
            }
            pageContext.setAttribute("ssoUserKey", userKey);
        }
    }

    public void processAdmin(PageContext pageContext, HttpServletRequest request) {
        // admin check
        if (! this.isAdmin()) {
            throw new SecurityException("Current user is not an admin and cannot access the admin view");
        }

        int pageNum = 1;
        int perPageNum = 20; // does not change
        if ( (request.getParameter("page") != null) ) {
            try {
                pageNum = Integer.parseInt( request.getParameter("page") );
                if (pageNum < 1) { pageNum = 1; }
            } catch (NumberFormatException e) {
                // nothing to do
                System.err.println("WARN: invalid page number: " + request.getParameter("page") + ":" + e);
            }
        }
        pageContext.setAttribute("page", pageNum);
        pageContext.setAttribute("perPage", perPageNum);
        String sort = "clickerId";
        if ( (request.getParameter("sort") != null) ) {
            sort = request.getParameter("sort");
        }
        pageContext.setAttribute("sort", sort);

        if ( "POST".equalsIgnoreCase(request.getMethod()) ) {
            if ( (request.getParameter("activate") != null) ) {
                // First arrived at this page
                boolean activate = Boolean.parseBoolean( request.getParameter("activate") );
                if ( (request.getParameter("registrationId") == null) ) {
                    ToolController.addMessage(pageContext, ToolController.KEY_ERROR,
                            "reg.activate.registrationId.empty", (Object[])null);
                } else {
                    try {
                        Long registrationId = Long.parseLong( request.getParameter("registrationId") );
                        // save a new clicker registration
                        ClickerRegistration cr = this.getLogic().setRegistrationActive(registrationId, activate);
                        if (cr != null) {
                            ToolController.addMessage(pageContext, ToolController.KEY_INFO,
                                    "admin.activate.success."+cr.isActivated(), cr.getClickerId(),
                                    this.getLogic().getUserDisplayName(cr.getOwnerId()) );
                        }
                    } catch (NumberFormatException e) {
                        ToolController.addMessage(pageContext, ToolController.KEY_ERROR,
                                "reg.activate.registrationId.nonnumeric", 
                                request.getParameter("registrationId") );
                    }
                }
            } else if ( (request.getParameter("remove") != null) ) {
                if ( (request.getParameter("registrationId") == null) ) {
                    ToolController.addMessage(pageContext, ToolController.KEY_ERROR,
                            "reg.activate.registrationId.empty", (Object[])null);
                } else {
                    try {
                        Long registrationId = Long.parseLong( request.getParameter("registrationId") );
                        ClickerRegistration cr = this.getLogic().getItemById(registrationId);
                        if (cr != null) {
                            this.getLogic().removeItem(cr);
                            ToolController.addMessage(pageContext, ToolController.KEY_INFO,
                                    "admin.delete.success", cr.getClickerId(), registrationId, 
                                    this.getLogic().getUserDisplayName(cr.getOwnerId()) );
                        }
                    } catch (NumberFormatException e) {
                        ToolController.addMessage(pageContext, ToolController.KEY_ERROR,
                                "reg.activate.registrationId.nonnumeric", 
                                request.getParameter("registrationId") );
                    }
                }
            } else if ( (request.getParameter("runner") != null) ) {
                // initiate the runner process
                String runnerType;
                if ( (request.getParameter("addAll") != null) ) {
                    runnerType = BigRunner.RUNNER_TYPE_ADD;
                } else if ( (request.getParameter("removeAll") != null) ) {
                    runnerType = BigRunner.RUNNER_TYPE_REMOVE;
                } else if ( (request.getParameter("syncAll") != null) ) {
                    runnerType = BigRunner.RUNNER_TYPE_SYNC;
                } else {
                    throw new IllegalArgumentException("Invalid request type: missing valid parameter");
                }
                try {
                    logic.startRunnerOperation(runnerType);
                    String msgKey = "admin.process.message." + runnerType;
                    ToolController.addMessage(pageContext, ToolController.KEY_INFO, msgKey, (Object[])null );
                } catch (ClickerLockException e) {
                    ToolController.addMessage(pageContext, ToolController.KEY_ERROR, "admin.process.message.locked", runnerType );
                } catch (IllegalStateException e) {
                    ToolController.addMessage(pageContext, ToolController.KEY_ERROR, "admin.process.message.locked", runnerType );
                }
            } else {
                // invalid POST
                System.err.println("WARN: Invalid POST: does not contain runner, remove, or activate, nothing to do");
            }
        }

        // put config data into page
        pageContext.setAttribute("ssoEnabled", logic.isSingleSignOnEnabled());
        if (logic.isSingleSignOnEnabled()) {
            pageContext.setAttribute("ssoSharedKey", logic.getSharedKey());
        }
        pageContext.setAttribute("domainURL", logic.domainURL);
        pageContext.setAttribute("workspacePageTitle", logic.workspacePageTitle);

        // put error data into page
        pageContext.setAttribute("recentFailures", logic.getFailures());

        // put runner status in page
        makeRunnerStatus(pageContext, true);

        // handling the calcs for paging
        int first = (pageNum - 1) * perPageNum;
        int totalCount = this.getLogic().countAllItems();
        int pageCount = (totalCount + perPageNum - 1) / perPageNum;
        pageContext.setAttribute("totalCount", totalCount);
        pageContext.setAttribute("pageCount", pageCount);
        pageContext.setAttribute("registrations", this.getLogic().getAllItems(first, perPageNum, sort, null, true));

        if (totalCount > 0) {
            StringBuilder sb = new StringBuilder();
            Date d = new Date();
            for (int i = 0; i < pageCount; i++) {
                int currentPage = i + 1;
                int currentStart = (i * perPageNum) + 1;
                int currentEnd = currentStart + perPageNum - 1;
                if (currentEnd > totalCount) {
                    currentEnd = totalCount;
                }
                String marker = "[" + currentStart + ".." + currentEnd + "]";
                if (currentPage == pageNum) {
                    // make it bold and not a link
                    sb.append("<span class=\"paging_current paging_item\">").append(marker).append("</span>\n");
                } else {
                    // make it a link
                    sb.append("<a class=\"paging_link paging_item\" href=\"").append(pageContext.findAttribute("adminPath")).append("&page=").append(currentPage).append("&sort=").append(sort).append("&nc=").append(d.getTime() + currentPage).append("\">").append(marker).append("</a>\n");
                }
            }
            pageContext.setAttribute("pagerHTML", sb.toString());
        }
    }

    public void makeRunnerStatus(PageContext pageContext, boolean clearOnComplete) {
        // check for running process and include the info in the page
        BigRunner runner = logic.getRunnerStatus();
        pageContext.setAttribute("runnerExists", runner != null );
        if (runner != null) {
            pageContext.setAttribute("runnerType", runner.getType());
            pageContext.setAttribute("runnerPercent", runner.getPercentCompleted());
            pageContext.setAttribute("runnerComplete", runner.isComplete());
            pageContext.setAttribute("runnerError", runner.isError());
            if (runner.isComplete() && clearOnComplete) {
                // clear the runner since it is completed
                logic.clearRunner();
            }
        } else {
            pageContext.setAttribute("runnerType", "none");
            pageContext.setAttribute("runnerPercent", 100);
            pageContext.setAttribute("runnerComplete", true);
            pageContext.setAttribute("runnerError", false);
        }
    }

    /**
     * Used to ensure that the view being displayed is valid 
     * and allowed for the current user
     * @param viewParam
     *            the value of the parameter "view"
     * @return the valid and allowed view for the given user
     */
    public String getValidView(String viewParam) {
        String view = VIEW_REGISTRATION;
        if (viewParam != null && !"".equals(viewParam)) {
            if (ArrayUtils.contains(VIEWS, viewParam)) {
                if (userAllowedForView(viewParam)) {
                    view = viewParam;
                }
            }
        }
        return view;
    }

    /**
     * Check if the user is allowed in a view
     * @param view the view constant from VIEWS
     * @return true if allowed, false if not
     */
    protected boolean userAllowedForView(String view) {
        boolean allowed = false;
        if (view != null && !"".equals(view)) {
            if (ArrayUtils.contains(VIEWS, view)) {
                String userId = externalLogic.getCurrentUserId();
                if (VIEW_ADMIN.equals(view)) {
                    if (externalLogic.isUserAdmin(userId)) {
                        allowed = true;
                    }
                } else if (VIEW_INSTRUCTOR.equals(view)) {
                    if (externalLogic.isUserAdmin(userId) || externalLogic.isUserInstructor(userId)) {
                        allowed = true;
                    }
                } else {
                    // everyone allowed on registration view
                    allowed = true;
                }
            }
        }
        return allowed;
    }


    /**************************************************************************
     * Services pass-throughs
     **************************************************************************
     */

    /**
     * This gets the list of clicker registrations for a current user (or visible to them)
     * 
     * @param locationId
     *            [OPTIONAL] a unique id which represents the current location of the user (entity
     *            reference)
     * @return a List of ClickerRegistration objects visible to the current user
     */
    public List<ClickerRegistration> getAllVisibleItems(String locationId) {
        String userId = externalLogic.getCurrentUserId();
        return logic.getAllVisibleItems(userId, locationId);
    }

    /**
     * Use this for passthroughs which require no additional work
     * @return the logic service
     */
    public IClickerLogic getLogic() {
        return this.logic;
    }

    public boolean isAdmin() {
        String userId = externalLogic.getCurrentUserId();
        return externalLogic.isUserAdmin(userId);
    }

    public boolean isInstructor() {
        String userId = externalLogic.getCurrentUserId();
        return externalLogic.isUserInstructor(userId);
    }

    /**************************************************************************
     * STATICS and methods for message handling
     **************************************************************************
     */

    private static final String ICLICKER_MESSAGES = "iClickerMessages";

    public static final String MESSAGE_BUNDLE = "i18n.Messages";
    public static final String KEY_INFO = "INFO";
    public static final String KEY_ERROR = "ERROR";
    public static final String KEY_BELOW = "BELOW";

    public static String springMessageBundle() {
        return "classpath:"+MESSAGE_BUNDLE.replace('.', '/');
    }

    @SuppressWarnings("unchecked")
    public static void addMessage(PageContext context, String key, String message) {
        if (context == null || key == null) {
            throw new IllegalArgumentException("context ("+context+") and key ("+key+") must both not be null");
        }
        if (message != null && ! "".equals(message)) {
            String keyVal = ICLICKER_MESSAGES + key;
            if (context.getAttribute(keyVal) == null) {
                context.setAttribute(keyVal, new Vector<String>(), PageContext.REQUEST_SCOPE);
            }
            List<String> l = (List<String>) context.getAttribute(keyVal, PageContext.REQUEST_SCOPE);
            l.add(message);
        }
    }

    public static void addMessage(PageContext context, String key, String messageKey, Object... args) {
        if (context == null || key == null) {
            throw new IllegalArgumentException("context ("+context+") and key ("+key+") must both not be null");
        }
        if (messageKey != null && ! "".equals(messageKey)) {
            // get the message from messageSource if possible
            MessageSource ms = (MessageSource) context.findAttribute("messageSource");
            Locale locale = context.getRequest().getLocale();
            String message;
            try {
                message = ms.getMessage(messageKey, args, locale);
            } catch (NoSuchMessageException e) {
                message = "{{INVALID KEY:"+messageKey+"}}";
            }
            addMessage(context, key, message);
        }
    }

    @SuppressWarnings("unchecked")
    public static String[] getMessages(PageContext context, String key) {
        if (context == null || key == null) {
            throw new IllegalArgumentException("context ("+context+") and key ("+key+") must both not be null");
        }
        String[] messages;
        String keyVal = ICLICKER_MESSAGES + key;
        if (context.getAttribute(keyVal, PageContext.REQUEST_SCOPE) == null) {
            messages = new String[] {};
        } else {
            List<String> l = (List<String>) context.getAttribute(keyVal, PageContext.REQUEST_SCOPE);
            messages = l.toArray(new String[l.size()]);
        }
        return messages;
    }

    public ReloadableResourceBundleMessageSource getMessageSource() {
        return messageSource;
    }

    public Locale getMessageLocale(@SuppressWarnings("UnusedParameters") PageContext context) {
        // l = context.getRequest().getLocale();
        return externalLogic.getCurrentLocale();
    }


    /**************************************************************************
     * Service setters for spring
     **************************************************************************
     */

    public void setLogic(IClickerLogic logic) {
        this.logic = logic;
    }

    public void setExternalLogic(ExternalLogic externalLogic) {
        this.externalLogic = externalLogic;
    }

    public void setMessageSource(ReloadableResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
    }

}