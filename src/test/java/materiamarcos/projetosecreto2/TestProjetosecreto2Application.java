package materiamarcos.projetosecreto2;

import org.springframework.boot.SpringApplication;

public class TestProjetosecreto2Application {

	public static void main(String[] args) {
		SpringApplication.from(Projetosecreto2Application::main).with(TestcontainersConfiguration.class).run(args);
	}

}
