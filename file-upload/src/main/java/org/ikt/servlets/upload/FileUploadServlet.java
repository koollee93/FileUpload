package org.ikt.servlets.upload;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.*;
import javax.servlet.*;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;

@WebServlet("/upload")
@MultipartConfig
public class FileUploadServlet extends HttpServlet {

	private final static Logger LOGGER = Logger.getLogger(FileUploadServlet.class.getCanonicalName());

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	private String getFileName(final Part part) {
		final String partHeader = part.getHeader("content-disposition");
		LOGGER.log(Level.INFO, "Part Header = {0}", partHeader);
		for (String content : part.getHeader("content-disposition").split(";")) {
			if (content.trim().startsWith("filename")) {
				return content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
			}
		}
		return null;
	}

	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");

		String path = request.getParameter("destination");
		Part filePart = request.getPart("file");
		String fileName = getFileName(filePart);

		PrintWriter writer = response.getWriter();

		InputStream in = null;
		OutputStream out = null;

		try {
			in = filePart.getInputStream();
			out = new FileOutputStream(new File(path + File.separator + fileName));

			int read;
			final byte[] bytes = new byte[1024];

			while ((read = in.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			writer.println("Novi fajl " + fileName + " je snimljen na putanji " + path);
			LOGGER.log(Level.INFO, "Fajl {0} je snimmljen na {1}", new Object[] { fileName, path });
		} catch (FileNotFoundException fne) {
			writer.println("Doslo je do greske! Ili niste poslali fajl na upload ili "
					+ "pokusavate da snimite na nepostojecu ili nedozvoljenu lokaciju!");
			writer.println("<br/> ERROR: " + fne.getMessage());

			LOGGER.log(Level.SEVERE, "Problem tokom file uplaod-a. Greska: {0}", new Object[] { fne.getMessage() });
		} finally {
			if (out != null) {
				out.flush();
				out.close();
			}
			if (in != null) {
				in.close();
			}
			if (writer != null) {
				writer.close();
			}
		}
	}
}
