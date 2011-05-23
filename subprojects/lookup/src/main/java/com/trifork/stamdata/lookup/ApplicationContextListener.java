package com.trifork.stamdata.lookup;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceServletContextListener;
import com.trifork.configuration.SystemPropertyBasedConfigurationLoader;
import com.trifork.stamdata.lookup.rest.RestModule;
import com.trifork.stamdata.lookup.security.SecurityModule;
import com.trifork.stamdata.ssl.CommonSslModule;

public class ApplicationContextListener extends GuiceServletContextListener {

	private static final Logger logger = getLogger(ApplicationContextListener.class);
	private static final String STAMDATA_ENVIRONMENT_STRING_SYSPROP = "sdm.environment";
	private static final String STAMDATA_CONFIG_DIRECTORY_SYSPROP = "sdm.config.directory";

	@Override
	protected Injector getInjector() {

		Injector injector = null;

		try {
			logger.info("Loading configuration.");
			Configuration config = new SystemPropertyBasedConfigurationLoader(
					"lookup", STAMDATA_ENVIRONMENT_STRING_SYSPROP,
					STAMDATA_CONFIG_DIRECTORY_SYSPROP).loadConfiguration();

			logger.info("Configuring Stamdata Service.");

			List<Module> modules = new ArrayList<Module>();
			boolean useOcesTest = config.getBoolean("security.ssl.test");
			String sslTerminationMethod = config.getString("security.ssl.termination.method");
			modules.add(new CommonSslModule(useOcesTest, sslTerminationMethod));
			@SuppressWarnings("unchecked")
			List<String> authorizedClients = config.getList("security.authorized.clients");
			modules.add(new SecurityModule(authorizedClients));
			modules.add(new RestModule());
			injector = Guice.createInjector(modules);

		} catch (Exception e) {
			throw new RuntimeException(
					"Initialization failed do to a configuration error.", e);
		}
		return injector;
	}

}
