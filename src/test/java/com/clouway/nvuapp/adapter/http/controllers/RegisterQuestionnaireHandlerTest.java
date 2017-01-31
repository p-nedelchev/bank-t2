package com.clouway.nvuapp.adapter.http.controllers;

import com.clouway.nvuapp.core.*;
import com.clouway.nvuapp.FakeRequest;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.LinkedList;

import static com.clouway.nvuapp.core.ResponseReader.reader;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

/**
 * @author Vasil Mitov <v.mitov.clouway@gmail.com>
 */
public class RegisterQuestionnaireHandlerTest {

  @Test
  public void happyPath() throws Exception {
    QuestionnaireRepository tests = new InMemoryQuestionnaireRepository();
    QuestionRepository questions = new InMemoryQuestionRepository(new LinkedList<Question>() {{
      add(new Question("4321", "A1", 1, 2, 3, 4, "This is a question", "a", "b", "c"));
      add(new Question("4321", "A1", 1, 2, 3, 4, "This is a questiona", "aс", "b", "cdads"));
      add(new Question("4321", "A1", 1, 2, 3, 4, "This is a questionаaa", "as", "dadb", "asdc"));
    }});

    SecuredHandler handler = new RegisterQuestionnaireHandler(questions, tests);
    FakeRequest fakeRequest = new FakeRequest(new LinkedHashMap<String, Object>() {{
      put("amount", "2");
      put("category", "A1");
      put("module", "1");
      put("submodule", "2");
      put("theme", "3");
      put("difficulty", "4");
    }});
    Response response = handler.handle(fakeRequest, new Tutor("", ""));
    assertThat(reader().read(response), containsString("Въпросник номер " + 1 + " беше обновен."));
  }

  @Test
  public void userRegistersFirstFewQuestions() throws Exception {
    QuestionnaireRepository tests = new InMemoryQuestionnaireRepository();
    QuestionRepository questions = new InMemoryQuestionRepository(new LinkedList<Question>() {{
      add(new Question("4321", "A1", 1, 2, 3, 4, "This is a question", "a", "b", "c"));
      add(new Question("5321", "A1", 1, 2, 3, 4, "This is also a question", "a", "b", "c"));
      add(new Question("5321", "A1", 1, 2, 3, 4, "Duplicate question type from above to see what will happend ", "a", "b", "c"));
      add(new Question("5321", "A1", 1, 2, 3, 4, "Another question", "a", "b", "c"));
      add(new Question("1321", "A2", 1, 2, 3, 4, "Im getting tired of questions", "a", "b", "c"));
      add(new Question("1321", "A2", 2, 3, 4, 5, "Question has a different name", "a", "b", "c"));
    }});

    SecuredHandler handler = new RegisterQuestionnaireHandler(questions, tests);
    FakeRequest fakeRequest = new FakeRequest(new LinkedHashMap<String, Object>() {{
      put("category", "A1");
      put("module", "1");
      put("submodule", "2");
      put("theme", "3");
      put("difficulty", "4");
      put("amount", "2");
    }});

    Response response = handler.handle(fakeRequest, new Tutor("", ""));

    assertTrue(pageContainsAtLeastOf(reader().read(response), 2, "This is a question",
            "This is also a question", "Another question",
            "Duplicate question type from above to see what will happend"));
    assertFalse(tests.getLastOrNewQuestionnaire().getQuestions().isEmpty());
    assertThat(tests.getLastOrNewQuestionnaire().getQuestions().size(), is(2));
    assertThat(tests.getLastOrNewQuestionnaire().getId(), is(1));
    assertThat(reader().read(response), containsString("Въпросник номер 1 беше обновен."));
  }

  @Test
  public void userAddsTwoTypesOfQuestions() throws Exception {
    QuestionRepository questions = new InMemoryQuestionRepository(new LinkedList<Question>() {{
      add(new Question("4321", "A1", 1, 2, 3, 4, "This is a question", "a", "b", "c"));
      add(new Question("5321", "A1", 1, 2, 3, 4, "This is also a question", "a", "b", "c"));
      add(new Question("5321", "A1", 1, 2, 3, 4, "Duplicate question type from above to see what will happend ", "a", "b", "c"));
      add(new Question("5321", "B1", 2, 3, 4, 5, "Another question", "a", "b", "c"));
      add(new Question("1321", "B1", 2, 3, 4, 5, "Im getting tired of questions", "a", "b", "c"));
      add(new Question("1321", "B1", 2, 3, 4, 5, "Question has a different name", "a", "b", "c"));
    }});

    QuestionnaireRepository tests = new InMemoryQuestionnaireRepository();

    SecuredHandler handler = new RegisterQuestionnaireHandler(questions, tests);
    FakeRequest fakeRequest = new FakeRequest(new LinkedHashMap<String, Object>() {{
      put("category", "A1");
      put("module", "1");
      put("submodule", "2");
      put("theme", "3");
      put("difficulty", "4");
      put("amount", "2");
    }});
    handler.handle(fakeRequest, new Tutor("", ""));

    fakeRequest.setParams(new LinkedHashMap<String, String>() {{
      put("category", "B1");
      put("module", "2");
      put("submodule", "3");
      put("theme", "4");
      put("difficulty", "5");
      put("amount", "1");
    }});

    handler.handle(fakeRequest, new Tutor("", ""));

    assertThat(tests.getLastOrNewQuestionnaire().getId(), is(1));
    assertThat(tests.getLastOrNewQuestionnaire().getQuestions().size(), is(3));
  }

