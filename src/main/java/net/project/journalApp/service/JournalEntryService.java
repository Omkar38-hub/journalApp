package net.project.journalApp.service;

import net.project.journalApp.entity.JournalEntry;
import net.project.journalApp.entity.UserEntry;
import net.project.journalApp.repository.JournalRepository;
import net.project.journalApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class JournalEntryService {

    @Autowired
    private JournalRepository journalRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void saveEntry(JournalEntry journalEntry, String username){
        try {
            UserEntry user = userRepository.findByUsername(username);
            JournalEntry saveEntity = journalRepository.save(journalEntry);
            user.getJournalEntryList().add(saveEntity);
            userRepository.save(user);
        }catch (Exception e){
            System.out.println(e);
            throw new RuntimeException("Exception has been thrown" +e);
        }

    }

    public void saveEntry(JournalEntry journalEntry){
        journalRepository.save(journalEntry);
    }

    public List<JournalEntry> getAll(){
        return journalRepository.findAll();
    }

    public Optional<JournalEntry> findById(String myId){
        return journalRepository.findById(myId);
    }

    @Transactional
    public boolean deleteById(String myId, String username){
        boolean result=false;
        try {
            UserEntry user= userRepository.findByUsername(username);
            result = user.getJournalEntryList().removeIf(x -> x.getId().equals(myId));
            if(result){
                userRepository.save(user);
                journalRepository.deleteById(myId);
            }
        }catch (Exception e){
            System.out.println(e);
            throw new RuntimeException("Exception occurred while deleting entry "+e);
        }
        return result;
    }
}
