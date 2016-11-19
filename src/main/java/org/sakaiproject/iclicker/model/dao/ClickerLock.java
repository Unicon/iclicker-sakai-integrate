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
package org.sakaiproject.iclicker.model.dao;

import java.util.Date;

/**
 * This defines locks to allow for cluster operations
 */
public class ClickerLock implements java.io.Serializable {

	private Long id;

	private Date lastModified;

	/**
	 * The name of the lock
	 */
	private String name;

	/**
	 * The holder (owner) of this lock
	 */
	private String holder;

	// Constructors

	/** default constructor */
	public ClickerLock() {
	}

	/** full constructor */
	public ClickerLock(String name, String holder) {
		this.lastModified = new Date();
		this.name = name;
		this.holder = holder;
	}

	// Property accessors
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getLastModified() {
		return this.lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
   
   public String getHolder() {
      return holder;
   }
   
   public void setHolder(String holder) {
      this.holder = holder;
   }


}
