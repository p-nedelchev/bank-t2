package com.clouway.nvuapp.adapter.persistence;

import com.clouway.nvuapp.core.QuestionnaireRepository;
import com.clouway.nvuapp.core.JsonCodec;
import com.clouway.nvuapp.core.Questionnaire;
import com.clouway.nvuapp.adapter.persistence.dao.DataStore;

import java.util.List;

/**
 * @author Vasil Mitov <v.mitov.clouway@gmail.com>
 */
public class PersistentQuestionnaireRepository implements QuestionnaireRepository {
  private final DataStore dataStore;
  private final JsonCodec codec = new JsonCodec();

  public PersistentQuestionnaireRepository(DataStore dataStore) {
    this.dataStore = dataStore;
  }

  @Override
  public void register(Questionnaire questionnaire) {
    String data = codec.marshallToString(questionnaire);
    String query = "INSERT INTO QUESTIONNAIRES (QUESTIONNAIRE) VALUES(?)";
    dataStore.update(query, data);
  }


  @Override
  public void update(Questionnaire questionnaire) {
    String data = codec.marshallToString(questionnaire);
    String query = "UPDATE QUESTIONNAIRES" +
            " SET QUESTIONNAIRE='" + data + "'" +
            " WHERE ID=" + questionnaire.getId();
    dataStore.update(query);
  }

  @Override
  public List<Questionnaire> findAllQuestionnaires() {
    String query = "select * from QUESTIONNAIRES";
    List<Questionnaire> questionnaireList = dataStore.fetchRows(query,
            resultSet -> codec.unmarshall(resultSet.getString(2)));
    return questionnaireList;
  }

  @Override
  public Questionnaire getQuestionnaire(int id) {
    String query = "select * from QUESTIONNAIRES where ID = " + id;
    List<String> result = dataStore.fetchRows(query, resultSet -> resultSet.getString(2));
    if (result.isEmpty()){
        return null;
    }
    Questionnaire questionnaire = codec.unmarshall(result.get(0));
    return questionnaire;
  }

  @Override
  public Questionnaire getLastOrNewQuestionnaire() {
    String query = "SELECT * FROM QUESTIONNAIRES ORDER BY ID DESC LIMIT 1";
    List<String> result = dataStore.fetchRows(query, resultSet -> resultSet.getString(2));
    if (result.isEmpty()) {
      Questionnaire questionnaire = new Questionnaire(1);
      register(questionnaire);
      return questionnaire;
    }
    Questionnaire questionnaire = codec.unmarshall(result.get(0));
    if (questionnaire.noAnswers()) {
      return questionnaire;
    }
    Questionnaire newQuestionnaire = new Questionnaire(questionnaire.getId() + 1);
    register(newQuestionnaire);
    return newQuestionnaire;
  }
}
