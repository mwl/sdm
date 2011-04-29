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

package com.trifork.stamdata.replication;

import static org.slf4j.LoggerFactory.getLogger;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceServletContextListener;
import com.trifork.stamdata.replication.db.DatabaseModule;
import com.trifork.stamdata.replication.gui.GuiModule;
import com.trifork.stamdata.replication.gui.security.saml.SamlSecurityModule;
import com.trifork.stamdata.replication.gui.security.twowayssl.TwoWaySslSecurityModule;
import com.trifork.stamdata.replication.logging.LoggingModule;
import com.trifork.stamdata.replication.monitoring.MonitoringModule;
import com.trifork.stamdata.replication.replication.RegistryModule;
import com.trifork.stamdata.replication.security.UnrestrictedSecurityModule;
import com.trifork.stamdata.replication.security.dgws.DGWSModule;
import com.trifork.stamdata.replication.security.ssl.SslModule;


public class ApplicationContextListener extends GuiceServletContextListener {

	private static final Logger logger = getLogger(ApplicationContextListener.class);

	@Override
	protected Injector getInjector() {

		Injector injector = null;

		try {
			logger.info("Loading configuration.");

			CompositeConfiguration config = new CompositeConfiguration();

			URL deploymentConfig = getClass().getClassLoader().getResource("stamdata-replication.properties");
			if (deploymentConfig != null) {
				logger.info("Using stamdata-replication.properties");
				config.addConfiguration(new PropertiesConfiguration(getClass().getClassLoader().getResource("stamdata-replication.properties")));
			}

			config.addConfiguration(new PropertiesConfiguration(getClass().getClassLoader().getResource("config.properties")));

			logger.info("Configuring Stamdata Service.");

			// The order these modules are added is not unimportant.
			// Since some of them add filters to the filter chain
			// they must be placed in the right order, e.i. some of
			// the filters depend on settings from previous filters.

			List<Module> modules = new ArrayList<Module>();

			// CONFIGURE DATA ACCESS
			
			modules.add(new DatabaseModule(
				config.getString("db.connection.driverClass"),
				config.getString("db.connection.sqlDialect"),
				config.getString("db.connection.jdbcURL"),
				config.getString("db.connection.username"),
				config.getString("db.connection.password", null)
			));

			// CONFIGURE PROFILING & MONITORING

			modules.add(new MonitoringModule());

			// CONFIGURE AUTHENTICATION & AUTHORIZATION

			String security = config.getString("security");
			if ("dgws".equals(security)) {
				modules.add(new DGWSModule());
			} else if ("twowayssl".equals(security)) {
				modules.add(new SslModule(config.getBoolean("security.ssl.test")));
			} else {
				modules.add(new UnrestrictedSecurityModule());
			}

			// CONFIGURE WHERE TO FIND THE VIEW CLASSES

			modules.add(new RegistryModule());

			// CONFIGURE THE ADMIN GUI

			modules.add(new GuiModule(
				config.getString("rid2cpr.endpoint"),
				getClass().getClassLoader().getResource(config.getString("rid2cpr.keystore")).toExternalForm(),
				config.getString("rid2cpr.keystorePassword"),
				config.getInt("rid2cpr.callTimeout"),
				getWhiteList(config)
			));
			
			String guiSecurity = config.getString("gui.security");
			if("saml".equals(guiSecurity)) {
				modules.add(new SamlSecurityModule());
			}
			else if ("twowayssl".equals(guiSecurity)) {
				modules.add(new TwoWaySslSecurityModule());
			}
			else {
				// TODO
			}

			// LOGGING

			modules.add(new LoggingModule());

			injector = Guice.createInjector(modules);

			logger.info("Service configured.");
		}
		catch (Exception e) {
			logger.error("Initialization failed do to a configuration error.", e);
		}

		return injector;
	}

	private Map<String, String> getWhiteList(final Configuration config) {

		String[] cvrs = config.getStringArray("whitelist");
		String[] names = config.getStringArray("whitelistNames");

		Map<String, String> whiteList = new HashMap<String, String>();

		for (int i = 0; i < cvrs.length; i++)
			whiteList.put(names[i], cvrs[i]);

		return whiteList;
	}
}
