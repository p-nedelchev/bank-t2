package com.clouway.nvuapp.adapter.http.controllers;

import com.clouway.nvuapp.core.QuestionnaireRepository;
import com.clouway.nvuapp.core.Questionnaire;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Vasil Mitov <v.mitov.clouway@gmail.com>
 */
public class InMemoryQuestionnaireRepository implements QuestionnaireRepository {

  private Map<Integer, Questionnaire> questionnaires = new LinkedHashMap<>();
  private Integer ID = 1;

  @Override
  public void register(Questionnaire questionnaire) {
    questionnaires.put(ID, questionnaire);
  }

  @Override
  public void update(Questionnaire questionnaire) {
    questionnaires.put(questionnaire.getId(), questionnaire);
  }

  @Override
  public List<Questionnaire> findAllQuestionnaires() {
    return null;
  }

  @Override
  public Questionnaire getQuestionnaire(int id) {
    return questionnaires.get(id);
  }

  @Override
  public Questionnaire getLastOrNewQuestionnaire() {
    if (!questionnaires.containsKey(ID)) {
      Questionnaire questionnaire = new Questionnaire(ID);
      register(questionnaire);
      return questionnaire;
    }
    return questionnaires.get(ID);
  }

}
