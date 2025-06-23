package net.project.journalApp.repository;

import net.project.journalApp.entity.UserEntry;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<UserEntry, String> {
    UserEntry findByUsername(String username);

    UserEntry deleteByUsername(String username);
}
