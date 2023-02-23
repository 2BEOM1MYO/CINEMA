package com.zb.cinema.waiting.service;

import java.time.LocalDateTime;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WatingService {
	private final RedisTemplate<String,Object> redisTemplate;
	private final RedisTemplate<String, Object> workerTemplate;
	private static final long FIRST_ELEMENT = 0;
	private static final long LAST_ELEMENT = -1;
	private static final long PUBLISH_SIZE = 10;
	private static final long LAST_INDEX = 1;

	public void addWatingQueue(String key, String name){
		final long now = System.currentTimeMillis();				// 언제?

		redisTemplate.opsForZSet().add(key, name, (int) now);		// 대기열에 추가
		log.info("대기열에 추가 - {} ({}초)", name, now);
	}

	public void getOrder(String key){
		final long start = FIRST_ELEMENT;
		final long end = LAST_ELEMENT;

		Set<Object> queue = redisTemplate.opsForZSet().range(key, start, end);

		for (Object people : queue) {
			Long rank = redisTemplate.opsForZSet().rank(key, people);
			log.info("'{}'님의 현재 대기열은 {}명 남았습니다.", people, rank);
		}
	}



	// 작업열
	public void addWorkerQueue(String key, String name){
		final long now = System.currentTimeMillis();				// 언제?

		workerTemplate.opsForZSet().add(key, name, (int) now);		// 대기열에 추가
		log.info("작업열에 추가 - {} ({}초)", name, now);
	}


	public void publish(String key){
		final long start = FIRST_ELEMENT;
		final long end = PUBLISH_SIZE - LAST_INDEX;

		Set<Object> queue = workerTemplate.opsForZSet().range(key, start, end);
		for (Object people : queue) {
			log.info("'{}'님의 결제가 완료되었습니다. ({})",people, LocalDateTime.now());
			workerTemplate.opsForZSet().remove(key, people);
		}
	}

	public long getSize(String key){
		return workerTemplate.opsForZSet().size(key);
	}
}
