package com.clouway.nvuapp.adapter.http.controllers;

import com.clouway.nvuapp.FakeRequest;
import com.clouway.nvuapp.core.*;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.List;

import static com.google.common.collect.ImmutableMap.of;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Petar Nedelchev (peter.krasimirov@gmail.com)
 */
public class UnfinishedQuestionnaireEditHandlerTest {

    @Test
    public void selectedQuestionRemoved() throws Exception {
        Questionnaire questionnaire = new Questionnaire(1);
        QuestionnaireRepository questionnaireRepository = createQuestionnaireRepositoryWithCurrentQuestionnaire(questionnaire);

        QuestionRepository questionRepository = createQuestionRepositoryWithCurrentlyEditableQuestions(Lists.newArrayList(
                new Question("peter", "A1", 1, 2, 1, 2, "Some question", "a", "b", "c"),
                new Question("peter", "A1", 1, 1, 3, 1, "Some question", "b", "a", "c"),
                new Question("peter", "A1", 1, 3, 2, 3, "Some question", "c", "b", "a"),
                new Question("peter", "A1", 1, 2, 4, 2, "Some question", "a", "c", "b")
        ));
        questionnaire.addQuestions(questionRepository.getQuestions());

        Question deletedQuestion = questionRepository.getQuestions().get(2);

        SecuredHandler handler = new UnfinishedQuestionnaireEditHandler(questionnaireRepository, questionRepository);
        FakeRequest request = new FakeRequest(of("index", 2, "action", "delete"));

        Response response = handler.handle(request, new Tutor("", ""));

        assertThat(questionnaire.getQuestions().size(), is(3));
        assertThat(questionnaire.getQuestions(), not(hasItem(deletedQuestion)));
    }

    @Test
    public void selectedQuestionReplaced() throws Exception {
        Questionnaire questionnaire = new Questionnaire(1);
        QuestionnaireRepository questionnaireRepository = createQuestionnaireRepositoryWithCurrentQuestionnaire(questionnaire);

        QuestionRepository questionRepository = createQuestionRepositoryWithCurrentlyEditableQuestions(Lists.newArrayList(
                new Question("peter", "A1", 1, 2, 1, 2, "Some question", "a", "b", "c"),
                new Question("peter", "A1", 1, 1, 3, 1, "Some question", "b", "a", "c"),
                new Question("peter", "A1", 1, 3, 2, 3, "Some question", "c", "b", "a"),
                new Question("peter", "A1", 1, 2, 4, 2, "Some question", "a", "c", "b")
        ));

        questionnaire.addQuestions(questionRepository.getQuestions());
        Question substituteQuestion = new Question("peter", "A1", 1, 3, 2, 3, "Other question", "bb", "aa", "cc");
        questionRepository.register(substituteQuestion);

        SecuredHandler handler = new UnfinishedQuestionnaireEditHandler(questionnaireRepository, questionRepository);

        FakeRequest request = new FakeRequest(of("index", 2, "action", "replace"));

        Response response = handler.handle(request, new Tutor("", ""));

        assertThat(questionnaire.getQuestions().size(), is(4));
        assertThat(questionnaire.getQuestions(), hasItem(substituteQuestion));
    }

    @Test
    public void notAvailableQuestionToReplace() throws Exception {
        Questionnaire questionnaire = new Questionnaire(1);
        QuestionnaireRepository questionnaireRepository = createQuestionnaireRepositoryWithCurrentQuestionnaire(questionnaire);

        QuestionRepository questionRepository = createQuestionRepositoryWithCurrentlyEditableQuestions(Lists.newArrayList(
                new Question("peter", "A1", 1, 2, 1, 2, "Some question", "a", "b", "c"),
                new Question("peter", "A1", 1, 1, 3, 1, "Some question", "b", "a", "c"),
                new Question("peter", "A1", 1, 3, 2, 3, "Some question", "c", "b", "a"),
                new Question("peter", "A1", 1, 2, 4, 2, "Some question", "a", "c", "b")
        ));
        questionnaire.addQuestions(questionRepository.getQuestions());

        SecuredHandler handler = new UnfinishedQuestionnaireEditHandler(questionnaireRepository, questionRepository);

        FakeRequest request = new FakeRequest(of("index", 2, "action", "replace"));

        Response response = handler.handle(request, new Tutor("", ""));

        assertThat(questionnaire.getQuestions().size(), is(4));
    }

    private QuestionRepository createQuestionRepositoryWithCurrentlyEditableQuestions(List<Question> questions) {
        return new InMemoryQuestionRepository(questions);
    }

    private QuestionnaireRepository createQuestionnaireRepositoryWithCurrentQuestionnaire(Questionnaire questionnaire) {
        QuestionnaireRepository repository = new InMemoryQuestionnaireRepository();
        repository.register(questionnaire);
        return repository;
    }

}
