package com.bixito.db;

import java.io.IOException;
import javax.servlet.http.*;

public class BixiDataServlet extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.getWriter().println(DB.getStationsData());
    }
}