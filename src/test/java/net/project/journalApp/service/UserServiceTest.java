package net.project.journalApp.service;

import net.project.journalApp.entity.UserEntry;
import net.project.journalApp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @ParameterizedTest
    @ArgumentsSource(UserArgumentProvider.class)
    public void saveUserTest(UserEntry user){
        assertTrue(userService.saveEntry(user));
    }

    @Test
    public void findByUsername(){
          assertEquals(
                  4,2+2
          );
        UserEntry user = userRepository.findByUsername("Omkar");
        assertNotNull(user);
        assertTrue(user.getJournalEntryList().isEmpty());
    }

    @ParameterizedTest
    @CsvSource({
            "1,2,3",
            "2,3,4"
    })
    public void test(int a,int b,int expected){
        assertEquals(expected,a+b);
    }



}
