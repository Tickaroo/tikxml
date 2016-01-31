/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tickaroo.tikxml.converter.htmlescape;

/**
 * <p>Escapes and unescapes {@code String}s for
 * Java, Java Script, HTML and XML.</p>
 *
 * <p>#ThreadSafe#</p>
 * @since 2.0
 */
public class StringEscapeUtils {

    /**
     * Translator object for escaping XML 1.0.
     * 
     * While {@link #escapeXml10(String)} is the expected method of use, this
     * object allows the XML escaping functionality to be used
     * as the foundation for a custom translator.
     *
     * @since 3.3
     */
    public static final CharSequenceTranslator ESCAPE_XML10 =
        new AggregateTranslator(
            new LookupTranslator(EntityArrays.BASIC_ESCAPE()),
            new LookupTranslator(EntityArrays.APOS_ESCAPE()),
            new LookupTranslator(
                    new String[][] {
                            { "\u0000", "" },
                            { "\u0001", "" },
                            { "\u0002", "" },
                            { "\u0003", "" },
                            { "\u0004", "" },
                            { "\u0005", "" },
                            { "\u0006", "" },
                            { "\u0007", "" },
                            { "\u0008", "" },
                            { "\u000b", "" },
                            { "\u000c", "" },
                            { "\u000e", "" },
                            { "\u000f", "" },
                            { "\u0010", "" },
                            { "\u0011", "" },
                            { "\u0012", "" },
                            { "\u0013", "" },
                            { "\u0014", "" },
                            { "\u0015", "" },
                            { "\u0016", "" },
                            { "\u0017", "" },
                            { "\u0018", "" },
                            { "\u0019", "" },
                            { "\u001a", "" },
                            { "\u001b", "" },
                            { "\u001c", "" },
                            { "\u001d", "" },
                            { "\u001e", "" },
                            { "\u001f", "" },
                            { "\ufffe", "" },
                            { "\uffff", "" }
                    }),
            NumericEntityEscaper.between(0x7f, 0x84),
            NumericEntityEscaper.between(0x86, 0x9f),
            new UnicodeUnpairedSurrogateRemover()
        );
    
    /**
     * Translator object for escaping XML 1.1.
     * 
     * While {@link #escapeXml11(String)} is the expected method of use, this
     * object allows the XML escaping functionality to be used
     * as the foundation for a custom translator.
     *
     * @since 3.3
     */
    public static final CharSequenceTranslator ESCAPE_XML11 =
        new AggregateTranslator(
            new LookupTranslator(EntityArrays.BASIC_ESCAPE()),
            new LookupTranslator(EntityArrays.APOS_ESCAPE()),
            new LookupTranslator(
                    new String[][] {
                            { "\u0000", "" },
                            { "\u000b", "&#11;" },
                            { "\u000c", "&#12;" },
                            { "\ufffe", "" },
                            { "\uffff", "" }
                    }),
            NumericEntityEscaper.between(0x1, 0x8),
            NumericEntityEscaper.between(0xe, 0x1f),
            NumericEntityEscaper.between(0x7f, 0x84),
            NumericEntityEscaper.between(0x86, 0x9f),
            new UnicodeUnpairedSurrogateRemover()
        );

    /**
     * Translator object for escaping HTML version 3.0.
     * 
     * While {@link #escapeHtml3(String)} is the expected method of use, this 
     * object allows the HTML escaping functionality to be used 
     * as the foundation for a custom translator. 
     *
     * @since 3.0
     */
    public static final CharSequenceTranslator ESCAPE_HTML3 = 
        new AggregateTranslator(
            new LookupTranslator(EntityArrays.BASIC_ESCAPE()),
            new LookupTranslator(EntityArrays.ISO8859_1_ESCAPE())
        );

    /**
     * Translator object for escaping HTML version 4.0.
     * 
     * While {@link #escapeHtml4(String)} is the expected method of use, this 
     * object allows the HTML escaping functionality to be used 
     * as the foundation for a custom translator. 
     *
     * @since 3.0
     */
    public static final CharSequenceTranslator ESCAPE_HTML4 = 
        new AggregateTranslator(
            new LookupTranslator(EntityArrays.BASIC_ESCAPE()),
            new LookupTranslator(EntityArrays.ISO8859_1_ESCAPE()),
            new LookupTranslator(EntityArrays.HTML40_EXTENDED_ESCAPE())
        );

