package net.project.journalApp.scheduler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserSchedulerTest {

    @Autowired
    private Scheduler scheduler;

    @Test
    public void fetchUserAndSendMailTest(){
//        scheduler.fetchUserAndSendMail();
    }
}
