package dk.nsi.stamdata.cpr;

import com.google.inject.*;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.trifork.stamdata.ComponentMonitor;
import com.trifork.stamdata.ConfigurationLoader;
import com.trifork.stamdata.MonitoringModule;
import dk.nsi.dgws.DGWSFilterSystemIDCardProvider;
import dk.nsi.dgws.DgwsIdcardFilter;
import dk.nsi.stamdata.cpr.pvit.WhitelistProvider;
import dk.nsi.stamdata.cpr.pvit.WhitelistProvider.Whitelist;
import dk.sosi.seal.model.SystemIDCard;
import org.hibernate.Session;

import java.util.Set;

import static com.google.inject.name.Names.bindProperties;


public class ComponentController extends GuiceServletContextListener
{
	private static final TypeLiteral<Set<String>> A_SET_OF_STRINGS = new TypeLiteral<Set<String>>() {};
	private static final String DISPLAY_SOAP_FAULT_STACK_TRACE = "com.sun.xml.ws.fault.SOAPFaultBuilder.disableCaptureStackTrace";
	public static final String COMPONENT_NAME = "stamdata-cpr-ws";


	@Override
	protected Injector getInjector()
	{
		return Guice.createInjector(Stage.PRODUCTION, new ComponentModule(), new ServiceModule());
	}


	public static class ComponentModule extends AbstractModule
	{
		@Override
		protected void configure()
		{
			// Load the components configuration and bind it to named
			// dependencies.

			bindProperties(binder(), ConfigurationLoader.loadForName(COMPONENT_NAME));

			// The white-list controls which clients have access to protected
			// data and which that do not.

			bind(A_SET_OF_STRINGS).annotatedWith(Whitelist.class).toProvider(WhitelistProvider.class);

			bind(Session.class).toProvider(HibernatePersistenceFilter.class);
		}
	}


	private class ServiceModule extends ServletModule
	{
		@Override
		protected void configureServlets()
		{
			// We don't want JAX-WS to expose the stack trace
			// when an exception occurs.

			System.setProperty(DISPLAY_SOAP_FAULT_STACK_TRACE, "false");

			// Tell the monitoring module how to monitor the component.
			// All monitor pages are bound to the URL /status.

			bind(ComponentMonitor.class).to(ComponentMonitorImpl.class);
			install(new MonitoringModule());

			// Filter everything through the DGWS filter,
			// but exclude the status page.

			filterRegex("(?!/status)/.*").through(DgwsIdcardFilter.class);
			bind(SystemIDCard.class).toProvider(DGWSFilterSystemIDCardProvider.class);
			
			// Transactions are managed by the session filter.
			
			filter("/*").through(HibernatePersistenceFilter.class);
		}
	}
}
