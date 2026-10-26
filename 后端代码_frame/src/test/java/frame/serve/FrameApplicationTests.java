package frame.serve;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import frame.server.FrameApplication;

@SpringBootTest(classes = FrameApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FrameApplicationTests {

	@Test
	void contextLoads() {
	}

}
