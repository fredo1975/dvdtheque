package fr.fredos.dvdtheque.batch;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication(scanBasePackages = "fr.fredos.dvdtheque.batch")
@ImportResource("spring-batch-job.xml")
public class BatchApplication {

}
