package model;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

@Repository
public interface SchwagContentRepository extends MongoRepository<SchwagContent, String> {

    public List<SchwagContent> findByImgLink(String imgLink);

}
