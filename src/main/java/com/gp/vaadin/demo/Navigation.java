package com.gp.vaadin.demo;


import com.gp.vaadin.demo.UI.ViewForCategory;
import com.gp.vaadin.demo.UI.ViewForHotel;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.PushStateNavigation;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import javax.servlet.annotation.WebServlet;

@Theme("mytheme")
@SpringUI
@PushStateNavigation
public class Navigation extends UI {
    final VerticalLayout layout = new VerticalLayout();
    private VerticalLayout content = new VerticalLayout();
    private Component componentMenu = new ComponentMenu();


    @Override
    protected void init(VaadinRequest vaadinRequest) {
        Navigator navigator = new Navigator(this, content);
        navigator.addView("", ViewForHotel.class);
        navigator.addView("category", ViewForCategory.class);
        layout.setMargin(true);
        layout.setSpacing(true);
        content.setSizeFull();
        content.setMargin(false);
        layout.addComponents(componentMenu, content);
        setContent(layout);
    }

    @WebServlet(urlPatterns = "/*", name = "DemoUiServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = Navigation.class, productionMode = false)
    public static class DemoUIServlet extends VaadinServlet {
    }
}