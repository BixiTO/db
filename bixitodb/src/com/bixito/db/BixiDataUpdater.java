package com.bixito.db;

import java.io.IOException;
import javax.servlet.http.*;

public class BixiDataUpdater extends HttpServlet {

	static int count = 0;
	static String oldStats;

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		try {
			String[] stats = DB.updateStationsData();
			if (count == 1) {
				oldStats = stats[0];
			}
			if (count > 30) {
				if (!oldStats.isEmpty() && oldStats != null)
					stats[0] = oldStats;
				DB.calculateStationsStats(stats);
				count = 0;
			}

			count++;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		resp.setContentType("text/html");
		resp.getWriter().println("db updated");
	}
}