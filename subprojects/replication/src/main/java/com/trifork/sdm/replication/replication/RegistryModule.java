package com.trifork.sdm.replication.replication;

import java.util.*;

import javax.persistence.Entity;
import javax.xml.bind.*;

import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.*;

import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.servlet.ServletModule;
import com.sun.xml.bind.marshaller.NamespacePrefixMapper;
import com.trifork.sdm.replication.replication.annotations.Registry;
import com.trifork.sdm.replication.replication.models.Record;
import com.trifork.sdm.replication.util.Namespace;

public class RegistryModule extends ServletModule {

	private JAXBContext context;


	@Override
	@SuppressWarnings("unchecked")
	protected void configureServlets() {

		// DISCOVER ALL ENTITY CLASSES
		//
		// For speed only the model package will be searched.

		String MODEL_PACKAGE = Record.class.getPackage().getName();
		Reflections reflector = new Reflections(new ConfigurationBuilder().filterInputsBy(new FilterBuilder.Include(FilterBuilder.prefix(MODEL_PACKAGE))).setUrls(ClasspathHelper.getUrlsForPackagePrefix(MODEL_PACKAGE)).setScanners(new TypeAnnotationsScanner()));
		Set<Class<?>> classes = reflector.getTypesAnnotatedWith(Entity.class);

		// MAP ENTITIES TO THEIR NAMES
		//
		// Map the entity classes to their respective authority/name/version.
		// Bind the map to the Map<String, Class> annotated with @Registry.
		//
		// A tree map is used so the entries are lexically sorted.

		Map<String, Class<? extends Record>> registry = new TreeMap<String, Class<? extends Record>>();

		for (Class<?> entity : classes) {
			Entity annotation = entity.getAnnotation(Entity.class);
			registry.put(annotation.name(), (Class<? extends Record>) entity);
		}

		bind(new TypeLiteral<Map<String, Class<? extends Record>>>() {}).annotatedWith(Registry.class).toInstance(registry);

		// BIND THE FEED WRITER
		//
		// We need a JAXB context that can marshal all
		// the entities to XML.

		bind(AtomFeedWriter.class);

		try {
			context = JAXBContext.newInstance(classes.toArray(new Class[0]), null);
		}
		catch (JAXBException e) {
			addError(e);
		}

		// SERVE THE REGISTRIES
		//
		// All requests default to Stamdata default to this servlet.

		serve("/stamdata/*").with(RegistryServlet.class);
	}


	@Provides
	@Registry
	public Marshaller provideEntityMarshaller() throws JAXBException {
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
		marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
		marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new NamespacePrefixMapper() {

			@Override
			public String[] getPreDeclaredNamespaceUris() {
				return new String[] { Namespace.STAMDATA_3_0 };
			}


			@Override
			public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {
				if (namespaceUri.equals(Namespace.STAMDATA_3_0)) return "sds";
				return suggestion;
			}
		});

		return marshaller;
	}
}
