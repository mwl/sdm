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

package com.trifork.stamdata.spooler;

import it.sauronsoftware.cron4j.Scheduler;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trifork.stamdata.config.Configuration;


/**
 * FileSpooler. Initiates and monitor file spoolers.
 *
 * @author Jan Buchholdt
 *
 */
public class SpoolerManager {

	private static final Logger logger = LoggerFactory.getLogger(SpoolerManager.class);

	Map<String, FileSpoolerImpl> spoolers = new HashMap<String, FileSpoolerImpl>();
	Map<String, JobSpoolerImpl> jobSpoolers = new HashMap<String, JobSpoolerImpl>();

	private static final int POLLING_INTERVAL = Configuration.getInt("inputfile.polling.interval");
	private Timer timer = new Timer(true);

	private List<JobSpoolerImpl> jobQueue = Collections.synchronizedList(new LinkedList<JobSpoolerImpl>());
	private Scheduler jobScheduler = new Scheduler();

	public SpoolerManager(String rootDir) {

		String spoolerSetup = Configuration.getString("spooler");

		logger.info("The following spoolers are configured: " + spoolerSetup);
		logger.info("The global root dir is set to: " + rootDir);

		if (spoolerSetup.length() == 0) {
			logger.error("Manager created but no spooler configured. Please configure a spooler");
			return;
		}

		if (spoolerSetup != null && spoolerSetup.length() != 0) {
			for (String spoolerName : spoolerSetup.split(",")) {
				spoolers.put(spoolerName, new FileSpoolerImpl(new FileSpoolerSetup(spoolerName, rootDir)));
			}
		}

		String jobSpoolerSetup = Configuration.getString("jobspooler");
		logger.info("The following job spoolers are configured: " + jobSpoolerSetup);

		if (jobSpoolerSetup != null && jobSpoolerSetup.length() != 0) {
			for (String jobSpoolerName : jobSpoolerSetup.split(",")) {
				JobSpoolerImpl jobSpooler = new JobSpoolerImpl(new JobSpoolerSetup(jobSpoolerName));
				jobSpoolers.put(jobSpoolerName, jobSpooler);
				jobScheduler.schedule(jobSpooler.getSetup().getSchedule(), new GernericJobSpoolerExecutor(jobSpooler));
			}

			jobScheduler.start();
			TimerTask pollTask = new PollingTask();
			timer.schedule(pollTask, 10 * 1000, POLLING_INTERVAL * 1000);
		}
	}

	public void destroy() {

		timer.cancel();
		jobScheduler.stop();
	}

	/**
	 * Checks that all configured spoolers exist and are running
	 */
	public boolean isAllSpoolersRunning() {

		for (FileSpoolerImpl spooler : spoolers.values()) {
			if (!spooler.getStatus().equals(FileSpoolerImpl.Status.RUNNING)) {
				return false;
			}
		}

		for (JobSpoolerImpl spooler : jobSpoolers.values()) {
			if (!spooler.getStatus().equals(JobSpoolerImpl.Status.RUNNING)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * @param uriString
	 * @return the uri converted to a file path. null if the uri was not a file
	 *         uri
	 */
	public static String uri2filepath(String uriString) {

		URI uri;

		try {
			uri = new URI(uriString);
			if (!"file".equals(uri.getScheme())) {
				String errorMessage = "uri2filepath(" + uriString + ") can only convert uri with scheme: 'file'!";
				logger.error(errorMessage);
				return null;
			}
			return uri.getPath();
		}
		catch (URISyntaxException e) {
			String errorMessage = "uri2filepath must be called with a uri";
			logger.error(errorMessage);
			return null;
		}
	}

	public boolean isAllRejectedDirsEmpty() {

		for (FileSpoolerImpl spooler : spoolers.values()) {
			if (!spooler.isRejectedDirEmpty()) {
				return false;
			}
		}
		return true;
	}

	public boolean isRejectDirEmpty(String type) {

		FileSpoolerImpl spooler = spoolers.get(type);
		if (spooler == null) return false;
		return spooler.isRejectedDirEmpty();
	}

	public boolean isNoOverdueImports() {

		for (FileSpoolerImpl spooler : spoolers.values()) {
			if (spooler.isOverdue()) {
				return false;
			}
		}
		return true;
	}

	public Map<String, FileSpoolerImpl> getSpoolers() {

		return spoolers;
	}

	public FileSpoolerImpl getSpooler(String type) {

		return spoolers.get(type);
	}

	public Map<String, JobSpoolerImpl> getJobSpoolers() {

		return jobSpoolers;
	}

	public JobSpoolerImpl getJobSpooler(String type) {

		return jobSpoolers.get(type);
	}


	public class PollingTask extends TimerTask {

		Throwable t;

		public void run() {

			ExecutePendingJobs();
			for (FileSpoolerImpl spooler : spoolers.values()) {
				try {
					spooler.execute();
				}
				catch (Throwable t) {
					if (this.t == null || !t.getMessage().equals(this.t.getMessage())) {
						logger.debug("Caught throwable while polling. Only logging once to avoid log file spamming", t);
						this.t = t;
					}
				}
				ExecutePendingJobs();
			}
		}

	}

	private void ExecutePendingJobs() {

		Throwable lastt = null;

		while (!jobQueue.isEmpty()) {
			JobSpoolerImpl next = jobQueue.get(0);
			try {
				next.execute();
				jobQueue.remove(0);
			}
			catch (Throwable t) {
				if (lastt == null || !t.getMessage().equals(lastt.getMessage())) {
					logger.debug("Caught throwable while running job " + next.getName() + ". Only logging once to avoid log file spamming", t);
					lastt = t;
				}

			}
		}
	}


	public class GernericJobSpoolerExecutor implements Runnable {

		final private JobSpoolerImpl jobImpl;

		public GernericJobSpoolerExecutor(JobSpoolerImpl jobImpl) {

			super();
			this.jobImpl = jobImpl;
		}

		@Override
		public void run() {

			// The job is activated. Add it to the jobQueue.
			// The JobQueue is emptied in the polling task to avoid two jobs
			// running simultaneously
			jobQueue.add(jobImpl);
		}

	}

}