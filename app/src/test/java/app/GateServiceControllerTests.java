package app;

import nlp.service.config.ApplicationConfiguration;
import nlp.service.controller.ServiceControllerTests;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;



@SpringBootTest(classes = ServiceApplication.class)
@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:test-application.properties")
@ContextConfiguration(classes = {ApplicationConfiguration.class})
@AutoConfigureMockMvc
public class GateServiceControllerTests extends ServiceControllerTests {
}
