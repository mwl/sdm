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

package dk.trifork.sdm.importer.sor;

import dk.trifork.sdm.config.MySQLConnectionManager;
import dk.trifork.sdm.dao.AuditingPersister;
import dk.trifork.sdm.importer.FileImporterControlledIntervals;
import dk.trifork.sdm.importer.exceptions.FileImporterException;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SORImporter implements FileImporterControlledIntervals {
	private static Logger logger = LoggerFactory.getLogger(SORImporter.class);
	
    @Override
    public boolean checkRequiredFiles(List<File> files) {
        if (files.size() == 0)
            return false;
        boolean xmlpresent = false;
        for (File file : files) {
            if (file.getName().endsWith(".xml"))
            	xmlpresent = true;
        }
        return xmlpresent;
    }

    @Override
    public void run(List<File> files) throws FileImporterException {
        Connection connection = null;
        try {
            connection = MySQLConnectionManager.getConnection();
            AuditingPersister dao = new AuditingPersister(connection);
            for (File file : files) {
            	SORDataSets dataSets = SORParser.parse(file);
                dao.persistCompleteDataset(dataSets.getPraksisDS());
                dao.persistCompleteDataset(dataSets.getYderDS());
                dao.persistCompleteDataset(dataSets.getSygehusDS());
                dao.persistCompleteDataset(dataSets.getSygehusAfdelingDS());
                dao.persistCompleteDataset(dataSets.getApotekDS());
            }
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                logger.error("Cannot rollback", e1);
            }
            String mess = "Error using database during import of autorisationsregister";
            logger.error(mess, e);
            throw new FileImporterException(mess, e);
        } finally {
            MySQLConnectionManager.close(connection);
        }
    }

    /**
     * Should be updated every day 
     */
    @Override
    public Calendar getNextImportExpectedBefore(Calendar lastImport) {
        Calendar cal;
        if (lastImport == null)
            cal = Calendar.getInstance();
        else cal = ((Calendar) lastImport.clone());
        cal.add(Calendar.DATE, 3);
        return cal;
	}

}
