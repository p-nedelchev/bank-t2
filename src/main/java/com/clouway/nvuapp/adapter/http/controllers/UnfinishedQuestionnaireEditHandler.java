package com.clouway.nvuapp.adapter.http.controllers;

import com.clouway.nvuapp.adapter.http.servlet.RsFreemarker;
import com.clouway.nvuapp.core.*;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

import java.util.List;

/**
 * @author Petar Nedelchev (peter.krasimirov@gmail.com)
 */
public class UnfinishedQuestionnaireEditHandler implements SecuredHandler {
    private final QuestionnaireRepository repository;
    private final QuestionRepository questionRepository;

    public UnfinishedQuestionnaireEditHandler(QuestionnaireRepository repository, QuestionRepository questionRepository) {
        this.repository = repository;
        this.questionRepository = questionRepository;
    }

    @Override
    public Response handle(Request request, Tutor tutor) {
        String action = request.param("action");
        String indexOfQuestion = request.param("index");

        Questionnaire questionnaire = repository.getLastOrNewQuestionnaire();
        List<Question> questions = questionnaire.getQuestions();

        if (!isValidIndex(indexOfQuestion, questions.size())) {
            String message = "Няма въпрос с този индекс";
            return new RsFreemarker("generateQuestionnaire.html", ImmutableMap.of("values", questions, "message", message));
        }

        int index = Integer.parseInt(indexOfQuestion);

        if (action.equals("delete")) {
            String message = "Въпросът беше изтрит";
            questionnaire = questionnaire.removeQuestionAt(index);
            repository.update(questionnaire);
            return new RsFreemarker("generateQuestionnaire.html", ImmutableMap.of("values", questionnaire.getQuestions(), "message", message));
        }

        Question selectedQuestion = questions.get(index);
        Optional<Question> newQuestion = questionRepository.getRandomQuestionExcluding(selectedQuestion, questionnaire);
        if (!newQuestion.isPresent()) {
            String message = "Няма наличен въпрос";
            return new RsFreemarker("generateQuestionnaire.html", ImmutableMap.of("values", questions, "message", message));
        }

        String message = "Въпросът беше заменен";
        questionnaire = questionnaire.setQuestionAt(index, newQuestion.get());
        repository.update(questionnaire);

        return new RsFreemarker("generateQuestionnaire.html", ImmutableMap.of("values", questions, "message", message));
    }

    private boolean isValidIndex(String value, int maxRange) {
        int index;
        try {
            index = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return false;
        }
        return index >= 0 && index < maxRange;
    }
}
