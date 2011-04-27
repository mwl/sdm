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

import dk.trifork.sdm.dao.AuditingPersister;
import dk.trifork.sdm.dao.DatabaseTableWrapper;
import dk.trifork.sdm.dao.DatabaseTableWrapper.StamdataEntityVersion;
import dk.trifork.sdm.importer.takst.model.DivEnheder;
import dk.trifork.sdm.importer.takst.model.Laegemiddel;
import dk.trifork.sdm.importer.takst.model.Takst;
import dk.trifork.sdm.importer.takst.model.TakstDataset;
import dk.trifork.sdm.model.StamdataEntity;
import dk.trifork.sdm.util.DateUtils;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;


public class MySQLStamDAOTest {

	private Takst takst;
	private Laegemiddel laegemiddel;
	private AuditingPersister dao;
	private DatabaseTableWrapper laegemiddeltableMock;

	@Before
	public void setUp() throws Exception {

		takst = new Takst(DateUtils.toCalendar(2009, 7, 1), DateUtils.toCalendar(2009, 7, 14));
		// Add a dataset to the takst with one member
		List<Laegemiddel> list = new ArrayList<Laegemiddel>();
		laegemiddel = new Laegemiddel();
		laegemiddel.setDrugid(1l);
		laegemiddel.setNavn("Zymedolinatexafylitungebraekker");
		list.add(laegemiddel);
		TakstDataset<Laegemiddel> dataset = new TakstDataset<Laegemiddel>(takst, list, Laegemiddel.class);
		takst.addDataset(dataset);

		/*
		 * // Add an empty dataset to the takst (should be ignored)
		 * List<Pakning> tomListe = new ArrayList<Pakning>(); Dataset<Pakning>
		 * tomtDataset = new Dataset<Pakning>(takst, tomListe, Pakning.class);
		 * takst.addDataset(tomtDataset);
		 */
		// Add a dataset to the takst, which should be ignored because it is not
		// rootMember
		List<DivEnheder> enheder = new ArrayList<DivEnheder>();
		DivEnheder enhed = new DivEnheder();
		enhed.setTekst("millimol pr. gigajoule");
		TakstDataset<DivEnheder> hiddenDataset = new TakstDataset<DivEnheder>(takst, enheder, DivEnheder.class);
		takst.addDataset(hiddenDataset);

		// ------ Setup database mocks -------
		Connection con = mock(Connection.class);
		AuditingPersister realDao = new AuditingPersister(con);
		dao = spy(realDao);
		laegemiddeltableMock = mock(DatabaseTableWrapper.class);
		doReturn(laegemiddeltableMock).when(dao).getTable(Laegemiddel.class);
	}

	@Test
	public void testPersistOneLaegemiddel() throws Exception {

		when(laegemiddeltableMock.fetchEntityVersions(anyObject(), any(Calendar.class), any(Calendar.class))).thenReturn(false);
		// Simulate no existing entities

		dao.persistCompleteDataset(takst.getDatasets());

		// Verify that the new record is inserted
		verify(laegemiddeltableMock, times(1)).insertRow(eq(laegemiddel), any(Calendar.class));
	}

	@Test
	public void testDeltaPutChanged() throws Exception {

		// Simulate that the entity is already present.
		when(laegemiddeltableMock.fetchEntityVersions(anyObject(), any(Calendar.class), any(Calendar.class))).thenReturn(true);

		// Simulate that the existing row's validity range is 1950 to infinity.
		// So it must be updated.
		when(laegemiddeltableMock.getCurrentRowValidFrom()).thenReturn(DateUtils.toCalendar(1950, 01, 1));
		when(laegemiddeltableMock.getCurrentRowValidTo()).thenReturn(DateUtils.FUTURE);

		// Simulate that the entity has changed.
		when(laegemiddeltableMock.dataInCurrentRowEquals(any(StamdataEntity.class))).thenReturn(false);

		dao.persistCompleteDataset(takst.getDatasets());

		// Verify that the new record is inserted
		verify(laegemiddeltableMock, times(1)).insertAndUpdateRow(eq(laegemiddel), any(Calendar.class));

		// Verify that the existing record is updated
		verify(laegemiddeltableMock, times(1)).updateValidToOnCurrentRow(eq(takst.getValidFrom()), any(Calendar.class));
	}

	@Test
	public void testDeltaPutUnchanged() throws Exception {

		// Simulate that the entity is already present.
		when(laegemiddeltableMock.fetchEntityVersions(anyObject(), any(Calendar.class), any(Calendar.class))).thenReturn(true);

		// Simulate that the existing row's validity range is 1950 to infinity.
		when(laegemiddeltableMock.getCurrentRowValidFrom()).thenReturn(DateUtils.toCalendar(1950, 01, 1));
		when(laegemiddeltableMock.getCurrentRowValidTo()).thenReturn(DateUtils.FUTURE);

		// Simulate that the entity is unchanged.
		when(laegemiddeltableMock.dataInCurrentRowEquals(any(StamdataEntity.class))).thenReturn(true);

		dao.persistCompleteDataset(takst.getDatasets());

		// Verify that the new record is inserted
		verify(laegemiddeltableMock, times(0)).insertRow(eq(laegemiddel), any(Calendar.class));

		// Verify that the existing record is not updated
		verify(laegemiddeltableMock, times(0)).updateValidToOnCurrentRow(eq(takst.getValidFrom()), any(Calendar.class));
	}

	@Test
	public void testDeltaPutRemoved() throws Exception {

		// An empty takst
		takst = new Takst(DateUtils.toCalendar(2009, 7, 1), DateUtils.toCalendar(2009, 7, 14));
		// ..with an empty dataset
		TakstDataset lmr = new TakstDataset(takst, new ArrayList<Laegemiddel>(), Laegemiddel.class);
		takst.addDataset(lmr);

		List<StamdataEntityVersion> sev = new ArrayList<StamdataEntityVersion>();
		// Simulate that there is one record
		StamdataEntityVersion sv = new StamdataEntityVersion();
		sv.id = 1;

		// Simulate that the existing row's validity range is 1950 to infinity.
		sv.validFrom = DateUtils.toCalendar(1950, 01, 1);
		sev.add(sv);

		when(laegemiddeltableMock.getEntityVersions(any(Calendar.class), any(Calendar.class))).thenReturn(sev);

		dao.persistCompleteDataset(takst.getDatasets());

		// Verify that the existing record is updated
		verify(laegemiddeltableMock, times(1)).updateValidToOnEntityVersion(eq(DateUtils.toCalendar(2009, 7, 1)), any(StamdataEntityVersion.class), any(Calendar.class));

	}

}
