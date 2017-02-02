package com.clouway.nvuapp.core;

import com.google.common.io.ByteStreams;
import com.google.common.primitives.Bytes;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertTrue;

/**
 * @author Petar Nedelchev (peter.krasimirov@gmail.com)
 */
public class PdfGeneratorTest {

    @Test
    public void pdfGenerated() throws Exception {
        ITextQuestionnairePdfGenerator pdfGenerator = new ITextQuestionnairePdfGenerator();
        Questionnaire questionnaire = new Questionnaire(1);
        questionnaire.add(new Question("", "A1", 1, 1, 1, 1, "Question", "b", "c", "a"));

        byte[] generatedPdf = pdfGenerator.generate(questionnaire);
        byte[] testFile = ByteStreams.toByteArray(PdfGeneratorTest.class.getResourceAsStream("test.pdf"));
        List<Byte> generatedBytes = Bytes.asList(generatedPdf);
        List<Byte> fileBytes = Bytes.asList(testFile);
        assertTrue(fileBytes.containsAll(generatedBytes));
    }

}

