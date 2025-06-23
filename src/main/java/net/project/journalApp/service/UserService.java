package net.project.journalApp.service;

import lombok.extern.slf4j.Slf4j;
import net.project.journalApp.entity.JournalEntry;
import net.project.journalApp.entity.UserEntry;
import net.project.journalApp.repository.JournalRepository;
import net.project.journalApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JournalRepository journalRepository;

    private static final PasswordEncoder passwordEncode= new BCryptPasswordEncoder();

    public boolean saveEntry(UserEntry userEntry){
        try {
            userRepository.save(userEntry);
            return true;
        } catch (Exception e) {
            log.trace("Error occurred for user");
            log.debug("Error occurred for user {}",userEntry.getUsername(),e);
            log.info("Error occurred for user {}",userEntry.getUsername(),e);
            log.warn("Error occurred for user {}",userEntry.getUsername(),e);
            log.error("Error occurred for user {}",userEntry.getUsername(),e);

            return false;
        }
    }
    public void saveAdmin(UserEntry userEntry){
        userRepository.save(userEntry);
    }

    public List<UserEntry> getAll(){
        return userRepository.findAll();
    }

    public Optional<UserEntry> findById(String myId){
        return userRepository.findById(myId);
    }

    public Boolean deleteById(String myId){
        try{
            Optional<UserEntry> userEntry = userRepository.findById(myId);
            if (userEntry .isPresent()) {
                UserEntry user = userEntry.get();
                List<JournalEntry> journalEntries = user.getJournalEntryList();
                for (JournalEntry entry : journalEntries) {
                    journalRepository.deleteById(entry.getId());
                }
                userRepository.deleteById(myId);
                return true;
            } else {
                log.error("User not found with ID");
            }

        } catch (Exception e) {
            log.error("Unable to delete user {}", String.valueOf(e));
        }
        return false;
    }

    public void deleteByUsername(String username){

        userRepository.deleteByUsername(username);
    }

    public UserEntry findByUsername(String username){
        return userRepository.findByUsername(username);
    }
}