    /**
     * Translator object for unescaping escaped HTML 3.0. 
     * 
     * While {@link #unescapeHtml3(String)} is the expected method of use, this 
     * object allows the HTML unescaping functionality to be used 
     * as the foundation for a custom translator. 
     *
     * @since 3.0
     */
    public static final CharSequenceTranslator UNESCAPE_HTML3 = 
        new AggregateTranslator(
            new LookupTranslator(EntityArrays.BASIC_UNESCAPE()),
            new LookupTranslator(EntityArrays.ISO8859_1_UNESCAPE()),
            new NumericEntityUnescaper()
        );

    /**
     * Translator object for unescaping escaped HTML 4.0. 
     * 
     * While {@link #unescapeHtml4(String)} is the expected method of use, this 
     * object allows the HTML unescaping functionality to be used 
     * as the foundation for a custom translator. 
     *
     * @since 3.0
     */
    public static final CharSequenceTranslator UNESCAPE_HTML4 = 
        new AggregateTranslator(
            new LookupTranslator(EntityArrays.BASIC_UNESCAPE()),
            new LookupTranslator(EntityArrays.ISO8859_1_UNESCAPE()),
            new LookupTranslator(EntityArrays.HTML40_EXTENDED_UNESCAPE()),
            new NumericEntityUnescaper()
        );

    /**
     * Translator object for unescaping escaped XML.
     * 
     * While {@link #unescapeXml(String)} is the expected method of use, this 
     * object allows the XML unescaping functionality to be used 
     * as the foundation for a custom translator. 
     *
     * @since 3.0
     */
    public static final CharSequenceTranslator UNESCAPE_XML = 
        new AggregateTranslator(
            new LookupTranslator(EntityArrays.BASIC_UNESCAPE()),
            new LookupTranslator(EntityArrays.APOS_UNESCAPE()),
            new NumericEntityUnescaper()
        );



    /* Helper functions */

    /**
     * <p>{@code StringEscapeUtils} instances should NOT be constructed in
     * standard programming.</p>
     *
     * <p>Instead, the class should be used as:</p>
     * <pre>StringEscapeUtils.escapeJava("foo");</pre>
     *
     * <p>This constructor is public to permit tools that require a JavaBean
     * instance to operate.</p>
     */
    public StringEscapeUtils() {
      super();
    }



    // HTML and XML
    //--------------------------------------------------------------------------
    /**
     * <p>Escapes the characters in a {@code String} using HTML entities.</p>
     *
     * <p>
     * For example:
     * </p> 
     * <p><code>"bread" &amp; "butter"</code></p>
     * becomes:
     * <p>
     * <code>&amp;quot;bread&amp;quot; &amp;amp; &amp;quot;butter&amp;quot;</code>.
     * </p>
     *
     * <p>Supports all known HTML 4.0 entities, including funky accents.
     * Note that the commonly used apostrophe escape character (&amp;apos;)
     * is not a legal entity and so is not supported). </p>
     *
     * @param input  the {@code String} to escape, may be null
     * @return a new escaped {@code String}, {@code null} if null string input
     * 
     * @see <a href="http://hotwired.lycos.com/webmonkey/reference/special_characters/">ISO Entities</a>
     * @see <a href="http://www.w3.org/TR/REC-html32#latin1">HTML 3.2 Character Entities for ISO Latin-1</a>
     * @see <a href="http://www.w3.org/TR/REC-html40/sgml/entities.html">HTML 4.0 Character entity references</a>
     * @see <a href="http://www.w3.org/TR/html401/charset.html#h-5.3">HTML 4.01 Character References</a>
     * @see <a href="http://www.w3.org/TR/html401/charset.html#code-position">HTML 4.01 Code positions</a>
     * 
     * @since 3.0
     */
    public static final String escapeHtml4(final String input) {
        return ESCAPE_HTML4.translate(input);
    }

    /**
     * <p>Escapes the characters in a {@code String} using HTML entities.</p>
     * <p>Supports only the HTML 3.0 entities. </p>
     *
     * @param input  the {@code String} to escape, may be null
     * @return a new escaped {@code String}, {@code null} if null string input
     * 
     * @since 3.0
     */
    public static final String escapeHtml3(final String input) {
        return ESCAPE_HTML3.translate(input);
    }

