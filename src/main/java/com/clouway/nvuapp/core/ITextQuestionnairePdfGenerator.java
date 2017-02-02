package com.clouway.nvuapp.core;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.ListNumberingType;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author Petar Nedelchev (peter.krasimirov@gmail.com)
 */
public class ITextQuestionnairePdfGenerator implements QuestionnairePdfGenerator {

    @Override
    public byte[] generate(Questionnaire questionnaire) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf).setFont(getTimesNewRomanFont());

        List<Question> questions = questionnaire.getQuestions();
        for (Question question : questions) {
            Paragraph paragraph = new Paragraph(question.getQuestion()).addStyle(getQuestionStyle());
            com.itextpdf.layout.element.List listOfAnswers = new com.itextpdf.layout.element.List(ListNumberingType.ENGLISH_UPPER).addStyle(getAnswerStyle());
            listOfAnswers.add(question.getAnswerA());
            listOfAnswers.add(question.getAnswerB());
            listOfAnswers.add(question.getAnswerC());
            document.add(paragraph);
            document.add(listOfAnswers);
        }
        document.add(new AreaBreak());
        com.itextpdf.layout.element.List answersList = new com.itextpdf.layout.element.List(ListNumberingType.DECIMAL);
        Map<Integer, String> answers = questionnaire.getAnswers();
        for (Integer integer : answers.keySet()) {
            answersList.add(answers.get(integer)).addStyle(getAnswerStyle());
        }
        document.add(answersList);
        document.close();
        return baos.toByteArray();
    }

    private PdfFont getTimesNewRomanFont() {
        PdfFont font = null;
        String timesNewRoman = ITextQuestionnairePdfGenerator.class.getResource("TimesNewRoman.ttf").getFile();
        try {
            font = PdfFontFactory.createFont(timesNewRoman, "CP1251", true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return font;
    }

    private Style getQuestionStyle() {
        return new Style()
                .setFontSize(14f)
                .setBold();
    }

    private Style getAnswerStyle() {
        return new Style()
                .setFontSize(12f)
                .setMarginLeft(25f);
    }
}
