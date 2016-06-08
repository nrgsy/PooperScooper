package model;

import org.springframework.data.mongodb.repository.MongoRepository;

@Repository
public interface GlobalVariablesRepository extends MongoRepository<GlobalVariables, String> {

}