    //-----------------------------------------------------------------------
    /**
     * <p>Unescapes a string containing entity escapes to a string
     * containing the actual Unicode characters corresponding to the
     * escapes. Supports HTML 4.0 entities.</p>
     *
     * <p>For example, the string {@code "&lt;Fran&ccedil;ais&gt;"}
     * will become {@code "<Français>"}</p>
     *
     * <p>If an entity is unrecognized, it is left alone, and inserted
     * verbatim into the result string. e.g. {@code "&gt;&zzzz;x"} will
     * become {@code ">&zzzz;x"}.</p>
     *
     * @param input  the {@code String} to unescape, may be null
     * @return a new unescaped {@code String}, {@code null} if null string input
     * 
     * @since 3.0
     */
    public static final String unescapeHtml4(final String input) {
        return UNESCAPE_HTML4.translate(input);
    }

    /**
     * <p>Unescapes a string containing entity escapes to a string
     * containing the actual Unicode characters corresponding to the
     * escapes. Supports only HTML 3.0 entities.</p>
     *
     * @param input  the {@code String} to unescape, may be null
     * @return a new unescaped {@code String}, {@code null} if null string input
     * 
     * @since 3.0
     */
    public static final String unescapeHtml3(final String input) {
        return UNESCAPE_HTML3.translate(input);
    }


    /**
     * <p>Escapes the characters in a {@code String} using XML entities.</p>
     *
     * <p>For example: {@code "bread" & "butter"} =&gt;
     * {@code &quot;bread&quot; &amp; &quot;butter&quot;}.
     * </p>
     *
     * <p>Note that XML 1.0 is a text-only format: it cannot represent control
     * characters or unpaired Unicode surrogate codepoints, even after escaping.
     * {@code escapeXml10} will remove characters that do not fit in the
     * following ranges:</p>
     * 
     * <p>{@code #x9 | #xA | #xD | [#x20-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]}</p>
     * 
     * <p>Though not strictly necessary, {@code escapeXml10} will escape
     * characters in the following ranges:</p>
     * 
     * <p>{@code [#x7F-#x84] | [#x86-#x9F]}</p>
     * 
     * <p>The returned string can be inserted into a valid XML 1.0 or XML 1.1
     * document. If you want to allow more non-text characters in an XML 1.1
     * document, use {@link #escapeXml11(String)}.</p>
     *
     * @param input  the {@code String} to escape, may be null
     * @return a new escaped {@code String}, {@code null} if null string input
     * @see #unescapeXml(String)
     * @since 3.3
     */
    public static String escapeXml10(final String input) {
        return ESCAPE_XML10.translate(input);
    }
    
    /**
     * <p>Escapes the characters in a {@code String} using XML entities.</p>
     *
     * <p>For example: {@code "bread" & "butter"} =&gt;
     * {@code &quot;bread&quot; &amp; &quot;butter&quot;}.
     * </p>
     *
     * <p>XML 1.1 can represent certain control characters, but it cannot represent
     * the null byte or unpaired Unicode surrogate codepoints, even after escaping.
     * {@code escapeXml11} will remove characters that do not fit in the following
     * ranges:</p>
     * 
     * <p>{@code [#x1-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]}</p>
     * 
     * <p>{@code escapeXml11} will escape characters in the following ranges:</p>
     * 
     * <p>{@code [#x1-#x8] | [#xB-#xC] | [#xE-#x1F] | [#x7F-#x84] | [#x86-#x9F]}</p>
     * 
     * <p>The returned string can be inserted into a valid XML 1.1 document. Do not
     * use it for XML 1.0 documents.</p>
     *
     * @param input  the {@code String} to escape, may be null
     * @return a new escaped {@code String}, {@code null} if null string input
     * @see #unescapeXml(String)
     * @since 3.3
     */
    public static String escapeXml11(final String input) {
        return ESCAPE_XML11.translate(input);
    }

    //-----------------------------------------------------------------------
    /**
     * <p>Unescapes a string containing XML entity escapes to a string
     * containing the actual Unicode characters corresponding to the
     * escapes.</p>
     *
     * <p>Supports only the five basic XML entities (gt, lt, quot, amp, apos).
     * Does not support DTDs or external entities.</p>
     *
     * <p>Note that numerical \\u Unicode codes are unescaped to their respective 
     *    Unicode characters. This may change in future releases. </p>
     *
     * @param input  the {@code String} to unescape, may be null
     * @return a new unescaped {@code String}, {@code null} if null string input
     * @see #escapeXml10(String)
     * @see #escapeXml11(String)
     */
    public static final String unescapeXml(final String input) {
        return UNESCAPE_XML.translate(input);
    }

}