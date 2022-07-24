package ddi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import ddi.dao.ComDao;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

@EnableSchedulerLock(defaultLockAtMostFor = "10m")
@Service("ScheduleService")
public class ScheduleService {

	@Autowired
	ComDao comDao;
	
	@Autowired
	ApiService api;
	
	private static final Logger logger = LoggerFactory.getLogger(ScheduleService.class);
	
	/**
	 * name : 스케줄 작업의 고유 이름
	 * lockAtLeastFor : 작업이 lock 되어야 할 최소한 시간
	 * lockAtMostFor : 작업을 진행 중인 노드가 소멸될 경우에도 lock 이 유지될 시간
	 * @throws Exception
	 */
	//매일 오전1시에 실행(초 분 시 일 월 요일)
	@Scheduled(cron = "0 0 * * * *")
	@SchedulerLock(name = "Scrap", lockAtLeastFor = "59m", lockAtMostFor = "59m")
	public void fileConvert() throws Exception {
		logger.info("ScheduleService.Scrap() START");

		api.news(null);
		logger.info("ScheduleService.Scrap() END");
		
	}
	
}
