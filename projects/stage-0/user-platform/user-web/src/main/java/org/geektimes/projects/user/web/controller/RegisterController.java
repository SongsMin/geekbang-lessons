package org.geektimes.projects.user.web.controller;

import org.geektimes.projects.user.domain.User;
import org.geektimes.projects.user.service.UserService;
import org.geektimes.web.mvc.controller.PageController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/register")
public class RegisterController implements PageController {

    @Resource(name = "bean/UserService")
    private UserService service;

    @POST
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        String email = request.getParameter("inputEmail");
        String password = request.getParameter("inputPassword");
        String phoneNumber = request.getParameter("inputPhoneNumber");
        User user = new User();
        user.setName(email);
        user.setEmail(email);
        user.setPassword(password);
        user.setPhoneNumber(phoneNumber);
//        UserService service = new DefaultUserService(new DatabaseUserRepository());
        boolean success = service.register(user);
        if (success) {
            return "register-successful.jsp";
        }
        return "register-failed.jsp";
    }
}
