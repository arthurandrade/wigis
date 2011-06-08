/*
 * ==============================================================================
 * =============================
 * 
 * Copyright (c) 2010, University of California, Santa Barbara All rights
 * reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * ==================================================
 * ===========================================================
 */

package net.wigis.svetlin;

/*
 * package Svetlin;
 * 
 * import java.io.StringReader; import java.util.Stack;
 * 
 * import javax.swing.text.BadLocationException; import
 * javax.swing.text.MutableAttributeSet; import javax.swing.text.html.HTML;
 * import javax.swing.text.html.HTMLEditorKit; import
 * javax.swing.text.html.parser.ParserDelegator;
 * 
 * public class HtmlParser { public static void main(String [] args) throws
 * Exception { HTMLEditorKit.ParserCallback callback = new HtmlParserCallBack();
 * StringReader reader = newStringReader(
 * "<table class=\"sparql\" border=\"1\">  <tr>    <th>p1</th>    <th>o1</th>  </tr>  <tr>    <td>http://www.w3.org/1999/02/22-rdf-syntax-ns#type</td>    <td>http://www.w3.org/2002/07/owl#Thing</td>  </tr>  <tr>    <td>http://www.w3.org/1999/02/22-rdf-syntax-ns#type</td>    <td>http://dbpedia.org/ontology/Band</td>  </tr>  <tr>    <td>http://www.w3.org/1999/02/22-rdf-syntax-ns#type</td>    <td>http://dbpedia.org/ontology/Organisation</td>  </tr>  <tr>    <td>http://www.w3.org/1999/02/22-rdf-syntax-ns#type</td>    <td>http://sw.opencyc.org/2008/06/10/concept/Mx4rvViqQZwpEbGdrcN5Y29ycA</td>  </tr>  <tr>    <td>http://dbpedia.org/ontology/genre</td>    <td>http://dbpedia.org/resource/Hard_rock</td>  </tr>  <tr>    <td>http://dbpedia.org/ontology/Artist/genre</td>    <td>http://dbpedia.org/resource/Hard_rock</td>  </tr>  <tr>    <td>http://dbpedia.org/ontology/MusicalArtist/background</td>    <td>group_or_band</td>  </tr>  <tr>    <td>http://dbpedia.org/ontology/homeTown</td>    <td>http://dbpedia.org/resource/United_States</td>  </tr>  <tr>    <td>http://dbpedia.org/ontology/Person/homeTown</td>    <td>http://dbpedia.org/resource/United_States</td>  </tr>  <tr>    <td>http://dbpedia.org/ontology/background</td>    <td>group_or_band</td>  </tr>  <tr>    <td>http://www.w3.org/2004/02/skos/core#subject</td>    <td>http://dbpedia.org/resource/Category:1990s_music_groups</td>  </tr>  <tr>    <td>http://www.w3.org/2004/02/skos/core#subject</td>    <td>http://dbpedia.org/resource/Category:American_rock_music_groups</td>  </tr>  <tr>    <td>http://www.w3.org/2004/02/skos/core#subject</td>    <td>http://dbpedia.org/resource/Category:Grammy_Award_winners</td>  </tr>  <tr>    <td>http://www.w3.org/2004/02/skos/core#subject</td>    <td>http://dbpedia.org/resource/Category:Hard_rock_musical_groups</td>  </tr>  <tr>    <td>http://www.w3.org/2004/02/skos/core#subject</td>    <td>http://dbpedia.org/resource/Category:Musical_quartets</td>  </tr>  <tr>    <td>http://www.w3.org/2004/02/skos/core#subject</td>    <td>http://dbpedia.org/resource/Category:1980s_music_groups</td>  </tr>  <tr>    <td>http://www.w3.org/2004/02/skos/core#subject</td>    <td>http://dbpedia.org/resource/Category:2000s_music_groups</td>  </tr>  <tr>    <td>http://dbpedia.org/property/wikiPageUsesTemplate</td>    <td>http://dbpedia.org/resource/Template:infobox_musical_artist</td>  </tr>  <tr>    <td>http://dbpedia.org/property/background</td>    <td>group_or_band</td>  </tr>  <tr>    <td>http://dbpedia.org/property/genre</td>    <td>http://dbpedia.org/resource/Hard_rock</td>  </tr>  <tr>    <td>http://dbpedia.org/property/imgSize</td>    <td>250</td>  </tr>  <tr>    <td>http://dbpedia.org/property/origin</td>    <td>http://dbpedia.org/resource/United_States</td>  </tr>  <tr>    <td>http://dbpedia.org/property/wordnet_type</td>    <td>http://www.w3.org/2006/03/wn/wn20/instances/synset-musician-noun-1</td>  </tr></table>"
 * ); ParserDelegator delegator = new ParserDelegator(); delegator.parse(reader,
 * callback, false); }
 * 
 * }
 * 
 * // Implement the call back class. Just like a SAX content handler class
 * HtmlParserCallBack extends HTMLEditorKit.ParserCallback { Stack<HTML.Tag>
 * stack = new Stack<HTML.Tag>(); public void flush() throws
 * BadLocationException{} //public void handleComment(char[] data, int pos){}
 * 
 * public void handleStartTag(HTML.Tag tag, MutableAttributeSet a, int pos) { //
 * get a tag and push it into a stack //System.out.println("Tag: " + tag );
 * stack.push(tag); }
 * 
 * //public void handleEndTag(HTML.Tag t, int pos){} //public void
 * handleSimpleTag(HTML.Tag t,MutableAttributeSet a, int pos){} //public void
 * handleError(String errorMsg, int pos){} //public void
 * handleEndOfLineString(String eol){}
 * 
 * public void handleText(char[] data, int pos) { // pop the stack to get the
 * latest tag processed. If you are interested // in parsing it and extracting
 * the data continue. else return Object o = stack.pop(); if ( !
 * ((HTML.Tag)o).toString().equals("td")) { return; } String strData=""; for
 * (char ch : data) { strData = strData + ch; } System.out.println("Text: " +
 * strData ); } }
 */