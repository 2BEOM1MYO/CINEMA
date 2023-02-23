package com.zb.cinema.waiting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.zb.cinema.waiting.service.WatingService;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
class WatingServiceTest {


	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@Autowired
	private WatingService watingService;

	@Test
	void waiting() throws InterruptedException {
		final int people = 100;
		final String key = "결제";

		final CountDownLatch countDownLatch = new CountDownLatch(people);

		List<Thread> workers = Stream
			.generate(() -> new Thread(new AddQueueWorker(countDownLatch, key)))
			.limit(people)
			.collect(Collectors.toList());
		workers.forEach(Thread::start);
		countDownLatch.await();
		Thread.sleep(5000); // 결제 스케줄러 작업 시간

		final long failEventPeople = watingService.getSize(key);
		assertEquals(people, failEventPeople);
	}

	private class AddQueueWorker implements Runnable {

		private CountDownLatch countDownLatch;
		private String key;

		public AddQueueWorker(CountDownLatch countDownLatch, String key) {
			this.countDownLatch = countDownLatch;
			this.key = key;
		}

		@Override
		public void run() {
			watingService.addWatingQueue(key, "고객");
			countDownLatch.countDown();
		}
	}
}