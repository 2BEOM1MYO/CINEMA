package com.zb.cinema.movie.job;

import com.zb.cinema.movie.entity.MovieCode;
import com.zb.cinema.movie.service.MovieService;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BoxOfficeJobConfig {

	private final JobBuilderFactory jobBuilderFactory;  //Job 빌더 생성용
	private final StepBuilderFactory stepBuilderFactory; //Step 빌더 생성용
	private final EntityManagerFactory entityManagerFactory;
	private final MovieService boxOfficeApiService;
	private static final int CHUNKSIZE = 100; //쓰기 단위인 청크사이즈

	// JobBuilderFactory를 통해서 tutorialJob을 생성
	@Bean
	public Job dailyBoxOfficeJob() {
		return jobBuilderFactory.get("dailyBoxOfficeJob").start(dailyBoxOfficeStep()).build();
	}

	@Bean
	public Step dailyBoxOfficeStep() {
		return stepBuilderFactory.get("dailyBoxOfficeStep").<MovieCode, MovieCode>chunk(CHUNKSIZE)
			.reader(dailyBoxOfficeReader()).writer(dailyBoxOfficeWriter()).build();
	}

	@Bean
	public ListItemReader<MovieCode> dailyBoxOfficeReader() {
		LocalDate day = LocalDate.now().minusDays(1);
		String dayString = day.toString().replace("-", "");
		List<MovieCode> dailyMovie = boxOfficeApiService.saveMovieCode(dayString);
		return new ListItemReader<>(dailyMovie);
	}

	@Bean
	public JpaItemWriter<MovieCode> dailyBoxOfficeWriter() {
		return new JpaItemWriterBuilder<MovieCode>().entityManagerFactory(this.entityManagerFactory)
			.build();
	}

}
