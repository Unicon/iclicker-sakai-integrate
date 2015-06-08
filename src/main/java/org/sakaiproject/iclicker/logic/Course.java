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
package org.sakaiproject.iclicker.logic;

import java.util.List;

/**
 * Represents a course
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public class Course {

    public String id;
    public String title;
    public String description;
    /**
     * This is the timecode (seconds) of the time when this course was created
     */
    private long createdTime;
    public boolean published;

    public List<Student> students = null;

    public Course(String id, String title) {
        this(id, title, null);
    }

    public Course(String id, String title, String description) {
        this(id, title, description, (System.currentTimeMillis() / 1000), true);
    }

    /**
     * @param id the course id
     * @param title the title
     * @param description
     * @param createdTime the timecode (seconds) of the time when this course was created (not in milliseconds)
     * @param published true if the course is published/available to students, false otherwise
     */
    public Course(String id, String title, String description, long createdTime, boolean published) {
        this.id = id;
        this.title = title;
        this.description = description;
        if (createdTime > (System.currentTimeMillis() / 1000)) {
            // must have used the milliseconds version instead
            createdTime = (createdTime / 1000);
        }
        this.createdTime = createdTime;
        this.published = published;
    }

    @Override
    public String toString() {
        return id + ":" + title;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    /**
     * NOTE: This is the unix timecode in seconds and NOT the milliseconds returned in java normally
     * 
     * @return the unix timecode (seconds) when this course was created
     */
    public long getCreatedTime() {
        return createdTime;
    }

    public boolean isPublished() {
        return published;
    }

    public List<Student> getStudents() {
        return students;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Course other = (Course) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
}
