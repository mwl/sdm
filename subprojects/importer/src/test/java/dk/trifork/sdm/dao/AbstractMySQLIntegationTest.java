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

package dk.trifork.sdm.dao;

import dk.trifork.sdm.config.MySQLConnectionManager;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Calendar;

public class AbstractMySQLIntegationTest {

	protected Calendar t0 = Calendar.getInstance();
	protected Calendar t1 = Calendar.getInstance();
	protected Calendar t2 = Calendar.getInstance();
	protected Calendar t3 = Calendar.getInstance();
	protected Calendar t4 = Calendar.getInstance();
	protected Calendar t1000 = Calendar.getInstance();

	@Before
	public void setup() throws Exception {
		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		Statement stmt = con.createStatement();
		stmt.executeUpdate("truncate table TakstVersion");
		stmt.close();
		con.close();
	}

	@Before
	public void initDates(){
		t0.clear();
		t1.clear();
		t2.clear();
		t3.clear();
		t4.clear();
		t1000.clear();
		t0.set(2000, 0, 1, 1,2,3);
		t1.set(2001, 0, 1, 1,2,3);
		t2.set(2002, 0, 1, 1,2,3);
		t3.set(2003, 0, 1, 1,2,3);
		t4.set(2003, 0, 1, 1,2,3);
		t1000.set(3003, 0, 1, 1,2,3);
	}

	@Test
	public void dummyTest(){
	}
}

