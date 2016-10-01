package Pluginator;

import org.junit.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Hello world!
 */
public class App {

  public static void main(String[] args) throws Exception {
    ApplicationContext context = new ClassPathXmlApplicationContext("app.xml");

    while (true) {
      ((ConfigurableApplicationContext) context).refresh();
      File feeds = new File("src/main/resources/feeds");
      File[] feedsFiles = feeds.listFiles(pathname -> pathname.getName().endsWith(".jar"));

      for (File feedsFile : feedsFiles) {
        try {
          URLClassLoader child =
              new URLClassLoader(new URL[]{feedsFile.toURL()}, App.class.getClassLoader());
          String feedsFileName = feedsFile.getName();
          String feedName = feedsFileName.split("\\.")[0];
          Class classToLoad = Class.forName(feedName, true, child);
          Object instance = classToLoad.newInstance();
          Feeder feeder = (Feeder) instance;
          ConfigurableListableBeanFactory
              beanFactory =
              ((ConfigurableApplicationContext) context).getBeanFactory();
          beanFactory.registerSingleton(feeder.getClass().getCanonicalName(), feeder);

          context.getBeansOfType(Feeder.class).values().forEach(Feeder::sayHi);
        } catch (Exception e) {
          System.err.println("Wrong feedFile: " + feedsFile);
        }
      }

      Thread.sleep(2000);
    }
  }
}
