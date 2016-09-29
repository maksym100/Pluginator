package Pluginator;

import org.springframework.stereotype.Component;

/**
 * Created by madamek on 28.09.2016.
 */
@Component
public class GoogleFeeder implements Feeder {
	public void sayHi() {
		System.out.println("Google said hi");
	}
}
