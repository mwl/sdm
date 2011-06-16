
// The contents of this file are subject to the Mozilla Public
// License Version 1.1 (the "License"); you may not use this file
// except in compliance with the License. You may obtain a copy of
// the License at http://www.mozilla.org/MPL/
//
// Software distributed under the License is distributed on an "AS
// IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
// implied. See the License for the specific language governing
// rights and limitations under the License.
//
// Contributor(s): Contributors are attributed in the source code
// where applicable.
//
// The Original Code is "Stamdata".
//
// The Initial Developer of the Original Code is Trifork Public A/S.
//
// Portions created for the Original Code are Copyright 2011,
// Lægemiddelstyrelsen. All Rights Reserved.
//
// Portions created for the FMKi Project are Copyright 2011,
// National Board of e-Health (NSI). All Rights Reserved.

package com.trifork.stamdata.logging;

import static javax.persistence.TemporalType.TIMESTAMP;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;


@Entity
public class LogEntry {

	@Id
	@GeneratedValue
	private String id;

	private String message;

	@Temporal(TIMESTAMP)
	private Date createdAt;

	protected LogEntry() {

	}

	public LogEntry(String message) {

		this.message = message;
	}

	public String getId() {

		return id;
	}

	public String getMessage() {

		return message;
	}

	public Date getCreatedAt() {

		return createdAt;
	}
}