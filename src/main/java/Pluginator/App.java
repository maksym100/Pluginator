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

	public static void main(String[] args) throws ClassNotFoundException, MalformedURLException, IllegalAccessException, InstantiationException, InterruptedException {
		ApplicationContext context = new ClassPathXmlApplicationContext("app.xml");

		while (true) {
			((ConfigurableApplicationContext) context).refresh();
			File feeds = new File("src/main/resources/feeds");
			File[] feedsFiles = feeds.listFiles(pathname -> {
				return pathname.getName().endsWith(".jar");
			});

			List<URL> urls = new ArrayList<>();
			for (File feedsFile : feedsFiles) {
				urls.add(feedsFile.toURL());
			}
			URLClassLoader child = new URLClassLoader(urls.toArray(new URL[0]), App.class.getClassLoader());
			Class classToLoad = Class.forName("Plugin.CustomFeeder", true, child);
			Object instance = classToLoad.newInstance();
			Feeder feeder = (Feeder) instance;
			ConfigurableListableBeanFactory beanFactory = ((ConfigurableApplicationContext) context).getBeanFactory();
			beanFactory.registerSingleton(feeder.getClass().getCanonicalName(), feeder);

			context.getBeansOfType(Feeder.class).values().forEach(Feeder::sayHi);
			Thread.sleep(2000);
		}
	}

	@Test
	public void test() {
		File file = new File("src/main/resources/feeds/com.plugin-1.0-SNAPSHOT.jar");
		assert file.exists();
	}
}
