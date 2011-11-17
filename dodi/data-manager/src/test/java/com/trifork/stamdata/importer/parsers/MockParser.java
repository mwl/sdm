/**
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Contributor(s): Contributors are attributed in the source code
 * where applicable.
 *
 * The Original Code is "Stamdata".
 *
 * The Initial Developer of the Original Code is Trifork Public A/S.
 *
 * Portions created for the Original Code are Copyright 2011,
 * Lægemiddelstyrelsen. All Rights Reserved.
 *
 * Portions created for the FMKi Project are Copyright 2011,
 * National Board of e-Health (NSI). All Rights Reserved.
 */
package com.trifork.stamdata.importer.parsers;

import com.trifork.stamdata.importer.parsers.OutOfSequenceException;
import com.trifork.stamdata.importer.parsers.Parser;
import com.trifork.stamdata.importer.parsers.ParserException;
import com.trifork.stamdata.importer.parsers.annotations.ParserInformation;
import org.joda.time.Instant;

import java.io.File;
import java.sql.Connection;

@ParserInformation(id="foo", name="Foo")
public class MockParser implements Parser
{
    private boolean throwOnProcess = false;

    @Override
    public void process(File dataSet, Connection connection, Instant transactionTime) throws OutOfSequenceException, ParserException, Exception
    {
        if (throwOnProcess) throw new RuntimeException("Fake Exception");
    }

    public void throwOnProcess()
    {
        throwOnProcess = true;
    }
}