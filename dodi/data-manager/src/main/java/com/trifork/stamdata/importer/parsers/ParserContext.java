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

import com.google.inject.Injector;
import com.trifork.stamdata.importer.jobs.ImportTimeManager;
import org.joda.time.DateTime;

public class ParserContext
{
    private Class<? extends Parser> parserClass;
    private int minimumImportFrequency;
    private boolean isRunning = false;

    public ParserContext(Class<? extends Parser> parserClass, int minimumImportFrequency)
    {
        this.parserClass = parserClass;
        this.minimumImportFrequency = minimumImportFrequency;
    }

    public Class<? extends Parser> getParserClass()
    {
        return parserClass;
    }

    public int getMinimumImportFrequency()
    {
        return minimumImportFrequency;
    }

    /**
     * Indicated whether a file delivery is overdue.
     *
     * If no files have previously been imported, this method always returns
     * false.
     *
     * @return true if the parser expected files but has not received any.
     */
    public boolean isOverdue()
    {
        return hasBeenRun() && getNextDeadline().isBeforeNow();
    }

    /**
     * The deadline for when the next files have to have been imported.
     *
     * The returned date will always be at midnight to avoid the day of time
     * slipping every time a new batch is imported.
     *
     * @return the timestamp with the deadline.
     */
    public DateTime getNextDeadline()
    {
        return getLatestRunTime().plusDays(minimumImportFrequency).toDateMidnight().toDateTime();
    }

    public DateTime getLatestRunTime()
    {
        // TODO: There are two ways the last run time is stored.

        return ImportTimeManager.getLastImportTime(identifier());
    }

    public boolean hasBeenRun()
    {
        return getLatestRunTime() != null;
    }

    public String identifier()
    {
        return Parsers.getIdentifier(getParserClass());
    }

    public boolean isOK(Injector injector)
    {
        // HACK: The inbox should not be here. Make a ParserManager class that knows stuff like this.

        Injector childInjector = injector.createChildInjector(ParserModule.using(this));
        Inbox inbox = childInjector.getInstance(Inbox.class);
        
        return !inbox.isLocked();
    }

    public String getHumanName()
    {
        return Parsers.getName(parserClass);
    }

    public boolean isRunning()
    {
        return isRunning;
    }

    public void setIsRunning(boolean isRunning)
    {
        this.isRunning = isRunning;
    }
}