package br.com.aurum.astrea.service;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import br.com.aurum.astrea.dao.ContactDao;
import br.com.aurum.astrea.domain.Contact;

@SuppressWarnings("serial")
public class ContactServlet extends HttpServlet {
	
	private static final ContactDao DAO = new ContactDao();
	private Gson gson = new GsonBuilder().create();

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		// TODO: Implementar um método que irá ler o corpo da requisição e, com essas informações,
		// salvar no banco de dados uma entidade do tipo 'Contato' com essas informações.
		Contact contact = gson.fromJson(req.getReader(), Contact.class);
		
		resp.setContentType("application/json");
		if(contact.getName() == null){
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			JsonObject error = new JsonObject();
			error.addProperty("error", "The name attribute is required");
			resp.getWriter().write(error.toString());
			return;
		}
		
		DAO.save(contact);
		resp.setStatus(HttpServletResponse.SC_CREATED);
		resp.getWriter().write(gson.toJson(contact));
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
		// TODO: Implementar um método que irá listar todas as entidades do tipo 'Contato' e devolver para o client essa listagem.
		List<Contact> contacts = DAO.list();
		resp.setContentType("application/json");
		resp.getWriter().write(gson.toJson(contacts));
	}
	
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
		// TODO: Implementar um método que irá deletar uma entidade do tipo 'Contato', dado parâmetro de identificação.
		Contact contact = gson.fromJson(req.getReader(), Contact.class);
		DAO.delete(contact.getId());
		resp.setStatus(HttpServletResponse.SC_OK);
	}
}
