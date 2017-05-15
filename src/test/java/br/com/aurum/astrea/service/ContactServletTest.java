package br.com.aurum.astrea.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.googlecode.objectify.ObjectifyService;

import br.com.aurum.astrea.domain.Contact;

public class ContactServletTest {

	private ContactServlet contactServlet;
	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

	private HttpServletRequest request;
	private HttpServletResponse response;
	private StringWriter stringWriter;
	private Gson gson;

	@Before
	public void setup() throws IOException {
		helper.setUp();
		contactServlet = new ContactServlet();

		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		stringWriter = new StringWriter();
		gson = new GsonBuilder().create();

		when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));
	}

	@After
	public void tearDownHelper() throws IOException {
		helper.tearDown();
	}

	@Test
	public void testPostContact() throws IOException {
		Contact contact = new Contact();
		contact.setName("Ana Post");
		contact.setEmails(Arrays.asList(new String[] { "ana@mail.com" }));
		contact.setPhones(Arrays.asList(new String[] { "48 9999 0099", "34 8888 9999" }));
		Reader send = new StringReader(gson.toJson(contact));

		when(request.getReader()).thenReturn(new BufferedReader(send));

		contactServlet.doPost(request, response);

		String result = stringWriter.getBuffer().toString().trim();
		Contact resturdned = gson.fromJson(result, Contact.class);

		assertNotNull(resturdned.getId());
	}
	
	@Test
	public void testPostInvalidContact() throws IOException {
		Contact contact = new Contact();
		contact.setEmails(Arrays.asList(new String[] { "ana@mail.com" }));
		contact.setPhones(Arrays.asList(new String[] { "48 9999 0099", "34 8888 9999" }));
		Reader send = new StringReader(gson.toJson(contact));

		when(request.getReader()).thenReturn(new BufferedReader(send));

		contactServlet.doPost(request, response);

		verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
	}
	
	@Test
	public void testGetContacs() throws IOException {
		Contact contact = new Contact();
		contact.setName("Ana");
		contact.setEmails(Arrays.asList(new String[] { "ana@mail.com" }));
		contact.setPhones(Arrays.asList(new String[] { "48 9999 0099", "34 8888 9999" }));

		ObjectifyService.ofy().save().entity(contact).now();

		contactServlet.doGet(request, response);

		String result = stringWriter.getBuffer().toString().trim();
		Type listType = new TypeToken<ArrayList<Contact>>() {
		}.getType();
		List<Contact> contacts = gson.fromJson(result, listType);
		assertEquals("Checking number of received Elements", 1, contacts.size());

		Contact firstContact = contacts.get(0);
		assertEquals("Checking Name", firstContact.getName(), contact.getName());
		assertEquals("Checking Emails", firstContact.getEmails(), contact.getEmails());
		assertEquals("Checking Phones", firstContact.getPhones(), contact.getPhones());
	}

	@Test
	public void testDeleteContact() throws IOException {
		Contact contact = new Contact();

		ObjectifyService.ofy().save().entity(contact).now();
		Reader send = new StringReader(gson.toJson(contact));

		when(request.getReader()).thenReturn(new BufferedReader(send));

		contactServlet.doDelete(request, response);
		
		assertEquals(0, ObjectifyService.ofy().load().type(Contact.class).count());
	}

}
