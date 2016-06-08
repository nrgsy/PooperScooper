package model;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface SchwergsyAccountsRepository extends MongoRepository<SchwergsyAccounts, String> {

  //TODO rewrite these methods based on what is searchable in DBH
    public SchwergsyAccounts findBySerial(String serial);
    public SchwergsyAccounts findByName(String name);
    public SchwergsyAccounts findByUrl(String url);

}
