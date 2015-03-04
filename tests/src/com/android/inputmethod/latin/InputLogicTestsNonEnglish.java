/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.inputmethod.latin;

import android.test.suitebuilder.annotation.LargeTest;

import com.android.inputmethod.latin.common.Constants;
import com.android.inputmethod.latin.settings.Settings;

@LargeTest
public class InputLogicTestsNonEnglish extends InputTestsBase {

    public void testAutoCorrectForFrench() {
        final String STRING_TO_TYPE = "irq ";
        final String EXPECTED_RESULT = "ira ";
        changeLanguage("fr");
        type(STRING_TO_TYPE);
        assertEquals("simple auto-correct for French", EXPECTED_RESULT,
                mEditText.getText().toString());
    }

    public void testManualPickThenSeparatorForFrench() {
        final String WORD1_TO_TYPE = "test";
        final String WORD2_TO_TYPE = "!";
        final String EXPECTED_RESULT = "test !";
        changeLanguage("fr");
        type(WORD1_TO_TYPE);
        pickSuggestionManually(WORD1_TO_TYPE);
        type(WORD2_TO_TYPE);
        assertEquals("manual pick then separator for French", EXPECTED_RESULT,
                mEditText.getText().toString());
    }

    public void testClusteringPunctuationForFrench() {
        final String WORD1_TO_TYPE = "test";
        final String WORD2_TO_TYPE = "!!?!:!";
        // In English, the expected result would be "test!!?!:!"
        final String EXPECTED_RESULT = "test !!?! : !";
        changeLanguage("fr");
        type(WORD1_TO_TYPE);
        pickSuggestionManually(WORD1_TO_TYPE);
        type(WORD2_TO_TYPE);
        assertEquals("clustering punctuation for French", EXPECTED_RESULT,
                mEditText.getText().toString());
    }

    public void testWordThenSpaceThenPunctuationFromStripTwiceForFrench() {
        final String WORD_TO_TYPE = "test ";
        final String PUNCTUATION_FROM_STRIP = "!";
        final String EXPECTED_RESULT = "test !!";
        changeLanguage("fr");
        type(WORD_TO_TYPE);
        sleep(DELAY_TO_WAIT_FOR_UNDERLINE_MILLIS);
        runMessages();
        assertTrue("type word then type space should display punctuation strip",
                mLatinIME.getSuggestedWordsForTest().isPunctuationSuggestions());
        pickSuggestionManually(PUNCTUATION_FROM_STRIP);
        pickSuggestionManually(PUNCTUATION_FROM_STRIP);
        assertEquals("type word then type space then punctuation from strip twice for French",
                EXPECTED_RESULT, mEditText.getText().toString());
    }

    public void testWordThenSpaceDisplaysPredictions() {
        final String WORD_TO_TYPE = "beaujolais ";
        final String EXPECTED_RESULT = "nouveau";
        changeLanguage("fr");
        type(WORD_TO_TYPE);
        sleep(DELAY_TO_WAIT_FOR_UNDERLINE_MILLIS);
        runMessages();
        final SuggestedWords suggestedWords = mLatinIME.getSuggestedWordsForTest();
        assertEquals("type word then type space yields predictions for French",
                EXPECTED_RESULT, suggestedWords.size() > 0 ? suggestedWords.getWord(0) : null);
    }

    public void testAutoCorrectForGerman() {
        final String STRING_TO_TYPE = "unf ";
        final String EXPECTED_RESULT = "und ";
        changeLanguage("de");
        type(STRING_TO_TYPE);
        assertEquals("simple auto-correct for German", EXPECTED_RESULT,
                mEditText.getText().toString());
    }

    public void testAutoCorrectWithUmlautForGerman() {
        final String STRING_TO_TYPE = "ueber ";
        final String EXPECTED_RESULT = "über ";
        changeLanguage("de");
        type(STRING_TO_TYPE);
        assertEquals("auto-correct with umlaut for German", EXPECTED_RESULT,
                mEditText.getText().toString());
    }

    // Corresponds to InputLogicTests#testDoubleSpace
    public void testDoubleSpaceHindi() {
        changeLanguage("hi");
        // Set default pref just in case
        setBooleanPreference(Settings.PREF_KEY_USE_DOUBLE_SPACE_PERIOD, true, true);
        // U+1F607 is an emoji
        final String[] STRINGS_TO_TYPE =
                new String[] { "this   ", "a+  ", "\u1F607  ", "||  ", ")  ", "(  ", "%  " };
        final String[] EXPECTED_RESULTS =
                new String[] { "this|  ", "a+| ", "\u1F607| ", "||  ", ")| ", "(  ", "%| " };
        for (int i = 0; i < STRINGS_TO_TYPE.length; ++i) {
            mEditText.setText("");
            type(STRINGS_TO_TYPE[i]);
            assertEquals("double space processing", EXPECTED_RESULTS[i],
                    mEditText.getText().toString());
        }
    }

    // Corresponds to InputLogicTests#testCancelDoubleSpace
    public void testCancelDoubleSpaceHindi() {
        changeLanguage("hi");
        final String STRING_TO_TYPE = "this  ";
        final String EXPECTED_RESULT = "this ";
        type(STRING_TO_TYPE);
        type(Constants.CODE_DELETE);
        assertEquals("double space make a period", EXPECTED_RESULT, mEditText.getText().toString());
    }
}
