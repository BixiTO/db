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
			DB.Log.warning("count is "+ count);
			
			if (count == 1) {
				oldStats = stats[0];
			}
			if (count > 600) {
				if (oldStats != null && !oldStats.isEmpty())
					stats[0] = oldStats;
				DB.Log.warning("count is "+ count + " calculating station stats");		
				DB.calculateStationsStats(stats);
				count = 0;
				oldStats = "";
			}

			count++;
		} catch (Exception e) {
			DB.Log.warning("crash "+ e.toString());
			e.printStackTrace();
		}
		resp.setContentType("text/html");
		resp.getWriter().println("db updated");
	}
}