package com.gitlab.muhammadkholidb.bianglala.utility;

import java.io.IOException;
import java.net.URL;

import com.gitlab.muhammadkholidb.bianglala.SpringConfig;
import com.gitlab.muhammadkholidb.jdbctemplatehelper.config.JdbcTemplateHelperConfig;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javafx.fxml.FXMLLoader;

public class SpringFXMLLoader {

    private static final ApplicationContext applicationContext = new AnnotationConfigApplicationContext(
        SpringConfig.class, 
        JdbcTemplateHelperConfig.class);

    public Object load(URL location) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(location);
        loader.setControllerFactory(applicationContext::getBean);
        return loader.load();
    }

}
