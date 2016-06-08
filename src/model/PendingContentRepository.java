package model;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

@Repository
public interface PendingContentRepository extends MongoRepository<PendingContent, String> {

    public List<PendingContent> findByType(String type);

}
