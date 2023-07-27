package com.android.inputmethod;

import static org.junit.Assert.assertEquals;


import androidx.test.filters.SmallTest;

import com.android.inputmethod.latin.PhoneticBangla;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


import java.text.Normalizer;
import java.util.Map;
@SmallTest
public class PhoneticTest {
    @Test
    public void testKeyMap() {
        PhoneticBangla phoneticBangla = new PhoneticBangla();

        Map<String, String> jbrMap = phoneticBangla.kar;
        for (Map.Entry<String, String> entry : jbrMap.entrySet()) {
            String input = entry.getKey();
            String expectedOutput = entry.getValue();

            // Test the phonetic method with each input from the jbr map
            String actualOutput = phoneticBangla.phonetic(new StringBuilder(input)).toString();

            // Normalize the strings to remove any non-printable characters
            expectedOutput = normalizeString(expectedOutput);
            actualOutput = normalizeString(actualOutput);

            assertEquals("Input: "+input ,expectedOutput, actualOutput);
            System.out.println("Test successful! Input "+input+" Expected: " + expectedOutput + ", Output: " + actualOutput);

        }
    }

    // Helper method to remove non-printable characters from a string
    private String normalizeString(String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFKC)
                .replaceAll("\\p{C}", "");
    }
}
