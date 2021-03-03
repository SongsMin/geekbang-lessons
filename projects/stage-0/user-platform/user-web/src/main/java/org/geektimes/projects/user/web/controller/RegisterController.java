package org.geektimes.projects.user.web.controller;

import org.geektimes.projects.user.domain.User;
import org.geektimes.projects.user.repository.DatabaseUserRepository;
import org.geektimes.projects.user.service.DefaultUserService;
import org.geektimes.projects.user.service.UserService;
import org.geektimes.projects.user.sql.DBConnectionManager;
import org.geektimes.web.mvc.controller.PageController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/register")
public class RegisterController implements PageController {
    @POST
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        String email = request.getParameter("inputEmail");
        String password = request.getParameter("inputPassword");
        User user = new User();
        user.setName(email);
        user.setEmail(email);
        user.setPassword(password);
        user.setPhoneNumber("");
        UserService service = new DefaultUserService(new DatabaseUserRepository(new DBConnectionManager()));
        service.register(user);
        return "register-successful.jsp";
    }
}
