package br.com.devbeise.spring_boot_kanban;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpringBootKanbanApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootKanbanApplication.class, args);
	}

}