  @Test
  public void tryingToAddQuestionsThatAreNotInTheDB() throws Exception {
    QuestionRepository questions = new InMemoryQuestionRepository(new LinkedList<Question>());
    QuestionnaireRepository tests = new InMemoryQuestionnaireRepository();

    SecuredHandler handler = new RegisterQuestionnaireHandler(questions, tests);
    FakeRequest fakeRequest = new FakeRequest(new LinkedHashMap<String, Object>() {{
      put("category", "A1");
      put("module", "1");
      put("submodule", "2");
      put("theme", "3");
      put("difficulty", "4");
      put("amount", "1");
    }});
    Response response = handler.handle(fakeRequest, new Tutor("", ""));
    assertThat(reader().read(response), containsString("Няма въпроси все още."));
    assertThat(reader().read(response), containsString("Нямаше с какво да обновим"));
  }

  @Test
  public void tryingToAddQuestionsThatAreAlreadyInTheQuestionnaire() throws Exception {
    QuestionRepository questions = new InMemoryQuestionRepository(new LinkedList<Question>() {{
      add(new Question("4321", "A1", 1, 2, 3, 4, "This is a question", "a", "b", "c"));
      add(new Question("5321", "A1", 1, 2, 3, 4, "This is also a question", "a", "b", "c"));
    }});

    QuestionnaireRepository tests = new InMemoryQuestionnaireRepository();

    SecuredHandler handler = new RegisterQuestionnaireHandler(questions, tests);
    FakeRequest fakeRequest = new FakeRequest(new LinkedHashMap<String, Object>() {{
      put("category", "A1");
      put("module", "1");
      put("submodule", "2");
      put("theme", "3");
      put("difficulty", "4");
      put("amount", "2");
    }});

    handler.handle(fakeRequest, new Tutor("", ""));

    fakeRequest.setParams(new LinkedHashMap<String, String>() {{
      put("category", "A1");
      put("module", "1");
      put("submodule", "2");
      put("theme", "3");
      put("difficulty", "4");
      put("amount", "2");
    }});

    Response response = handler.handle(fakeRequest, new Tutor("", ""));
    assertThat(reader().read(response), containsString("Нямаше с какво да обновим"));
    assertThat(reader().read(response), containsString("Няма регистрирани въпроси от този вид или вече са добавени"));
  }

  @Test
  public void userAsksForMoreQuestionsThanTheDatabaseHas() throws Exception {
    QuestionRepository questions = new InMemoryQuestionRepository(new LinkedList<Question>() {{
      add(new Question("4321", "A1", 1, 2, 3, 4, "This is a question", "a", "b", "c"));
      add(new Question("5321", "A1", 1, 2, 3, 4, "This is also a question", "a", "b", "c"));
    }});

    QuestionnaireRepository tests = new InMemoryQuestionnaireRepository();

    SecuredHandler handler = new RegisterQuestionnaireHandler(questions, tests);
    FakeRequest fakeRequest = new FakeRequest(new LinkedHashMap<String, Object>() {{
      put("category", "A1");
      put("module", "1");
      put("submodule", "2");
      put("theme", "3");
      put("difficulty", "4");
      put("amount", "3");
    }});
    Response response = handler.handle(fakeRequest, new Tutor("", ""));
    assertThat(reader().read(response), containsString("Въпросник номер 1 беше обновен."));
    assertThat(reader().read(response), containsString("Имаше само 2 въпроса от вида който искахте които можем да добавим."));
    assertThat(tests.getLastOrNewQuestionnaire().getQuestions().size(), is(2));

  }

  @Test
  public void excludingQuesionsThatAreAlredyInTheQuestionnaire() throws Exception {
    QuestionRepository questions = new InMemoryQuestionRepository(new LinkedList<Question>() {{
      add(new Question("4321", "A1", 1, 2, 3, 4, "This is a question", "a", "b", "c"));
      add(new Question("5321", "A1", 1, 2, 3, 4, "This is also a question", "a", "b", "c"));
      add(new Question("5321", "A1", 1, 2, 3, 4, "Duplicate question type from above to see what will happend ", "a", "b", "c"));
    }});

    QuestionnaireRepository tests = new InMemoryQuestionnaireRepository();

    SecuredHandler handler = new RegisterQuestionnaireHandler(questions, tests);
    FakeRequest fakeRequest = new FakeRequest(new LinkedHashMap<String, Object>() {{
      put("category", "A1");
      put("module", "1");
      put("submodule", "2");
      put("theme", "3");
      put("difficulty", "4");
      put("amount", "2");
    }});
    handler.handle(fakeRequest, new Tutor("", ""));

    fakeRequest.setParams(new LinkedHashMap<String, String>() {{
      put("category", "A1");
      put("module", "1");
      put("submodule", "2");
      put("theme", "3");
      put("difficulty", "4");
      put("amount", "3");
    }});

    handler.handle(fakeRequest, new Tutor("", ""));

    assertThat(tests.getLastOrNewQuestionnaire().getQuestions().size(), is(3));

  }

  private boolean pageContainsAtLeastOf(String page, Integer amount, String... strings) {
    Integer currentAmount = 0;
    for (String question :
            strings) {
      if (page.contains(question)) {
        currentAmount++;
      }
    }
    if (currentAmount.equals(amount)) {
      return true;
    }
    return false;
  }
}