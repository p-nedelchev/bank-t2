package com.clouway.nvuapp.core;

import org.junit.Test;

import java.util.LinkedList;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * @author Vasil Mitov <v.mitov.clouway@gmail.com>
 */
public class JsonCodecTest {
  @Test
  public void happyPath() throws Exception {
    JsonCodec codec = new JsonCodec();
    Question question = new Question("admin", "A1", 1, 2, 3, 4, "q", "a", "b", "c");
    Questionnaire questionnaire = new Questionnaire(1);
    questionnaire.addQuestions(new LinkedList<Question>() {{
      add(question);
    }});
    String data = codec.marshallToString(questionnaire);
    Questionnaire unmarshalledQuestionnaire = codec.unmarshall(data);
    assertThat(questionnaire, is(unmarshalledQuestionnaire));

  }

  @Test
  public void jsonCodecEscapeQuotes() throws Exception {
    JsonCodec codec = new JsonCodec();

    Question question = new Question("admin", "A1", 1, 2, 3, 4, "question with \"quotes\"", "a", "b", "c");
    Questionnaire realQuestionnaire = new Questionnaire(1);

    realQuestionnaire.addQuestions(new LinkedList<Question>() {{
      add(question);
    }});

    String data = codec.marshallToString(realQuestionnaire);
    Questionnaire unmarshalledQuestionnaire = codec.unmarshall(data);

    assertThat(realQuestionnaire, is(unmarshalledQuestionnaire));
  }
}