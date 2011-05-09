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

package com.trifork.stamdata.importer.takst;


import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.trifork.stamdata.config.MySQLConnectionManager;
import com.trifork.stamdata.dao.AuditingPersister;
import com.trifork.stamdata.importer.takst.TakstImporter;
import com.trifork.stamdata.importer.takst.model.Doseringskode;
import com.trifork.stamdata.importer.takst.model.Takst;
import com.trifork.stamdata.importer.takst.model.TakstDataset;
import com.trifork.stamdata.util.DateUtils;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.*;


public class TakstImporterTest {

	@Before
	@After
	public void cleanup() throws Exception {

		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		Statement stmt = con.createStatement();
		stmt.executeQuery("truncate table LaegemiddelDoseringRef");
		stmt.executeQuery("truncate table TakstVersion");
		stmt.close();
		con.close();
	}

	@Test
	public void testAreRequiredInputFilesPresent() throws Exception {

		TakstImporter ti = new TakstImporter();
		File dir = FileUtils.toFile(getClass().getClassLoader().getResource("data/takst/initial/"));
		List<File> files = Arrays.asList(dir.listFiles());

		assertTrue(ti.checkRequiredFiles(files));
	}

	@Test
	public void testAreRequiredInputFilesPresent2() throws Exception {

		TakstImporter ti = new TakstImporter();
		File dir = FileUtils.toFile(getClass().getClassLoader().getResource("data/takst/incomplete/"));
		List<File> files = Arrays.asList(dir.listFiles());

		assertFalse(ti.checkRequiredFiles(files));
	}

	@Test
	public void testGetNextImportExpectedBefore() throws SQLException {

		assertTrue(new TakstImporter().getNextImportExpectedBefore(null).before(Calendar.getInstance()));

		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		Statement stmt = con.createStatement();
		stmt.execute("INSERT INTO TakstVersion (TakstUge, ModifiedBy, ModifiedDate, CreatedBy, CreatedDate, validFrom, validTo) " + "" + "VALUES (\"201001\", \"UnitTest\", \"2010-01-01 00:00:00\", \"UnitTest\", \"2010-01-01 00:00:00\", \"2010-01-01 00:00:00\", \"2999-12-31 00:00:00\") ");
		stmt.close();
		con.close();

		// We expect that the next takst after the first week in 2010 will be 3
		// week in 2010, or latest sat. January 16th at noon
		assertEquals(DateUtils.toCalendar(2010, 1, 16, 12, 0, 0).getTime(), new TakstImporter().getNextImportExpectedBefore(DateUtils.toCalendar(2008, 12, 12, 15, 10, 0)).getTime());
	}

	@Test
	public void testLaegemiddelDoseringRef() throws Exception {

		Calendar from = DateUtils.toCalendar(2008, 01, 01);
		Calendar to = DateUtils.toCalendar(2009, 01, 01);
		Takst takst = new Takst(from, to);
		Doseringskode d = new Doseringskode();
		d.setDoseringskode(1l);
		d.setDrugid(2l);
		List<Doseringskode> dk = new ArrayList<Doseringskode>();
		dk.add(d);
		TakstDataset<Doseringskode> dataset = new TakstDataset<Doseringskode>(takst, dk, Doseringskode.class);
		takst.addDataset(dataset);
		assertEquals(1, takst.getEntities().size());
		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		AuditingPersister dao = new AuditingPersister(con);
		dao.persistCompleteDataset(dataset);

		ResultSet rs = con.createStatement().executeQuery("select count(*) from LaegemiddelDoseringRef");
		rs.next();
		assertEquals(1, rs.getInt(1));
		dao.persistCompleteDataset(dataset);
		rs = con.createStatement().executeQuery("select count(*) from LaegemiddelDoseringRef");
		rs.next();
		assertEquals(1, rs.getInt(1));
	}

}