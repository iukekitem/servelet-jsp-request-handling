package com.joao;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/Main") //Whenever someone sends a request to the URL path /Main, run this class

public class Main extends HttpServlet {
    private static final long serialVersionUID = 1L;
    public Main(){
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) //hand it over to doPost
            throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)//sets the response header to tell the browser
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

                //Retrieve the parameters from the HTML form
                String firstName = request.getParameter("firstName");
                String lastName = request.getParameter("lastName");
                String gender = request.getParameter("gender");

                //Validate empty or unselected fields
                boolean isFirstNameMissing = (firstName == null || firstName.trim().isEmpty());
                boolean isLastNameMissing = (lastName == null || lastName.trim().isEmpty());
                boolean isGenderMissing = (gender == null || gender.trim().isEmpty());

                out.println("<!DOCTYPE html>");
                out.println("<html>");
                out.println("<head>");
                out.println("<title>Shop Response</title>");
                out.println("<style>");
                out.println("body { font-family: Arial, sans-serif; background-color: #f4f6f8; display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0; }");
                out.println(".card { background: #fff; padding: 30px; border-radius: 8px; box-shadow: 0 4px 10px rgba(0,0,0,0.1); width: 400px; text-align: center; }");
                out.println(".error { color: #d9534f; font-weight: bold; margin-bottom: 15px; }");
                out.println(".btn-back { display: inline-block; margin-top: 15px; padding: 8px 16px; background-color: #007bff; color: white; text-decoration: none; border-radius: 4px; }");
                out.println("</style>");
                out.println("</head>");
                out.println("<body>");
                out.println("<div class='card'>");

                //Handle Error Scenarios
                if (isFirstNameMissing || isLastNameMissing || isGenderMissing) {
                    out.println("<h2 style='color: #d9534f;'>Error</h2>");
                    if (isFirstNameMissing && isLastNameMissing) {
                        out.println("<p class='error'>Please enter both your First Name and Last Name.</p>");
                    } else if (isFirstNameMissing) {
                        out.println("<p class='error'>Please enter your First Name.</p>");
                    } else if (isLastNameMissing) {
                        out.println("<p class='error'>Please enter your Last Name.</p>");
                    }

                    if (isGenderMissing) {
                        out.println("<p class='error'>Please select a Gender.</p>");
                    }
                    out.println("<a href='Main.html' class='btn-back'>Back to Form</a>");
                } else {
                    //Handle Success Scenarios
                    String title = "";
                    if ("Male".equalsIgnoreCase(gender)) {
                        title = "Mr. ";
                    } else if ("Female".equalsIgnoreCase(gender)) {
                        title = "Ms. ";
                    }

                    String fullName = title + firstName.trim() + " " + lastName.trim();

                    out.println("<h1>Welcome to my shop</h1>");
                    out.println("<p style='font-size: 18px; color: #333;'>Thank you " + fullName + ".</p>");
                }

                out.println("</div>");
                out.println("</body>");
                out.println("</html>");

                out.close();
            }
}
