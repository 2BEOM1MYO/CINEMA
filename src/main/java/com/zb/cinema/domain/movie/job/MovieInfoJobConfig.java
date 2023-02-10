package com.zb.cinema.domain.movie.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MovieInfoJobConfig {
//
//	private final JobBuilderFactory jobBuilderFactory;  //Job 빌더 생성용
//	private final StepBuilderFactory stepBuilderFactory; //Step 빌더 생성용
//	private final EntityManagerFactory entityManagerFactory;
//	private final MovieService movieService;
//	private static final int CHUNKSIZE = 100;
//
//	@Bean
//	public Job movieInfoJob() {
//		return jobBuilderFactory.get("movieInfoJob")
//			.start(movieInfoStep())
//			.build();
//	}
//
//	@Bean
//	public Step movieInfoStep() {
//		return stepBuilderFactory.get("movieInfoStep")
//			.<Long, Movie>chunk(CHUNKSIZE)
//			.reader(movieInfoReader())
//			.processor(movieInfoProcessor())
//			.writer(movieInfoWriter())
//			.build();
//	}
//
//	@Bean
//	@StepScope
//	public JpaPagingItemReader<Long> movieInfoReader() {
//
//		JpaPagingItemReader<Long> reader = new JpaPagingItemReader<Long>() {
//			@Override
//			public int getPage() {
//				return 0;
//			}
//		};
//		reader.setQueryString("SELECT m.code from MovieCode m WHERE m.batchStatus IS NULL ORDER BY m.code asc");
//		reader.setPageSize(CHUNKSIZE);
//		reader.setEntityManagerFactory(entityManagerFactory);
//		reader.setName("movieInfoReader");
//
//		return reader;
//	}
//
//	@Bean
//	public ItemProcessor<Long, Movie> movieInfoProcessor() {
//		return movieCode -> movieService.saveMovieInfoByMovieCode(movieCode);
//	}
//
//	@Bean
//	public JpaItemWriter<Movie> movieInfoWriter(){
//		JpaItemWriter<Movie> jpaItemWriter = new JpaItemWriter<>();
//		jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
//		return jpaItemWriter;
//	}

}
