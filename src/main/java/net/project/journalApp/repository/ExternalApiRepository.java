package net.project.journalApp.repository;

import net.project.journalApp.entity.ConfigAPIEntry;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ExternalApiRepository extends MongoRepository<ConfigAPIEntry, ObjectId> {
}
