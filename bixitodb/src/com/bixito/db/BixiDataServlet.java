package com.bixito.db;

import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BixiDataServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		try {
			resp.getWriter().println(DB.getStationsData());
			if (req.getParameter(Constants.DEVICE_ID) != null)
				DB.addStatistics(req.getParameter(Constants.DEVICE_ID));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}