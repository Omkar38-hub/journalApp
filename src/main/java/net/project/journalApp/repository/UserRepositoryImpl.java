package net.project.journalApp.repository;

import net.project.journalApp.entity.UserEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.*;

public class UserRepositoryImpl{

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<UserEntry>findUserSA(){
        Query query= new Query();
        query.addCriteria(Criteria.where("sentimentAnalysis").is(true));
        query.addCriteria(Criteria.where("email").regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
));
        List<UserEntry> userEntries = mongoTemplate.find(query, UserEntry.class);
        return userEntries;
    }
}
