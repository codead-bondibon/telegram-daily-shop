package codeadlive.bot.daily.shops;

import org.springframework.boot.SpringApplication;

public class TestBotDailyShopsApplication {

	public static void main(String[] args) {
		SpringApplication.from(BotDailyShopsApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
