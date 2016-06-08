package model;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

@Repository
public interface ContentRepository extends MongoRepository<Content, String> {

    public List<Content> findByType(String type);

}
