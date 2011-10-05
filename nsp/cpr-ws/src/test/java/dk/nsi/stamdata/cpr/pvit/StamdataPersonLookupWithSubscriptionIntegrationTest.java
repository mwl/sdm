package dk.nsi.stamdata.cpr.pvit;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Stage;
import com.trifork.stamdata.models.cpr.Person;
import dk.nsi.stamdata.cpr.ComponentController.ComponentModule;
import dk.nsi.stamdata.cpr.Factories;
import dk.nsi.stamdata.cpr.integrationtest.dgws.DGWSHeaderUtil;
import dk.nsi.stamdata.cpr.integrationtest.dgws.SecurityWrapper;
import dk.nsi.stamdata.cpr.jaxws.SealNamespaceResolver;
import dk.nsi.stamdata.cpr.ws.*;
import org.hibernate.Session;
import org.hisrc.hifaces20.testing.webappenvironment.testing.junit4.AbstractWebAppEnvironmentJUnit4Test;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.xml.namespace.QName;
import javax.xml.ws.Holder;
import java.net.URL;
import java.util.Date;

import static org.junit.Assert.assertEquals;


public class StamdataPersonLookupWithSubscriptionIntegrationTest extends AbstractWebAppEnvironmentJUnit4Test
{
	public static final QName PVIT_WITH_SUBSCRIPTIONS_SERVICE = new QName("http://nsi.dk/2011/09/23/StamdataCpr/", "StamdataPersonLookupWithSubscriptionService");

	public static final String REQUEST_CVR = "12345678";

	private StamdataPersonLookupWithSubscription client;

	@Inject
	private Session session;

	private Holder<Security> securityHolder;
	private Holder<Header> medcomHolder;
	private static final String EXAMPLE_CPR = "1111111111";

	@Before
	public void setUp() throws Exception
	{
		// Use Guice to inject dependencies.
		Guice.createInjector(Stage.DEVELOPMENT, new ComponentModule()).injectMembers(this);

		purgePersonTable();
		createExamplePersonInDatabase();

		URL wsdlLocation = new URL("http://localhost:8100/service/StamdataPersonLookupWithSubscription?wsdl");
		StamdataPersonLookupWithSubscriptionService serviceCatalog = new StamdataPersonLookupWithSubscriptionService(wsdlLocation, PVIT_WITH_SUBSCRIPTIONS_SERVICE);

		// SEAL enforces that the XML prefixes are excatly
		// as it creates them. So we have to make sure we
		// don't change them.

		serviceCatalog.setHandlerResolver(new SealNamespaceResolver());

		client = serviceCatalog.getStamdataPersonLookupWithSubscription();

		SecurityWrapper securityHeaders = DGWSHeaderUtil.getVocesTrustedSecurityWrapper(REQUEST_CVR, "foo", "bar");
		securityHolder = new Holder<Security>(securityHeaders.getSecurity());
		medcomHolder = new Holder<Header>(securityHeaders.getMedcomHeader());
	}


	@After
	public void tearDown() throws Exception
	{
		session.disconnect();
	}

	@Test
	public void returnsAllSubscribedPersonsForRequestWithoutSince() throws Exception
	{
		CprAbbsRequest request = new CprAbbsRequest();
		PersonLookupResponseType response = client.getSubscribedPersonDetails(securityHolder, medcomHolder, request);

		assertEquals(1, response.getPersonInformationStructure().size());

		PersonInformationStructureType information = response.getPersonInformationStructure().get(0);
		assertReturnedResponseMatchesPersonFromDatabase(information);
	}


	private void purgePersonTable()
	{
		session.createSQLQuery("TRUNCATE Person").executeUpdate();
	}


	private void createExamplePersonInDatabase()
	{
		createPerson(EXAMPLE_CPR, "M", "8464", "Thomas", "Greve", "Kristensen", new DateTime(1982, 4, 15, 0, 0));
		createPerson("0101821234", "F", "8000", "Margit", "Greve", "Kristensen", new DateTime(1982, 1, 1, 0, 0));
		createPerson("0101821232", "F", "8100", "Margit", "Greve", "Kristensen", new DateTime(1929, 1, 1, 0, 0));
	}


	private Person createPerson(String cpr, String koen, String vejkode, String fornavn, String mellemnavn,
			String efternavn, DateTime foedselsdato)
	{
		Person person = Factories.createPersonWithoutAddressProtection();
		person.cpr = cpr;
		person.koen = koen;
		person.vejKode = vejkode;
		person.fornavn = fornavn;
		if (mellemnavn != null)
		{
			person.mellemnavn = mellemnavn;
		}
		person.efternavn = efternavn;
		person.foedselsdato = foedselsdato.toDate();

		person.setModifiedDate(new Date());
		person.setCreatedDate(new Date());
		person.setValidFrom(DateTime.now().minusDays(1).toDate());
		person.setValidTo(DateTime.now().plusDays(1).toDate());

		savePerson(person);

		return person;
	}


	private Person savePerson(Person person)
	{
		session.getTransaction().begin();

		session.save(person);

		session.getTransaction().commit();

		return person;
	}


	private void assertReturnedResponseMatchesPersonFromDatabase(PersonInformationStructureType information)
	{
		assertEquals(EXAMPLE_CPR, information.getRegularCPRPerson().getSimpleCPRPerson()
				.getPersonCivilRegistrationIdentifier());
		assertEquals("8464", information.getPersonAddressStructure().getAddressComplete().getAddressAccess()
				.getStreetCode());
	}
}
