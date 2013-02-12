package com.bixito.db;

import java.io.IOException;
import javax.servlet.http.*;

public class BixiDataUpdater extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		try {
			DB.updateStationsData();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		resp.setContentType("text/html");
		resp.getWriter().println("db updated");
	}
}