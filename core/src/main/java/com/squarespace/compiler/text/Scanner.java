/**
 * Copyright (c) 2017 Squarespace, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.squarespace.compiler.text;

import com.squarespace.compiler.match.Recognizers.Recognizer;


/**
 * Simple string scanner supporting multiple simultaneous "streams" over it.
 */
public class Scanner {

  private final String raw;

  public Scanner(String raw) {
    this.raw = raw;
  }

  public Stream stream() {
    return new Stream();
  }

  /**
   * Represents a single range of characters that can be traversed and
   * matched against.
   */
  public class Stream {

    public int pos;
    public int end;

    public Stream() {
      this(0, raw.length());
    }

    /**
     * Create a stream with the given bounds.
     */
    public Stream(int pos, int end) {
      set(pos, end);
    }

    /**
     * Return reference to the raw underlying string.
     */
    public String raw() {
      return raw;
    }

    /**
     * Set the bounds of this stream.
     */
    public void set(int pos, int end) {
      this.pos = pos;
      this.end = end;
    }

    /**
     * Set the bounds of this stream to match the 'other' stream.
     */
    public void setFrom(Stream other) {
      this.pos = other.pos;
      this.end = other.end;
    }

    /**
     * Jump this state over 'other'.
     */
    public void jump(Stream other) {
      this.pos = other.end;
    }

    /**
     * Peek at the next character.
     */
    public char peek() {
      return pos < end ? raw.charAt(pos) : Chars.EOF;
    }

    /**
     * Advance past the next character and return it.
     */
    public char seek() {
      char ch = peek();
      pos++;
      return ch;
    }

    /**
     * Match the given recognizer against the current stream, and if
     * matching set the bounds of the 'other' stream. Return a boolean
     * indicating whether the recognizer matched.
     */
    public boolean seek(Recognizer matcher, Stream other) {
      int r = matcher.match(raw, pos, end);
      if (r != -1) {
        other.set(pos, r);
        return true;
      }
      return false;
    }

    /**
     * Skip over any whitespace characters.
     */
    public void skipWs() {
      while (pos < end) {
        if (!DefaultCharClassifier.whitespace(raw.charAt(pos))) {
          break;
        }
        pos++;
      }
    }

    /**
     * Set the bounds of the other stream to the area enclosed by the left
     * and right delimiters. Also handles skipping over nested delimiters
     * to find the matching ones.
     */
    public boolean seekBounds(Stream other, char left, char right) {
      int depth = 0;
      int start = -1;
      while (pos < end) {
        char ch = raw.charAt(pos);
        if (ch == left) {
          if (depth == 0) {
            start = pos;
          }
          depth++;
        } else if (ch == right && start != -1) {
          depth--;
          if (depth == 0) {
            pos++;
            other.set(start, pos);
            return true;
          }
        }
        pos++;
      }
      return false;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof Stream) {
        Stream other = (Stream) obj;
        return equalsCharacters(other.raw(), other.pos, other.end);
      }
      return false;
    }

    @Override
    public int hashCode() {
      throw new UnsupportedOperationException("hashCode() not supported");
    }

    @Override
    public String toString() {
      return raw.substring(pos, end);
    }

    /**
     * Compares this stream's range of characters against another range, returning
     * true if they are equal, false otherwise.
     */
    public boolean equalsCharacters(CharSequence other, int bs, int be) {
      int as = pos;
      int ae = end;
      int len = ae - as;
      if (len != be - bs) {
        return false;
      }
      for (int i = 0; i < len; i++) {
        if (raw.charAt(as + i) != other.charAt(bs + i)) {
          return false;
        }
      }
      return true;
    }

  }
}
