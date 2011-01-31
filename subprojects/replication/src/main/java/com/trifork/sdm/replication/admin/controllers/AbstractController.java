package com.trifork.sdm.replication.admin.controllers;

import static com.trifork.sdm.replication.admin.models.RequestAttributes.*;

import java.io.*;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import freemarker.template.*;


public class AbstractController extends HttpServlet
{
	private static final long serialVersionUID = -7480013116213951161L;


	protected void render(HttpServletRequest request, HttpServletResponse response, Template template, Map<String, Object> root) throws IOException, ServletException
	{
		Writer writer = new OutputStreamWriter(response.getOutputStream());

		root.put("contextRoot", request.getContextPath());
		
		try
		{
			template.process(root, writer);
		}
		catch (TemplateException e)
		{
			throw new ServletException(e);
		}
	}


	protected String getUserCPR(HttpServletRequest request)
	{
		return request.getAttribute(USER_CPR).toString();
	}
}
